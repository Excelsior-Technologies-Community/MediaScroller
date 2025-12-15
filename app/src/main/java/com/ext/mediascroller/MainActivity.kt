package com.ext.mediascroller

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ext.media_scroller.ReelView
import com.ext.media_scroller.callbacks.ReelInteractionListenerAdapter
import com.ext.media_scroller.config.ReelConfig
import com.ext.media_scroller.models.*

class MainActivity : AppCompatActivity() {

    private lateinit var reelView: ReelView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Handle system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        reelView = findViewById(R.id.reelView)

        setupReelView()
        loadSampleReels()
    }

    private fun setupReelView() {
        // Configure the reel view
        val config = ReelConfig(
            showLikeIcon = true,
            showCommentIcon = true,
            showRepostIcon = true,
            showShareIcon = true,
            iconColor = Color.WHITE,
            iconSize = 32,
            indicatorDotSize = 8,
            indicatorActiveDotSize = 10,
            indicatorDotColor = Color.parseColor("#80FFFFFF"),
            indicatorActiveDotColor = Color.WHITE,
            enableFloatingAvatars = true,
            maxFloatingAvatars = 3,
            avatarSize = 40,
            showOwnerInfo = true,
            showFollowButton = true,
            showVerifiedBadge = true,
            autoPlayVideos = true,
            loopVideos = true,
            enableDoubleTapToLike = true
        )

        reelView.setConfig(config)

        // Set interaction listener
        reelView.setInteractionListener(object : ReelInteractionListenerAdapter() {
            override fun onLikeClicked(reelItem: ReelItem, position: Int) {
                // Update the reel item with new like status
                // In production, you would update your data source and refresh
            }

            override fun onCommentClicked(reelItem: ReelItem, position: Int) {
            }

            override fun onRepostClicked(reelItem: ReelItem, position: Int) {
            }

            override fun onShareClicked(reelItem: ReelItem, position: Int) {
            }

            override fun onOwnerProfileClicked(owner: ReelOwner, position: Int) {
            }

            override fun onFollowClicked(owner: ReelOwner, position: Int) {
            }

            override fun onDoubleTap(reelItem: ReelItem, position: Int) {
            }

            override fun onReelChanged(reelItem: ReelItem, position: Int) {
                // Track analytics, preload next items, etc.
            }
        })
    }

    private fun loadSampleReels() {
        val sampleReels = listOf(
            // Video Reel
            VideoReelItem(
                id = "1",
                owner = ReelOwner(
                    userId = "user1",
                    username = "travel_lover",
                    profileImageUrl = "https://picsum.photos/200/200?random=1",
                    isVerified = true,
                    isFollowing = false
                ),
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                thumbnailUrl = "https://picsum.photos/400/600?random=1",
                caption = "Amazing sunset at the beach! 🌅 #travel #beach",
                likes = 1250,
                comments = 89,
                shares = 34,
                isLiked = false,
                isAd = false,
                likedUsers = listOf(
                    LikedUser("u1", "john_doe", "https://picsum.photos/100/100?random=10"),
                    LikedUser("u2", "jane_smith", "https://picsum.photos/100/100?random=11"),
                    LikedUser("u3", "alex_photos", "https://picsum.photos/100/100?random=12")
                )
            ),

            // Single Image Reel
            ImageReelItem(
                id = "2",
                owner = ReelOwner(
                    userId = "user2",
                    username = "food_blogger",
                    profileImageUrl = "https://picsum.photos/200/200?random=2",
                    isVerified = false,
                    isFollowing = false
                ),
                imageUrl = "https://picsum.photos/1080/1920?random=2",
                caption = "Delicious homemade pasta 🍝 #food #cooking",
                likes = 567,
                comments = 45,
                shares = 12,
                isLiked = true,
                isAd = false,
                likedUsers = listOf(
                    LikedUser("u4", "mike_chef", "https://picsum.photos/100/100?random=13")
                )
            ),

            // Carousel Reel
            CarouselReelItem(
                id = "3",
                owner = ReelOwner(
                    userId = "user3",
                    username = "fashion_style",
                    profileImageUrl = "https://picsum.photos/200/200?random=3",
                    isVerified = true,
                    isFollowing = true
                ),
                imageUrls = listOf(
                    "https://picsum.photos/1080/1920?random=10",
                    "https://picsum.photos/1080/1920?random=11",
                    "https://picsum.photos/1080/1920?random=12",
                    "https://picsum.photos/1080/1920?random=13"
                ),
                caption = "New collection drop! Swipe to see all 👉 #fashion #style",
                likes = 3450,
                comments = 234,
                shares = 89,
                isLiked = false,
                isAd = false,
                likedUsers = listOf(
                    LikedUser("u5", "sarah_model", "https://picsum.photos/100/100?random=14"),
                    LikedUser("u6", "tom_designer", "https://picsum.photos/100/100?random=15")
                )
            ),

            // Ad Video Reel (no repost button)
            VideoReelItem(
                id = "4",
                owner = ReelOwner(
                    userId = "brand1",
                    username = "awesome_brand",
                    profileImageUrl = "https://picsum.photos/200/200?random=4",
                    isVerified = true,
                    isFollowing = false
                ),
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                thumbnailUrl = "https://picsum.photos/400/600?random=4",
                caption = "Check out our new product line! Limited time offer 🎉 #sponsored",
                likes = 890,
                comments = 67,
                shares = 23,
                isLiked = false,
                isAd = true, // This will hide the repost button
                likedUsers = emptyList()
            ),

            // Another Image Reel
            ImageReelItem(
                id = "5",
                owner = ReelOwner(
                    userId = "user4",
                    username = "nature_shots",
                    profileImageUrl = "https://picsum.photos/200/200?random=5",
                    isVerified = false,
                    isFollowing = false
                ),
                imageUrl = "https://picsum.photos/1080/1920?random=20",
                caption = "Morning in the mountains 🏔️ #nature #photography",
                likes = 2100,
                comments = 156,
                shares = 78,
                isLiked = false,
                isAd = false,
                likedUsers = listOf(
                    LikedUser("u7", "emma_outdoor", "https://picsum.photos/100/100?random=16"),
                    LikedUser("u8", "lucas_hiker", "https://picsum.photos/100/100?random=17"),
                    LikedUser("u9", "olivia_explore", "https://picsum.photos/100/100?random=18")
                )
            )
        )

        reelView.submitList(sampleReels)
    }

    override fun onPause() {
        super.onPause()
        reelView.pause()
    }

    override fun onResume() {
        super.onResume()
        reelView.resume()
    }

    override fun onStop() {
        super.onStop()
        reelView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        reelView.release()
    }
}