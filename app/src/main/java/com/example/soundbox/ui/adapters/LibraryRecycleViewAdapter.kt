package com.example.soundbox.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.soundbox.R
import com.example.soundbox.ui.adapters.models.BaseRecycleViewItem
import com.example.soundbox.ui.adapters.viewholders.BaseViewHolder
import com.example.soundbox.ui.adapters.viewholders.SongViewHolder

class LibraryRecycleViewAdapter(items: List<BaseRecycleViewItem>) : BaseRecycleViewAdapter(items) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
       return when(BaseRecycleViewItem.ItemType.values()[viewType]){
            BaseRecycleViewItem.ItemType.SONG ->SongViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_song_layout_sample,parent,false),BaseViewHolder.ViewType.LIST)
       }
    }


}