
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.com.google.gson.Gson
import org.jetbrains.kotlin.ir.backend.js.scriptRemoveReceiverLowering

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
        implementation(libs.json.schema)
        implementation(libs.kotlinx.coroutines.test)
        implementation(libs.mockk)
      }
    }
    commonMain.dependencies {
      implementation(compose.runtime)
      implementation(compose.foundation)
      implementation(compose.material3)
      implementation(libs.jSystemThemeDetector)
      implementation(compose.components.resources)
      implementation(libs.jna.platform)
      implementation(libs.jna)
      implementation(libs.jkeymaster)
      implementation(libs.apache.commons)
      implementation(libs.app.dirs)
      implementation(libs.serialization.json.jvm)

      implementation(libs.slf4j)
      implementation(libs.tinylog)
      implementation(libs.tinylog.impl)
      implementation(libs.tinylog.slf4j)

      implementation(libs.imageio.core)
      implementation(libs.imageio.icns)
      implementation(libs.plist)

      implementation(libs.oshi.core)
    }
  }
}
val appJson = File(projectDir.absolutePath + "/src/commonMain/composeResources/files/app.json").readText()

data class AppInfo(
  val name: String,
  val updateDate: String,
  val version: String
)

val app: AppInfo = Gson().fromJson(appJson, AppInfo::class.java)

compose.desktop {
  nativeApplication {
    scriptRemoveReceiverLowering
  }
  application {
    mainClass = "MainKt"

    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Rpm)
      modules("java.instrument", "java.management", "java.naming", "java.sql", "jdk.unsupported")
      packageName = app.name
      packageVersion = app.version
      vendor = "zyue.wiki"
      copyright = "Copyright © 2024 $vendor All Rights Reserved."
      licenseFile.set(rootProject.file("LICENSE.txt"))
      outputBaseDir.set(project.layout.buildDirectory.dir("packages"))
      appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))
      println(project.layout.projectDirectory.dir("resources"))
      windows {
        shortcut = true
        menu = true
        menuGroup = packageName
        perUserInstall = true
        dirChooser = true
        iconFile.set(project.file("icon/win.ico"))
      }
      linux {
        shortcut = true
        menuGroup = packageName
        appCategory = "Monitor"
        iconFile.set(project.file("icon/linux.png"))
      }
      macOS {
        iconFile.set(project.file("icon/mac.icns"))
        infoPlist {
          extraKeysRawXml = """
              <key>LSUIElement</key>
              <string>true</string>
              <key>LSBackgroundOnly</key>
              <string>true</string>
          """.trimIndent()
        }
      }
    }
    buildTypes.release.proguard {
      configurationFiles.from(project.file("compose-desktop.pro"))
    }
  }

}




