package ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import i18n.Locale
import i18n.lang.LangEnum

val LocalLanguage = compositionLocalOf { Locale.getDefaultLang() }

@Composable
fun PortViewTheme(
  darkTheme: Boolean = false,
  lang: LangEnum = LangEnum.ZH,
  content: @Composable() () -> Unit
) {
  val colors = if (darkTheme) {
    DarkColorPalette // 将 primary 设置为蓝色
  } else {
    LightColorPalette // 将 primary 设置为红色
  }
  CompositionLocalProvider(LocalLanguage provides Locale.langMap.getOrDefault(lang, Locale.getDefaultLang())) {
    MaterialTheme(
      colors = colors,
      content = content
    )
  }
}

private val DarkColorPalette = darkColors(
  primary = Color(22, 125, 255),
  onPrimary = Color.White,
  onSecondary = Color(0xff9e9e9e),
  onBackground = Color.White
)
private val LightColorPalette = lightColors(
  primary = Color(22, 125, 255),
  error = Color(0xf4fc5959),
  onPrimary = Color.Black,
  onSecondary = Color.LightGray,
  onBackground = Color.Black
)