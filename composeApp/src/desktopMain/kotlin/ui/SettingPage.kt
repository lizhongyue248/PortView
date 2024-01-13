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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
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
import model.ExternalLink
import model.ThemeOption
import java.awt.Desktop
import java.awt.event.KeyEvent
import java.io.File
import java.net.URI


val fontSize = 14.sp

@Preview
@Composable
fun Setting(store: AppStore) {
  val desktop = Desktop.getDesktop()
  Column(
    modifier = Modifier.padding(horizontal = 24.dp),
    verticalArrangement = Arrangement.spacedBy(3.dp)
  ) {
    Divider(color = MaterialTheme.colors.onSecondary, thickness = 1.dp)
    TitleInfo(desktop = desktop)
    Spacer(Modifier.height(12.dp))
    SystemInfo()
    Spacer(Modifier.height(12.dp))
    LanguageSelect()
    Spacer(Modifier.height(10.dp))
    ThemeSelect(store)
    Spacer(Modifier.height(6.dp))
    KeyboardField(store)
    Spacer(Modifier.height(6.dp))
    RefreshField(store)
    Spacer(Modifier.height(12.dp))
    Unknown(store)
    Spacer(Modifier.height(12.dp))
    Links(desktop = desktop)
  }

}

@Composable
private fun TitleInfo(desktop: Desktop) {
  val folder = File("./logs")
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
      Text("Port View", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colors.onPrimary)
      Text("2023.03.01", fontSize = 12.sp, color = MaterialTheme.colors.onSecondary)
    }
    OutlinedButton(
      onClick = { desktop.open(folder) },
      elevation = null,
      border = BorderStroke(1.dp, MaterialTheme.colors.onPrimary),
      colors = ButtonDefaults.outlinedButtonColors(
        contentColor = MaterialTheme.colors.onPrimary
      ),
      modifier = Modifier
        .pointerHoverIcon(PointerIcon.Hand)
    ) {
      Text("显示日志文件", fontSize = 12.sp, color = MaterialTheme.colors.onPrimary)
    }
  }
}

@Composable
private fun SystemInfo() {
  Text("Windows 11", fontSize = fontSize, color = MaterialTheme.colors.onPrimary)
}

@Composable
private fun LanguageSelect() {
  val selectType = rememberSaveable { mutableStateOf("简体中文") }
  val dropdownMenuState = rememberSaveable { mutableStateOf(false) }
  val languageList = mutableListOf(
    "简体中文",
    "English"
  )
  Row {
    Text("语言：", fontSize = fontSize)
    Box(
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(
        selectType.value,
        fontSize = fontSize,
        color = MaterialTheme.colors.onPrimary,
        modifier = Modifier.clickable {
          dropdownMenuState.value = !dropdownMenuState.value
        }.pointerHoverIcon(PointerIcon.Hand)
      )
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
                Text(text = it, fontSize = fontSize, color = MaterialTheme.colors.onPrimary)
              },
              modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
            )
          }
        }
      )
    }
  }
}

@Composable
private fun ThemeSelect(store: AppStore) {
  Row(
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text("主题：", fontSize = fontSize, color = MaterialTheme.colors.onPrimary)
    listOf(ThemeOption.LIGHT, ThemeOption.SYSTEM, ThemeOption.DARK)
      .forEach {
        OutlinedButton(
          onClick = {
            store.configTheme(it)
          },
          elevation = null,
          modifier = Modifier
            .background(
              color = if (it == store.config.theme) {
                MaterialTheme.colors.primary
              } else {
                MaterialTheme.colors.background
              }
            )
            .size(64.dp, 24.dp)
            .pointerHoverIcon(PointerIcon.Hand),
          shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp),
          colors = ButtonDefaults.textButtonColors(contentColor = Color.Black),
          contentPadding = PaddingValues(),
        ) {
          Text(
            it.text, color = if (it == store.config.theme) {
              Color.White
            } else {
              MaterialTheme.colors.onPrimary
            }
          )
        }
      }
  }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun KeyboardField(store: AppStore) {
  val pressedKeys by remember { mutableStateOf(linkedSetOf<Key>()) }
  MyTextField(
    value = store.config.keyboard,
    onValueChange = { },
    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
    label = {
      Text("打开 Port View 的全局快捷键", color = MaterialTheme.colors.onSecondary, fontSize = 12.sp)
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
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun RefreshField(store: AppStore) {
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
      Text("进程列表刷新间隔时间（s）", color = MaterialTheme.colors.onSecondary, fontSize = 12.sp)
    },
    singleLine = true,
    contentPadding = TextFieldDefaults.textFieldWithoutLabelPadding(
      top = 0.dp,
      bottom = 8.dp,
      start = 0.dp
    ),
    minHeight = 24.dp,
    textStyle = TextStyle(fontSize = 16.sp)
  )
}

@Composable
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
private fun Unknown(store: AppStore) {
  Row(
    verticalAlignment = Alignment.CenterVertically
  ) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
      Switch(
        checked = store.config.showUnknown,
        onCheckedChange = { store.configShowUnknown(it) },
        colors = SwitchDefaults.colors(
          checkedThumbColor = MaterialTheme.colors.primary
        ),
        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
      )
    }
    Spacer(Modifier.width(8.dp))
    Text("是否显示 Unknown 进程", fontSize = 14.sp, color = MaterialTheme.colors.onPrimary)
    Spacer(Modifier.width(4.dp))
    TooltipArea(
      tooltip = {
        Surface(
          modifier = Modifier.shadow(4.dp).background(color = MaterialTheme.colors.onBackground),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            text = "部分进程属于受系统保护的进程，我们无法获取到它们的具体信息。",
            modifier = Modifier.padding(10.dp).widthIn(0.dp, 150.dp),
            color = MaterialTheme.colors.onPrimary
          )
        }
      },
      delayMillis = 100,
      tooltipPlacement = TooltipPlacement.CursorPoint(
        alignment = Alignment.TopCenter,
        offset = DpOffset(0.dp, (-10).dp)
      )
    ) {
      MyIconButton(onClick = {}, modifier = Modifier.size(18.dp).padding(top = 2.dp)) {
        Icon(
          tint = Color.LightGray,
          contentDescription = "Help",
          imageVector = rememberHelp(),
          modifier = Modifier.height(12.dp)
        )
      }
    }

  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Links(desktop: Desktop) {
  listOf(
    ExternalLink("Source Code", "https://github.com/lizhongyue248/PortView", GithubMark)
  )
    .forEach {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
          .onClick {
            val uri = URI.create(it.link)
            desktop.browse(uri)
          }
      ) {
        Icon(
          imageVector = it.icon,
          tint = Color(68, 122, 227),
          contentDescription = it.title,
          modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
          it.title,
          color = Color(68, 122, 227),
          fontSize = 14.sp
        )
        Icon(
          imageVector = rememberArrowOutward(),
          tint = Color(68, 122, 227),
          contentDescription = it.title,
          modifier = Modifier.size(16.dp)
        )
      }
    }

}