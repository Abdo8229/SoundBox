package com.example.soundbox.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.example.soundbox.ui.adapters.models.BaseRecycleViewItem
import com.example.soundbox.ui.adapters.viewholders.BaseViewHolder
import org.jetbrains.annotations.NotNull

abstract class BaseRecycleViewAdapter(items: List<BaseRecycleViewItem>) :
    RecyclerView.Adapter<BaseViewHolder>() {
    private var m_vItems: List<BaseRecycleViewItem>
    init {
       this.m_vItems =  items
    }
      override fun onBindViewHolder(@NotNull holder : BaseViewHolder, position : Int){
        holder.onBindViewHolder(this.m_vItems[position])
    }

    override fun getItemCount(): Int {
        return this.m_vItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return this.m_vItems[position].m_itemType.ordinal
    }
}