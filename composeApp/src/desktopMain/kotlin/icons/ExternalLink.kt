package icons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Composable
fun rememberArrowOutward(): ImageVector {
  return remember {
    ImageVector.Builder(
      name = "arrow_outward",
      defaultWidth = 40.0.dp,
      defaultHeight = 40.0.dp,
      viewportWidth = 40.0f,
      viewportHeight = 40.0f
    ).apply {
      path(
        fill = SolidColor(Color.Black),
        fillAlpha = 1f,
        stroke = null,
        strokeAlpha = 1f,
        strokeLineWidth = 1.0f,
        strokeLineCap = StrokeCap.Butt,
        strokeLineJoin = StrokeJoin.Miter,
        strokeLineMiter = 1f,
        pathFillType = PathFillType.NonZero
      ) {
        moveTo(27.292f, 12.875f)
        lineTo(11.375f, 28.792f)
        quadToRelative(-0.375f, 0.416f, -0.917f, 0.416f)
        quadToRelative(-0.541f, 0f, -0.916f, -0.416f)
        quadToRelative(-0.417f, -0.375f, -0.417f, -0.917f)
        reflectiveQuadToRelative(0.417f, -0.958f)
        lineToRelative(15.916f, -15.875f)
        horizontalLineTo(11.375f)
        quadToRelative(-0.542f, 0f, -0.917f, -0.375f)
        reflectiveQuadToRelative(-0.375f, -0.959f)
        quadToRelative(0f, -0.541f, 0.375f, -0.937f)
        reflectiveQuadToRelative(0.917f, -0.396f)
        horizontalLineToRelative(17.25f)
        quadToRelative(0.542f, 0f, 0.937f, 0.396f)
        quadToRelative(0.396f, 0.396f, 0.396f, 0.937f)
        verticalLineToRelative(17.25f)
        quadToRelative(0f, 0.542f, -0.396f, 0.917f)
        quadToRelative(-0.395f, 0.375f, -0.937f, 0.375f)
        quadToRelative(-0.583f, 0f, -0.958f, -0.375f)
        reflectiveQuadToRelative(-0.375f, -0.917f)
        close()
      }
    }.build()
  }
}