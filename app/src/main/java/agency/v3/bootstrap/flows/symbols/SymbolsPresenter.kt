package agency.v3.bootstrap.flows.symbols

import agency.v3.bootstrap.core.elements.ErrorWithRecovery
import agency.v3.bootstrap.core.elements.Variable
import agency.v3.bootstrap.core.mvp.Presenter
import agency.v3.bootstrap.data.TradingSymbol
import io.reactivex.disposables.Disposable
import javax.inject.Inject


class SymbolsPresenter @Inject constructor(val interactor: SymbolsInteractor) : Presenter<ISymbolView, SymbolsPresenter.State>(State()) {

    override fun onInit() {
        act("loadSymbols",
                interactor.loadSymbols {
                    start {
                        state.inProgress.value = true
                    }
                    next {
                        with(state) {
                            inProgress.value = false
                            symbols.value = it
                        }
                    }
                    error {
                        with(state) {
                            inProgress.value = false
                            error.value = ErrorWithRecovery(it)
                        }
                    }
                }
        )

    }

    override fun onAttach(view: ISymbolView): Disposable {
        return ioBinding {
            with(state) {
                addAll(
                        symbols.observe { view.setItems(it) },
                        error.observe { view.error(it) },
                        inProgress.observe { view.inProgress = it }
                )
            }
        }
    }


    class State {
        val symbols = Variable.value(listOf<TradingSymbol>())
        val error = Variable.signal<ErrorWithRecovery>()
        val inProgress = Variable.value(false)
    }
}