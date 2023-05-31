package com.example.soundbox.ui.adapters.models

abstract class BaseRecycleViewItem(
  private val title: String,
  private val itemType: ItemType
) {
     var m_title: String
     var m_itemType: ItemType

    init {
        this.m_title = title
        this.m_itemType = itemType
    }

    enum class ItemType {
        SONG
    }


}