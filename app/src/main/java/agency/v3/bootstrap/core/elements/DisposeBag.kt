package agency.v3.bootstrap.core.elements

import io.reactivex.disposables.Disposable
import java.util.HashMap


/**
 * A helper to remember Subscriptions, Destroyable (by name) and dispose them (by name, all)
 * when needed.
 * <br></br>
 *
 *
 * **This class is not thread safe. Please address to it from one thread only.**
 *
 * @author drew
 */

class DisposeBag {

    //keeps named references to disposables
    private val namedDisposables: HashMap<String, Disposable> = HashMap()
    //keeps named references to destroyables
    private val namedDestroyables: HashMap<String, Destroyable> = HashMap()


    /**
     * This method allows to add named operation handle, so that it could be further
     * cancelled by name.
     * Adding subsequent subscription with the same name will cancel previous ongoing operation
     */
    fun addDisposable(name: String, subscription: Disposable) {
        cancel(name)
        this.namedDisposables[name] = subscription

    }


    /**
     * This method allows to add named operation handle, so that it could be further
     * cancelled by name.
     * Adding subsequent subscription with the same name will cancel previous ongoing operation
     */
    fun addDisposable(name: String, disposable: Destroyable) {
        cancel(name)
        this.namedDestroyables[name] = disposable
    }


    /**
     * Disposes previously added disposable by name with which it was added.
     */
    private fun cancelDestroyableByName(name: String) {
        val previous = this.namedDestroyables.remove(name)
        previous?.destroy()
    }


    /**
     * Disposes previously added disposable by name with which it was added.
     */
    private fun cancelDisposableByName(name: String) {
        val previous = this.namedDisposables.remove(name)
        if (previous != null && !previous.isDisposed) {
            previous.dispose()
        }
    }

    /**
     * Disposes previously added disposable by name with which it was added.
     */
    private fun cancel(name: String) {
        cancelDisposableByName(name)
        cancelDestroyableByName(name)
    }


    /**
     * Disposes all disposables contained in this container.
     */
    fun disposeAll() {

        //dispose from all actions
        for (d in namedDestroyables.values) {
            d.destroy()
        }
        namedDestroyables.clear()

        //unsubscribe from all subscriptions and get rid from them
        for (s in namedDisposables.values) {
            if (!s.isDisposed) {
                s.dispose()
            }
        }
        namedDisposables.clear()

    }

}




