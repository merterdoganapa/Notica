package com.mea.notica

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mea.notica.ui.theme.NoticaTheme

class MainActivity : ComponentActivity() {

    private var isPlaying: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoticaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MusicPlayerApp(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        onSongSelected = { song ->
                            playSongWithService(song)
                        },
                        onPlayPause = {
                            togglePlayPauseWithService()
                        }
                    )
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val stopIntent = Intent(this, MusicPlayerService::class.java)
        stopService(stopIntent)
    }


    private fun playSongWithService(song: Song) {
        isPlaying = true
        val intent = Intent(this, MusicPlayerService::class.java).apply {
            putExtra("SONG_TITLE", song.title)
            putExtra("SONG_ARTIST", song.artist)
            putExtra("SONG_RES_ID", song.rawId)
            putExtra("SONG_THUMBNAIL_ID", song.thumbnailId)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun togglePlayPauseWithService() {
        val action = if (isPlaying) "PAUSE" else "PLAY"
        val intent = Intent(this, MusicPlayerService::class.java).apply {
            putExtra("ACTION", action)
        }
        startService(intent)
        isPlaying = !isPlaying
    }
}

@Composable
fun MusicPlayerApp(
    modifier: Modifier,
    onSongSelected: (Song) -> Unit,
    onPlayPause: () -> Unit
) {
    val songs = MusicPlayerService.songs

    var currentSong by remember { mutableStateOf<Song?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFF121212)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopBar(
                title = "Daily Mix 1",
                isPlaying = isPlaying,
                onPlayClick = {
                    if (currentSong != null && isPlaying) {
                        // If a song is already playing, just toggle play/pause
                        isPlaying = !isPlaying
                        onPlayPause()
                    } else if (currentSong != null) {
                        // If there's a current song but it's paused, resume it
                        isPlaying = true
                        onPlayPause()
                    } else {
                        // If no song is playing, start from the first song
                        currentSong = songs.first()
                        isPlaying = true
                        onSongSelected(songs.first())
                    }
                }
            )
            // Main playlist content
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                items(songs) { song ->
                    SongItem(
                        song = song,
                        isSelected = currentSong == song,
                        onClick = {
                            currentSong = song
                            isPlaying = true
                            onSongSelected(song)
                        }
                    )
                }
            }

            // Bottom player bar
            BottomPlayerBar(
                currentSong = currentSong,
                isPlaying = isPlaying,
                onPlayPauseClick = {
                    isPlaying = !isPlaying
                    onPlayPause()
                }
            )
        }
    }
}

@Composable
fun TopBar(
    title: String,
    onPlayClick: () -> Unit,
    isPlaying: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF232323))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFF1ED760))
                .clickable(onClick = onPlayClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = if (isPlaying) painterResource(R.drawable.ic_pause) else painterResource(R.drawable.ic_play),
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = Color.Black,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun SongItem(
    song: Song,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Song thumbnail
        Image(
            painter = painterResource(id = song.thumbnailId),
            contentDescription = "${song.title} cover art",
            modifier = Modifier
                .size(50.dp)
                .clip(MaterialTheme.shapes.small),
            contentScale = ContentScale.Crop
        )

        // Song info
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = song.title,
                fontSize = 16.sp,
                color = if (isSelected) Color.Green else Color.White,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = song.artist,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        // More options icon
        IconButton(onClick = { /* Show options */ }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options",
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun BottomPlayerBar(
    currentSong: Song?,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit
) {
    if (currentSong == null) return // Şarkı yoksa barı gösterme

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color(0xFF1E1E1E)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = currentSong.thumbnailId),
                contentDescription = "Album cover",
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = currentSong.title,
                    fontSize = 15.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = currentSong.artist,
                    fontSize = 13.sp,
                    color = Color(0xFFB0B0B0),
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Devices",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Artı ikonu
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Add to playlist",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Oynat/duraklat butonu
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable(onClick = onPlayPauseClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = if (isPlaying) painterResource(R.drawable.ic_pause) else painterResource(
                        R.drawable.ic_play
                    ),
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MusicPlayerPreview() {
    MusicPlayerApp(Modifier, {}, {})
}