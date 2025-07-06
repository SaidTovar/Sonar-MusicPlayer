package org.tovars.sonar.presentation.Home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import compose.icons.TablerIcons
import compose.icons.tablericons.ArrowsMaximize
import compose.icons.tablericons.ArrowsMinimize
import compose.icons.tablericons.ArrowsSort
import compose.icons.tablericons.PlayerPause
import compose.icons.tablericons.PlayerPlay
import compose.icons.tablericons.PlayerSkipBack
import compose.icons.tablericons.PlayerSkipForward
import compose.icons.tablericons.Playlist
import compose.icons.tablericons.Volume
import compose.icons.tablericons.Volume2
import compose.icons.tablericons.Volume3
import compose.icons.tablericons.X
import korlibs.audio.format.WAV
import korlibs.audio.format.mp3.MP3Decoder
import korlibs.audio.format.toWav
import korlibs.audio.sound.SoundChannel
import korlibs.audio.sound.nativeSoundProvider
import korlibs.audio.sound.playing
import korlibs.io.stream.openAsync
import korlibs.time.seconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import sonar_musicplayer.composeapp.generated.resources.Res
import sonar_musicplayer.composeapp.generated.resources.compose_multiplatform
import java.io.File
import java.net.URL
import kotlin.math.sqrt

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

    val amplitudes = remember { mutableStateListOf<Float>() }
    var currentProgress by remember { mutableStateOf(0) }
    var progressAudio by remember { mutableStateOf(0f) }
    val downloadComplete = remember { MutableStateFlow(false) }
    val channelRef = remember { mutableStateOf<SoundChannel?>(null) }
    val durationSeconds = remember { mutableStateOf(1f) }

    val mp3Url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"

    // Actualizar barra de progreso de audio
    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            channelRef.value?.let { ch ->
                if (!ch.playing) return@let
                val pos = ch.current.seconds.toFloat()
                durationSeconds.value = ch.total.seconds.toFloat().coerceAtLeast(1f)
                progressAudio = pos / durationSeconds.value
            }
        }
    }

    // Iniciar descarga y reproducción
    LaunchedEffect(Unit) {
        val tempFile = File.createTempFile("streaming_audio", ".mp3")

        coroutineScope {
            launch(Dispatchers.IO) {
                // Descargar progresivamente el mp3
                val conn = URL(mp3Url).openConnection()
                val totalSize = conn.contentLengthLong
                conn.getInputStream().use { input ->
                    tempFile.outputStream().use { output ->
                        val buffer = ByteArray(8192)
                        var totalRead = 0L
                        var bytesRead: Int

                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            totalRead += bytesRead
                            val percent = (totalRead * 100 / totalSize).toInt()
                            if (percent != currentProgress) {
                                currentProgress = percent
                                if (percent >= 100) {
                                    downloadComplete.value = true
                                }
                            }
                        }
                    }
                }
            }

            launch {
                delay(5000) // Esperamos a que se descargue un poco
                val stream = tempFile.inputStream().readAllBytes()
                val audioData = MP3Decoder.decode(stream) ?: error("No se pudo decodificar MP3")
                val channel = nativeSoundProvider.createSound(audioData, streaming = true).play()
                channelRef.value = channel

                // Extraer amplitudesvl
                val wavData = WAV.decode(audioData.toWav().openAsync())
                wavData?.let {

                    val samples = it.samplesInterleaved.data
                    val floatSamples = samples.map { s -> s.toFloat() / Short.MAX_VALUE }
                    val downsampled = downsampleAmplitudes(floatSamples, 100)
                    val normalized = downsampled.map { it / (downsampled.maxOrNull() ?: 1f) }
                    amplitudes.clear()
                    amplitudes.addAll(normalized)
                }
            }
        }
    }

    Row (
        verticalAlignment = Alignment.CenterVertically
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
                onClick = {
                    channelRef.value?.let {
                        println("play: ${it.playing}")
                        if (it.playing) it.pause() else it.resume()
                    }
                }
            ) {
                Icon(
                    modifier = Modifier.clip(CircleShape)
                        .size(40.dp)
                        .background(Color(0xfff6fb8d))
                        .padding(10.dp),
                    imageVector = if (channelRef.value?.playing == true) TablerIcons.PlayerPause else TablerIcons.PlayerPlay,
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
                .width(200.dp),
            contentAlignment = Alignment.Center

        ){

            /*
            if (amplitudes.isNotEmpty()) {
                WaveformView(
                    amplitudes = amplitudes,
                    modifier = Modifier.padding(vertical = 16.dp)
                        .fillMaxSize()
                )
            }
            */

/*

            Slider(
                value = progressAudio,
                onValueChange = {
                    progressAudio = it
                },
                onValueChangeFinished = {
                    channelRef.value?.let { ch ->
                        ch.current = (progressAudio * durationSeconds.value).seconds
                    }
                },
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.LightGray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp) // cambia el alto total del Slider
            )
*/

            /*
            ThinSlider(
                value = progressAudio,
                onValueChange = {
                    progressAudio = it
                    channelRef.value?.let { ch ->
                        ch.current = (progressAudio * durationSeconds.value).seconds
                    }
                                },
                modifier = Modifier.fillMaxWidth(),
                trackHeight = 1.dp,
                thumbRadius = 6.dp,
                activeColor = Color.White,
                inactiveColor = Color.Gray
            )
            */

            WaveformSlider(
                value = progressAudio,
                onValueChange = {
                    progressAudio = it
                    channelRef.value?.let { ch ->
                        ch.current = (progressAudio * durationSeconds.value).seconds
                    }
                },
                amplitudes = amplitudes,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )


        }

        Text(
            "${progressAudioToString(channelRef.value?.current?.seconds?.toFloat()?:0f)} : ${progressAudioToString(durationSeconds.value)}",
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
                onClick = {
                    channelRef.value?.let {
                        if (it.volume > 0f) it.volume = 0.0 else it.volume = 1.0
                    }
                }
            ) {
                Icon(
                    imageVector = if (channelRef.value?.volume == 0.0) TablerIcons.Volume3 else TablerIcons.Volume,
                    "",
                    tint = Color.White
                )
            }

        }


    }

}

