package model

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.FileResourceLoader
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

data class ExternalLink(
  val title: String,
  val link: String,
  val icon: ImageVector
)

enum class ThemeOption {
  LIGHT, SYSTEM, DARK;

  fun isDark(): Boolean = this == DARK
}

@Serializable
data class AppInformation(
  val name: String = "Port View",
  val updateDate: String,
  val version: String
)

const val UNKNOWN: String = "Unknown"

object Information {
  val app: AppInformation = fromFile()

  @OptIn(ExperimentalComposeUiApi::class)
  private fun fromFile(): AppInformation {
    val json = Json { ignoreUnknownKeys = true }
    val resource = FileResourceLoader(File("app.json"))
    if (!resource.root.exists()) {
      throw Exception("Not found app.json resource.")
    }
    val jsonString = File(resource.root.absolutePath).readText()
    return json.decodeFromString(jsonString)
  }
}