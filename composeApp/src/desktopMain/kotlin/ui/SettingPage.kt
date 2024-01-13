package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import component.MyIconButton
import component.MyTextField
import icons.GithubMark
import icons.rememberArrowOutward
import icons.rememberHelp
import model.AppStore
import java.awt.event.KeyEvent

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Preview
@Composable
fun Setting(store: AppStore) {
  Column(
    modifier = Modifier.padding(horizontal = 24.dp),
    verticalArrangement = Arrangement.spacedBy(3.dp)
  ) {
    Divider(color = Color.LightGray, thickness = 1.dp)
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(top = 12.dp)
    ) {
      Image(
        painter = painterResource("icon.png"),
        contentDescription = "icon",
        modifier = Modifier.size(64.dp)
      )
      Column(
        modifier = Modifier
          .weight(1f)
          .padding(start = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        Text("Port View", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("2023.03.01", fontSize = 12.sp, color = Color.LightGray)
      }
      OutlinedButton(
        onClick = {},
        elevation = null,
        border = BorderStroke(1.dp, Color.Black),
        colors = ButtonDefaults.outlinedButtonColors(
          contentColor = Color.Black
        )
      ) {
        Text("显示日志文件", fontSize = 12.sp)
      }
    }
    Spacer(modifier = Modifier.height(12.dp))

    val fontSize = 14.sp

    Text("Windows 11", fontSize = fontSize)
    val dropdownMenuState = rememberSaveable { mutableStateOf(false) }
    val languageList = mutableListOf(
      "简体中文",
      "English"
    )
    val selectType = rememberSaveable { mutableStateOf("简体中文") }

    Spacer(modifier = Modifier.height(12.dp))
    Row {
      Text("语言：", fontSize = fontSize)
      Box(
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(selectType.value,
          fontSize = fontSize,
          modifier = Modifier.clickable {
            dropdownMenuState.value = !dropdownMenuState.value
          })
        DropdownMenu(
          expanded = dropdownMenuState.value,
          onDismissRequest = { dropdownMenuState.value = false },
          content = {
            languageList.forEach {
              DropdownMenuItem(
                onClick = {
                  dropdownMenuState.value = !dropdownMenuState.value
                  selectType.value = it
                },
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                content = {
                  Text(text = it, fontSize = fontSize)
                }
              )
            }
          }
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))
    Row(
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("主题：", fontSize = fontSize)
      OutlinedButton(
        onClick = {},
        elevation = null,
        modifier = Modifier
          .size(64.dp, 24.dp),
        shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp),
        colors = ButtonDefaults.textButtonColors(contentColor = Color.Black),
        contentPadding = PaddingValues()
      ) {
        Text("浅色")
      }
      OutlinedButton(
        onClick = {},
        elevation = null,
        enabled = false,
        modifier = Modifier
          .size(64.dp, 24.dp),
        shape = RoundedCornerShape(0.dp),
        colors = ButtonDefaults.textButtonColors(
          backgroundColor = Color(22, 125, 255),
          contentColor = Color.Black,
          disabledContentColor = Color.White
        ),
        contentPadding = PaddingValues(0.dp)
      ) {
        Text("系统")
      }
      OutlinedButton(
        onClick = {},
        elevation = null,
        modifier = Modifier
          .size(64.dp, 24.dp),
        shape = RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp),
        colors = ButtonDefaults.textButtonColors(contentColor = Color.Black),
        contentPadding = PaddingValues()
      ) {
        Text("暗色")
      }
    }

    val pressedKeys by remember { mutableStateOf(linkedSetOf<Key>()) }

    Spacer(modifier = Modifier.height(6.dp))
    MyTextField(
      value = store.config.keyboard,
      onValueChange = {  },
      colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
      label = {
        Text("打开 Port View 的全局快捷键", color = Color.LightGray, fontSize = 12.sp)
      },
      singleLine = true,
      contentPadding = TextFieldDefaults.textFieldWithoutLabelPadding(
        top = 0.dp,
        bottom = 8.dp,
        start = 0.dp
      ),
      minHeight = 24.dp,
      textStyle = TextStyle(fontSize = 16.sp),
      modifier = Modifier.onKeyEvent {
        when (it.type) {
          KeyEventType.KeyDown -> {
            pressedKeys.add(it.key)
            store.configKeyboard(pressedKeys.joinToString(" ") { key ->
              val keyText = KeyEvent.getKeyText(key.nativeKeyCode)
              if (keyText.length > 1) {
                keyText.lowercase()
              } else {
                keyText.uppercase()
              }
            })
          }
          KeyEventType.KeyUp -> {
            if (pressedKeys.size > 1) {
              store.updateKeyboard()
            }
            pressedKeys.clear()
          }
        }
        false
      }
    )

    Spacer(modifier = Modifier.height(6.dp))

    MyTextField(
      value = store.config.refreshTime.toString(),
      onValueChange = {
        val time = it.toIntOrNull()
        if (time == null) {
          store.configRefreshTime(5)
        } else {
          store.configRefreshTime(time)
        }
      },
      colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
      label = {
        Text("进程列表刷新间隔时间（s）", color = Color.LightGray, fontSize = 12.sp)
      },
      singleLine = true,
      contentPadding = TextFieldDefaults.textFieldWithoutLabelPadding(
        top = 0.dp,
        bottom = 8.dp,
        start = 0.dp
      ),
      minHeight = 24.dp,
      textStyle = TextStyle(fontSize = 16.sp),
      modifier = Modifier.onFocusChanged {
        println("${it.isFocused} ${it.isCaptured}")
      }
    )
    Spacer(Modifier.height(12.dp))

    var checkedState by remember { mutableStateOf(true) }
    Row(
      verticalAlignment = Alignment.CenterVertically
    ) {
      CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
        Switch(
          checked = checkedState,
          onCheckedChange = { checkedState = it }
        )
      }
      Spacer(Modifier.width(8.dp))
      Text("是否显示 Unknown 进程", fontSize = 14.sp)
      Spacer(Modifier.width(4.dp))
      TooltipArea(
        tooltip = {
          Surface(
            modifier = Modifier.shadow(4.dp),
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = "部分进程属于受系统保护的进程，我们无法获取到它们的具体信息。",
              modifier = Modifier.padding(10.dp).widthIn(0.dp, 150.dp)
            )
          }
        },
        delayMillis = 600,
        tooltipPlacement = TooltipPlacement.CursorPoint(
          alignment = Alignment.TopCenter,
          offset = DpOffset(0.dp, (-10).dp)
        )
      ) {
        MyIconButton(onClick = {}, modifier = Modifier.size(18.dp)) {
          Icon(
            tint = Color.LightGray,
            contentDescription = "Help",
            imageVector = rememberHelp(),
            modifier = Modifier.height(12.dp)
          )
        }
      }

    }
    Spacer(Modifier.height(12.dp))
    Row(
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = GithubMark,
        tint = Color(68, 122, 227),
        contentDescription = "Github",
        modifier = Modifier.size(16.dp)
      )
      Spacer(Modifier.width(8.dp))
      Text(
        "Source Code",
        color = Color(68, 122, 227),
        fontSize = 14.sp
      )
      Icon(
        imageVector = rememberArrowOutward(),
        tint = Color(68, 122, 227),
        contentDescription = "Github Link",
        modifier = Modifier.size(16.dp)
      )
    }
  }

}