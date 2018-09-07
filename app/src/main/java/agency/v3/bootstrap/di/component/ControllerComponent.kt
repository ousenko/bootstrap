package agency.v3.bootstrap.di.component

import agency.v3.bootstrap.di.ControllerScope
import agency.v3.bootstrap.di.module.ControllerModule
import dagger.Subcomponent

@ControllerScope
@Subcomponent(modules = [ControllerModule::class])
interface ControllerComponent