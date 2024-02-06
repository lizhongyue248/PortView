package ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.rememberNotification
import core.Platform
import core.PortInfo
import core.TestTag
import core.TestTag.Companion.PORT_SCROLLBAR
import model.AppStore
import model.UNKNOWN
import org.apache.commons.lang3.StringUtils
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

@Composable
fun Content(store: AppStore) {
  val lazyListState = rememberSaveable(saver = LazyListState.Saver) { LazyListState(0, 0) }
  val confirmDialog = remember { mutableStateOf(false) }
  val currentProcess = remember { mutableStateOf<PortInfo?>(null) }
  val i18n = LocalLanguage.current
  val contextMenuRepresentation = if (store.isDarkTheme()) {
    DarkDefaultContextMenuRepresentation
  } else {
    LightDefaultContextMenuRepresentation
  }
  Box(modifier = Modifier.padding(bottom = 64.dp)) {
    LazyColumn(Modifier.testTag(TestTag.PORT_LIST).fillMaxSize(), state = lazyListState) {
      items(store.state.list, key = { "${it.port}-${it.name}" }) { item ->
        CompositionLocalProvider(LocalContextMenuRepresentation provides contextMenuRepresentation) {
          ContextMenuArea(
            items = {
              listOf(
                ContextMenuItem(i18n.tip.openPath) {
                  if (StringUtils.equalsIgnoreCase(item.name, UNKNOWN)) {
                    return@ContextMenuItem
                  }
                  Platform.actionStrategy.open(item.path)
                },
                ContextMenuItem(i18n.tip.copy) {
                  val clipboard = Toolkit.getDefaultToolkit().systemClipboard
                  val selection = StringSelection(item.command)
                  clipboard.setContents(selection, null)
                },
                ContextMenuItem("${item.address}:${item.port}") {},
                ContextMenuItem("PID - ${item.pid}") {},
              )
            }
          ) { PortItem(item, confirmDialog, currentProcess) }
        }
      }
    }
    VerticalScrollbar(
      rememberScrollbarAdapter(lazyListState),
      modifier = Modifier
        .align(Alignment.CenterEnd).fillMaxHeight()
        .testTag(PORT_SCROLLBAR)
    )
  }
  if (confirmDialog.value) {
    Alert(confirmDialog, currentProcess, store)
  }
}

@Composable
private fun PortItem(item: PortInfo, confirmDialog: MutableState<Boolean>, currentProcess: MutableState<PortInfo?>) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .testTag(TestTag.PORT_ITEM(item.port))
      .clickable {
        confirmDialog.value = true
        currentProcess.value = item
      }
      .pointerHoverIcon(PointerIcon.Hand)
      .padding(horizontal = 24.dp, vertical = 6.dp)
  ) {
    Box(
      modifier = Modifier
        .padding(end = 8.dp)
        .size(36.dp)
        .clip(CircleShape)
        .background(Color.White)
        .shadow(8.dp, CircleShape)
    ) {
      Image(
        painter = if (item.image === null) painterResource("logo-ghost.png") else item.image.toPainter(),
        contentDescription = "logo",
        modifier = Modifier.fillMaxSize()
          .padding(2.dp)
          .background(Color.White),
      )
    }
    Column(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = item.name,
          fontSize = MaterialTheme.typography.body2.fontSize,
          color = MaterialTheme.colors.onPrimary
        )
      }
      Text(
        text = item.path,
        modifier = Modifier.fillMaxWidth(),
        overflow = TextOverflow.Ellipsis,
        fontSize = MaterialTheme.typography.caption.fontSize,
        color = MaterialTheme.colors.onSecondary,
        maxLines = 2
      )
    }
    Text(
      modifier = Modifier
        .width(90.dp).padding(start = 10.dp),
      text = ":${item.port}",
      fontSize = 18.sp,
      fontWeight = SemiBold,
      textAlign = TextAlign.End,
      color = MaterialTheme.colors.onPrimary
    )
  }
}

@Composable
private fun Alert(confirmDialog: MutableState<Boolean>, currentProcess: MutableState<PortInfo?>, store: AppStore) {
  AlertDialog(
    modifier = Modifier.testTag(TestTag.CLOSE_ALERT),
    onDismissRequest = { confirmDialog.value = false },
    title = { Text(text = LocalLanguage.current.tip.killTitle) },
    text = { Text(LocalLanguage.current.tip.kill.format(currentProcess.value?.name, currentProcess.value?.port)) },
    confirmButton = {
      val errorTip = rememberNotification(LocalLanguage.current.tip.killError, "", Notification.Type.Error)
      Button(
        modifier = Modifier.testTag(TestTag.CLOSE_ALERT_CONFIRM),
        onClick = {
          val result = Platform.actionStrategy.closeProcess(currentProcess.value?.pid)
          if (result.first) {
            store.updateItems()
          } else {
            store.sendNotification(errorTip.copy(message = result.second))
          }
          confirmDialog.value = false
        }) {
        Text(LocalLanguage.current.ui.confirm, color = Color.White)
      }
    },
    dismissButton = {
      Button(
        modifier = Modifier.testTag(TestTag.CLOSE_ALERT_CANCEL),
        colors = ButtonDefaults.buttonColors(
          backgroundColor = MaterialTheme.colors.error
        ),
        onClick = {
          confirmDialog.value = false
        }) {
        Text(LocalLanguage.current.ui.cancel, color = Color.White)
      }
    }
  )
}
