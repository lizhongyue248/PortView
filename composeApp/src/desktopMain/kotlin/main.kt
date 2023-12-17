import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

fun main() = application {
  var isVisible by remember { mutableStateOf(false) }

  val trayState = rememberTrayState()
  var positionX by remember { mutableStateOf(50.dp) }
  var positionY by remember { mutableStateOf(50.dp) }
  var selectedItem by remember { mutableStateOf(0) }
  Window(
    onCloseRequest = { isVisible = false },
    visible = isVisible,
    alwaysOnTop = true,
    undecorated = true,
    transparent = true,
    resizable = false,
    icon = MyAppIcon,
    state = WindowState(
      width = 380.dp, height = 630.dp,
      position = WindowPosition(positionX, positionY)
    )
  ) {
    MaterialTheme {
      Scaffold(
        modifier = Modifier.background(Color.Transparent)
          .fillMaxSize()
          .padding(top = 10.dp, start = 20.dp, end = 20.dp, bottom = 10.dp)
          .shadow(20.dp, RoundedCornerShape(20.dp)),
        bottomBar = {
          BottomNavigation {
            BottomNavigationItem(
              icon = { Icon(Icons.Filled.Home, "Home") },
              label = { Text("Home") },
              selected = selectedItem == 0,
              onClick = {
                selectedItem = 0
              }
            )
            BottomNavigationItem(
              icon = { Icon(Icons.Filled.Settings, "Settings") },
              label = { Text("Settings") },
              selected = selectedItem == 1,
              onClick = { selectedItem = 1 }
            )
          }
        }
      ) {
        WindowDraggableArea {
          Content(hide = { isVisible = false })
        }
      }
    }

    TraySetting(state = trayState, exit = { exitApplication() }, onAction = { x, y ->
      positionX = x
      positionY = y
      isVisible = !isVisible
    })

  }

  val notification = rememberNotification("Port view setup success!", "")
  LaunchedEffect(Unit) {
    trayState.sendNotification(notification)
  }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TraySetting(
  state: TrayState = rememberTrayState(),
  exit: () -> Unit,
  onAction: (x: Dp, y: Dp) -> Unit
) {
  val image = ColorPainter(Color.Red).toAwtImage(Density(10F), LayoutDirection.Ltr, Size(16F, 16F))
  val popupMenu = remember { PopupMenu() }

  // 获取屏幕大小
  val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
  val screenWidth = screenSize.width.dp
  val screenHeight = screenSize.height.dp
  val windowInfo = LocalWindowInfo.current

  val tray = remember {
    TrayIcon(image).apply {
      isImageAutoSize = true
      addMouseListener(object : MouseAdapter() {
        override fun mousePressed(e: MouseEvent) {
          if (e.button == MouseEvent.BUTTON1) {
            val x = e.x.dp
            val y = e.y.dp
            val screenBounds: Rectangle = GraphicsEnvironment.getLocalGraphicsEnvironment().maximumWindowBounds
            val screenVisibleWidth = screenBounds.width.dp
            val screenVisibleHeight = screenBounds.height.dp
            val trayHeight = screenHeight.minus(screenVisibleHeight)
            if (y > screenHeight.div(2)) {
              // 任务栏在下方
              val positionX = x.minus(windowInfo.containerSize.width.dp.div(2)).plus(20.dp)
              val positionY = screenHeight.minus(trayHeight).minus(windowInfo.containerSize.height.dp).plus(10.dp)
              onAction(positionX, positionY)
            }
          }
        }
      })
    }
  }
  val composition = rememberCompositionContext()
  val coroutineScope = rememberCoroutineScope()
  DisposableEffect(Unit) {
    tray.popupMenu = popupMenu
    val menuComposition = popupMenu.setContent(composition) {
      Item("Exit", onClick = exit)
    }
    SystemTray.getSystemTray().add(tray)

    state.notificationFlow
      .onEach(tray::displayMessage)
      .launchIn(coroutineScope)

    onDispose {
      menuComposition.dispose()
      SystemTray.getSystemTray().remove(tray)
    }
  }
}

private fun TrayIcon.displayMessage(notification: Notification) {
  val messageType = when (notification.type) {
    Notification.Type.None -> TrayIcon.MessageType.NONE
    Notification.Type.Info -> TrayIcon.MessageType.INFO
    Notification.Type.Warning -> TrayIcon.MessageType.WARNING
    Notification.Type.Error -> TrayIcon.MessageType.ERROR
  }

  displayMessage(notification.title, notification.message, messageType)
}

