package agency.v3.bootstrap.flows.launcher

import dagger.Subcomponent

@Subcomponent(modules = [RootModule::class])
interface RootInjector {
    fun inject(activity: RootActivity)
}