package model

import androidx.compose.ui.graphics.vector.ImageVector

data class ExternalLink(
  val title: String,
  val link: String,
  val icon: ImageVector
)