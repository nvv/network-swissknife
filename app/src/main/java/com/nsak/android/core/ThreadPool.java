package com.nsak.android.core;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Vlad Namashko.
 */
public class ThreadPool implements Executor {

    protected static final int CPU_COUNT = Runtime.getRuntime().availableProcessors() * 2;
    private static final int MAX_ACTIVE_LONG_TASKS = CPU_COUNT * 8;
    private static final int CHECK_DOWNLOAD_QUEUE_PERIOD = 1;

    private ThreadPoolExecutor mTasksPool;
    private ConcurrentMap<Integer, Runnable> mTasks = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Integer, Future> mSubmittedTasks = new ConcurrentHashMap<>();
    private Set<Integer> mNetworkScanTasks = Collections.synchronizedSet(new HashSet<Integer>());

    public ThreadPool() {
        mTasksPool = new ThreadPoolExecutor(MAX_ACTIVE_LONG_TASKS / 2, MAX_ACTIVE_LONG_TASKS,
                CHECK_DOWNLOAD_QUEUE_PERIOD, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>()) {

            @Override
            protected void afterExecute(Runnable runnable, Throwable t) {
                super.afterExecute(runnable, t);
                Integer id = extractId(runnable);
                mTasks.remove(id);
                mNetworkScanTasks.remove(id);
                mSubmittedTasks.remove(id);
            }

        };
    }

    @Override
    public void execute(@NonNull Runnable runnable) {
        Integer id = extractId(runnable);
        if (!mTasks.containsKey(id)) {
            mTasks.put(id, runnable);
            mSubmittedTasks.put(id, mTasksPool.submit(runnable));
        }
    }

    public void executeNetworkTask(@NonNull Runnable runnable) {
        mNetworkScanTasks.add(extractId(runnable));
        execute(runnable);
    }

    public void stopAllTasks() {
        mTasks.clear();
        mNetworkScanTasks.clear();
        for (Future future : mSubmittedTasks.values()) {
            future.cancel(true);
        }
    }

    public void stopNetworkScanTasks() {
        for (Integer id : mSubmittedTasks.keySet()) {
            if (mNetworkScanTasks.contains(id)) {
                mNetworkScanTasks.remove(id);
                mTasks.remove(id);
                mSubmittedTasks.remove(id);
            }
        }
    }

    private Integer extractId(Runnable runnable) {
        return runnable instanceof ThreadPoolRunnable ? ((ThreadPoolRunnable) runnable).getId() : runnable.hashCode();
    }

}
