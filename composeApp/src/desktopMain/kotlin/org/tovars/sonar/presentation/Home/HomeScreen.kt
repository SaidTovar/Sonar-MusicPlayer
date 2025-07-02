package org.tovars.sonar.presentation.Home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import sonar_musicplayer.composeapp.generated.resources.Res
import sonar_musicplayer.composeapp.generated.resources.compose_multiplatform
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioSystem
import kotlin.math.absoluteValue

@Composable
fun HomeScreen(
    exitApplication: () -> Unit = {},
    maximizeApplication: () -> Unit = {},
    minimizeApplication: () -> Unit = {},
    viewModel: VideoPlayerViewModel = koinViewModel()
){

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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

fun downloadFileFromUrl(url: String): File {
    val connection = URL(url).openConnection() as HttpURLConnection
    val totalBytes = connection.contentLengthLong

    val tempFile = File.createTempFile("temp_video", ".mp4")

    connection.inputStream.use { input ->
        tempFile.outputStream().use { output ->
            val buffer = ByteArray(8 * 1024)
            var bytesRead: Int
            var downloadedBytes = 0L
            var lastProgress = -1

            while (input.read(buffer).also { bytesRead = it } != -1) {
                output.write(buffer, 0, bytesRead)
                downloadedBytes += bytesRead

                // Calcular el progreso solo si se conoce el total
                if (totalBytes > 0) {
                    val progress = (downloadedBytes * 100 / totalBytes).toInt()
                    if (progress != lastProgress) {
                        println("Descargando: $progress%")
                        lastProgress = progress
                    }
                }
            }
        }
    }

    println("Descarga completa: ${tempFile.absolutePath}")
    return tempFile
}

fun extractWaveformAmplitudesFromVideo(videoUrl: String): List<Float> {

    val amplitudes = mutableListOf<Float>()
    val sampleRate = 44100
    val bufferSize = 2048
    val overlap = 1024

    val videoFile = downloadFileFromUrl(videoUrl)

    val dispatcher = AudioDispatcherFactory.fromPipe(videoFile.absolutePath, sampleRate, bufferSize, overlap)
    dispatcher.addAudioProcessor(object : AudioProcessor {
        override fun process(audioEvent: AudioEvent): Boolean {
            amplitudes += audioEvent.floatBuffer.map { it.absoluteValue }
            return true
        }
        override fun processingFinished() {}
    })

    dispatcher.run()

    // Limpia el archivo después de usarlo
    videoFile.delete()

    return amplitudes
}

@Composable
fun MediaPlayerScreenORI() {
    val playerState = rememberVideoPlayerState()

    val amplitudes = remember { mutableStateListOf<Float>() }

    LaunchedEffect(Unit) {
        val videoUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"

        playerState.openUri(videoUrl)

        withContext(Dispatchers.IO) {
            //val amplitudesRaw = extractWaveformAmplitudesFromVideo(videoUrl)
            val amplitudesRaw = extractWaveformAmplitudesFromVideo(videoUrl)

            val downsampled = downsampleAmplitudes(amplitudesRaw, 500)
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

fun convertMp3ToWav(mp3File: File, wavFile: File): Boolean {
    return try {
        val inputStream = AudioSystem.getAudioInputStream(mp3File)
        val decodedStream = AudioSystem.getAudioInputStream(
            AudioSystem.getAudioFileFormat(wavFile).format,
            inputStream
        )

        AudioSystem.write(decodedStream, AudioFileFormat.Type.WAVE, wavFile)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun extractWaveformAmplitudes(audioFile: File): List<Float> {
    val amplitudes = mutableListOf<Float>()
    val path = audioFile.absolutePath
    val bufferSize = 2048
    val overlap = 1024
    val sampleRate = 44100 // Puedes ajustarlo si sabes el real

    val dispatcher = AudioDispatcherFactory.fromPipe(path, sampleRate, bufferSize, overlap)

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
fun WaveformView2(amplitudes: List<Float>, modifier: Modifier = Modifier) {
    val maxAmplitude = amplitudes.maxOrNull() ?: 1f

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

@Composable
fun WaveformView(
    amplitudes: List<Float>,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    val targetSize = 500

    // Animatables para cada barra
    val animatables = remember {
        List(targetSize) { Animatable(0f) }
    }

    // Animación continua que refleja el cambio en amplitudes
    LaunchedEffect(amplitudes) {
        while (true) {
            val maxAmplitude = amplitudes.maxOrNull()?.takeIf { it > 0f } ?: 1f
            val paddedAmps = if (amplitudes.size < targetSize) {
                amplitudes + List(targetSize - amplitudes.size) { 0f }
            } else {
                amplitudes.takeLast(targetSize)
            }

            paddedAmps.forEachIndexed { i, amp ->
                val norm = (amp / maxAmplitude).coerceIn(0f, 1f)
                launch {
                    animatables[i].animateTo(
                        targetValue = norm,
                        animationSpec = tween(100, easing = LinearEasing)
                    )
                }
            }

            delay(200) // control de frecuencia de actualización
        }
    }

    // Dibujar las barras
    Canvas(modifier = modifier) {
        val barWidth = size.width / targetSize
        animatables.forEachIndexed { i, anim ->
            val height = anim.value * size.height
            val x = i * barWidth
            drawLine(
                color = Color.White,
                start = Offset(x, size.height / 2 - height / 2),
                end = Offset(x, size.height / 2 + height / 2),
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


//////////
fun downloadFileStreaming(
    url: String,
    outputFile: File,
    onChunkDownloaded: (downloadedBytes: Long, totalBytes: Long) -> Unit
) {
    val connection = URL(url).openConnection() as HttpURLConnection
    val totalBytes = connection.contentLengthLong

    connection.inputStream.use { input ->
        outputFile.outputStream().use { output ->
            val buffer = ByteArray(8 * 1024)
            var bytesRead: Int
            var downloadedBytes = 0L

            while (input.read(buffer).also { bytesRead = it } != -1) {
                output.write(buffer, 0, bytesRead)
                output.flush()
                downloadedBytes += bytesRead
                onChunkDownloaded(downloadedBytes, totalBytes)
            }
        }
    }
}

@Composable
fun MediaPlayerScreen() {
    val playerState = rememberVideoPlayerState()

    val amplitudes = remember { mutableStateListOf<Float>() }
    var totalBytes by remember { mutableStateOf(0L) }
    var currentPrgress by remember { mutableStateOf(0) }
    val downloadComplete = MutableStateFlow(false)

    var progressVideo by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {

        while (true){
            delay(500)
            if (!playerState.userDragging)
            progressVideo = playerState.sliderPos
        }
    }

    LaunchedEffect(Unit) {

        val videoUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        //val videoUrl = "E:\\Descargas\\idm\\Facebook.mp4"
        val tempFile = File.createTempFile("streaming_video", ".mp4")

        // Lanzamos descarga y análisis en paralelo
        coroutineScope {

            // Inicia descarga mientras se analiza
            launch(Dispatchers.IO) {
                if (videoUrl.contains("http:") || videoUrl.contains("https:")){
                    downloadFileStreaming(videoUrl, tempFile) { downloaded, total ->
                        val percent = (downloaded * 100 / total).toInt()
                        if (percent != currentPrgress) {
                            currentPrgress = percent
                            totalBytes = total
                            println("Progreso de descarga: $currentPrgress%")
                            if (currentPrgress == 100) {

                                downloadComplete.value = true

                                val currentProgressVideo = playerState.sliderPos

                                playerState.openFile(PlatformFile(tempFile.absolutePath))

                                playerState.seekTo(currentProgressVideo)

                                playerState.loop = true


                            }
                        }
                    }
                } else {

                    playerState.openFile(PlatformFile(videoUrl))

                }

            }

            // Inicia reproducción del archivo local aunque se esté escribiendo
            launch {
                delay(1000)
                playerState.openFile(PlatformFile(tempFile.absolutePath))
            }

            launch(Dispatchers.IO) {
                delay(1000)

                while (!downloadComplete.value) {
                    println("⏳ Ejecutando dispatcher...")
                    val amplitudesRaw = mutableListOf<Float>()

                    val dispatcher = AudioDispatcherFactory.fromPipe(
                        tempFile.absolutePath,
                        44100,
                        2048,
                        1024
                    )

                    dispatcher.addAudioProcessor(object : AudioProcessor {
                        var normalized = listOf<Float>()
                        var progress = 0

                        override fun process(audioEvent: AudioEvent): Boolean {

                            amplitudesRaw += audioEvent.floatBuffer.map { it.absoluteValue }

                            if (progress != currentPrgress) {

                                progress = currentPrgress

                                val downsampled = downsampleAmplitudes(amplitudesRaw, (500 * currentPrgress / 100))

                                normalized = downsampled

                                CoroutineScope(Dispatchers.Main).launch {
                                    amplitudes.clear()
                                    amplitudes.addAll(normalized)

                                }
                            }
                            return true
                        }

                        override fun processingFinished() {
                            println("🔁 Dispatcher terminado, se reiniciará si falta archivo")
                        }
                    })

                    dispatcher.run()

                    // espera antes de reiniciar si aún no ha terminado la descarga
                    delay(1000)
                }

                println("✅ Descarga completa, análisis finalizado.")
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
            ){
                if (amplitudes.isNotEmpty()) {
                    Text("Waveform",  color = Color.White, style = MaterialTheme.typography.labelLarge)

                    //println("amplitudes size: ${amplitudes.size}")
                    WaveformView(
                        amplitudes = amplitudes,
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(100.dp)
                    )
                }

                // Slider
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(bottom = 110.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Slider(
                        value = progressVideo,
                        onValueChange = {
                            progressVideo = it
                            playerState.userDragging = true
                            println("Position changed: $it")
                        },
                        onValueChangeFinished = {
                            playerState.userDragging = false
                            playerState.seekTo(progressVideo)
                            println("Position finalized: ${playerState.sliderPos}")
                        },
                        valueRange = 0f..1000f
                    )

                    Text(
                        text = "${playerState.positionText} / ${playerState.durationText}",
                        color = Color.White
                    )
                }

            }
        }

        Spacer(Modifier.height(8.dp))

        // Waveform


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


fun formatTime(millis: Long): String {
    val totalSec = millis / 1000
    val min = totalSec / 60
    val sec = totalSec % 60
    return "%02d:%02d".format(min, sec)
}

fun durationToLong(duration: String): Long {
    val parts = duration.split(":")
    val minutes = parts[0].toLong()
    val seconds = parts[1].toLong()
    return (minutes * 60 + seconds) * 1000
}

















fun extractWaveformAmplitudesFromVideo2(videoUrl: String): List<Float> {
    val amplitudes = mutableListOf<Float>()
    val bufferSize = 2048
    val overlap = 1024
    val sampleRate = 44100

    val dispatcher = AudioDispatcherFactory.fromPipe(videoUrl, sampleRate, bufferSize, overlap)

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
fun MediaPlayerScreen2() {
    val playerState = rememberVideoPlayerState()

    val amplitudes = remember { mutableStateListOf<Float>() }

    LaunchedEffect(Unit) {
        playerState.openUri("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")

        withContext(Dispatchers.IO) {
            val sourceFile = File("E:\\Descargas\\idm\\audio.mp3")
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

fun extractWaveformAmplitudes22(audioFile: File): List<Float> {
    val fileToProcess = if (audioFile.extension.lowercase() == "mp3") {
        val wavFile = File.createTempFile("converted_", ".wav")
        if (convertMp3ToWav(audioFile, wavFile)) wavFile else audioFile
    } else audioFile

    val amplitudes = mutableListOf<Float>()

    val dispatcher = AudioDispatcherFactory.fromFile(fileToProcess, 2048, 1024)
    dispatcher.addAudioProcessor(object : AudioProcessor {
        override fun process(audioEvent: AudioEvent): Boolean {
            amplitudes += audioEvent.floatBuffer.map { it.absoluteValue }
            return true
        }
        override fun processingFinished() {}
    })

    dispatcher.run()

    // Limpieza si fue un archivo temporal
    if (fileToProcess != audioFile) fileToProcess.delete()

    return amplitudes
}

fun extractWaveformAmplitudes2(audioFile: File): List<Float> {
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
