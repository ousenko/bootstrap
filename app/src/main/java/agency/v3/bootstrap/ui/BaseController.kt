package agency.v3.bootstrap.ui

import agency.v3.bootstrap.BootstrapApp
import agency.v3.bootstrap.core.elements.Destroyable
import agency.v3.bootstrap.core.elements.DisposeBag
import agency.v3.bootstrap.di.component.ActivityComponentOwner
import agency.v3.bootstrap.di.component.ControllerComponent
import agency.v3.bootstrap.di.module.ControllerModule
import agency.v3.bootstrap.os.safeWatch
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import butterknife.ButterKnife
import butterknife.Unbinder
import com.bluelinelabs.conductor.RestoreViewOnCreateController
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*

/**
 * Base class for all UI controllers; contains dependency injection and lifecycle-related logic
 * */
abstract class BaseController : RestoreViewOnCreateController {


    private val unbinders = ArrayList<Unbinder>()

    /**
     * injections made by this controller
     * */
    private var injectedReferences: Any? = null

    private var controllerComponent: ControllerComponent? = null

    private val onDestroyBag = DisposeBag()
    private val autodetachable = CompositeDisposable()

    /**
     * indicates whether this controller was created, i.e. injected
     */
    private var onCreated = false


    protected constructor() : super()
    protected constructor(bundle: Bundle) : super(bundle)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
        val view = createView(inflater, container, savedInstanceState)
        autoUnbind(ButterKnife.bind(this, view))
        maybePerformCreate()
        onViewCreated()
        return view
    }

    /**
     * Override this method to change soft input mode of window from default. Default is adjustResize|stateAlwaysVisible
     */
    protected open fun setSoftInputMode() {
        activity?.window?.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE or
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )

    }

    /**
     * Perform after-view-created setup, like setting adapters for lists
     */
    protected abstract fun onViewCreated()

    /**
     * Performs dependency injection, and returns injected references (component)
     */
    protected abstract fun inject(controllerComponent: ControllerComponent): Any

    override fun onDestroyView(view: View) {
        unbindAll()
        super.onDestroyView(view)
        BootstrapApp.watcher.safeWatch(view)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (onCreated) {
            dispose()
        }
        onCreated = false

        BootstrapApp.watcher.safeWatch(injectedReferences)
        BootstrapApp.watcher.safeWatch(controllerComponent)
        injectedReferences = null //lose graph reference
        controllerComponent = null
        BootstrapApp.watcher.safeWatch(this)
    }

    private fun maybePerformCreate() {
        if (!onCreated) {
            val componentOwner = activity as? ActivityComponentOwner
                    ?: throw IllegalStateException("Activity should be of ActivityComponentOwner type")

            controllerComponent = componentOwner.component.plus(ControllerModule())

            injectedReferences = inject(controllerComponent!!)
            setSoftInputMode()
            onCreated = true
        }
    }

    private fun dispose() {
        onDestroyBag.disposeAll()
        autodetachable.clear()
        unbindAll()
    }


    override fun onAttach(view: View) {
        autodetachable.clear()
        super.onAttach(view)
    }

    override fun onDetach(view: View) {
        autodetachable.clear()
        super.onDetach(view)
    }

    protected abstract fun createView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View


    private fun autoUnbind(unbinder: Unbinder): Unbinder {
        unbinders.add(unbinder)
        return unbinder
    }

    private fun unbindAll() {
        for (u in unbinders) {
            u.unbind()
        }
        unbinders.clear()
    }


    /**
     * register an object that should be released when view is detached from controller, though controller is not yet destroyed
     */
    protected fun autoDetach(subscription: Disposable) {
        autodetachable.add(subscription)
    }

    /**
     * register an object that should be released when controller is destroyed
     */
    protected fun <T : Destroyable> autoDestroy(tag: String, subscription: T): T {
        onDestroyBag.addDisposable(tag, subscription)
        return subscription
    }

    protected fun autoDestroy(name: String, subscription: Disposable) {
        onDestroyBag.addDisposable(name, subscription)
    }

}

