package com.mea.notica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mea.notica.ui.theme.NoticaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoticaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MusicPlayerApp(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

data class Song(
    val title: String,
    val artist: String,
    val thumbnailId: Int,
    val isPlaying: Boolean = false,
    val isSelected: Boolean = false
)

@Composable
fun MusicPlayerApp(modifier: Modifier) {
    val songs = listOf(
        Song("Ölsem", "Sena Şener", R.drawable.thumbnail_placeholder),
        Song("Gelin Olmuş", "Hayko Cepkin", R.drawable.thumbnail_placeholder),
        Song("Uyku", "Son Feci Bisiklet", R.drawable.thumbnail_placeholder),
        Song("Dut", "Doğan Duru", R.drawable.thumbnail_placeholder),
        Song(
            "Love Is What You Make It",
            "Comatr",
            R.drawable.thumbnail_placeholder,
            isSelected = true
        ),
        Song("Mutsuz Punk", "Yasemin Mori", R.drawable.thumbnail_placeholder),
        Song("Biliyorsun", "Redd", R.drawable.thumbnail_placeholder),
        Song("En Güzel Yerinde Evin", "Büyük Ev Ablukada", R.drawable.thumbnail_placeholder),
        Song(
            "How They Fall",
            "Sophia Fatouaki",
            R.drawable.thumbnail_placeholder,
            isSelected = true
        ),
        Song("I'm the series Arcane League of", "Freya Ridings", R.drawable.thumbnail_placeholder)
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFF121212)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Main playlist content
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                items(songs) { song ->
                    SongItem(song = song)
                }
            }

            // Bottom player bar
            BottomPlayerBar()
        }
    }
}

@Composable
fun SongItem(song: Song) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Song thumbnail
//        Image(
//            painter = painterResource(id = song.thumbnailId),
//            contentDescription = "${song.title} cover art",
//            modifier = Modifier
//                .size(50.dp)
//                .clip(MaterialTheme.shapes.small),
//            contentScale = ContentScale.Crop
//        )

        // Song info
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = song.title,
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = song.artist,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        // Selection check mark if song is selected
        if (song.isSelected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Selected",
                tint = Color.Green,
                modifier = Modifier.padding(end = 8.dp)
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
fun BottomPlayerBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color(0xFF1E1E1E))
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Player controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Current song icon
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "Expand player",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Play/pause button in circular background
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Add button
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "Add to playlist",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MusicPlayerPreview() {
    MusicPlayerApp(Modifier)
}