fun progressAudioToString(progress: Float): String {

    val minutes = (progress / 60).toInt()
    val seconds = (progress % 60).toInt()

    return String.format("%02d:%02d", minutes, seconds)

}

@Composable
fun ThinSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    trackHeight: Dp = 2.dp,
    thumbRadius: Dp = 6.dp,
    activeColor: Color = Color.White,
    inactiveColor: Color = Color.LightGray,
    enabled: Boolean = true
) {
    val thumbRadiusPx = with(LocalDensity.current) { thumbRadius.toPx() }
    val trackHeightPx = with(LocalDensity.current) { trackHeight.toPx() }

    var sliderWidthPx by remember { mutableStateOf(1f) }

    // Recalculamos el offset real del pulgar según el valor actual
    val thumbOffsetX = remember(value, sliderWidthPx) {
        (value.coerceIn(0f, 1f)) * sliderWidthPx
    }

    val draggableState = rememberDraggableState { delta ->
        if (!enabled) return@rememberDraggableState

        val newOffset = (thumbOffsetX + delta).coerceIn(0f, sliderWidthPx)
        val newValue = (newOffset / sliderWidthPx).coerceIn(0f, 1f)
        onValueChange(newValue)
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(thumbRadius * 2)
            .onGloballyPositioned {
                sliderWidthPx = it.size.width.toFloat()
            }
            .draggable(
                orientation = Orientation.Horizontal,
                state = draggableState,
                enabled = enabled
            )
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                detectTapGestures { offset ->
                    val newValue = (offset.x / sliderWidthPx).coerceIn(0f, 1f)
                    onValueChange(newValue)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerY = size.height / 2

            // Inactive track
            drawLine(
                color = inactiveColor,
                start = Offset(0f, centerY),
                end = Offset(size.width, centerY),
                strokeWidth = trackHeightPx
            )

            // Active track
            drawLine(
                color = activeColor,
                start = Offset(0f, centerY),
                end = Offset(thumbOffsetX, centerY),
                strokeWidth = trackHeightPx
            )

            // Thumb
            drawCircle(
                color = activeColor,
                radius = thumbRadiusPx,
                center = Offset(thumbOffsetX, centerY)
            )
        }
    }
}




