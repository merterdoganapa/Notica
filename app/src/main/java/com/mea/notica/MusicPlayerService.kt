package com.mea.notica

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat

class MusicPlayerService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var currentSong: Song? = null

    companion object {
        private var currentSongIndex = 0
        val songs = listOf(
            Song("Without Me", "Eminem", R.drawable.withoutme, R.raw.muse),
            Song("My Name Is", "Eminem", R.drawable.mynameis, R.raw.twenty)
        )
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.getStringExtra("ACTION")
        val songTitle = intent?.getStringExtra("SONG_TITLE")
        val songArtist = intent?.getStringExtra("SONG_ARTIST")
        val songResId = intent?.getIntExtra("SONG_RES_ID", -1) ?: -1
        val songThumbnailId =
            intent?.getIntExtra("SONG_THUMBNAIL_ID", R.drawable.thumbnail_placeholder)
                ?: R.drawable.thumbnail_placeholder

        if (songTitle != null && songArtist != null && songResId != -1) {
            currentSong = Song(songTitle, songArtist, songThumbnailId, songResId)
            currentSongIndex =
                songs.indexOfFirst { it.title == songTitle && it.artist == songArtist }
            playSong()
        }

        when (action) {
            "PLAY" -> resumeSong()
            "PAUSE" -> pauseSong()
            "PREV" -> handlePrev()
            "NEXT" -> handleNext()
        }

        showNotification()
        return START_STICKY
    }

    private fun playSong() {
        currentSong?.let { song ->
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(this, song.rawId)
            mediaPlayer?.start()
            isPlaying = true
        }
    }

    private fun resumeSong() {
        mediaPlayer?.start()
        isPlaying = true
    }

    private fun pauseSong() {
        mediaPlayer?.pause()
        isPlaying = false
    }

    private fun showNotification() {
        val channelId = "music_player_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, "Music Player", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notificationLayout = RemoteViews(packageName, R.layout.notification_player)
        notificationLayout.setTextViewText(R.id.tv_title, currentSong?.title ?: "No Title")
        notificationLayout.setTextViewText(R.id.tv_artist, currentSong?.artist ?: "")
        notificationLayout.setImageViewResource(
            R.id.iv_album,
            currentSong?.thumbnailId ?: R.drawable.thumbnail_placeholder
        )
        notificationLayout.setImageViewResource(
            R.id.btn_play_pause,
            if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        )

        // PendingIntents
        val prevIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, MusicPlayerService::class.java).apply { putExtra("ACTION", "PREV") },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val playPauseIntent = PendingIntent.getService(
            this,
            1,
            Intent(this, MusicPlayerService::class.java).apply {
                putExtra(
                    "ACTION",
                    if (isPlaying) "PAUSE" else "PLAY"
                )
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val nextIntent = PendingIntent.getService(
            this,
            2,
            Intent(this, MusicPlayerService::class.java).apply { putExtra("ACTION", "NEXT") },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        notificationLayout.setOnClickPendingIntent(R.id.btn_prev, prevIntent)
        notificationLayout.setOnClickPendingIntent(R.id.btn_play_pause, playPauseIntent)
        notificationLayout.setOnClickPendingIntent(R.id.btn_next, nextIntent)

        // Update text colors to white
        notificationLayout.setTextColor(R.id.tv_title, Color.WHITE)
        notificationLayout.setTextColor(R.id.tv_artist, Color.WHITE)

        notificationLayout.setImageViewResource(
            R.id.btn_play_pause,
            if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        )
        notificationLayout.setImageViewResource(R.id.btn_next, R.drawable.ic_next)
        notificationLayout.setImageViewResource(R.id.btn_prev, R.drawable.ic_prev)

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCustomContentView(notificationLayout)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setOngoing(isPlaying)
            .setOnlyAlertOnce(true)
            .build()

        startForeground(1, notification)
    }

    private fun handlePrev() {
        if (songs.isEmpty()) return

        currentSongIndex = if (currentSongIndex == 0) {
            songs.size - 1
        } else {
            currentSongIndex - 1
        }

        currentSong = songs[currentSongIndex]
        playSong()
        showNotification()
    }

    private fun handleNext() {
        if (songs.isEmpty()) return

        currentSongIndex = if (currentSongIndex == songs.size - 1) {
            0
        } else {
            currentSongIndex + 1
        }

        currentSong = songs[currentSongIndex]
        playSong()
        showNotification()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
} 