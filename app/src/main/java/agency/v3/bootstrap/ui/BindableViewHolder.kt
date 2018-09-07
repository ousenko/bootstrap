package agency.v3.bootstrap.ui

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import butterknife.ButterKnife


abstract class BindableViewHolder<Item>(parent: ViewGroup, @LayoutRes resId: Int)
    : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(resId, parent, false)) {

    init {
        ButterKnife.bind(this, itemView)
    }

    abstract fun bind(item: Item)
}