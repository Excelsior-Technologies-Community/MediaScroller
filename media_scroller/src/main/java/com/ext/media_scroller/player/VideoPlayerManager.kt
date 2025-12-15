package com.ext.media_scroller.player

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.ui.StyledPlayerView

class VideoPlayerManager(private val context: Context) {

    private var currentPlayer: ExoPlayer? = null
    private var currentPlayerView: StyledPlayerView? = null
    private var playerListener: Player.Listener? = null

    // Track user-intended state
    private var isManuallyPaused = false

    fun play(
        playerView: StyledPlayerView,
        videoUrl: String,
        isMuted: Boolean = true,
        onBuffering: ((Boolean) -> Unit)? = null,
        onReady: (() -> Unit)? = null,
        onError: (() -> Unit)? = null
    ) {
        // Switch player instance only when view changes
        if (currentPlayerView != playerView) {
            release()
            isManuallyPaused = false // reset when switching reel
        }

        currentPlayerView = playerView

        if (currentPlayer == null) {
            currentPlayer = ExoPlayer.Builder(context).build().apply {
                repeatMode = Player.REPEAT_MODE_ONE
                volume = if (isMuted) 0f else 1f

                playerListener = object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        when (state) {
                            Player.STATE_BUFFERING -> onBuffering?.invoke(true)
                            Player.STATE_READY -> {
                                onBuffering?.invoke(false)
                                onReady?.invoke()
                            }
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        onBuffering?.invoke(false)
                        onError?.invoke()
                    }
                }
                addListener(playerListener!!)
            }
        }

        playerView.player = currentPlayer

        val mediaSource = ProgressiveMediaSource.Factory(DefaultDataSource.Factory(context))
            .createMediaSource(MediaItem.fromUri(videoUrl))

        currentPlayer?.apply {
            setMediaSource(mediaSource, true) // reset position to 0
            prepare()
            // Respect manual pause state
            playWhenReady = !isManuallyPaused
        }

        // Initial buffering
        onBuffering?.invoke(true)
    }

    fun pause() {
        currentPlayer?.let {
            isManuallyPaused = true
            it.playWhenReady = false
        }
    }

    fun resume() {
        currentPlayer?.let {
            isManuallyPaused = false
            it.playWhenReady = true
        }
    }

    fun togglePlayPause(onStateChanged: ((Boolean) -> Unit)? = null) {
        currentPlayer?.let {
            val willPlay = !it.playWhenReady
            isManuallyPaused = !willPlay
            it.playWhenReady = willPlay
            onStateChanged?.invoke(willPlay) // true = playing, false = paused
        }
    }

    fun mute(muted: Boolean) {
        currentPlayer?.volume = if (muted) 0f else 1f
    }

    fun isPlaying(): Boolean = currentPlayer?.playWhenReady == true

    fun release() {
        playerListener?.let { currentPlayer?.removeListener(it) }
        currentPlayer?.release()
        currentPlayer = null
        currentPlayerView?.player = null
        currentPlayerView = null
        playerListener = null
    }

    fun getCurrentPosition(): Long = currentPlayer?.currentPosition ?: 0L
    fun getDuration(): Long = currentPlayer?.duration ?: 0L
}