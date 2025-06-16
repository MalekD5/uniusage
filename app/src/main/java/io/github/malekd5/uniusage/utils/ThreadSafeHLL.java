package io.github.malekd5.uniusage.utils;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.clearspring.analytics.stream.cardinality.CardinalityMergeException;
import com.clearspring.analytics.stream.cardinality.HyperLogLog;

public class ThreadSafeHLL {
    private final HyperLogLog hll;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public ThreadSafeHLL(int precision) {
        this.hll = new HyperLogLog(precision);
    }

    public void offer(Object o) {
        lock.writeLock().lock();
        try {
            hll.offer(o);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public long cardinality() {
        lock.readLock().lock();
        try {
            return hll.cardinality();
        } finally {
            lock.readLock().unlock();
        }
    }

    public void merge(ThreadSafeHLL other) {
        lock.writeLock().lock();
        try {
            hll.addAll(other.hll);
        } catch (CardinalityMergeException e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
    }
}