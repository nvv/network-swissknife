package com.nsak.android.core;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
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

    private ConcurrentMap<Integer, Future> mSubmitedTasks = new ConcurrentHashMap<>();

    public ThreadPool() {
        mTasksPool = new ThreadPoolExecutor(MAX_ACTIVE_LONG_TASKS / 2, MAX_ACTIVE_LONG_TASKS,
                CHECK_DOWNLOAD_QUEUE_PERIOD, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>()) {

            @Override
            protected void afterExecute(Runnable runnable, Throwable t) {
                super.afterExecute(runnable, t);
                mTasks.remove(extractId(runnable));
                mSubmitedTasks.remove(extractId(runnable));
            }

        };
    }

    @Override
    public void execute(@NonNull Runnable runnable) {
        int id = extractId(runnable);
        if (!mTasks.containsKey(id)) {
            mTasks.put(id, runnable);
            mSubmitedTasks.put(id, mTasksPool.submit(runnable));
        }
    }

    public void stopAllTasks() {
        mTasks.clear();
        for (Future future : mSubmitedTasks.values()) {
            future.cancel(true);
        }
    }

    private int extractId(Runnable runnable) {
        return runnable instanceof ThreadPoolRunnable ? ((ThreadPoolRunnable) runnable).getId() : runnable.hashCode();
    }

}
