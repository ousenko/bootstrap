package agency.v3.bootstrap.flows.symbols

import agency.v3.bootstrap.core.elements.ErrorWithRecovery
import agency.v3.bootstrap.core.mvp.IView
import agency.v3.bootstrap.data.TradingSymbol

interface ISymbolView : IView {
    var inProgress: Boolean

    fun setItems(items: List<TradingSymbol>)
    fun error(e: ErrorWithRecovery)
}