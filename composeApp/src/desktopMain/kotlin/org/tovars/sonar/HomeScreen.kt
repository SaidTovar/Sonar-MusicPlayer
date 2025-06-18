package org.tovars.sonar


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory
import be.tarsos.dsp.io.jvm.JVMAudioInputStream
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowsMaximize
import compose.icons.tablericons.ArrowsMinimize
import compose.icons.tablericons.ArrowsSort
import compose.icons.tablericons.PlayerPlay
import compose.icons.tablericons.PlayerSkipBack
import compose.icons.tablericons.PlayerSkipForward
import compose.icons.tablericons.Playlist
import compose.icons.tablericons.Volume
import compose.icons.tablericons.X
import io.github.kdroidfilter.composemediaplayer.VideoPlayerSurface
import io.github.kdroidfilter.composemediaplayer.rememberVideoPlayerState
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource
import sonar_musicplayer.composeapp.generated.resources.Res
import sonar_musicplayer.composeapp.generated.resources.compose_multiplatform
import sonar_musicplayer.composeapp.generated.resources.logo
import java.io.File
import java.net.URL
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import kotlin.math.absoluteValue
import kotlin.math.sqrt

@Composable
fun HomeScreen(
    exitApplication: () -> Unit = {},
    maximizeApplication: () -> Unit = {},
    minimizeApplication: () -> Unit = {}
){

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        backgroundColor = Color.Transparent
    ){

        ContentHomeScreen(
            exitApplication = exitApplication,
            maximizeApplication = maximizeApplication,
            minimizeApplication = minimizeApplication
        )

    }

}

@Composable
fun ContentHomeScreen(
    exitApplication: () -> Unit = {},
    maximizeApplication: () -> Unit = {},
    minimizeApplication: () -> Unit = {}
){

    Column (horizontalAlignment = Alignment.CenterHorizontally){

        Box {

            Column (
                modifier = Modifier.clip(RoundedCornerShape(20.dp))
                    .background(Color(0xfe6565251))
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .padding(16.dp),
            ){

                ButtonsWindows(
                    exitApplication = exitApplication,
                    maximizeApplication = maximizeApplication,
                    minimizeApplication = minimizeApplication

                )

                Spacer( modifier = Modifier.height(50.dp))

                ItemsMenu()

            }

            Column (
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black)
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.85f)
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
            ){

                MediaPlayerScreen()



            }

        }


        Spacer( modifier = Modifier.size(16.dp))

        Row (
            modifier = Modifier.clip(RoundedCornerShape(20.dp))
                .background(Color(0xfe6565251))
                .fillMaxWidth(0.8f)
                .height(100.dp)
                .padding(16.dp),
        ){

            MusicPlayer()

        }

    }


}

@Composable
fun ButtonsWindows(
    exitApplication: () -> Unit = {},
    maximizeApplication: () -> Unit = {},
    minimizeApplication: () -> Unit = {}
){
    Row (
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ){

        IconButton(
            onClick = exitApplication,
            modifier = Modifier.clip(CircleShape)
                .size(16.dp)
                .background(Color(0xffe77467))
                .padding(2.dp)
        ){
            Icon(
                TablerIcons.X,
                "",
                tint = Color.Black

            )
        }

        IconButton(
            onClick = minimizeApplication,
            modifier = Modifier.clip(CircleShape)
                .size(16.dp)
                .background(Color(0xfff2c040))
                .padding(2.dp)
        ){
            Icon(
                TablerIcons.ArrowsMinimize,
                "",
                tint = Color.Black

            )
        }

        IconButton(
            onClick = maximizeApplication,
            modifier = Modifier.clip(CircleShape)
                .size(16.dp)
                .background(Color(0xff27d140))
                .padding(2.dp)
        ){
            Icon(
                TablerIcons.ArrowsMaximize,
                "",
                tint = Color.Black

            )
        }

    }
}

enum class ItemsMenu(name: String) {
    Browse("Browse"),
    Songs("Songs"),
    Albums("Albums"),
    Artists("Artists"),
    Radio("Radio"),
    RecentlyPlayed("Recently Played"),
    FavoriteSongs("Favorite Songs"),
    LocalFile("Local File")
}

@Composable
fun ItemsMenu(){

    var items by remember { mutableStateOf(0) }

    Column {

        Text(
            modifier = Modifier.padding(vertical = 16.dp),
            text = "Library",
            color = Color.White,
            fontSize = 26.sp
        )


        ItemsMenu.entries.forEachIndexed { index, itemsMenu ->

            if (itemsMenu == ItemsMenu.RecentlyPlayed){

                Text(
                    modifier = Modifier.padding(vertical = 16.dp),
                    text = "My Music",
                    color = Color.White,
                    fontSize = 26.sp
                )

            }

            Box (
                modifier = Modifier.clip(RoundedCornerShape(40.dp))
                    .clickable {
                        items = index
                    }
                    .background(if (items == index) Color(0xff3d3b39) else Color.Transparent)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
            ){
                Text(
                    itemsMenu.name,
                    fontSize = 16.sp,
                    color = if (items == index) Color(0xffdcdeb6) else Color.LightGray
                )
            }

            Spacer( modifier = Modifier.height(8.dp))

        }


    }

}

