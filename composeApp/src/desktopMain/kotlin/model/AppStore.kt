package model

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.TrayState
import com.jthemedetecor.OsThemeDetector
import core.Platform
import core.PortInfo
import i18n.lang.LangEnum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.harawata.appdirs.AppDirsFactory
import org.apache.commons.lang3.StringUtils
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource
import org.tinylog.configuration.Configuration
import org.tinylog.kotlin.Logger
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption


val USER_CONFIG_DIR: String = AppDirsFactory.getInstance().getUserConfigDir("PortView", null, null)
val CONFIG_PATH: String = USER_CONFIG_DIR + File.separatorChar + "config.json"
val LOGGER_PATH: String = USER_CONFIG_DIR + File.separatorChar + "port-view.log"
val ELEVATE_PATH: String = USER_CONFIG_DIR + File.separatorChar + "Elevate.exe"

class AppStore {
  var config: ConfigState by mutableStateOf(initialConfig())
    private set
  var state: AppState by mutableStateOf(initialState())
    private set

  @OptIn(ExperimentalResourceApi::class)
  private fun initialState(): AppState {
    if (Platform.isWindows) {
      val elevatePath = File(ELEVATE_PATH)
      if (!elevatePath.exists()) {
        val name = "helper/Elevate_${
          if (Platform.x64) "x64"
          else "x86"
        }.exe"
        val bytes = runBlocking {
          resource(name).readBytes()
        }
        try {
          Files.write(Path.of(ELEVATE_PATH), bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
        } catch (e: Exception) {
          Logger.warn("Write Elevate file error.", e)
        }
      }
    }
    return AppState(
      keyboard = config.getKeyStrokeString(),
      showUnknown = config.showUnknown
    )
  }

  private fun initialConfig(): ConfigState {
    Configuration.set("writer2.file", LOGGER_PATH)
    Logger.info("Success set logger writer file. $LOGGER_PATH")
    val json = Json { ignoreUnknownKeys = true }
    var fileConfig = runCatching {
      val jsonString = File(CONFIG_PATH).readText()
      Logger.info("Load config file from $CONFIG_PATH. Get config json string: $jsonString")
      json.decodeFromString<ConfigState>(jsonString)
    }.getOrNull()
    if (fileConfig == null) {
      fileConfig = ConfigState(
        language = LangEnum.ZH,
        theme = ThemeOption.SYSTEM,
        keyboard = "ctrl shift P",
        refreshTime = 5,
        showUnknown = false,
        showNotification = false
      )
    }
    Logger.info("Get config $fileConfig")
    return fileConfig
  }

  fun visibleToggle() {
    setState {
      copy(isVisible = !isVisible)
    }
    this.updateItems()
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

  fun isEmpty() = state.list.isEmpty()

  fun updateItems() {
    setState {
      copy(loading = true)
    }
    CoroutineScope(Dispatchers.Default).launch {
      val items = Platform.portStrategy.portList(state.items) { newPorts ->
        if (state.items.isNotEmpty() && newPorts.isNotEmpty() && config.showNotification) {
          val tip = if (config.language == LangEnum.ZH) {
            "新的端口占用: "
          } else {
            "New ports: "
          } + newPorts.filter {
            config.showUnknown || StringUtils.isNotEmpty(it.command)
          }.joinToString(",") { "${it.name}(${it.port})" }
          sendNotification(tip, Notification.Type.Info)
        }
      }
      setState {
        copy(items = items, loading = false)
      }
    }
  }

  fun changeCurrentTab(current: Int) {
    setState {
      copy(currentTab = current)
    }
  }

  fun updateKeyboard() {
    setState {
      copy(
        keyboard = config.getKeyStrokeString()
      )
    }
  }

  fun sendNotification(notification: Notification) {
    state.trayState.sendNotification(notification)
  }

  fun sendWarn(title: String) {
    sendNotification(title, Notification.Type.Warning)
  }

  fun sendNotification(title: String, type: Notification.Type) {
    val notification = Notification(
      title, "", type
    )
    state.trayState.sendNotification(notification)
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

  fun configLanguage(language: LangEnum) {
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
    setState {
      copy(showUnknown = showUnknown)
    }
    setConfig {
      copy(showUnknown = showUnknown)
    }
  }

  fun configShowNotice(showNotice: Boolean) {
    setConfig {
      copy(showNotification = showNotice)
    }
  }

  private inline fun setConfig(update: ConfigState.() -> ConfigState) {
    config = config.update()
    config.save()
  }

  fun searchFocus() {
    if (state.currentTab == 0 && state.list.isNotEmpty()) {
      state.searchFocusRequester.requestFocus()
    }
  }

  data class AppState(
    val items: List<PortInfo> = emptyList(),
    val searchText: String = "",
    val searchFocusRequester: FocusRequester = FocusRequester(),
    val searchInteractionSource: MutableInteractionSource = MutableInteractionSource(),
    val focusRequester: FocusRequester = FocusRequester(),
    val lazyListState: LazyListState = LazyListState(0, 0),
    val currentTab: Int = 0,
    val editingItemId: Long? = null,
    val keyboard: String = "",
    val isVisible: Boolean = true,
    val trayState: TrayState = TrayState(),
    val loading: Boolean = false,
    val showUnknown: Boolean
  ) {
    val list: List<PortInfo>
      get() {
        val conditionInt = searchText.toIntOrNull()
        return items.filter {
          it.name.trim().contains(searchText.trim(), ignoreCase = true)
            || (conditionInt != null && "${it.port}".contains("$conditionInt"))
        }.filter {
          if (showUnknown) {
            true
          } else {
            !StringUtils.equalsIgnoreCase(it.name, UNKNOWN)
              && StringUtils.isNotEmpty(it.command)
          }
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
    val language: LangEnum,
    val theme: ThemeOption,
    val keyboard: String,
    val refreshTime: Int,
    val showUnknown: Boolean,
    val showNotification: Boolean
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
