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
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.rememberNotification
import component.MyIconButton
import component.MyTextField
import core.Platform
import i18n.lang.Lang
import i18n.lang.LangEnum
import icons.rememberArrowOutward
import icons.rememberGithub
import icons.rememberHelp
import icons.rememberSensorOccupied
import model.*
import org.tinylog.kotlin.Logger
import java.awt.Desktop
import java.awt.event.KeyEvent
import java.io.File
import java.net.URI
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess


@Preview
@Composable
fun Setting(store: AppStore) {
  val desktop = Desktop.getDesktop()
  Column(
    modifier = Modifier.padding(horizontal = 24.dp),
    verticalArrangement = Arrangement.spacedBy(3.dp)
  ) {
    Divider(color = MaterialTheme.colors.onSecondary, thickness = 1.dp)
    TitleInfo(store, desktop)
    Spacer(Modifier.height(12.dp))
    SystemInfo()
    Spacer(Modifier.height(12.dp))
    LanguageSelect(store)
    Spacer(Modifier.height(10.dp))
    ThemeSelect(store)
    Spacer(Modifier.height(6.dp))
    KeyboardField(store)
    Spacer(Modifier.height(6.dp))
    RefreshField(store)
    Spacer(Modifier.height(12.dp))
    ShowNotice(store)
    Spacer(Modifier.height(12.dp))
    Unknown(store)
    Spacer(Modifier.height(12.dp))
    External(store, desktop)
  }

}

@Composable
private fun TitleInfo(store: AppStore, desktop: Desktop) {
  val file = File(LOGGER_PATH)
  val errorTip = rememberNotification("Can not find log file! $LOGGER_PATH", "", Notification.Type.Error)
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
      Text(Information.app.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colors.onPrimary)
      Text(Information.app.updateDate, fontSize = MaterialTheme.typography.caption.fontSize, color = MaterialTheme.colors.onSecondary)
    }
    OutlinedButton(
      onClick = {
        if (file.exists()) {
          desktop.open(file)
        } else {
          store.sendNotification(errorTip)
        }
      },
      elevation = null,
      border = BorderStroke(1.dp, MaterialTheme.colors.onPrimary),
      colors = ButtonDefaults.outlinedButtonColors(
        contentColor = MaterialTheme.colors.onPrimary
      ),
      modifier = Modifier
        .pointerHoverIcon(PointerIcon.Hand)
    ) {
      Text(LocalLanguage.current.ui.showLog, fontSize = MaterialTheme.typography.caption.fontSize, color = MaterialTheme.colors.onPrimary)
    }
  }
}

@Composable
private fun SystemInfo() {
  Text(
    Platform.osName,
    fontSize = MaterialTheme.typography.subtitle2.fontSize,
    color = MaterialTheme.colors.onPrimary
  )
}

