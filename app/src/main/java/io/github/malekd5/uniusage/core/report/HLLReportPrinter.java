package io.github.malekd5.uniusage.core.report;

import java.util.List;
import java.util.Map;

import io.github.malekd5.uniusage.utils.ThreadSafeHLL;
import io.github.malekd5.uniusage.utils.Utils;

public class HLLReportPrinter {
    public static void printReport(List<Map.Entry<String, ThreadSafeHLL>> topKOperations, long totalUsers, int topK,
            int precision) {
        System.out.printf(
                "Top %d operations by unique users with standard error of %.2f%%:%n%n",
                topK,
                Utils.calculateStandardError(precision) * 100);

        for (var entry : topKOperations) {
            String operation = entry.getKey();
            long userCount = entry.getValue().cardinality();
            Utils.printOperationUsage(operation, userCount, totalUsers);
        }
    }
}
