package agency.v3.bootstrap.di.component

import agency.v3.bootstrap.BootstrapApp
import agency.v3.bootstrap.di.module.ActivityModule
import agency.v3.bootstrap.di.module.AppModule
import agency.v3.bootstrap.di.module.NetworkModule
import dagger.Component
import javax.inject.Singleton

/**
 * Application-scope component, used to inject Application itself, Services, Receivers etc
 * */
@Singleton
@Component(modules = [AppModule::class, NetworkModule::class])
interface AppComponent {

    fun plus(activityModule: ActivityModule): ActivityComponent

    fun inject(app: BootstrapApp)

}