package com.example.soundbox.glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.module.AppGlideModule
import com.example.soundbox.glide.audiocover.AudioFileCover
import com.example.soundbox.glide.audiocover.AudioFileCoverLoader
import java.io.InputStream

class SampleGlideModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        registry.append(
            AudioFileCover::class.java,
            InputStream::class.java,
            AudioFileCoverLoader.Companion.Factory()
        )
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false;
    }
}