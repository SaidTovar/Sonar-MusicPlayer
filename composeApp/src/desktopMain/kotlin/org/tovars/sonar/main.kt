package org.tovars.sonar

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.resources.painterResource
import sonar_musicplayer.composeapp.generated.resources.Res
import sonar_musicplayer.composeapp.generated.resources.logo2
import java.awt.Frame
import java.awt.MouseInfo
import java.awt.Window

fun main() = application {

    val windowState = rememberWindowState(
        width = 1480.dp,
        height = 960.dp
    )

    var windowRef by remember { mutableStateOf<Window?>(null) }

    var initialMousePosition by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var initialWindowLocation by remember { mutableStateOf<Pair<Int, Int>?>(null) }


    Window(
        onCloseRequest = ::exitApplication,
        title = "Sonar - Music Player",
        transparent = true,
        undecorated = true,
        state = windowState,
        icon = painterResource(Res.drawable.logo2)
    ) {
        // Guardamos la referencia AWT una sola vez
        LaunchedEffect(Unit) {
            windowRef = Window::class.java
                .cast(this@Window.javaClass.getDeclaredMethod("getWindow").apply { isAccessible = true }
                    .invoke(this@Window))
            println("Window reference: $windowRef")
        }

        Box (
            modifier = Modifier.pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        val mousePos = MouseInfo.getPointerInfo().location
                        initialMousePosition = mousePos.x to mousePos.y
                        windowRef?.let { win ->
                            initialWindowLocation = win.location.x to win.location.y
                        }
                    },
                    onDrag = { _, _ ->
                        val currentMousePos = MouseInfo.getPointerInfo().location
                        val (startMouseX, startMouseY) = initialMousePosition ?: return@detectDragGestures
                        val (startWindowX, startWindowY) = initialWindowLocation ?: return@detectDragGestures

                        val deltaX = currentMousePos.x - startMouseX
                        val deltaY = currentMousePos.y - startMouseY

                        windowRef?.setLocation(
                            startWindowX + deltaX,
                            startWindowY + deltaY
                        )
                    },
                    onDragEnd = {
                        initialMousePosition = null
                        initialWindowLocation = null
                    },
                    onDragCancel = {
                        initialMousePosition = null
                        initialWindowLocation = null
                    }
                )
            }
        ){

            App(
                exitApplication = ::exitApplication,
                maximizeApplication = {
                    val awtWindow = Frame.getFrames().firstOrNull { it.isActive }
                    if (awtWindow != null) {
                        awtWindow.extendedState = Frame.MAXIMIZED_BOTH // Maximizar
                    }
                },
                minimizeApplication = {
                    val awtWindow = Frame.getFrames().firstOrNull { it.isActive }
                    if (awtWindow != null) {
                        awtWindow.extendedState = Frame.ICONIFIED // Minimizar
                    }
                }
            )

        }
    }
}