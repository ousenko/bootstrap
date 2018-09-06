package agency.v3.bootstrap.core.elements

import io.reactivex.observers.DisposableObserver

/**
 * A helper for building [DisposableObserver] in a fluent/DSL way
 */

class DisposableBuilder<T>
/**
 * @param allowQuietDisposable whether to allow all callbacks to be empty, or throw an
 */
(private val allowQuietDisposable: Boolean = false) {

    private var whenStart: (() -> Unit)? = null
    private var whenDone: (() -> Unit)? = null
    private var whenNext: ((T) -> Unit)? = null
    private var whenError: ((Throwable) -> Unit)? = null

    /**
     * Invoke this callback in [DisposableObserver.onNext]
     */
    fun next(onNext: ((T) -> Unit)): DisposableBuilder<T> {
        this.whenNext = onNext
        return this
    }

    /**
     * Invoke this callback in [DisposableObserver.onStart]
     */
    fun start(onStart: (() -> Unit)): DisposableBuilder<T> {
        this.whenStart = onStart
        return this
    }

    /**
     * Invoke this callback in [DisposableObserver.onError]
     */
    fun error(onError: ((Throwable) -> Unit)): DisposableBuilder<T> {
        this.whenError = onError
        return this
    }

    /**
     * Invoke this callback in [DisposableObserver.onComplete]
     */
    fun done(onDone: (() -> Unit)): DisposableBuilder<T> {
        this.whenDone = onDone
        return this
    }


    /**
     * Builds a [DisposableObserver].
     * @throws IllegalArgumentException if no callbacks provided AND allowQuietDisposable param is false
     */
    fun build(): DisposableObserver<T> {
        if (!allowQuietDisposable && whenStart == null && whenDone == null && whenError == null && whenNext == null) {
            throw IllegalArgumentException("Disposable does not define any callback, and allowQuietDisposable param is FALSE")
        }
        return object : DisposableObserver<T>() {

            override fun onComplete() {
                whenDone?.invoke()
            }

            override fun onError(e: Throwable) {
                whenError?.invoke(e)
            }

            override fun onNext(t: T) {
                whenNext?.invoke(t)
            }

            public override fun onStart() {
                whenStart?.invoke()
            }
        }
    }

}




