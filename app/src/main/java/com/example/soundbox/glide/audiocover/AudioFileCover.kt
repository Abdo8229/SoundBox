package com.example.soundbox.glide.audiocover

class AudioFileCover( val m_vPath:String  ) {

    override fun hashCode(): Int {
        return  this.m_vPath.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is AudioFileCover)
            return false
        return other.equals( other.m_vPath as AudioFileCover)
    }
    fun getFilePath():String = this.m_vPath
}


