package com.ext.media_scroller

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.ext.media_scroller.adapter.ReelAdapter
import com.ext.media_scroller.adapter.ReelViewHolder
import com.ext.media_scroller.callbacks.ReelInteractionListener
import com.ext.media_scroller.config.ReelConfig
import com.ext.media_scroller.models.ReelItem
import com.ext.media_scroller.player.VideoPlayerManager

/**
 * Main view component for displaying Instagram Reels-style content
 */
class ReelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val recyclerView: RecyclerView
    private val videoPlayerManager: VideoPlayerManager
    private lateinit var reelAdapter: ReelAdapter

    private var config: ReelConfig = ReelConfig()
    private var listener: ReelInteractionListener? = null
    private var currentPosition = 0

    init {
        recyclerView = RecyclerView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            // Enable paging behavior
            PagerSnapHelper().attachToRecyclerView(this)

            // Optimize performance
            setHasFixedSize(true)
            itemAnimator = null
        }

        addView(recyclerView)

        videoPlayerManager = VideoPlayerManager(context)

        setupScrollListener()
    }

    private fun setupScrollListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val position = layoutManager.findFirstCompletelyVisibleItemPosition()

                    if (position != RecyclerView.NO_POSITION && position != currentPosition) {
                        currentPosition = position
                        onReelChanged(position)
                    }
                }
            }
        })

        // Attach/detach listener for proper video management
        recyclerView.addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: android.view.View) {
                val holder = recyclerView.getChildViewHolder(view)
                if (holder is ReelViewHolder) {
                    val position = holder.adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        reelAdapter.onViewHolderAttached(holder, position)
                    }
                }
            }

            override fun onChildViewDetachedFromWindow(view: android.view.View) {
                val holder = recyclerView.getChildViewHolder(view)
                if (holder is ReelViewHolder) {
                    reelAdapter.onViewHolderDetached(holder)
                }
            }
        })
    }

    /**
     * Set the configuration for the reel view
     */
    fun setConfig(config: ReelConfig) {
        this.config = config
        if (::reelAdapter.isInitialized) {
            reelAdapter = ReelAdapter(config, listener, videoPlayerManager)
            recyclerView.adapter = reelAdapter
        }
    }

    /**
     * Set the interaction listener
     */
    fun setInteractionListener(listener: ReelInteractionListener) {
        this.listener = listener
        if (::reelAdapter.isInitialized) {
            reelAdapter = ReelAdapter(config, listener, videoPlayerManager)
            recyclerView.adapter = reelAdapter
        }
    }

    /**
     * Submit a list of reel items
     */
    fun submitList(reels: List<ReelItem>) {
        if (!::reelAdapter.isInitialized) {
            reelAdapter = ReelAdapter(config, listener, videoPlayerManager)
            recyclerView.adapter = reelAdapter
        }
        reelAdapter.submitList(reels)
    }

    /**
     * Scroll to a specific reel position
     */
    fun scrollToPosition(position: Int, smooth: Boolean = true) {
        if (smooth) {
            recyclerView.smoothScrollToPosition(position)
        } else {
            recyclerView.scrollToPosition(position)
        }
    }

    /**
     * Get the current reel position
     */
    fun getCurrentPosition(): Int = currentPosition

    /**
     * Pause the current video
     */
    fun pause() {
        if (::reelAdapter.isInitialized) {
            reelAdapter.pauseCurrent()
        }
    }

    /**
     * Resume the current video
     */
    fun resume() {
        if (::reelAdapter.isInitialized) {
            reelAdapter.resumeCurrent()
        }
    }

    /**
     * Release resources
     */
    fun release() {
        videoPlayerManager.release()
    }

    private fun onReelChanged(position: Int) {
        // Notify listener if set
        if (::reelAdapter.isInitialized && position < reelAdapter.itemCount) {
            val item = reelAdapter.currentList.getOrNull(position)
            item?.let {
                listener?.onReelChanged(it, position)
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        release()
    }
}