package agency.v3.bootstrap.core.elements

import io.reactivex.Scheduler

/**
 * Thread abstraction representing where jobs are actually executed by ReactiveX
 */
interface ExecutionContext {
    val scheduler: Scheduler
}

/**
 * Thread abstraction representing which thread job results are delivered on, e.g. Notification thread (usually UI thread in Android apps)
 */
interface PostExecutionContext {
    val scheduler: Scheduler
}