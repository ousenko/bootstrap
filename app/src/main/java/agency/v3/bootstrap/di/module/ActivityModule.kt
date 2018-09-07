package agency.v3.bootstrap.di.module

import agency.v3.bootstrap.flows.launcher.RootActivity
import com.bluelinelabs.conductor.Router
import dagger.Module
import dagger.Provides


@Module
class ActivityModule(private val activity: RootActivity, private val router: Router) {

    @Provides
    fun provideActivity() = activity

    @Provides
    fun router(): Router {
        return router
    }
}