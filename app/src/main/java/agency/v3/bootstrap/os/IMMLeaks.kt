package agency.v3.bootstrap.os

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.ContextWrapper
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.os.MessageQueue
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import timber.log.Timber
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * A dirty hack for removing annoying memory leaks related to InputMethodManager that seemingly
 * will never be fixed by platform developers
 * */
object IMMLeaks {

    internal class ReferenceCleaner(private val inputMethodManager: InputMethodManager, private val mHField: Field, private val mServedViewField: Field,
                                    private val finishInputLockedMethod: Method) : MessageQueue.IdleHandler, View.OnAttachStateChangeListener, ViewTreeObserver.OnGlobalFocusChangeListener {

        override fun onGlobalFocusChanged(oldFocus: View?, newFocus: View?) {
            if (newFocus == null) {
                return
            }
            oldFocus?.removeOnAttachStateChangeListener(this)
            Looper.myQueue().removeIdleHandler(this)
            newFocus.addOnAttachStateChangeListener(this)
        }

        override fun onViewAttachedToWindow(v: View) {}

        override fun onViewDetachedFromWindow(v: View) {
            v.removeOnAttachStateChangeListener(this)
            Looper.myQueue().removeIdleHandler(this)
            Looper.myQueue().addIdleHandler(this)
        }

        override fun queueIdle(): Boolean {
            clearInputMethodManagerLeak()
            return false
        }

        private fun clearInputMethodManagerLeak() {
            try {
                val lock = mHField.get(inputMethodManager)
                // This is highly dependent on the InputMethodManager implementation.
                synchronized(lock) {
                    val servedView = mServedViewField.get(inputMethodManager) as View?
                    if (servedView != null) {

                        val servedViewAttached = servedView.windowVisibility != View.GONE

                        if (servedViewAttached) {
                            // The view held by the IMM was replaced without a global focus change. Let's make
                            // sure we get notified when that view detaches.

                            // Avoid double registration.
                            servedView.removeOnAttachStateChangeListener(this)
                            servedView.addOnAttachStateChangeListener(this)
                        } else {
                            // servedView is not attached. InputMethodManager is being stupid!
                            val activity = extractActivity(servedView.context)
                            if (activity == null || activity.window == null) {
                                // Unlikely case. Let's finish the input anyways.
                                finishInputLockedMethod.invoke(inputMethodManager)
                            } else {
                                val decorView = activity.window.peekDecorView()
                                val windowAttached = decorView.windowVisibility != View.GONE
                                if (!windowAttached) {
                                    finishInputLockedMethod.invoke(inputMethodManager)
                                } else {
                                    decorView.requestFocusFromTouch()
                                }
                            }
                        }
                    }
                }
            } catch (unexpected: IllegalAccessException) {
                Timber.e(unexpected, "Unexpected reflection exception")
            } catch (unexpected: InvocationTargetException) {
                Timber.e(unexpected, "Unexpected reflection exception")
            }

        }

        private fun extractActivity(context1: Context): Activity? {
            var context = context1
            while (true) {
                when (context) {
                    is Application -> return null
                    is Activity -> return context
                    is ContextWrapper -> {
                        val baseContext = context.baseContext
                        // Prevent Stack Overflow.
                        if (baseContext === context) {
                            return null
                        }
                        context = baseContext
                    }
                    else -> return null
                }
            }
        }
    }

    /**
     * Fix for https://code.google.com/p/android/issues/detail?id=171190 .
     *
     * When a view that has focus gets detached, we wait for the main thread to be idle and then
     * check if the InputMethodManager is leaking a view. If yes, we tell it that the decor view got
     * focus, which is what happens if you press home and come back from recent apps. This replaces
     * the reference to the detached view with a reference to the decor view.
     *
     * Should be called from [Activity.onCreate] )}.
     */
    @SuppressLint("ObsoleteSdkInt")
    internal fun fixFocusedViewLeak(application: Application) {

        // Don't know about other versions yet.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            return
        }

        val inputMethodManager = application.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        val mServedViewField: Field
        val mHField: Field
        val finishInputLockedMethod: Method
        val focusInMethod: Method
        try {
            mServedViewField = InputMethodManager::class.java.getDeclaredField("mServedView")
            mServedViewField.isAccessible = true
            mHField = InputMethodManager::class.java.getDeclaredField("mServedView")
            mHField.isAccessible = true
            finishInputLockedMethod = InputMethodManager::class.java.getDeclaredMethod("finishInputLocked")
            finishInputLockedMethod.isAccessible = true
            focusInMethod = InputMethodManager::class.java.getDeclaredMethod("focusIn", View::class.java)
            focusInMethod.isAccessible = true
        } catch (unexpected: NoSuchMethodException) {
            Timber.e(unexpected, "Unexpected reflection exception")
            return
        } catch (unexpected: NoSuchFieldException) {
            Timber.e(unexpected, "Unexpected reflection exception")
            return
        }

        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                val cleaner = ReferenceCleaner(inputMethodManager, mHField, mServedViewField,
                        finishInputLockedMethod)
                val rootView = activity.window.decorView.rootView
                val viewTreeObserver = rootView.viewTreeObserver
                viewTreeObserver.addOnGlobalFocusChangeListener(cleaner)
            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {

            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {

            }

            override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {

            }

            override fun onActivityDestroyed(activity: Activity) {

            }
        })
    }
}