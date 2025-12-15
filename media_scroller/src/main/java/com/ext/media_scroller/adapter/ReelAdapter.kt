package com.ext.media_scroller.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.ext.media_scroller.R
import com.ext.media_scroller.callbacks.ReelInteractionListener
import com.ext.media_scroller.config.ReelConfig
import com.ext.media_scroller.models.ReelItem
import com.ext.media_scroller.models.VideoReelItem
import com.ext.media_scroller.player.VideoPlayerManager

class ReelAdapter(
    private val config: ReelConfig,
    private val listener: ReelInteractionListener?,
    private val videoPlayerManager: VideoPlayerManager
) : ListAdapter<ReelItem, ReelViewHolder>(ReelDiffCallback()) {

    private var currentViewHolder: ReelViewHolder? = null
    private var currentPosition: Int = -1
    private var currentReelItem: ReelItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.reel_item, parent, false)
        return ReelViewHolder(view, config, listener, videoPlayerManager)
    }

    override fun onBindViewHolder(holder: ReelViewHolder, position: Int) {
        val reelItem = getItem(position)
        holder.bind(reelItem, position)
    }

    /**
     * Called when a ViewHolder becomes fully visible
     */
    fun onViewHolderAttached(holder: ReelViewHolder, position: Int) {
        // Pause the previous reel
        currentViewHolder?.pause()

        // Update current state
        currentViewHolder = holder
        currentPosition = position
        currentReelItem = getItem(position)

        // Auto-resume video if enabled and it's a video reel
        if (config.autoPlayVideos && currentReelItem is VideoReelItem) {
            holder.resume()
        }

        listener?.onReelChanged(currentReelItem!!, position)
    }

    /**
     * Called when a ViewHolder is scrolled off-screen
     */
    fun onViewHolderDetached(holder: ReelViewHolder) {
        if (currentViewHolder == holder) {
            holder.pause()
        }
    }

    fun pauseCurrent() {
        currentViewHolder?.pause()
    }

    fun resumeCurrent() {
        if (config.autoPlayVideos && currentReelItem is VideoReelItem) {
            currentViewHolder?.resume()
        }
    }

    fun getCurrentReelItem(): ReelItem? = currentReelItem

    fun getCurrentPosition(): Int = currentPosition

    private class ReelDiffCallback : DiffUtil.ItemCallback<ReelItem>() {
        override fun areItemsTheSame(oldItem: ReelItem, newItem: ReelItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ReelItem, newItem: ReelItem): Boolean {
            return oldItem == newItem
        }
    }
}