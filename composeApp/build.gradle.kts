import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.kotlinSerialization)
  alias(libs.plugins.jetbrainsCompose)
}

kotlin {
  jvm("desktop")

  sourceSets {
    val desktopMain by getting

    desktopMain.dependencies {
      implementation(compose.desktop.currentOs)
    }
    val desktopTest by getting {
      dependencies {
        implementation(compose.desktop.uiTestJUnit4)
        implementation(compose.desktop.currentOs)
      }
    }
    commonMain.dependencies {
      implementation(compose.runtime)
      implementation(compose.foundation)
      implementation(compose.material3)
      implementation(libs.jSystemThemeDetector)
      @OptIn(ExperimentalComposeLibrary::class)
      implementation(compose.components.resources)
      implementation(libs.jna.platform)
      implementation(libs.jna)
      implementation(libs.jkeymaster)
      implementation(libs.apache.commons)
      implementation(libs.app.dirs)
      implementation(libs.serialization.json.jvm)

      // Logging
      implementation(libs.slf4j)
      implementation(libs.tinylog)
      implementation(libs.tinylog.impl)
      implementation(libs.tinylog.slf4j)
//      implementation(libs.log4j)
//      implementation(libs.log4j.slf4j)
//      implementation(libs.kotlin.logging)
    }
  }
}


compose.desktop {
  application {
    mainClass = "MainKt"

    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
      modules("java.instrument", "java.management", "java.naming", "java.sql", "jdk.unsupported")
      packageName = "PortView"
      packageVersion = "1.0.0"
    }

    buildTypes.release.proguard {
      configurationFiles.from(project.file("compose-desktop.pro"))
    }
  }

}
