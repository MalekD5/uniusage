package io.github.malekd5.uniusage.cli.commands;

import java.io.File;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;

import io.github.malekd5.uniusage.core.analyzer.BFTopAnalyzerService;
import io.github.malekd5.uniusage.core.report.BFTopReportPrinter;

@Parameters(commandDescription = "Brute force check for top K operations and user percentage, useful for smaller datasets")
public class TopCommand implements Runnable {

    @Parameter(names = "--logfile", description = "Path to log file", required = true, converter = FileConverter.class)
    private File file;

    @Parameter(names = { "--k",
            "--top-k" }, description = "Top K operations to display", defaultValueDescription = "Defaults to 2")
    private int topK = 2;

    private final BFTopAnalyzerService service;

    public TopCommand() {
        this.service = new BFTopAnalyzerService();
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

        var result = service.analyze(file, topK);
        long totalUsers = result.totalUsers();

        if (totalUsers == 0) {
            System.out.println("No users found.");
            return;
        }

        BFTopReportPrinter.printReport(result.topKOperations(), totalUsers, topK);
    }

}
