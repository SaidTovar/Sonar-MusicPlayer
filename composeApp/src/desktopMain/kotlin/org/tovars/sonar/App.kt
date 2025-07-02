package org.tovars.sonar

import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.tovars.sonar.presentation.Home.HomeScreen

@Composable
@Preview
fun App(
    exitApplication: () -> Unit = {},
    maximizeApplication: () -> Unit = {},
    minimizeApplication: () -> Unit = {}
) {

    HomeScreen(
        exitApplication = exitApplication,
        maximizeApplication = maximizeApplication,
        minimizeApplication = minimizeApplication
    )

}