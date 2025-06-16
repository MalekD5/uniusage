package io.github.malekd5.uniusage.cli.commands;

import java.io.File;
import java.io.IOException;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;

import io.github.malekd5.uniusage.thread.ThreadManager;

@Parameters(commandDescription = "Find top K operations and user percentage")
public class TopOperationsCommand implements Runnable {

    @Parameter(names = { "--dup",
            "--allow-duplicates" }, description = "Allow duplicate users in operations", defaultValueDescription = "Defaults to false")
    private boolean allowDuplicates;

    @Parameter(names = { "--k",
            "--top-k" }, description = "Top K operations to display", defaultValueDescription = "Defaults to 2")
    private int topK = 2;

    @Parameter(names = "--logfile", description = "Path to log file", required = true, converter = FileConverter.class)
    private File file;

    private final ThreadManager threadManager;

    public TopOperationsCommand() {
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
            threadManager.consumeLogFile(file, (entry) -> {
                // TODO: Implement
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}