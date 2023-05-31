package com.example.mediaplayer

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.media.MediaDescription
import android.media.MediaMetadata
import android.provider.MediaStore
import android.util.Log
import com.example.mediaplayer.model.Song
import java.util.TreeMap

class LibraryManager {
    companion object {
        private const val TAG = "Music"
        private val CURSOR_PROJECTION =
            arrayOf(
                "_id",
                "artist",
                "album",
                "title",
                "duration",
                "_display_name",
                "_data",
                "_size"
            )

        fun getSongs(context: Context): List<Song> {
            val cursor: Cursor? = context.contentResolver
                .query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    CURSOR_PROJECTION,
                    "is_music != 0",
                    null,
                    "_display_name ASC"
                )
            var _id: Int
            val results: MutableList<Song> = mutableListOf()
            val startTime: Long = System.currentTimeMillis()
            if ((cursor != null) && (cursor.count > 0)) {
                while (cursor.moveToNext()) {
                    val id: Int =getCursorStringByIndex(cursor, "_id").toInt()
                    var artistName: String = getCursorStringByIndex(cursor, "artist")
                    val albumName: String = getCursorStringByIndex(cursor, "album")
                    val title: String = getCursorStringByIndex(cursor, "title")
                    var displayName: String = getCursorStringByIndex(cursor, "_display_name")
                    val data: String = getCursorStringByIndex(cursor, "_data")
                    val duration: Long = getCursorLongByIndex(cursor, "duration")
//                    val size: String = getCursorStringByIndex(cursor, "_size")
                    if (artistName.isEmpty()) {
                        artistName = "<unknown>"
                    }
                    if (displayName.contains("AUD-") && title.isNotEmpty()) {
                        displayName = title
                    }
                    results.add(
                        Song(
                            id.toLong(),
                            title,
                            duration,
                            data,
                            albumName,
                            artistName,
                            displayName
                        )
                    )
                }
            }
            if (cursor != null) {
                cursor.close()
                val endTime: Long = System.currentTimeMillis()
                val duration: Long = endTime - startTime // how fast this fun

            }
            results.forEach{

                Log.d(TAG, "getSongs: ${it} ")

            }
            return results
        }

        fun getTreeMapOfSongs(songs: List<Song>): TreeMap<Int, Song> {
            val results: TreeMap<Int, Song> = TreeMap()
            songs.forEach { song ->
                results.put(song.m_Id.toInt(), song)
            }
            return results
        }

        fun getMediaDescription(song: Song): MediaDescription? {
            if (song == null) {
                return null
            }
            val builder: MediaDescription.Builder = MediaDescription.Builder()
            builder.setMediaId(song.m_Id.toString())
            builder.setIconBitmap(getSongArt(song))
            builder.setTitle(song.m_Title)
            builder.setSubtitle(song.m_vArtistName)
            builder.setDescription(song.m_Title)
            return builder.build()
        }

        private fun getMediaMetaData(song: Song): MediaMetadata? {

            if (song == null) {
                return null
            }
            val builder: MediaMetadata.Builder = MediaMetadata.Builder()
            builder.putString(MediaMetadata.METADATA_KEY_MEDIA_ID, song.m_Id.toString())
            builder.putString(MediaMetadata.METADATA_KEY_ALBUM, song.m_vAlbumName)
            builder.putString(MediaMetadata.METADATA_KEY_ARTIST, song.m_vArtistName)
            builder.putString(MediaMetadata.METADATA_KEY_TITLE, song.m_Title)
            builder.putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, song.m_DisplayName)
            builder.putLong(MediaMetadata.METADATA_KEY_DURATION, song.m_vDuration)
            val songArt: Bitmap? = getSongArt(song)
            if (songArt != null) {
                builder.putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, songArt)
            }
            return builder.build()
        }

        private fun getCursorLongByIndex(cursor: Cursor, columName: String): Long {
            val index: Int = cursor.getColumnIndex(columName)
            return if (index > -1) {
                cursor.getLong(index)
            } else {
                -1L
            }
        }

        private fun getCursorStringByIndex(cursor: Cursor, columName: String): String {
            val index: Int = cursor.getColumnIndex(columName)
            return if (index > 0) {
                cursor.getString(index)
            } else {
                ""
            }
        }

        private fun getSongArt(song: Song): Bitmap? {
            return null
        }
    }


}