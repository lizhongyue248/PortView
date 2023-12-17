import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
  alias(libs.plugins.kotlinMultiplatform)

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
      implementation(compose.material)
      @OptIn(ExperimentalComposeLibrary::class)
      implementation(compose.components.resources)

      implementation("net.java.dev.jna:jna:5.14.0")
      implementation("net.java.dev.jna:jna-platform:5.14.0")
      implementation("org.apache.commons:commons-lang3:3.14.0")
    }
  }
}


compose.desktop {
  application {
    mainClass = "MainKt"

    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
      packageName = "PortView"
      packageVersion = "1.0.0"
    }

    buildTypes.release.proguard {
      configurationFiles.from(project.file("compose-desktop.pro"))
    }
  }

}
