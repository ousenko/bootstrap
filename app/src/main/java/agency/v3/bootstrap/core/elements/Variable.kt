package agency.v3.bootstrap.core.elements

import io.reactivex.disposables.Disposable


/**
 * Variable that holds value and notifies it's consumer about value changes. Only one consumer is supported.
 * Similar to RxSwift's Variable, but is much more simplified.

 * Two kinds of [Variable] are available:
 * - signal: accumulates result unless there's a consumer available, flushes value to consumer, clears the value so that subsequent subscribers will not receive it
 * - variable: keeps the value and notifies consumer about value changes; after notification, value is not cleared so subsequent subscribers will receive it at least once
 */
class Variable<T> private constructor(value: T?, private val shouldClearValueAfterConsumption: Boolean) {


    @Volatile
    var value: T? = value
        @Synchronized get() {
            return field
        }
        @Synchronized set(value) {

            val isDistinct = (field != value)
            field = value

            if (isDistinct) {
                maybeConsume(field)
            }
        }


    private var consumer: ((T) -> Unit)? = null

    private fun maybeConsume(captureVal: T?) {
        if (captureVal != null) {
            var consumeCount = 0
            consumer?.let {
                consumeCount++
                it.invoke(captureVal)
                maybeClearValue(true)
            }
        }
    }


    private fun maybeClearValue(wasConsumed: Boolean) {
        if (wasConsumed && shouldClearValueAfterConsumption) {
            this.value = null
        }
    }

    /**
     * Connect observer to that variable. Observer will be notified of only distinct value changes
     * @return Disposable that can be disposed thus ending the subscription of observer to this [Variable]
     * @throws IllegalStateException if you try to resubscribe already subscribed observer, or if this [Variable] is already subscribed to by some observer
     */
    fun observe(observer: (T) -> Unit): Disposable {
        synchronized(this) {
            if (consumer != null) {
                if (observer == consumer) {
                    throw IllegalStateException("Attempt to RE-SUBSCRIBE the same observer")
                } else {
                    throw IllegalStateException("Attempt to SUBSCRIBE observer while Variable already has observer")
                }
            } else {
                this.consumer = observer
                maybeConsume(value)
            }
        }

        return object : Disposable {
            //TODO: atomic reference, compare and set?
            @Volatile
            private var isDisposed = false

            override fun dispose() {
                synchronized(this) {
                    consumer = null
                    isDisposed = true
                }
            }

            override fun isDisposed(): Boolean {
                return isDisposed
            }
        }
    }

    companion object {
        /**
         * Constructs an empty Variable that can keep some value
         */
        fun <T> empty(): Variable<T> {
            return Variable(null, /*keep value*/false)
        }

        /**
         * Constructs a Variable that keeps value that's been set into it.
         */
        fun <T> value(value: T): Variable<T> {
            return Variable(value, /*keep value*/false)
        }

        /**
         * Constructs a Variable that keeps value only until it is consumed; after it is consumed, Variable does not hold value
         */
        fun <T> signal(): Variable<T> {
            return Variable(null, /*clear value after it's been consumed */true)
        }
    }
}