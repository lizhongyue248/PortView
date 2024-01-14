package ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.TrayState
import androidx.compose.ui.window.rememberTrayState
import androidx.compose.ui.window.setContent
import component.MyIconButton
import core.TestTag
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import model.AppStore
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.tinylog.kotlin.Logger
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent


@Composable
internal fun TopBar(store: AppStore) {
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
          tint = MaterialTheme.colors.onSecondary,
          contentDescription = "Close Window",
          imageVector = Icons.Filled.Close
        )
      }
    }
  }
}

@Composable
internal fun BottomNav(selectedItem: MutableState<Int>) {
  BottomNavigation(
    backgroundColor = MaterialTheme.colors.background
  ) {
    listOf(LocalLanguage.current.ui.homeNav, LocalLanguage.current.ui.settingNav).forEachIndexed { index, item ->
      BottomNavigationItem(
        selectedContentColor = MaterialTheme.colors.primary,
        unselectedContentColor = MaterialTheme.colors.onPrimary,
        icon = {
          when (index) {
            0 -> Icon(Icons.Outlined.Home, contentDescription = null)
            else -> Icon(Icons.Outlined.Settings, contentDescription = null)
          }
        },
        label = { Text(item) },
        selected = selectedItem.value == index,
        onClick = { selectedItem.value = index }
      )
    }
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
    Logger.info("Success launch tray.")
    onDispose {
      menuComposition.dispose()
      SystemTray.getSystemTray().remove(tray)
      Logger.info("Success remove tray.")
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
        .testTag(TestTag.SEARCH_INPUT)
        .padding(start = 24.dp, end = 24.dp, bottom = 6.dp)
        .fillMaxWidth()
        .height(36.dp),
      cursorBrush = SolidColor(MaterialTheme.colors.onPrimary),
      singleLine = true,
      textStyle = TextStyle(color = MaterialTheme.colors.onPrimary)
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
        placeholder = {
          Text(LocalLanguage.current.tip.search, color = MaterialTheme.colors.onSecondary)
        },
        interactionSource = interactionSource,
        visualTransformation = VisualTransformation.None,
        contentPadding = TextFieldDefaults.textFieldWithoutLabelPadding(
          top = 0.dp,
          bottom = 0.dp
        ),
        trailingIcon = {
          MyIconButton(
            onClick = store::updateItems,
            enabled = !store.state.loading
          ) {
            if (store.state.loading) {
              Icon(Icons.Filled.Refresh, "Search")
            } else {
              Icon(Icons.Filled.Search, "Search")
            }
          }
        }
      )
    }
  }
}