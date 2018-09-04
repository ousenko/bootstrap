package agency.v3.bootstrap

import agency.v3.bootstrap.BuildConfig.DEBUG
import agency.v3.bootstrap.di.component.AppComponent
import agency.v3.bootstrap.di.component.DaggerAppComponent
import agency.v3.bootstrap.di.module.AppModule
import agency.v3.bootstrap.os.AppNotificationChannel
import agency.v3.bootstrap.os.IMMLeaks
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import timber.log.Timber
import javax.inject.Inject

/**
 * An application
 * */
class BootstrapApp : Application() {

    companion object {
        lateinit var watcher: RefWatcher
        lateinit var component: AppComponent
        lateinit var instance: BootstrapApp

        val inForeground: Boolean get() = instance.monitor.isAppForeground
    }

    @Inject internal lateinit var monitor: AppStateMonitor



    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        instance = this



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = AppNotificationChannel.SOME_CHANNEL
            val notificationChannel = NotificationChannel(channel.channelId, getString(channel.channelNameResId), channel.importance)
            notificationChannel.description = getString(channel.descriptionResId)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }

        watcher = LeakCanary.install(this)

        component = DaggerAppComponent.builder()
                .appModule(AppModule(applicationContext))
                .build()


        IMMLeaks.fixFocusedViewLeak(this)
        if (DEBUG) Timber.plant(Timber.DebugTree())
        component.inject(this)

        registerActivityLifecycleCallbacks(monitor)

    }
}

