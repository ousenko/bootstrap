package agency.v3.bootstrap.flows.symbols

import agency.v3.bootstrap.core.elements.ExecutionContext
import agency.v3.bootstrap.core.elements.PostExecutionContext
import agency.v3.bootstrap.core.mvp.Interactor
import agency.v3.bootstrap.core.mvp.Receiver
import agency.v3.bootstrap.data.MarketApi
import agency.v3.bootstrap.data.TradingSymbol
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class SymbolsInteractor
@Inject constructor (
        executor: ExecutionContext,
        postExecutionContext: PostExecutionContext,
        private val api: MarketApi
        ): Interactor(executor, postExecutionContext) {


    fun loadSymbols(receiver: Receiver<List<TradingSymbol>>): Disposable {
        return api.symbols().toDisposable(receiver)
    }

}