@Composable
fun WaveformView(
    amplitudes: List<Float>,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    val targetSize = 100

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
                strokeWidth = barWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun WaveformSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    amplitudes: List<Float>,
    modifier: Modifier = Modifier,
    trackHeight: Dp = 36.dp,
    thumbRadius: Dp = 6.dp,
    foregroundColor: Color = Color(0xfff4f89b),
    backgroundColor: Color = Color.Gray,
    enabled: Boolean = true
) {
    val thumbRadiusPx = with(LocalDensity.current) { thumbRadius.toPx() }
    val trackHeightPx = with(LocalDensity.current) { trackHeight.toPx() }

    var sliderWidthPx by remember { mutableStateOf(1f) }

    // Calcular posición del pulgar en X según el valor actual
    val thumbOffsetX = remember(value, sliderWidthPx) {
        (value.coerceIn(0f, 1f)) * sliderWidthPx
    }

    val draggableState = rememberDraggableState { delta ->
        if (!enabled) return@rememberDraggableState

        val newOffset = (thumbOffsetX + delta).coerceIn(0f, sliderWidthPx)
        val newValue = (newOffset / sliderWidthPx).coerceIn(0f, 1f)
        onValueChange(newValue)
    }

    val targetSize = 100
    val animatables = remember {
        List(targetSize) { Animatable(0f) }
    }

    // Animar amplitudes
    LaunchedEffect(amplitudes) {
        while (true) {
            val maxAmp = amplitudes.maxOrNull()?.takeIf { it > 0f } ?: 1f
            val paddedAmps = if (amplitudes.size < targetSize) {
                amplitudes + List(targetSize - amplitudes.size) { 0f }
            } else {
                amplitudes.takeLast(targetSize)
            }

            paddedAmps.forEachIndexed { i, amp ->
                val norm = (amp / maxAmp).coerceIn(0f, 1f)
                launch {
                    animatables[i].animateTo(
                        targetValue = norm,
                        animationSpec = tween(100, easing = LinearEasing)
                    )
                }
            }

            delay(200)
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(trackHeight)
            .onGloballyPositioned { sliderWidthPx = it.size.width.toFloat() }
            .draggable(
                orientation = Orientation.Horizontal,
                state = draggableState,
                enabled = enabled
            )
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                detectTapGestures { offset ->
                    val newValue = (offset.x / sliderWidthPx).coerceIn(0f, 1f)
                    onValueChange(newValue)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val barWidth = size.width / targetSize
            val centerY = size.height / 2

            animatables.forEachIndexed { i, anim ->
                val x = i * barWidth
                val barHeight = anim.value * size.height

                val color = if (x <= thumbOffsetX) foregroundColor else backgroundColor

                drawLine(
                    color = color,
                    start = Offset(x, centerY - barHeight / 2),
                    end = Offset(x, centerY + barHeight / 2),
                    strokeWidth = barWidth,
                    cap = StrokeCap.Round
                )
            }

            // Thumb
            drawCircle(
                color = Color.Transparent,
                radius = thumbRadiusPx,
                center = Offset(thumbOffsetX, centerY)
            )
        }
    }
}


/*

@Composable
fun MediaPlayerScreenUIOK() {
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

*/

fun downsampleAmplitudesORI(amplitudes: List<Float>, targetSize: Int): List<Float> {
    if (amplitudes.size <= targetSize) return amplitudes

    val blockSize = amplitudes.size / targetSize
    return (0 until targetSize).map { i ->
        val start = i * blockSize
        val end = minOf(start + blockSize, amplitudes.size)
        amplitudes.subList(start, end).average().toFloat()
    }
}

fun downsampleAmplitudes(amplitudes: List<Float>, targetSize: Int): List<Float> {
    if (amplitudes.size <= targetSize) return amplitudes

    val blockSize = amplitudes.size / targetSize
    return (0 until targetSize).map { i ->
        val start = i * blockSize
        val end = minOf(start + blockSize, amplitudes.size)
        val chunk = amplitudes.subList(start, end)
        sqrt(chunk.map { it * it }.average().toFloat())
    }
}


@Composable
fun MediaPlayerScreen() {
    Text("Hoa")
    /*
    val amplitudes = remember { mutableStateListOf<Float>() }
    var currentProgress by remember { mutableStateOf(0) }
    var progressAudio by remember { mutableStateOf(0f) }
    val downloadComplete = remember { MutableStateFlow(false) }
    val channelRef = remember { mutableStateOf<SoundChannel?>(null) }
    val durationSeconds = remember { mutableStateOf(1f) }

    val mp3Url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"

    // Actualizar barra de progreso de audio
    LaunchedEffect(Unit) {
        while (true) {
            delay(300)
            channelRef.value?.let { ch ->
                if (!ch.playing) return@let
                val pos = ch.current.seconds.toFloat()
                durationSeconds.value = ch.total.seconds.toFloat().coerceAtLeast(1f)
                progressAudio = pos / durationSeconds.value
            }
        }
    }

    // Iniciar descarga y reproducción
    LaunchedEffect(Unit) {
        val tempFile = File.createTempFile("streaming_audio", ".mp3")

        coroutineScope {
            launch(Dispatchers.IO) {
                // Descargar progresivamente el mp3
                val conn = URL(mp3Url).openConnection()
                val totalSize = conn.contentLengthLong
                conn.getInputStream().use { input ->
                    tempFile.outputStream().use { output ->
                        val buffer = ByteArray(8192)
                        var totalRead = 0L
                        var bytesRead: Int

                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            totalRead += bytesRead
                            val percent = (totalRead * 100 / totalSize).toInt()
                            if (percent != currentProgress) {
                                currentProgress = percent
                                if (percent >= 100) {
                                    downloadComplete.value = true
                                }
                            }
                        }
                    }
                }
            }

            launch {
                delay(1000) // Esperamos a que se descargue un poco
                val stream = tempFile.inputStream().readAllBytes()
                val audioData = MP3Decoder.decode(stream) ?: error("No se pudo decodificar MP3")
                val channel = nativeSoundProvider.createSound(audioData, streaming = true).play()
                channelRef.value = channel

                // Extraer amplitudes
                val wavData = WAV.decode(audioData.toWav().openAsync())
                wavData?.let {
                    val samples = it.samplesInterleaved.data
                    val floatSamples = samples.map { s -> s.toFloat() / Short.MAX_VALUE }
                    val downsampled = downsampleAmplitudes(floatSamples, 500)
                    val normalized = downsampled.map { it / (downsampled.maxOrNull() ?: 1f) }
                    amplitudes.clear()
                    amplitudes.addAll(normalized)
                }
            }
        }
    }

    // UI
    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Text("MP3 Player (Streaming)", color = Color.White)

        Spacer(Modifier.height(12.dp))

        if (amplitudes.isNotEmpty()) {
            WaveformView(
                amplitudes = amplitudes,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color.Black)
            )
        }

        Spacer(Modifier.height(8.dp))

        // Slider
        Slider(
            value = progressAudio,
            onValueChange = {
                progressAudio = it
            },
            onValueChangeFinished = {
                channelRef.value?.let { ch ->
                    ch.current = (progressAudio * durationSeconds.value).seconds
                }
            },
            valueRange = 0f..1f
        )

        Text(
            text = "Posición: ${(progressAudio * durationSeconds.value).toInt()}s / ${durationSeconds.value.toInt()}s",
            color = Color.White
        )

        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { channelRef.value?.resume() }) { Text("Play") }
            Button(onClick = { channelRef.value?.pause() }) { Text("Pause") }
        }

        Spacer(Modifier.height(8.dp))
        Text("Progreso de descarga: $currentProgress%", color = Color.White)
    }
    */

}
