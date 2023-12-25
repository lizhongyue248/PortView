import TestTag.Companion.SEARCH_INPUT
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import component.MyDialogWindow
import component.MyIconButton
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

@OptIn(ExperimentalResourceApi::class)
fun main() = application {
  val store = remember { AppStore() }
  val state = store.state
  val trayState = rememberTrayState()
  var positionX by remember { mutableStateOf(50.dp) }
  var positionY by remember { mutableStateOf(50.dp) }
  var selectedItem by remember { mutableStateOf(0) }
  val items = listOf("Home", "Setting")
  MyDialogWindow(
    onCloseRequest = store::hidden,
    visible = state.isVisible,
    alwaysOnTop = true,
    undecorated = true,
    transparent = true,
    resizable = false,
    icon = painterResource("icon.png"),
    state = DialogState(
      width = 420.dp, height = 700.dp,
      position = WindowPosition(positionX, positionY)
    ),
  ) {
    MaterialTheme {
      Scaffold(
        modifier = Modifier.background(Color.Transparent)
          .fillMaxSize()
          .padding(top = 10.dp, start = 20.dp, end = 20.dp, bottom = 10.dp)
          .shadow(20.dp, RoundedCornerShape(5.dp)),
        topBar = {
          WindowDraggableArea {
            Column {
              Row(
                modifier = Modifier.fillMaxWidth()
                  .padding(top = 12.dp, bottom = 6.dp, start = 24.dp, end = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Port View",
                  style = MaterialTheme.typography.h6,
                  fontWeight = FontWeight.Bold,
                  fontSize = 24.sp,
                  lineHeight = 36.sp
                )
                MyIconButton(onClick = store::hidden) {
                  Icon(
                    tint = Color.LightGray,
                    contentDescription = "Close Window",
                    imageVector = Icons.Filled.Close
                  )
                }
              }
            }
          }
        },
        bottomBar = {
          BottomNavigation(
            backgroundColor = Color.White
          ) {
            items.forEachIndexed { index, item ->
              BottomNavigationItem(
                selectedContentColor = Color.Blue,
                unselectedContentColor = Color.Black,
                icon = {
                  when (index) {
                    0 -> Icon(Icons.Outlined.Home, contentDescription = null)
                    else -> Icon(Icons.Outlined.Settings, contentDescription = null)
                  }
                },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = { selectedItem = index }
              )
            }
          }
        }
      ) {
        if (selectedItem == 0) {
          Column {
            SearchField(store)
            Content(store)
          }
        } else {
          Setting()
        }
      }
    }

    TraySetting(state = trayState, exit = { exitApplication() }, onAction = { x, y ->
      positionX = x
      positionY = y
      store.visibleToggle()
    })

  }

  val notification = rememberNotification("Port view setup success!", "")
  LaunchedEffect(Unit) {
    trayState.sendNotification(notification)
    store.setTimer()
  }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalResourceApi::class)
@Composable
fun TraySetting(
  state: TrayState = rememberTrayState(),
  exit: () -> Unit,
  onAction: (x: Dp, y: Dp) -> Unit
) {
  val image = painterResource("icon.png").toAwtImage(
    density = Density(1f, 1f),
    layoutDirection = LayoutDirection.Ltr
  )
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


@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun SearchField(store: AppStore) {
  Row {
    BasicTextField(
      value = store.state.searchText,
      onValueChange = store::changeSearchText,
      modifier = Modifier
        .testTag(SEARCH_INPUT)
        .padding(start = 24.dp, end = 24.dp, bottom = 6.dp)
        .fillMaxWidth()
        .height(36.dp),
      singleLine = true,
    ) { innerTextField ->
      val interactionSource = remember { MutableInteractionSource() }
      TextFieldDefaults.OutlinedTextFieldDecorationBox(
        value = store.state.searchText,
        innerTextField = innerTextField,
        enabled = true,
        border = {
          TextFieldDefaults.BorderBox(
            enabled = true, isError = false,
            interactionSource,
            TextFieldDefaults.outlinedTextFieldColors(),
            shape = RoundedCornerShape(4.dp)
          )
        },
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
        placeholder = {
          Text("Input port or name", color = Color.LightGray)
        },
        interactionSource = interactionSource,
        visualTransformation = VisualTransformation.None,
        contentPadding = TextFieldDefaults.textFieldWithoutLabelPadding(
          top = 0.dp,
          bottom = 0.dp
        ),
        trailingIcon = {
          MyIconButton(
            onClick = { },
          ) {
            Icon(Icons.Filled.Search, "Search")
          }
        }
      )
    }
  }
}
