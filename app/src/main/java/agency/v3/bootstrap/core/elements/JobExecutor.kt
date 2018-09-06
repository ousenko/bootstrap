package agency.v3.bootstrap.core.elements

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Decorated [java.util.concurrent.ThreadPoolExecutor]
 */
class JobExecutor : ExecutionContext {

    private val threadPoolExecutor: ThreadPoolExecutor

    override val scheduler: Scheduler
        get() = Schedulers.from(threadPoolExecutor)

    /**
     * Constructs [JobExecutor] using default params and provided thread factory
     */
    constructor(threadFactory: ThreadFactory) {
        this.threadPoolExecutor = ThreadPoolExecutor(
                DEF_CORE_POOL_SIZE,
                DEF_MAX_POOL_SIZE,
                DEF_KEEP_ALIVE_TIME,
                DEF_KEEP_ALIVE_TIME_UNIT,
                LinkedBlockingQueue<Runnable>(),
                threadFactory
        )
    }

    /**
     * Constructs [JobExecutor] using provided thread pool executor
     */
    constructor(poolExecutor: ThreadPoolExecutor) {
        this.threadPoolExecutor = poolExecutor
    }

    companion object {
        private const val DEF_CORE_POOL_SIZE = 3
        private const val DEF_MAX_POOL_SIZE = 5
        private const val DEF_KEEP_ALIVE_TIME = 10L
        private val DEF_KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS
    }

}