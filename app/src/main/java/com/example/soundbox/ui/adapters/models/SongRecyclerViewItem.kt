package com.example.soundbox.ui.adapters.models

import com.example.mediaplayer.model.Song

class SongRecyclerViewItem( song: Song) : BaseRecycleViewItem(song.m_Title, ItemType.SONG) {
    private var m_vItem: Song

    init {
        this.m_vItem = song

    }
    fun getFilePath(): String {
        return this.m_vItem.m_Data
    }
}