package ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import core.PortInfo
import core.TestTag
import core.getActionStrategy
import model.AppStore

@Composable
fun Content(store: AppStore) {
  val appState = store.state
  val lazyListState = rememberLazyListState()
  val confirmDialog = remember { mutableStateOf(false) }
  val currentProcess = remember { mutableStateOf<PortInfo?>(null) }
  Box(modifier = Modifier.padding(bottom = 64.dp)) {
    LazyColumn(Modifier.testTag(TestTag.PORT_LIST).fillMaxSize(), state = lazyListState) {
      items(appState.list) { item ->
        PortItem(item, confirmDialog, currentProcess)
      }
    }
    VerticalScrollbar(
      rememberScrollbarAdapter(lazyListState),
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .fillMaxHeight()
    )
  }

  if (confirmDialog.value) {
    Alter(confirmDialog, currentProcess, store)
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
      Text(text = item.name, fontSize = 14.sp, color = MaterialTheme.colors.onPrimary)
      Text(
        text = item.command,
        modifier = Modifier.fillMaxWidth(),
        overflow = TextOverflow.Ellipsis,
        fontSize = 12.sp,
        color = MaterialTheme.colors.onSecondary,
        maxLines = 2
      )
    }
    Text(
      modifier = Modifier
        .width(70.dp).padding(start = 10.dp),
      text = ":${item.port}",
      fontSize = 18.sp,
      fontWeight = SemiBold,
      textAlign = TextAlign.End,
      color = MaterialTheme.colors.onPrimary
    )
  }
}

@Composable
private fun Alter(confirmDialog: MutableState<Boolean>, currentProcess: MutableState<PortInfo?>, store: AppStore) {
  AlertDialog(
    onDismissRequest = {
      confirmDialog.value = false
    },
    title = {
      Text(text = "Confirm ${currentProcess.value?.name}")
    },
    text = {
      Text("Do you want to close port :${currentProcess.value?.port} process?")
    },
    confirmButton = {
      val errorTip = rememberNotification("Port view kill error!", "", Notification.Type.Error)

      Button(
        onClick = {
          val result = getActionStrategy().closeProcess(currentProcess.value?.pid)
          if (result.first) {
            store.updateItems()
          } else {
            store.state.trayState.sendNotification(errorTip.copy(message = result.second))
          }
          confirmDialog.value = false
        }) {
        Text("Confirm", color = Color.White)
      }
    },
    dismissButton = {
      Button(
        colors = ButtonDefaults.buttonColors(
          backgroundColor = MaterialTheme.colors.error
        ),
        onClick = {
          confirmDialog.value = false
        }) {
        Text("Cancel", color = Color.White)
      }
    }
  )
}
