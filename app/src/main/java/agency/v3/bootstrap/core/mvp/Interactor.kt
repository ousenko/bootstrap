package agency.v3.bootstrap.core.mvp

import agency.v3.bootstrap.core.elements.DisposableBuilder
import agency.v3.bootstrap.core.elements.ExecutionContext
import agency.v3.bootstrap.core.elements.PostExecutionContext
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable

typealias Receiver<T> = DisposableBuilder<T>.() -> Unit


/**
 * An interactor base class
 */
abstract class Interactor {

    private val worker: Scheduler
    private val notifier: Scheduler

    constructor(worker: Scheduler, notifier: Scheduler){
        this.worker = worker
        this.notifier = notifier
    }

    constructor(executionContext: ExecutionContext, postExecutionContext: PostExecutionContext){
        this.worker = executionContext.scheduler
        this.notifier = postExecutionContext.scheduler
    }


    /**
     * applies this [Interactor]'s schedulers to transform source Observable and subscribes [DisposableObserver] to transformed [Observable]
     */
    protected fun <T> Observable<T>.toDisposable(init: Receiver<T>): Disposable {
        val disposableBuilder = DisposableBuilder<T>(false)
        init.invoke(disposableBuilder)
        return compose { o -> o.subscribeOn(worker).observeOn(notifier) }.subscribeWith(disposableBuilder.build())
    }
}