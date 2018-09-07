package agency.v3.bootstrap.ui

import agency.v3.bootstrap.R
import agency.v3.bootstrap.core.elements.Destroyable
import agency.v3.bootstrap.core.elements.ErrorWithRecovery
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.inputmethod.InputMethodManager
import javax.inject.Inject

/**
 * Extension to Base controller that adds useful functionality to manipulate keyboard, dialogs etc
 * */
abstract class RichController: BaseController {

    @Inject
    lateinit var context: Context

    protected constructor() : super()
    protected constructor(bundle: Bundle) : super(bundle)


    /**
     * Open keyboard without changing focus
     */
    protected fun showSoftKeyboard() {
        activity?.let {
            showSoftKeyboard(it.currentFocus)
        }

    }

    /**
     * Open keyboard and focis specific view
     *
     * @param view View which will be get focus
     */
    protected fun showSoftKeyboard(view: View?) {
        view?.let{
            it.post{
                it.requestFocus()
                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }


    protected fun hideSoftKeyboard(view: View?) {
        view?.let{
            it.post{
                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(it.windowToken, 0)
            }
        }
    }

    protected fun hideSoftKeyboard() {
        hideSoftKeyboard(activity?.currentFocus)
    }

    /**
     * Display a standardised application error dialog with recovery possibility
     */
    fun displayErrorDialog(errorWithRecoveryResolution: ErrorWithRecovery) {
        //only display if controller is attached to activity
        activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setCancelable(false)

            val recoveryOperation = errorWithRecoveryResolution.recovery
            val cancelOperation = errorWithRecoveryResolution.cancellation
            if (recoveryOperation != null || cancelOperation != null) {
                if (recoveryOperation != null) {
                    builder.setNegativeButton(it.getString(R.string.try_again)) { _, _ -> recoveryOperation.invoke() }
                }
                if (cancelOperation != null) {
                    builder.setPositiveButton(it.getString(R.string.cancel)) { _, _ -> cancelOperation.invoke() }
                }

            } else {
                builder.setPositiveButton(R.string.ok) { _, _ -> }
            }

            builder.setTitle(it.getString(R.string.error))
            builder.setMessage(errorWithRecoveryResolution.reason)
            val dialog = builder.show()
            autoDestroy("dialog", object : Destroyable {
                override fun destroy() {
                    dialog.cancel()
                }
            })
        }
    }
}