@Composable
private fun LanguageSelect(store: AppStore) {
  val dropdownMenuState = rememberSaveable { mutableStateOf(false) }
  Row {
    Text("${LocalLanguage.current.ui.language}：", fontSize = MaterialTheme.typography.subtitle2.fontSize)
    Box(
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(
        store.config.language.displayName,
        fontSize = MaterialTheme.typography.subtitle2.fontSize,
        color = MaterialTheme.colors.onPrimary,
        modifier = Modifier.clickable {
          dropdownMenuState.value = !dropdownMenuState.value
        }.pointerHoverIcon(PointerIcon.Hand)
      )
      DropdownMenu(
        expanded = dropdownMenuState.value,
        onDismissRequest = { dropdownMenuState.value = false },
        content = {
          LangEnum.entries.forEach {
            DropdownMenuItem(
              onClick = {
                dropdownMenuState.value = !dropdownMenuState.value
                store.configLanguage(it)
              },
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
              content = {
                Text(text = it.displayName, fontSize = MaterialTheme.typography.subtitle2.fontSize, color = MaterialTheme.colors.onPrimary)
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
    Text("${LocalLanguage.current.ui.theme}：", fontSize = MaterialTheme.typography.subtitle2.fontSize, color = MaterialTheme.colors.onPrimary)
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
            LocalLanguage.current.ui.themeOption.getOrDefault(it.name.lowercase(), UNKNOWN),
            color = if (it == store.config.theme) {
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
      Text(LocalLanguage.current.ui.keyboard, color = MaterialTheme.colors.onSecondary, fontSize = 12.sp)
    },
    singleLine = true,
    contentPadding = TextFieldDefaults.textFieldWithoutLabelPadding(
      top = 0.dp,
      bottom = 8.dp,
      start = 0.dp
    ),
    minHeight = 24.dp,
    textStyle = TextStyle(fontSize = MaterialTheme.typography.subtitle1.fontSize),
    modifier = Modifier.onKeyEvent {
      if (it.key == Key.Tab) {
        return@onKeyEvent false
      }
      when (it.type) {
        KeyEventType.KeyDown -> {
          pressedKeys.add(it.key)
          store.configKeyboard(pressedKeys.joinToString(" ") { key ->
            val keyText = KeyEvent.getKeyText(key.nativeKeyCode)
              .replace("⌥", "alt")
              .replace("⇧", "shift")
              .replace("⌘", "meta")
              .replace("⌃", "ctrl")
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
      true
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
      Text(LocalLanguage.current.ui.refreshTime, color = MaterialTheme.colors.onSecondary, fontSize = 12.sp)
    },
    singleLine = true,
    contentPadding = TextFieldDefaults.textFieldWithoutLabelPadding(
      top = 0.dp,
      bottom = 8.dp,
      start = 0.dp
    ),
    minHeight = 24.dp,
    textStyle = TextStyle(fontSize = MaterialTheme.typography.subtitle1.fontSize)
  )
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun ShowNotice(store: AppStore) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
      Switch(
        checked = store.config.showNotification,
        onCheckedChange = { store.configShowNotice(it) },
        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colors.primary),
        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
      )
    }
    Spacer(Modifier.width(8.dp))
    Text(LocalLanguage.current.ui.showNotice, fontSize = 14.sp, color = MaterialTheme.colors.onPrimary)
  }
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
        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colors.primary),
        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
      )
    }
    Spacer(Modifier.width(8.dp))
    Text(LocalLanguage.current.ui.unknown, fontSize = 14.sp, color = MaterialTheme.colors.onPrimary)
    Spacer(Modifier.width(4.dp))
    TooltipArea(
      tooltip = {
        Surface(
          modifier = Modifier.shadow(4.dp).background(color = MaterialTheme.colors.onBackground),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            text = LocalLanguage.current.tip.unknownHelp,
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
private fun External(store: AppStore, desktop: Desktop) {
  val currentLanguage = LocalLanguage.current
  val externalInfoList = mutableListOf(
    ExternalInfo(
      currentLanguage.links.sourceCode,
      prefixIcon = {
        Icon(
          imageVector = rememberGithub(),
          tint = Color(68, 122, 227),
          contentDescription = "to link github",
          modifier = Modifier.size(16.dp)
        )
      },
      suffixIcon = {
        Icon(
          imageVector = rememberArrowOutward(),
          tint = Color(68, 122, 227),
          contentDescription = "to link github",
          modifier = Modifier.size(16.dp)
        )
      },
      onClick = {
        desktop.browse(URI.create("https://github.com/lizhongyue248/PortView"))
      }
    ),
  )
  if (!Platform.isDebug && Platform.isWindows) {
    externalInfoList.add(
      ExternalInfo(
        currentLanguage.ui.elevate,
        onClick = { elevateApplication(store, currentLanguage) },
        prefixIcon = {
          Icon(
            imageVector = rememberSensorOccupied(),
            tint = Color(68, 122, 227),
            contentDescription = "to admin",
            modifier = Modifier.size(16.dp)
          )
        },
      )
    )
  }
  externalInfoList
    .forEach {
      Spacer(Modifier.height(6.dp))
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
          .onClick(onClick = it.onClick)
      ) {
        it.prefixIcon()
        Spacer(Modifier.width(8.dp))
        Text(
          it.title,
          color = Color(68, 122, 227),
          fontSize = MaterialTheme.typography.subtitle2.fontSize
        )
        it.suffixIcon()
      }
    }

}

private fun elevateApplication(store: AppStore, currentLanguage: Lang) {
  val elevateFile = File(ELEVATE_PATH)
  val exeFile = File(System.getProperty("user.dir") + File.separatorChar + "PortView.exe")
  if (!exeFile.exists()) {
    Logger.warn("${exeFile.absolutePath} not exist")
    store.sendWarn(currentLanguage.tip.errorNoExecute)
    return
  }
  if (!elevateFile.exists()) {
    Logger.warn("${elevateFile.absolutePath} not exist")
    store.sendWarn(currentLanguage.tip.errorNoElevate)
    return
  }
  val processBuilder = ProcessBuilder(elevateFile.absolutePath, exeFile.absolutePath)
  if (processBuilder.start().waitFor(60, TimeUnit.SECONDS)) {
    exitProcess(0)
  } else {
    store.sendWarn(currentLanguage.tip.errorNoRun)
  }
}