@Composable
fun MusicPlayer(){

    Row (
        verticalAlignment = Alignment.CenterVertically,
    ){

        Image(
            modifier = Modifier.clip(RoundedCornerShape(10.dp))
                .background(Color.Gray),
            painter = painterResource(Res.drawable.compose_multiplatform),
            contentDescription = ""
        )

        Column (
            modifier = Modifier.padding(horizontal = 16.dp)
                .weight(1f)
        ){

            Text(
                "Artist Name",
                color = Color.LightGray,
                fontSize = 12.sp
            )
            Text(
                "Music Name",
                color = Color.White,
                fontSize = 16.sp
            )

        }

        Row (
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ){

            IconButton(
                onClick = {}
            ) {
                Icon(
                    modifier = Modifier.clip(CircleShape)
                        .size(40.dp)
                        .padding(10.dp),
                    imageVector = TablerIcons.PlayerSkipBack,
                    contentDescription = "",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = {}
            ) {
                Icon(
                    modifier = Modifier.clip(CircleShape)
                        .size(40.dp)
                        .background(Color(0xfff6fb8d))
                        .padding(10.dp),
                    imageVector = TablerIcons.PlayerPlay,
                    contentDescription = "",
                    tint = Color.Black
                )
            }

            IconButton(
                onClick = {}
            ) {
                Icon(
                    modifier = Modifier.clip(CircleShape)
                        .size(40.dp)
                        .padding(10.dp),
                    imageVector = TablerIcons.PlayerSkipForward,
                    contentDescription = "",
                    tint = Color.White
                )
            }

        }

        Box (
            modifier = Modifier.padding(horizontal = 16.dp)
                .fillMaxHeight()
                .width(200.dp)
                .background(Color.DarkGray)

        )

        Text(
            "0:00",
            color = Color.White,
            fontSize = 12.sp
        )

        Spacer( modifier = Modifier.weight(0.1f))

        Row {

            IconButton(
                onClick = {}
            ) {
                Icon(
                    TablerIcons.ArrowsSort,
                    "",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = {}
            ) {
                Icon(
                    imageVector = TablerIcons.Playlist,
                    contentDescription = "",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = {}
            ) {
                Icon(
                    TablerIcons.Volume,
                    "",
                    tint = Color.White
                )
            }

        }


    }

}


@Composable
fun MediaPlayerScreen() {
    val playerState = rememberVideoPlayerState()

    var amplitudes = remember { mutableStateListOf<Float>() }

    LaunchedEffect(Unit) {
        playerState.openUri("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")

        withContext(Dispatchers.IO) {
            val sourceFile = File("C:\\Users\\SaidTovar\\Downloads\\dg.wav")
            //val raw = extractWaveformAmplitudes(sourceFile) // ya reducido

            val raw = extractWaveformAmplitudes(sourceFile)
            val downsampled = downsampleAmplitudes(raw, 500)
            val normalized = downsampled.map { it / (downsampled.maxOrNull() ?: 1f) }

            withContext(Dispatchers.Main) {
                amplitudes.clear()
                amplitudes.addAll(normalized)
            }
        }


    }




    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        // Video
        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            VideoPlayerSurface(
                playerState = playerState,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(Modifier.height(8.dp))

        // Waveform
        if (amplitudes.isNotEmpty()) {
            Text("Waveform",  color = Color.White, style = MaterialTheme.typography.labelLarge)

            println("amplitudes size: ${amplitudes.size}")
            WaveformView(
                amplitudes = amplitudes,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color.Black)
            )
        }

        Spacer(Modifier.height(8.dp))

        // Controles
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { playerState.play() }) { Text("Play") }
            Button(onClick = { playerState.pause() }) { Text("Pause") }
        }

        Text("Volume: ${(playerState.volume * 100).toInt()}%")
        Slider(
            value = playerState.volume,
            onValueChange = { playerState.volume = it },
            valueRange = 0f..1f
        )
    }
}

fun extractWaveformAmplitudes(audioFile: File): List<Float> {
    val amplitudes = mutableListOf<Float>()

    val dispatcher = AudioDispatcherFactory.fromFile(audioFile, 2048, 1024)
    dispatcher.addAudioProcessor(object : AudioProcessor {
        override fun process(audioEvent: AudioEvent): Boolean {
            amplitudes += audioEvent.floatBuffer.map { it.absoluteValue }
            return true
        }
        override fun processingFinished() {}
    })

    dispatcher.run()
    return amplitudes
}

@Composable
fun WaveformView(amplitudes: List<Float>, modifier: Modifier = Modifier) {
    val maxAmplitude = amplitudes.maxOrNull() ?: 1f
print(amplitudes.take(20))
    Canvas(modifier = modifier) {
        val barWidth = size.width / amplitudes.size
        amplitudes.forEachIndexed { i, amp ->

            val height = (amp / maxAmplitude) * size.height
            drawLine(
                color = Color.White,
                start = Offset(i * barWidth, size.height / 2 - height / 2),
                end = Offset(i * barWidth, size.height / 2 + height / 2),
                strokeWidth = barWidth
            )
        }
    }
}

fun downsampleAmplitudes(amplitudes: List<Float>, targetSize: Int): List<Float> {
    if (amplitudes.size <= targetSize) return amplitudes

    val blockSize = amplitudes.size / targetSize
    return (0 until targetSize).map { i ->
        val start = i * blockSize
        val end = minOf(start + blockSize, amplitudes.size)
        amplitudes.subList(start, end).average().toFloat()
    }
}
