package com.example.soundbox.ui.adapters.viewholders

import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import com.example.soundbox.ui.adapters.models.BaseRecycleViewItem

abstract class BaseViewHolder(val itemView: View, val viewType: ViewType ) : RecyclerView.ViewHolder(itemView) {
    private var m_viewType: ViewType
    enum class ViewType{
//        this viewType will be used to which type layout was created list or grid
        LIST
    }

    init {
        this.m_viewType = viewType
    }

     abstract fun onBindViewHolder(viewItem : BaseRecycleViewItem)


     fun findViewById(@IdRes id: Int): View {
        return this.itemView.findViewById(id)
    }
}
