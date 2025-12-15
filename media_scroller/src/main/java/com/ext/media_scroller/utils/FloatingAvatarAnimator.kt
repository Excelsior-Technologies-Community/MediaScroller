// com/ext/media_scroller/utils/FloatingAvatarAnimator.kt
package com.ext.media_scroller.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.Gravity
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ext.media_scroller.R
import com.ext.media_scroller.config.ReelConfig
import com.ext.media_scroller.models.LikedUser

class FloatingAvatarAnimator(
    private val container: ViewGroup,
    private val config: ReelConfig
) {

    fun animateLikedUsers(likedUsers: List<LikedUser>) {
        container.removeAllViews()

        val usersToShow = likedUsers.take(config.maxFloatingAvatars)

        if (usersToShow.isEmpty()) {
            container.visibility = ViewGroup.GONE
            return
        }

        val avatarSize = (config.avatarSize * container.context.resources.displayMetrics.density).toInt()
        val spacing = (avatarSize * 0.3f).toInt()

        // Main horizontal container
        val horizontalContainer = android.widget.LinearLayout(container.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            orientation = android.widget.LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL // Ensures vertical centering within container height
        }

        usersToShow.forEachIndexed { index, user ->
            val avatarWrapper = FrameLayout(container.context).apply {
                layoutParams = android.widget.LinearLayout.LayoutParams(avatarSize, avatarSize).apply {
                    if (index > 0) marginStart = spacing
                }
                // Prevent clipping by adding padding to wrapper (safe area)
                setPadding(4, 4, 4, 4)
                clipChildren = false
                clipToPadding = false
            }

            val avatar = ImageView(container.context).apply {
                layoutParams = FrameLayout.LayoutParams(avatarSize, avatarSize)
                scaleType = ImageView.ScaleType.CENTER_CROP

                Glide.with(context)
                    .load(user.profileImageUrl)
                    .circleCrop()
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(this)

                elevation = 6f
            }

            avatarWrapper.addView(avatar)

            // Add heart icon on EVERY avatar in BOTTOM-RIGHT corner
            val heartIcon = ImageView(container.context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    avatarSize / 2,
                    avatarSize / 2
                ).apply {
                    gravity = Gravity.BOTTOM or Gravity.END
                    marginStart=10
                }

                setImageResource(R.drawable.ic_heart_filled)
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                elevation = 20f
            }
            avatarWrapper.addView(heartIcon)

            horizontalContainer.addView(avatarWrapper)

            // Gentle floating + subtle wobble animation
            val wobbleX = ObjectAnimator.ofFloat(avatarWrapper, "translationX", -5f, 5f).apply {
                duration = 2400
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
                interpolator = AccelerateDecelerateInterpolator()
                startDelay = index * 180L
            }

            val floatY = ObjectAnimator.ofFloat(avatarWrapper, "translationY", -10f, 6f).apply {
                duration = 3200
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
                startDelay = index * 180L
            }

            AnimatorSet().apply {
                playTogether(wobbleX, floatY)
                start()
            }
        }

        container.addView(horizontalContainer)
        container.visibility = ViewGroup.VISIBLE
    }
}