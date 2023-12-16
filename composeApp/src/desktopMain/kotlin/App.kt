import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun Content(hide: () -> Unit) {
  val portStrategy = getPortStrategy()
  var list by remember { mutableStateOf(portStrategy.portList()) }
  var text by remember { mutableStateOf("") }

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
        lineHeight = 30.sp
      )
      MyIconButton(onClick = { hide() }) {
        Icon(Icons.Filled.Close, "Close")
      }
    }
    searchField(text)
    LazyColumn(
      contentPadding = PaddingValues(bottom = 72.dp)
    ) {
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
            modifier = Modifier.width(24.dp).height(24.dp)
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
    LaunchedEffect(true) {
      while (true) {
        list = portStrategy.portList()
        delay(5000)
        println("Update success.")
      }
    }
  }
}

@Composable
private fun searchField(text: String) {
  var text1 = text
  Row {
    BasicTextField(
      modifier = Modifier.fillMaxWidth()
        .padding(start = 24.dp, end = 24.dp, bottom = 6.dp)
        .height(36.dp)
        .border(2.dp, Color.LightGray, RoundedCornerShape(6.dp)),
      value = text1,
      onValueChange = {
        text1 = it
      },
      singleLine = true,
      decorationBox = { innerTextField ->
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 10.dp)
        ) {
          Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
          ) {
            innerTextField()
          }
          MyIconButton(
            onClick = { },
          ) {
            Icon(Icons.Filled.Search, null)
          }
        }
      }
    )
  }
}
