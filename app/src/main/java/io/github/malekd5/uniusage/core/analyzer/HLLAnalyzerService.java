package io.github.malekd5.uniusage.core.analyzer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.malekd5.uniusage.thread.ThreadManager;
import io.github.malekd5.uniusage.utils.ThreadSafeHLL;
import io.github.malekd5.uniusage.utils.Utils;

public class HLLAnalyzerService {

    private final ThreadManager threadManager;

    public HLLAnalyzerService(ThreadManager threadManager) {
        this.threadManager = threadManager;
    }

    public HLLAnalysisResult analyze(File file, int precision, int topK) throws IOException {
        ThreadSafeHLL totalUserCount = new ThreadSafeHLL(precision);
        ConcurrentHashMap<String, ThreadSafeHLL> estimatedCount = new ConcurrentHashMap<>();

        threadManager.consumeLogFile(file, (entry) -> {
            estimatedCount
                    .computeIfAbsent(entry.operation(), op -> new ThreadSafeHLL(precision))
                    .offer(entry.userId());
            totalUserCount.offer(entry.userId());
        });

        return new HLLAnalysisResult(
                Utils.getTopK(estimatedCount, topK),
                totalUserCount.cardinality());
    }

    public record HLLAnalysisResult(List<Map.Entry<String, ThreadSafeHLL>> topKOperations, long totalUsers) {
    }

}
