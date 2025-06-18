import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            //Icons
            implementation("br.com.devsrsouza.compose.icons:tabler-icons:1.1.1")

            //Audio and video
            //implementation("io.github.kdroidfilter:composemediaplayer:0.7.1")
            implementation("io.github.kdroidfilter:composemediaplayer:0.7.1")
            implementation("be.tarsos.dsp:core:2.5")
            implementation("be.tarsos.dsp:jvm:2.5")
            implementation("com.googlecode.soundlibs:mp3spi:1.9.5.4")

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}


compose.desktop {
    application {
        mainClass = "org.tovars.sonar.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.tovars.sonar"
            packageVersion = "1.0.0"
        }
    }
}
