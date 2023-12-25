package icons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Composable
fun rememberHelp(): ImageVector {
  return remember {
    ImageVector.Builder(
      name = "help",
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
        moveTo(20.125f, 29.625f)
        quadToRelative(0.667f, 0f, 1.167f, -0.479f)
        reflectiveQuadToRelative(0.5f, -1.188f)
        quadToRelative(0f, -0.708f, -0.48f, -1.187f)
        quadToRelative(-0.479f, -0.479f, -1.187f, -0.479f)
        quadToRelative(-0.708f, 0f, -1.187f, 0.479f)
        quadToRelative(-0.48f, 0.479f, -0.48f, 1.187f)
        quadToRelative(0f, 0.667f, 0.48f, 1.167f)
        quadToRelative(0.479f, 0.5f, 1.187f, 0.5f)
        close()
        moveToRelative(0f, -16.917f)
        quadToRelative(1.333f, 0f, 2.167f, 0.73f)
        quadToRelative(0.833f, 0.729f, 0.833f, 1.854f)
        quadToRelative(0f, 0.791f, -0.458f, 1.562f)
        quadToRelative(-0.459f, 0.771f, -1.5f, 1.688f)
        quadToRelative(-1f, 0.875f, -1.729f, 1.896f)
        quadToRelative(-0.73f, 1.02f, -0.688f, 2.062f)
        quadToRelative(0f, 0.458f, 0.354f, 0.75f)
        reflectiveQuadToRelative(0.854f, 0.292f)
        quadToRelative(0.5f, 0f, 0.854f, -0.334f)
        quadToRelative(0.355f, -0.333f, 0.48f, -0.875f)
        quadToRelative(0.125f, -0.75f, 0.541f, -1.375f)
        quadToRelative(0.417f, -0.625f, 1.375f, -1.458f)
        quadToRelative(1.209f, -1.042f, 1.75f, -2.062f)
        quadToRelative(0.542f, -1.021f, 0.542f, -2.271f)
        quadToRelative(0f, -2.167f, -1.438f, -3.479f)
        quadToRelative(-1.437f, -1.313f, -3.812f, -1.313f)
        quadToRelative(-1.583f, 0f, -2.896f, 0.646f)
        quadToRelative(-1.312f, 0.646f, -2.187f, 1.854f)
        quadToRelative(-0.292f, 0.458f, -0.229f, 0.938f)
        quadToRelative(0.062f, 0.479f, 0.437f, 0.729f)
        quadToRelative(0.458f, 0.333f, 1f, 0.208f)
        quadToRelative(0.542f, -0.125f, 0.875f, -0.625f)
        quadToRelative(0.5f, -0.708f, 1.229f, -1.062f)
        quadToRelative(0.729f, -0.355f, 1.646f, -0.355f)
        close()
        moveTo(20f, 36.375f)
        quadToRelative(-3.417f, 0f, -6.417f, -1.25f)
        reflectiveQuadToRelative(-5.208f, -3.458f)
        quadToRelative(-2.208f, -2.209f, -3.479f, -5.209f)
        quadToRelative(-1.271f, -3f, -1.271f, -6.458f)
        reflectiveQuadToRelative(1.271f, -6.437f)
        quadToRelative(1.271f, -2.98f, 3.479f, -5.188f)
        quadToRelative(2.208f, -2.208f, 5.208f, -3.479f)
        reflectiveQuadTo(20f, 3.625f)
        quadToRelative(3.417f, 0f, 6.396f, 1.271f)
        reflectiveQuadToRelative(5.208f, 3.479f)
        quadToRelative(2.229f, 2.208f, 3.5f, 5.188f)
        quadToRelative(1.271f, 2.979f, 1.271f, 6.437f)
        reflectiveQuadToRelative(-1.271f, 6.458f)
        quadToRelative(-1.271f, 3f, -3.5f, 5.209f)
        quadToRelative(-2.229f, 2.208f, -5.208f, 3.458f)
        reflectiveQuadTo(20f, 36.375f)
        close()
        moveToRelative(0f, -16.667f)
        close()
        moveToRelative(0f, 14.042f)
        quadToRelative(5.708f, 0f, 9.729f, -4.042f)
        quadTo(33.75f, 25.667f, 33.75f, 20f)
        reflectiveQuadToRelative(-4.021f, -9.708f)
        quadTo(25.708f, 6.25f, 20f, 6.25f)
        quadToRelative(-5.75f, 0f, -9.75f, 4.042f)
        quadToRelative(-4f, 4.041f, -4f, 9.708f)
        reflectiveQuadToRelative(4f, 9.708f)
        quadToRelative(4f, 4.042f, 9.75f, 4.042f)
        close()
      }
    }.build()
  }
}