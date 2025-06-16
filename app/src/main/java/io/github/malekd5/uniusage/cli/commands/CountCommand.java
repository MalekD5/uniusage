package io.github.malekd5.uniusage.cli.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;

import io.github.malekd5.uniusage.cli.MainArgs;
import io.github.malekd5.uniusage.thread.ThreadManager;

@Parameters(commandDescription = "Count number of entries in log file")
public class CountCommand implements Runnable {

    @Parameter(names = "--logfile", description = "Path to log file", required = true, converter = FileConverter.class)
    private File file;

    private final ThreadManager threadManager;

    public CountCommand(MainArgs args) {
        this.threadManager = new ThreadManager();
    }

    @Override
    public void run() {
        if (!file.exists()) {
            System.out.println("File does not exist: " + file.getAbsolutePath());
            return;
        }

        if (!file.canRead()) {
            System.out.print("File cannot be read: " + file.getAbsolutePath());
            return;
        }

        try {
            AtomicInteger totalCount = new AtomicInteger();
            threadManager.consumeLogFile(file, (entry) -> {
                totalCount.incrementAndGet();
            });
            System.out.println("Total entries: " + totalCount.get());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}