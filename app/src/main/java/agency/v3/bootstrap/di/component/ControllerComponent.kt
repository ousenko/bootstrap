package agency.v3.bootstrap.di.component

import agency.v3.bootstrap.di.ControllerScope
import agency.v3.bootstrap.di.module.ControllerModule
import agency.v3.bootstrap.flows.symbols.SymbolsController
import dagger.Subcomponent

@ControllerScope
@Subcomponent(modules = [ControllerModule::class])
interface ControllerComponent{
    fun inject(symbolsController: SymbolsController)

}