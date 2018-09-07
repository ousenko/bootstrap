package agency.v3.bootstrap.flows.symbols

import agency.v3.bootstrap.R
import agency.v3.bootstrap.core.elements.ErrorWithRecovery
import agency.v3.bootstrap.data.TradingSymbol
import agency.v3.bootstrap.di.component.ControllerComponent
import agency.v3.bootstrap.ui.RichController
import agency.v3.bootstrap.ui.loading
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import butterknife.BindView
import javax.inject.Inject

class SymbolsController : RichController(), ISymbolView {

    @BindView(R.id.progress)
    lateinit var progress: ProgressBar



    @Inject
    internal lateinit var presenter: SymbolsPresenter

    @Inject
    internal lateinit var adapter: SymbolAdapter

    @BindView(R.id.list)
    lateinit var list: RecyclerView

    override var inProgress: Boolean = false
        set(value) {
            progress.loading(value)
        }


    override fun error(e: ErrorWithRecovery) {
        displayErrorDialog(e)
    }

    override fun setItems(items: List<TradingSymbol>) {
        adapter.items = items
    }

    override fun onViewCreated() {
        list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        list.adapter = adapter
    }

    override fun inject(controllerComponent: ControllerComponent): Any {
        controllerComponent.inject(this)
        autoDestroy("presenter", presenter)
        return controllerComponent
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.screen_symbols, container, false)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        autoDetach(presenter.attach(this))
    }
}