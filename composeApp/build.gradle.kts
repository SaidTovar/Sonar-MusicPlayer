import org.gradle.declarative.dsl.schema.FqName.Empty.packageName
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties
import com.codingfeline.buildkonfig.compiler.FieldSpec

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.buildkonfig)
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
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            //koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)

            api(libs.koin.annotations)
            implementation(libs.koin.ksp.compiler)

            //ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.okhttp)

            implementation(libs.jetbrains.compose.navigation)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            //Icons
            implementation("br.com.devsrsouza.compose.icons:tabler-icons:1.1.1")
            implementation(compose.materialIconsExtended)

            //usar reproductor kmp y korau para las amplitudes
            implementation("com.soywiz.korlibs.korio:korio-jvm:4.0.0")
            implementation("com.soywiz.korlibs.korau:korau-jvm:4.0.0")
            implementation("io.github.khubaibkhan4:mediaplayer-kmp:2.0.9")


        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)

        }

    }

    sourceSets.named("commonMain").configure {
        kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
    }

}

ksp {
    arg("KOIN_USE_COMPOSE_VIEWMODEL","true")
    arg("KOIN_CONFIG_CHECK","true")
}

dependencies {
    add("kspCommonMainMetadata", libs.koin.ksp.compiler)
    ksp(libs.koin.ksp.compiler)
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

buildkonfig {
    packageName = "org.tovars.sonar"

    val localProperties =
        Properties().apply {
            val propsFile = rootProject.file("local.properties")
            if (propsFile.exists()) {
                load(propsFile.inputStream())
            }
        }

    defaultConfigs {
        buildConfigField(
            FieldSpec.Type.STRING,
            "API_KEY",
            localProperties["API_KEY"]?.toString() ?: "",
        )

        buildConfigField(
            FieldSpec.Type.STRING,
            "spotify_client_id",
            localProperties["spotify_client_id"]?.toString() ?: "",
        )

        buildConfigField(
            FieldSpec.Type.STRING,
            "spotify_client_secret",
            localProperties["spotify_client_secret"]?.toString() ?: "",
        )
    }
}
