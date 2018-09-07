package agency.v3.bootstrap.flows.symbols

import agency.v3.bootstrap.R
import agency.v3.bootstrap.data.TradingSymbol
import agency.v3.bootstrap.ui.BindableViewHolder
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import javax.inject.Inject

class SymbolAdapter @Inject constructor(): RecyclerView.Adapter<TradingSymbolViewHolder>() {

    var items: List<TradingSymbol> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, type: Int): TradingSymbolViewHolder {
        return TradingSymbolViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(vh: TradingSymbolViewHolder, position: Int) {
        vh.bind(items[position])
    }


}

class TradingSymbolViewHolder(view: ViewGroup) : BindableViewHolder<TradingSymbol>(view, R.layout.listitem_symbol) {

    @BindView(R.id.title)
    lateinit var title: TextView

    override fun bind(item: TradingSymbol) {
        title.text = item.symbol
    }

}