// com/ext/media_scroller/models/ReelModels.kt
package com.ext.media_scroller.models

/**
 * Base sealed class for different reel content types
 */
sealed class ReelItem {
    abstract val id: String
    abstract val owner: ReelOwner
    abstract val likes: Int
    abstract val comments: Int
    abstract val shares: Int
    abstract val isLiked: Boolean
    abstract val isAd: Boolean
    abstract val likedUsers: List<LikedUser>
}

/**
 * Video reel item
 */
data class VideoReelItem(
    override val id: String,
    override val owner: ReelOwner,
    override val likes: Int = 0,
    override val comments: Int = 0,
    override val shares: Int = 0,
    override val isLiked: Boolean = false,
    override val isAd: Boolean = false,
    override val likedUsers: List<LikedUser> = emptyList(),
    val videoUrl: String,
    val thumbnailUrl: String? = null,
    val caption: String? = null,
    val duration: Long = 0L,
    val isMuted: Boolean = false
) : ReelItem()

/**
 * Single image reel item
 */
data class ImageReelItem(
    override val id: String,
    override val owner: ReelOwner,
    override val likes: Int = 0,
    override val comments: Int = 0,
    override val shares: Int = 0,
    override val isLiked: Boolean = false,
    override val isAd: Boolean = false,
    override val likedUsers: List<LikedUser> = emptyList(),
    val imageUrl: String,
    val caption: String? = null
) : ReelItem()

/**
 * Multiple images carousel reel item
 */
data class CarouselReelItem(
    override val id: String,
    override val owner: ReelOwner,
    override val likes: Int = 0,
    override val comments: Int = 0,
    override val shares: Int = 0,
    override val isLiked: Boolean = false,
    override val isAd: Boolean = false,
    override val likedUsers: List<LikedUser> = emptyList(),
    val imageUrls: List<String>,
    val caption: String? = null
) : ReelItem()

/**
 * Reel owner information
 */
data class ReelOwner(
    val userId: String,
    val username: String,
    val profileImageUrl: String?,
    val isVerified: Boolean = false,
    val isFollowing: Boolean = false
)

/**
 * User who liked the reel
 */
data class LikedUser(
    val userId: String,
    val username: String,
    val profileImageUrl: String?
)