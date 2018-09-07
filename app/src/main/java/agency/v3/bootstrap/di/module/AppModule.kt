package agency.v3.bootstrap.di.module

import agency.v3.bootstrap.AppStateMonitor
import agency.v3.bootstrap.ApplicationCallbacks
import agency.v3.bootstrap.core.elements.ExecutionContext
import agency.v3.bootstrap.core.elements.PostExecutionContext
import agency.v3.bootstrap.os.NotificationsService
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.v4.app.NotificationManagerCompat
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import javax.inject.Singleton
import agency.v3.bootstrap.core.elements.JobExecutor
import java.util.concurrent.ThreadFactory
import javax.inject.Named


@Module
class AppModule(private val applicationContext: Context) {

    @Singleton
    @Provides
    fun provideApplicationContext(): Context = applicationContext

    @Provides
    @Singleton
    fun provideSharedPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(applicationContext)
    }


    @Provides
    @Singleton
    fun notificationsService(): NotificationsService {
        return NotificationsService(applicationContext, NotificationManagerCompat.from(applicationContext))
    }


    @Provides
    @Singleton
    fun stateMonitor(): AppStateMonitor {
        return AppStateMonitor(object : ApplicationCallbacks {
            override fun onApplicationWentBackground() {
                Timber.i("App went background")
            }

            override fun onApplicationWentForeground() {
                Timber.i("App went foreground")
            }
        })
    }

    @Provides
    @Singleton
    internal fun postExecutionContext(): PostExecutionContext {
        return object : PostExecutionContext {
            override val scheduler: Scheduler
                get() = AndroidSchedulers.mainThread()
        }
    }




    @Provides
    @Singleton
    fun executionContext(@Named("multipleBackgroundFactory") factory: ThreadFactory): ExecutionContext {
        return JobExecutor(factory)
    }


    @Provides
    @Named("multipleBackgroundFactory")
    fun multiBackgroundThreadFactory(): ThreadFactory {

        return object : ThreadFactory {

            var count: Int = 0

            override fun newThread(r: Runnable): Thread {
                return Thread(r, "executor$" + count++)
            }
        }
    }

}