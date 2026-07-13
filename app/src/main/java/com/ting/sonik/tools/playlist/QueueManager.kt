package com.ting.sonik.tools.playlist

import com.ting.sonik.tools.Song

object QueueManager {

    fun moveToFront(playlist: List<Song>, currentSong: Song, targetSong: Song, frontCount: Int = 0): List<Song> {
        val currentIdx = playlist.indexOfFirst { it.id == currentSong.id }
        val targetIdx = playlist.indexOfFirst { it.id == targetSong.id }
        if (targetIdx == -1 || currentIdx == -1 || targetIdx <= currentIdx) return playlist

        val mutable = playlist.toMutableList()
        mutable.removeAt(targetIdx)
        val insertAt = (currentIdx + 1 + frontCount).coerceAtMost(mutable.size)
        mutable.add(insertAt, targetSong)
        return mutable
    }

    fun moveToEnd(playlist: List<Song>, currentSong: Song, targetSong: Song): List<Song> {
        val currentIdx = playlist.indexOfFirst { it.id == currentSong.id }
        val targetIdx = playlist.indexOfFirst { it.id == targetSong.id }
        if (targetIdx == -1 || currentIdx == -1 || targetIdx == currentIdx) return playlist

        val mutable = playlist.toMutableList()
        mutable.removeAt(targetIdx)
        mutable.add(targetSong)
        return mutable
    }
}
