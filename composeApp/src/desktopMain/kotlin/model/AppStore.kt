package model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.TrayState
import com.jthemedetecor.OsThemeDetector
import core.PortInfo
import core.getPortStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.harawata.appdirs.AppDirsFactory
import org.apache.commons.lang3.StringUtils
import org.tinylog.kotlin.Logger
import java.io.File


val CONFIG_PATH: String = AppDirsFactory.getInstance().getUserConfigDir("PortView", null, "zyue") + File.separatorChar + "config.json"

class AppStore {
  var config: ConfigState by mutableStateOf(initialConfig())
    private set
  var state: AppState by mutableStateOf(initialState())
    private set

  private fun initialState(): AppState {
    return AppState(
      items = portInfoList(),
      keyboard = config.getKeyStrokeString()
    )
  }

  private fun initialConfig(): ConfigState {
    val json = Json { ignoreUnknownKeys = true }
    var fileConfig = runCatching {
      val jsonString = File(CONFIG_PATH).readText()
      Logger.info("Load config file from $CONFIG_PATH. Get config json string: $jsonString")
      json.decodeFromString<ConfigState>(jsonString)
    }.getOrNull()
    if (fileConfig == null) {
      fileConfig = ConfigState(
        language = "简体中文",
        theme = ThemeOption.SYSTEM,
        keyboard = "ctrl shift P",
        refreshTime = 5,
        showUnknown = true
      )
    }
    Logger.info("Get config $fileConfig")
    return fileConfig
  }

  fun visibleToggle() {
    setState {
      copy(isVisible = !isVisible)
    }
  }

  fun hidden() {
    setState {
      copy(isVisible = false)
    }
  }

  fun changeSearchText(value: String) {
    setState {
      copy(searchText = value)
    }
  }

  fun updateItems() {
    setState {
      copy(loading = true)
    }
    val items = portInfoList()
    setState {
      copy(items = items, loading = false)
    }
  }

  private fun portInfoList(): List<PortInfo> {
    val items = if (config.showUnknown) {
      getPortStrategy().portList()
    } else {
      getPortStrategy().portList()
        .filter { !StringUtils.equalsIgnoreCase(it.name, "unknown") }
    }
    return items
  }

  fun updateKeyboard() {
    setState {
      copy(
        keyboard = config.getKeyStrokeString()
      )
    }
  }

  private inline fun setState(update: AppState.() -> AppState) {
    state = state.update()
  }

  fun configKeyboard(keyboard: String) {
    setConfig {
      copy(keyboard = keyboard)
    }
  }

  fun configTheme(theme: ThemeOption) {
    setConfig {
      copy(theme = theme)
    }
  }

  fun configLanguage(language: String) {
    setConfig {
      copy(language = language)
    }
  }

  fun configRefreshTime(time: Int) {
    setConfig {
      copy(refreshTime = time)
    }
  }

  fun configShowUnknown(showUnknown: Boolean) {
    setConfig {
      copy(showUnknown = showUnknown)
    }
  }

  private inline fun setConfig(update: ConfigState.() -> ConfigState) {
    config = config.update()
    config.save()
  }

  data class AppState(
    val items: List<PortInfo> = emptyList(),
    val searchText: String = "",
    val editingItemId: Long? = null,
    val keyboard: String = "",
    val isVisible: Boolean = true,
    val trayState: TrayState = TrayState(),
    val loading: Boolean = false
  ) {
    val list: List<PortInfo>
      get() {
        val conditionInt = searchText.toIntOrNull()
        return items.filter {
          it.name.trim().contains(searchText.trim(), ignoreCase = true)
            || (conditionInt != null && it.port == conditionInt)
        }
      }
  }

  fun isDarkTheme(): Boolean {
    if (config.theme == ThemeOption.LIGHT) {
      return false
    }
    if (config.theme == ThemeOption.DARK) {
      return true
    }
    return OsThemeDetector.getDetector().isDark
  }

  @Serializable
  data class ConfigState(
    val language: String,
    val theme: ThemeOption,
    val keyboard: String,
    val refreshTime: Int,
    val showUnknown: Boolean
  ) {
    fun save() {
      val json = Json { prettyPrint = true }
      val file = File(CONFIG_PATH)
      if (!file.exists()) {
        File(file.parent).mkdirs()
        file.createNewFile()
      }
      file.writeText(json.encodeToString(this))
    }

    fun getKeyStrokeString(): String =
      if (keyboard.contains("ctrl", true)) {
        keyboard.replace("ctrl", "control", true)
      } else {
        keyboard
      }

  }
}

enum class ThemeOption(val text: String) {
  LIGHT("浅色"), SYSTEM("系统"), DARK("暗色");

  fun isDark(): Boolean = this == DARK

}