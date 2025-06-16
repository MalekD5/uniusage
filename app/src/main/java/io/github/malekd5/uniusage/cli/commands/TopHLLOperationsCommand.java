package io.github.malekd5.uniusage.cli.commands;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;
import io.github.malekd5.uniusage.thread.ThreadManager;
import io.github.malekd5.uniusage.utils.ThreadSafeHLL;
import io.github.malekd5.uniusage.utils.Utils;

@Parameters(commandDescription = "Find top K operations and user percentage using HyperLogLog, useful for larger datasets")
public class TopHLLOperationsCommand implements Runnable {

    @Parameter(names = { "--k",
            "--top-k" }, description = "Top K operations to display", defaultValueDescription = "Defaults to 2")
    private int topK = 2;

    @Parameter(names = { "--precision",
            "--hll-precision" }, description = "HyperLogLog precision, the higher the value, the more accurate the estimation, but the slower the computation", defaultValueDescription = "Defaults to 18 with standard error of 0.2%")
    private int precision = 18;

    @Parameter(names = "--logfile", description = "Path to log file", required = true, converter = FileConverter.class)
    private File file;

    private final ThreadManager threadManager;

    public TopHLLOperationsCommand() {
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

            ThreadSafeHLL totalUserCount = new ThreadSafeHLL(precision);

            ConcurrentHashMap<String, ThreadSafeHLL> estimatedCount = new ConcurrentHashMap<>();

            threadManager.consumeLogFile(file, (entry) -> {
                estimatedCount.computeIfAbsent(entry.operation(), op -> new ThreadSafeHLL(precision))
                        .offer(entry.userId());
                ;

                totalUserCount.offer(entry.userId());
            });

            var topKOperations = Utils.getTopK(estimatedCount, this.topK);
            long totalUsers = totalUserCount.cardinality();

            System.out.printf(
                    "Top %d operations by unique users with standard error of %.2f%%:%n%n",
                    topK,
                    Utils.calculateStandardError(precision));

            for (var entry : topKOperations) {
                String operation = entry.getKey();
                long userCount = entry.getValue().cardinality();

                Utils.printOperationUsage(operation, userCount, totalUsers);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}