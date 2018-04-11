package com.library.base.executor


import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit



/**
 * 异步执行池
 * @Author: jerome
 * @Date: 2017-08-07
 */

 object JobExecutor : ThreadExecutor {

    private val mThreadPoolExecutor: ThreadPoolExecutor
    private val INITIAL_POOL_SIZE = 3
    private val MAX_POOL_SIZE = 5
    // Sets the amount of time an idle thread waits before terminating
    private val KEEP_ALIVE_TIME = 10L
    // Sets the Time Unit to seconds
    private val KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS

    init {
        val workQueue = LinkedBlockingQueue<Runnable>()
        val threadFactory = JobThreadFactory()
        mThreadPoolExecutor = ThreadPoolExecutor(INITIAL_POOL_SIZE, MAX_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, workQueue, threadFactory)
    }

    override fun execute(runnable: Runnable) {
        mThreadPoolExecutor.execute(runnable)
    }

    internal class JobThreadFactory : ThreadFactory {
        private var counter = 0

       override fun newThread(runnable: Runnable): Thread {
            return Thread(runnable, THREAD_NAME_ + counter++)
        }

        companion object {
            private val THREAD_NAME_ = "android_"
        }
    }
}