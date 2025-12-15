package com.ext.media_scroller.callbacks

import com.ext.media_scroller.models.ReelItem
import com.ext.media_scroller.models.ReelOwner

/**
 * Callback interface for reel interactions
 */
interface ReelInteractionListener {
    fun onLikeClicked(reelItem: ReelItem, position: Int)
    fun onCommentClicked(reelItem: ReelItem, position: Int)
    fun onRepostClicked(reelItem: ReelItem, position: Int)
    fun onShareClicked(reelItem: ReelItem, position: Int)
    fun onOwnerProfileClicked(owner: ReelOwner, position: Int)
    fun onFollowClicked(owner: ReelOwner, position: Int)
    fun onDoubleTap(reelItem: ReelItem, position: Int)
    fun onReelChanged(reelItem: ReelItem, position: Int)
}

/**
 * Empty implementation for optional callbacks
 */
open class ReelInteractionListenerAdapter : ReelInteractionListener {
    override fun onLikeClicked(reelItem: ReelItem, position: Int) {}
    override fun onCommentClicked(reelItem: ReelItem, position: Int) {}
    override fun onRepostClicked(reelItem: ReelItem, position: Int) {}
    override fun onShareClicked(reelItem: ReelItem, position: Int) {}
    override fun onOwnerProfileClicked(owner: ReelOwner, position: Int) {}
    override fun onFollowClicked(owner: ReelOwner, position: Int) {}
    override fun onDoubleTap(reelItem: ReelItem, position: Int) {}
    override fun onReelChanged(reelItem: ReelItem, position: Int) {}
}