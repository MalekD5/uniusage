package io.github.malekd5.uniusage.thread;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import io.github.malekd5.uniusage.core.data.LogEntry;
import io.github.malekd5.uniusage.thread.ByteRangeThread.ByteRangeThreadBuilder;
import io.github.malekd5.uniusage.utils.Utils;

public class ThreadManager {

    private final ExecutorService executorService;

    public ThreadManager() {
        this.executorService = Executors.newFixedThreadPool(Utils.THREAD_COUNT);
    }

    public void submit(Runnable runnable) {
        this.executorService.submit(runnable);
    }

    public void consumeLogFile(File file, Consumer<LogEntry> consumer) throws FileNotFoundException, IOException {
        int THREAD_COUNT = Utils.THREAD_COUNT;
        long fileSize = file.length();
        long chunkSize = fileSize / THREAD_COUNT;

        try (RandomAccessFile raf = new RandomAccessFile(file, "r");
                FileChannel fc = raf.getChannel()) {

            long[] starts = new long[THREAD_COUNT];
            long[] ends = new long[THREAD_COUNT];
            starts[0] = 0;

            for (int i = 0; i < THREAD_COUNT; i++) {
                if (i == THREAD_COUNT - 1) {
                    ends[i] = fileSize;
                } else {
                    ends[i] = Utils.findNextNewline(fc, (i + 1) * chunkSize, fileSize);
                    starts[i + 1] = ends[i];
                }
            }

            for (int i = 0; i < THREAD_COUNT; i++) {
                var builder = new ByteRangeThreadBuilder()
                        .start(starts[i])
                        .end(ends[i])
                        .buffer(fc.map(FileChannel.MapMode.READ_ONLY, 0, fileSize))
                        .consumer(consumer);

                this.submit(builder.build());
            }

            this.shutdown();
        }
    }

    public void shutdown() {
        this.executorService.shutdown();
        try {
            this.executorService.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
