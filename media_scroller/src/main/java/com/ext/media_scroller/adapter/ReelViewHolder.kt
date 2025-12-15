// com/ext/media_scroller/adapter/ReelViewHolder.kt
package com.ext.media_scroller.adapter

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.ext.media_scroller.R
import com.ext.media_scroller.callbacks.ReelInteractionListener
import com.ext.media_scroller.config.ReelConfig
import com.ext.media_scroller.models.*
import com.ext.media_scroller.player.VideoPlayerManager
import com.ext.media_scroller.utils.FloatingAvatarAnimator
import com.google.android.exoplayer2.ui.StyledPlayerView

class ReelViewHolder(
    itemView: View,
    private val config: ReelConfig,
    private val listener: ReelInteractionListener?,
    private val videoPlayerManager: VideoPlayerManager
) : RecyclerView.ViewHolder(itemView) {

    private val videoPlayerView: StyledPlayerView = itemView.findViewById(R.id.videoPlayerView)
    private val singleImageContainer: FrameLayout = itemView.findViewById(R.id.singleImageContainer)
    private val singleImageView: ImageView = itemView.findViewById(R.id.singleImageView)
    private val carouselViewPager: ViewPager2 = itemView.findViewById(R.id.carouselViewPager)
    private val carouselIndicator: LinearLayout = itemView.findViewById(R.id.carouselIndicator)
    private val loadingIndicator: View = itemView.findViewById(R.id.loadingIndicator)
    private val pausePlayOverlay: ImageView = itemView.findViewById(R.id.pausePlayOverlay)

    private val ownerProfileImage: ImageView = itemView.findViewById(R.id.ownerProfileImage)
    private val ownerUsername: TextView = itemView.findViewById(R.id.ownerUsername)
    private val ownerUserId: TextView = itemView.findViewById(R.id.ownerUserId)
    private val verifiedBadge: ImageView = itemView.findViewById(R.id.verifiedBadge)
    private val followButton: TextView = itemView.findViewById(R.id.followButton)
    private val captionText: TextView = itemView.findViewById(R.id.captionText)

    private val likeButton: ImageView = itemView.findViewById(R.id.likeButton)
    private val likeCount: TextView = itemView.findViewById(R.id.likeCount)
    private val likeButtonContainer: View = itemView.findViewById(R.id.likeButtonContainer)

    private val commentButton: ImageView = itemView.findViewById(R.id.commentButton)
    private val commentCount: TextView = itemView.findViewById(R.id.commentCount)
    private val commentButtonContainer: View = itemView.findViewById(R.id.commentButtonContainer)

    private val repostButton: ImageView = itemView.findViewById(R.id.repostButton)
    private val repostCount: TextView = itemView.findViewById(R.id.repostCount)
    private val repostButtonContainer: View = itemView.findViewById(R.id.repostButtonContainer)

    private val shareButton: ImageView = itemView.findViewById(R.id.shareButton)
    private val shareCount: TextView = itemView.findViewById(R.id.shareCount)
    private val shareButtonContainer: View = itemView.findViewById(R.id.shareButtonContainer)

    private val floatingAvatarsContainer: ViewGroup =
        itemView.findViewById(R.id.floatingAvatarsContainer)
    private val doubleTapHeart: ImageView = itemView.findViewById(R.id.doubleTapHeart)

    private var currentReelItem: ReelItem? = null
    private var currentPosition: Int = -1
    private val floatingAvatarAnimator = FloatingAvatarAnimator(floatingAvatarsContainer, config)

    private val gestureDetector =
        GestureDetector(itemView.context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                currentReelItem?.let { reel ->
                    if (config.enableDoubleTapToLike && !reel.isLiked) {
                        listener?.onDoubleTap(reel, currentPosition)
                        animateDoubleTapHeart()
                    }
                }
                return true
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                if (currentReelItem is VideoReelItem) {
                    videoPlayerManager.togglePlayPause { isPlaying ->
                        updatePausePlayOverlay(isPlaying)
                    }
                }
                return true
            }
        })

    private fun updatePausePlayOverlay(isPlaying: Boolean) {
        if (isPlaying) {
            pausePlayOverlay.animate()
                .alpha(0f)
                .scaleX(0.8f)
                .scaleY(0.8f)
                .setDuration(200)
                .start()
        } else {
            pausePlayOverlay.setImageResource(R.drawable.ic_pause_circle)
            pausePlayOverlay.alpha = 0f
            pausePlayOverlay.scaleX = 0.8f
            pausePlayOverlay.scaleY = 0.8f
            pausePlayOverlay.isVisible = true

            pausePlayOverlay.animate()
                .alpha(1f)
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(200)
                .withEndAction {
                    pausePlayOverlay.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }
                .start()
        }
    }

    init {
        setupTouchListener()
        setupClickListeners()
    }

    private fun setupTouchListener() {
        itemView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    private fun setupClickListeners() {
        likeButtonContainer.setOnClickListener {
            currentReelItem?.let { listener?.onLikeClicked(it, currentPosition) }
        }
        commentButtonContainer.setOnClickListener {
            currentReelItem?.let { listener?.onCommentClicked(it, currentPosition) }
        }
        repostButtonContainer.setOnClickListener {
            currentReelItem?.let { listener?.onRepostClicked(it, currentPosition) }
        }
        shareButtonContainer.setOnClickListener {
            currentReelItem?.let { listener?.onShareClicked(it, currentPosition) }
        }
        ownerProfileImage.setOnClickListener {
            currentReelItem?.let { listener?.onOwnerProfileClicked(it.owner, currentPosition) }
        }
        ownerUsername.setOnClickListener {
            currentReelItem?.let { listener?.onOwnerProfileClicked(it.owner, currentPosition) }
        }
        followButton.setOnClickListener {
            currentReelItem?.let { listener?.onFollowClicked(it.owner, currentPosition) }
        }
    }

    fun bind(reelItem: ReelItem, position: Int) {
        currentReelItem = reelItem
        currentPosition = position

        hideAllViews()
        setupOwnerInfo(reelItem.owner)
        setupActionButtons(reelItem)

        when (reelItem) {
            is VideoReelItem -> bindVideo(reelItem)
            is ImageReelItem -> bindSingleImage(reelItem)
            is CarouselReelItem -> bindCarousel(reelItem)
        }

        if (config.enableFloatingAvatars && reelItem.likedUsers.isNotEmpty()) {
            floatingAvatarsContainer.isVisible = true
            floatingAvatarAnimator.animateLikedUsers(reelItem.likedUsers)
        }
    }

    private fun hideAllViews() {
        floatingAvatarsContainer.isVisible = false
        videoPlayerView.isVisible = false
        singleImageContainer.isVisible = false
        carouselViewPager.isVisible = false
        carouselIndicator.isVisible = false
        loadingIndicator.isVisible = false
        pausePlayOverlay.isVisible = false
    }

    private fun bindVideo(videoReel: VideoReelItem) {
        videoPlayerView.isVisible = true
        captionText.isVisible = !videoReel.caption.isNullOrEmpty()
        captionText.text = videoReel.caption

        if (config.autoPlayVideos) {
            videoPlayerManager.play(
                playerView = videoPlayerView,
                videoUrl = videoReel.videoUrl,
                isMuted = videoReel.isMuted,
                onBuffering = { isBuffering ->
                    if (config.showBuffering) {
                        loadingIndicator.isVisible = isBuffering
                    }
                },
                onReady = {
                    loadingIndicator.isVisible = false
                    updatePausePlayOverlay(true)
                },
                onError = {
                    loadingIndicator.isVisible = false
                }
            )
        } else {
            loadingIndicator.isVisible = false
        }
    }

    private fun bindSingleImage(imageReel: ImageReelItem) {
        singleImageContainer.isVisible = true
        captionText.isVisible = !imageReel.caption.isNullOrEmpty()
        captionText.text = imageReel.caption

        Glide.with(itemView.context)
            .load(imageReel.imageUrl)
            .into(singleImageView)
    }

    private fun bindCarousel(carouselReel: CarouselReelItem) {
        carouselViewPager.isVisible = true
        carouselIndicator.isVisible = true
        captionText.isVisible = !carouselReel.caption.isNullOrEmpty()
        captionText.text = carouselReel.caption

        val adapter = CarouselImageAdapter(carouselReel.imageUrls)
        carouselViewPager.adapter = adapter

        setupCarouselIndicator(carouselReel.imageUrls.size)

        carouselViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateCarouselIndicator(position)
            }
        })
    }

    private fun setupOwnerInfo(owner: ReelOwner) {
        ownerUsername.text = owner.username
        ownerUserId.text = "@${owner.userId}"
        verifiedBadge.isVisible = config.showVerifiedBadge && owner.isVerified
        followButton.isVisible = config.showFollowButton && !owner.isFollowing

        Glide.with(itemView.context)
            .load(owner.profileImageUrl)
            .circleCrop()
            .placeholder(R.drawable.ic_profile_placeholder)
            .into(ownerProfileImage)
    }

    private fun setupActionButtons(reelItem: ReelItem) {
        likeButtonContainer.isVisible = config.showLikeIcon
        val likeIcon = if (reelItem.isLiked) {
            config.likedIconRes ?: R.drawable.ic_heart_filled
        } else {
            config.likeIconRes ?: R.drawable.ic_like
        }
        likeButton.setImageResource(likeIcon)
        likeCount.text = formatCount(reelItem.likes)

        commentButtonContainer.isVisible = config.showCommentIcon
        commentCount.text = formatCount(reelItem.comments)

        repostButtonContainer.isVisible = config.showRepostIcon && !reelItem.isAd
        repostCount.text = formatCount(reelItem.shares)

        shareButtonContainer.isVisible = config.showShareIcon
        shareCount.text = formatCount(reelItem.shares)
    }

    private fun setupCarouselIndicator(count: Int) {
        carouselIndicator.removeAllViews()

        val dotSize =
            (config.indicatorDotSize * itemView.context.resources.displayMetrics.density).toInt()
        val spacing =
            (config.indicatorSpacing * itemView.context.resources.displayMetrics.density).toInt()

        for (i in 0 until count) {
            val dot = View(itemView.context).apply {
                layoutParams = LinearLayout.LayoutParams(dotSize, dotSize).apply {
                    if (i > 0) marginStart = spacing
                }
                setBackgroundResource(R.drawable.indicator_dot)
                alpha = if (i == 0) 1f else 0.5f
            }
            carouselIndicator.addView(dot)
        }
    }

    private fun updateCarouselIndicator(position: Int) {
        for (i in 0 until carouselIndicator.childCount) {
            carouselIndicator.getChildAt(i).alpha = if (i == position) 1f else 0.5f
        }
    }

    private fun animateDoubleTapHeart() {
        doubleTapHeart.alpha = 1f
        doubleTapHeart.scaleX = 0f
        doubleTapHeart.scaleY = 0f

        val scaleX = ObjectAnimator.ofFloat(doubleTapHeart, "scaleX", 0f, 1.2f, 1f)
        val scaleY = ObjectAnimator.ofFloat(doubleTapHeart, "scaleY", 0f, 1.2f, 1f)
        val alpha = ObjectAnimator.ofFloat(doubleTapHeart, "alpha", 1f, 0f).apply {
            startDelay = config.doubleTapAnimationDuration / 2
        }

        AnimatorSet().apply {
            playTogether(scaleX, scaleY, alpha)
            duration = config.doubleTapAnimationDuration
            interpolator = OvershootInterpolator()
            start()
        }
    }

    fun pause() {
        if (currentReelItem is VideoReelItem) {
            videoPlayerManager.pause()
            updatePausePlayOverlay(false)
        }
    }

    fun resume() {
        if (currentReelItem is VideoReelItem && config.autoPlayVideos) {
            videoPlayerManager.resume()
            updatePausePlayOverlay(true)
        }
    }

    private fun formatCount(count: Int): String {
        return when {
            count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
            count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
            else -> count.toString()
        }
    }
}