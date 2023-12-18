import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun Content() {
  val portStrategy = getPortStrategy()
  var list by remember { mutableStateOf(portStrategy.portList()) }
  val state = rememberLazyListState()
  Box {
    LazyColumn(Modifier.fillMaxSize(), state = state) {
      items(list) { item ->
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clickable {
              println("Hello")
            }
            .pointerHoverIcon(PointerIcon.Hand)
            .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
          Image(
            painter = if (item.image === null) painterResource("logo-ghost.png") else item.image.toPainter(),
            contentDescription = "logo",
            modifier = Modifier.width(24.dp)
              .height(24.dp)
              .padding(end = 8.dp)
          )
          Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = item.name, fontSize = 12.sp)
            Text(
              text = item.command,
              modifier = Modifier.fillMaxWidth(),
              overflow = TextOverflow.Ellipsis,
              fontSize = 10.sp,
              color = Color.LightGray,
              maxLines = 2
            )
          }
          Text(
            modifier = Modifier.width(70.dp).padding(start = 10.dp),
            text = ":${item.port}",
            fontSize = 16.sp,
            fontWeight = SemiBold
          )
        }
      }
    }

    VerticalScrollbar(
      rememberScrollbarAdapter(state),
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .fillMaxHeight()
    )
    LaunchedEffect(true) {
      while (true) {
        list = portStrategy.portList()
        delay(5000)
        println("Update success.")
      }
    }
  }
}
