package com.example.soundbox.glide.audiocover

import android.media.MediaMetadataRetriever
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception

class AudioFileCoverFetcher(
    val m_vModel: AudioFileCover
) : DataFetcher<InputStream> {
    private  var m_vStream: InputStream?
    init {
        this.m_vStream = null
    }

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
      val retriever :   MediaMetadataRetriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(this.m_vModel.getFilePath())
            val data : ByteArray? = retriever.embeddedPicture
            if (data!=null&& data.isNotEmpty()){
                this.m_vStream = ByteArrayInputStream(data)
                callback.onDataReady(this.m_vStream)
            }
        }catch (ex : Exception){
            callback.onLoadFailed(ex)
        }finally {
           try {
               retriever.release()
           } catch (_: IOException){

           }
        }
    }

    override fun cleanup() {
        if (this.m_vStream!=null){
            try{ this.m_vStream!!.close()
            }catch (_ : Exception ){}
        }
    }

    override fun cancel() {
//        can't Cancel
    }

    override fun getDataClass(): Class<InputStream> {
        return InputStream::class.java
    }

    override fun getDataSource(): DataSource {
        return DataSource.LOCAL

    }
}