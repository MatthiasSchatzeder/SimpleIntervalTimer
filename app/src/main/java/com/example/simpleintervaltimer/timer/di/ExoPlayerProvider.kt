package com.example.simpleintervaltimer.timer.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer

object ExoPlayerProvider {
    private var _player: ExoPlayer? = null

    fun getPlayer(context: Context): ExoPlayer = synchronized(this) {
        _player?.let { return it }
        _player = ExoPlayer.Builder(context).build()
        return _player!!
    }

    fun closePlayer() = synchronized(this) {
        _player?.release()
        _player = null
    }
}
