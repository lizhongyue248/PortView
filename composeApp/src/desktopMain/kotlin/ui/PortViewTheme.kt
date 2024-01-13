package ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun PortViewTheme(
  darkTheme: Boolean = false,
  content: @Composable() () -> Unit
) {
  val colors = if (darkTheme) {
    DarkColorPalette // 将 primary 设置为蓝色
  } else {
    LightColorPalette // 将 primary 设置为红色
  }
  MaterialTheme(
    colors = colors,
    content = content
  )
}

private val DarkColorPalette = darkColors(
  primary = Color(22, 125, 255),
  onPrimary = Color.White,
  onSecondary = Color.LightGray,
  onBackground = Color.White
)
private val LightColorPalette = lightColors(
  primary = Color(22, 125, 255),
  error = Color(0xf4fc5959),
  onPrimary = Color.Black,
  onSecondary = Color.LightGray,
  onBackground = Color.Black
)