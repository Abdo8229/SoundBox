package com.example.soundbox.ui.adapters.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.soundbox.R
import com.example.soundbox.glide.audiocover.AudioFileCover
import com.example.soundbox.glide.audiocover.AudioFileCoverLoader
import com.example.soundbox.ui.adapters.models.BaseRecycleViewItem
import com.example.soundbox.ui.adapters.models.SongRecyclerViewItem

class SongViewHolder(itemView: View, viewType: ViewType) : BaseViewHolder(itemView,viewType) {

    override fun onBindViewHolder(viewItem: BaseRecycleViewItem) {
        val  item : SongRecyclerViewItem = viewItem as SongRecyclerViewItem
//   TextView
      val title :  TextView = findViewById(R.id.item_song_title) as  TextView
      val imageVeiw :  ImageView = findViewById(R.id.item_song_art) as  ImageView
        title.text = viewItem.m_title
        Glide.with(itemView.context)
            .load(AudioFileCover(item.getFilePath()))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(imageVeiw)

    }

}