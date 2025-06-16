package io.github.malekd5.uniusage.thread;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.util.Optional;
import java.util.function.Consumer;

import io.github.malekd5.uniusage.data.LogEntry;
import io.github.malekd5.uniusage.utils.Utils;

public class ByteRangeThread implements Runnable {

    private final MappedByteBuffer buffer;
    private final long start;
    private final long end;

    private Consumer<LogEntry> consumer;

    public ByteRangeThread(MappedByteBuffer buffer, long start, long end, Consumer<LogEntry> consumer) {
        this.buffer = buffer;
        this.start = start;
        this.end = end;
        this.consumer = consumer;
    }

    @Override
    public void run() {
        try {
            final MappedByteBuffer buffer = this.buffer.duplicate();

            buffer.position((int) start);
            buffer.limit((int) end);

            CharBuffer chars = Utils.UTF8_DECODER.decode(buffer.slice());

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < chars.length(); i++) {
                char ch = chars.charAt(i);

                if (ch == '\n') {
                    Optional<LogEntry> entryOpt = LogEntry.parse(sb.toString());
                    entryOpt.ifPresent(consumer);
                    sb.setLength(0);
                } else {
                    sb.append(ch);
                }
            }
            if (sb.length() > 0) {
                Optional<LogEntry> entryOpt = LogEntry.parse(sb.toString());
                entryOpt.ifPresent(consumer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class ByteRangeThreadBuilder {
        private MappedByteBuffer buffer;
        private long start;
        private long end;
        private Consumer<LogEntry> consumer;

        public ByteRangeThreadBuilder buffer(MappedByteBuffer buffer) {
            this.buffer = buffer;
            return this;
        }

        public ByteRangeThreadBuilder start(long start) {
            this.start = start;
            return this;
        }

        public ByteRangeThreadBuilder end(long end) {
            this.end = end;
            return this;
        }

        public ByteRangeThreadBuilder consumer(Consumer<LogEntry> consumer) {
            this.consumer = consumer;
            return this;
        }

        public ByteRangeThread build() {
            return new ByteRangeThread(buffer, start, end, consumer);
        }
    }

}
