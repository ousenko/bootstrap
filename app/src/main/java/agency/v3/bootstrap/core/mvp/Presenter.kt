package agency.v3.bootstrap.core.mvp

import agency.v3.bootstrap.core.elements.Destroyable
import agency.v3.bootstrap.core.elements.DisposeBag
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Base presenter claass
 */
abstract class Presenter<View : IView, PresenterState>(protected val state: PresenterState) : Destroyable {

    /**
     * Stuff that gets cleared on complete activity destroy
     */
    private val onDestroyBag = DisposeBag()

    /**
     * Maps View to it's properties' bindings
     * @deprecated
     */
//    private val connections = HashMap<View, DisposeBag>()

    private var isInitialized = false

    /**
     * Performs a one time initialization - if ViewModel outlives Activity or Fragment, and new instance of Activity/Fragment calls init, initialization will not happen again
     */
    private fun init() {
        if (!isInitialized) {

            onInit()
            isInitialized = true
        }
    }

    /**
     * Invoked when [Presenter] is initialized first time. You may perform initial loading here
     */
    protected abstract fun onInit()

    /**
     * Invoked each time when View is attached to presenter. Subscribe to all view's properties here.
     *
     * @return Subscription of connection of view's properties to presenter
     */
    protected abstract fun onAttach(view: View): Disposable

    /**
     *
     * Attaches to view.
     *
     * View and Presenter are connected functionally: presenter consumes view's outputs and notifies view's inputs
     * View layer should manage the subscriptions in order not to leak memory
     */
    fun attach(view: View): Disposable {
        init()
        return onAttach(view)
    }

    /**
     * Begins building [CompositeDisposable] binding for e.g. View's I/O
     */
    protected fun ioBinding(init: CompositeDisposable.() -> Unit): CompositeDisposable {
        val compositeDisposable = CompositeDisposable()
        init.invoke(compositeDisposable)
        return compositeDisposable
    }

    /**
     * Add an operation, that will be canceled upon container destroy
     * */
    fun act(name: String, d: Disposable) {
        onDestroyBag.addDisposable(name, d)
    }


    /**
     * Destroy this [Presenter]. Make sure you destroy presenter when [Controller] is destroyed (**not** when [View] is)
     */
    override fun destroy() {
//        for ((_, bag) in connections) {
//            bag.disposeAll()
//        }
//        connections.clear()

        onDestroyBag.disposeAll()
    }


}



