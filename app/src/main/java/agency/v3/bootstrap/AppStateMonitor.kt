package agency.v3.bootstrap

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * Callbacks for watching specific application events: going background and foreground
 * */
interface ApplicationCallbacks {
    fun onApplicationWentBackground()

    fun onApplicationWentForeground()
}


/**
 * Implementation of ActivityLifecycleCallbacks that accounts
 * for number of activities started in process and thus tracks background/foreground state of app from a perspective of user
 * */
class AppStateMonitor(private val callbacks: ApplicationCallbacks)
    : Application.ActivityLifecycleCallbacks {

    @Volatile
    private var startedActivitiesCount = 0

    /**
     * Is app visible to user ?
     */
    val isAppForeground: Boolean
        @Synchronized get() = startedActivitiesCount > 0

    override fun onActivityStarted(activity: Activity) {
        val from = startedActivitiesCount
        val to = ++startedActivitiesCount
        maybeInvokeCallback(from, to)
    }

    private fun maybeInvokeCallback(from: Int, to: Int) {
        if (from > to && to == 0) {
            callbacks.onApplicationWentBackground()
        } else if (from < to && to == 1) {
            callbacks.onApplicationWentForeground()
        }
    }


    override fun onActivityStopped(activity: Activity) {
        val from = startedActivitiesCount
        val to = --startedActivitiesCount
        maybeInvokeCallback(from, to)
    }


    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }
}