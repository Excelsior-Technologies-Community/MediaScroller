// com/ext/media_scroller/config/ReelConfig.kt
package com.ext.media_scroller.config

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes

/**
 * Configuration class for customizing reel appearance and behavior
 */
data class ReelConfig(
    // Action Icons Configuration
    val showLikeIcon: Boolean = true,
    val showCommentIcon: Boolean = true,
    val showRepostIcon: Boolean = true,
    val showShareIcon: Boolean = true,

    @DrawableRes val likeIconRes: Int? = null,
    @DrawableRes val likedIconRes: Int? = null,
    @DrawableRes val commentIconRes: Int? = null,
    @DrawableRes val repostIconRes: Int? = null,
    @DrawableRes val shareIconRes: Int? = null,

    @ColorInt val iconColor: Int = Color.WHITE,
    val iconSize: Int = 32, // dp

    // Indicator Configuration
    val indicatorDotSize: Int = 8, // dp
    val indicatorActiveDotSize: Int = 10, // dp
    @ColorInt val indicatorDotColor: Int = Color.parseColor("#80FFFFFF"),
    @ColorInt val indicatorActiveDotColor: Int = Color.WHITE,
    val indicatorSpacing: Int = 8, // dp

    // Floating Avatars Configuration
    val enableFloatingAvatars: Boolean = true,
    val maxFloatingAvatars: Int = 3,
    val avatarSize: Int = 40, // dp
    val avatarAnimationDuration: Long = 2000L, // ms

    // Owner Info Configuration
    val showOwnerInfo: Boolean = true,
    val showFollowButton: Boolean = true,
    val showVerifiedBadge: Boolean = true,

    // Video Configuration
    val autoPlayVideos: Boolean = true,
    val loopVideos: Boolean = true,
    val showBuffering: Boolean = true,

    // Double Tap Configuration
    val enableDoubleTapToLike: Boolean = true,
    val doubleTapAnimationDuration: Long = 800L
)