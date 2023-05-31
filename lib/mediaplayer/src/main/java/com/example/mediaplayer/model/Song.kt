package com.example.mediaplayer.model

data class Song(
    val m_Id: Long,
    val m_Title: String,
    val m_vDuration: Long,
    val m_Data: String,
    val m_vAlbumName: String,
    val m_vArtistName: String,
    val m_DisplayName: String
){
    companion object{
        fun emptySong ():Song{
            return Song(-1,"",-1,"","","","")
        }
    }
}
