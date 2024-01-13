import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberDialogState
import androidx.compose.ui.window.rememberNotification
import com.jthemedetecor.OsThemeDetector
import com.tulskiy.keymaster.common.Provider
import component.MyDialogWindow
import component.rightBottom
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import model.AppStore
import model.ThemeOption
import org.apache.logging.log4j.kotlin.logger
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import ui.*
import java.awt.GraphicsEnvironment
import javax.swing.KeyStroke
import kotlin.time.Duration.Companion.seconds

private val logger = logger("Main")

@OptIn(ExperimentalResourceApi::class)
fun main() = application {
  val store = remember { AppStore() }
  val state = store.state
  val selectedItem = remember { mutableStateOf(0) }

  val rightBottom = GraphicsEnvironment.getLocalGraphicsEnvironment().maximumWindowBounds.rightBottom
  val dialogState = rememberDialogState(
    width = 420.dp,
    height = 700.dp,
    position = WindowPosition((rightBottom.x - 400).dp, (rightBottom.y - 690).dp)
  )

  val darkTheme = remember { mutableStateOf(store.isDarkTheme()) }
  PortViewTheme(darkTheme = darkTheme.value) {
    MyDialogWindow(
      onCloseRequest = store::hidden,
      visible = state.isVisible,
      alwaysOnTop = true,
      undecorated = true,
      transparent = true,
      resizable = false,
      icon = painterResource("icon.png"),
      state = dialogState,
    ) {
      MaterialTheme {
        Scaffold(
          modifier = Modifier.background(Color.Transparent).fillMaxSize()
            .padding(top = 10.dp, start = 20.dp, end = 20.dp, bottom = 10.dp)
            .shadow(20.dp, RoundedCornerShape(5.dp)),
          topBar = {
            WindowDraggableArea(modifier = Modifier
              .pointerInput(Unit) {
                detectTransformGestures { _, panGesture, _, _ ->
                  dialogState.position = WindowPosition((dialogState.position.x.value + panGesture.x).dp,
                    (dialogState.position.y.value + panGesture.y).dp)
                }
              }) { TopBar(store) }
          },
          bottomBar = { BottomNav(selectedItem) }
        ) {
          if (selectedItem.value == 0) {
            Column {
              SearchField(store)
              Content(store)
            }
          } else {
            Setting(store)
          }
        }
      }

      TraySetting(state = state.trayState, exit = { exitApplication() }, onAction = { x, y ->
        dialogState.position = WindowPosition(x, y)
        store.visibleToggle()
      })
    }
  }

  val notification = rememberNotification("Port view setup success!", "")

  LaunchedEffect(Unit) {
    state.trayState.sendNotification(notification)
    logger.info("Send init notification.")
  }

  refreshEffect(store)
  themeEffect(store, darkTheme)
  keyboardEffect(store)
}

@Composable
private fun refreshEffect(store: AppStore) {
  LaunchedEffect(store.config.refreshTime) {
    logger.info("Refresh time update to ${store.config.refreshTime}s.")
    while (isActive) {
      delay(store.config.refreshTime.seconds)
      store.updateItems()
    }
  }
}

@Composable
private fun keyboardEffect(store: AppStore) {
  DisposableEffect(store.state.keyboard) {
    val provider = Provider.getCurrentProvider(true)
    logger.info("Keyboard update to ${store.state.keyboard}")
    val keyStroke = KeyStroke.getKeyStroke(store.state.keyboard)
    if (keyStroke == null) {
      logger.warn("Can not get keyStroke from ${store.state.keyboard}.")
    } else {
      provider.register(keyStroke) {
        store.visibleToggle()
      }
      logger.info("Register keyboard success.")
    }
    onDispose {
      if (keyStroke != null) {
        provider.unregister(keyStroke)
        logger.info("Unregister keyboard ${store.state.keyboard} success.")
      }
    }
  }
}

@Composable
private fun themeEffect(store: AppStore, darkTheme: MutableState<Boolean>) {
  val changeTheme: (theme: Boolean) -> Unit = {
    if (store.config.theme != ThemeOption.SYSTEM) {
      logger.info("The current theme follows the system.")
      darkTheme.value = store.config.theme.isDark()
    } else {
      logger.info("Theme is dark: ${it}.")
      darkTheme.value = it
    }
  }

  LaunchedEffect(store.config.theme) {
    logger.info("Change config theme ${store.config.theme}")
    changeTheme(store.isDarkTheme())
  }

  DisposableEffect(Unit) {
    val detector = OsThemeDetector.getDetector()
    detector.registerListener(changeTheme)
    logger.info("Add system theme listener.")
    onDispose {
      detector.removeListener(changeTheme)
      logger.info("Remove system theme listener.")
    }
  }
}


