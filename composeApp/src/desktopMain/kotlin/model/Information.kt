package model

import androidx.compose.runtime.Composable
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource

data class ExternalInfo(
  val title: String,
  val onClick: () -> Unit,
  val prefixIcon: @Composable () -> Unit = {},
  val suffixIcon: @Composable () -> Unit = {}
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

  @OptIn(ExperimentalResourceApi::class)
  private fun fromFile(): AppInformation {
    val json = Json { ignoreUnknownKeys = true }
    return json.decodeFromString(runBlocking {
      resource("app.json").readBytes().decodeToString()
    })
  }
}