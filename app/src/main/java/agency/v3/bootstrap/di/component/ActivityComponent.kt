package agency.v3.bootstrap.di.component

import agency.v3.bootstrap.di.ActivityScope
import agency.v3.bootstrap.di.module.ActivityModule
import agency.v3.bootstrap.di.module.ControllerModule
import agency.v3.bootstrap.flows.launcher.RootInjector
import agency.v3.bootstrap.flows.launcher.RootModule
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {

    fun plus(controllerModule: ControllerModule): ControllerComponent
    fun plus(rootModule: RootModule): RootInjector
}

/**
 * Marker interface for objects possessing activity component
 * */
interface ActivityComponentOwner {
    val component: ActivityComponent
}

