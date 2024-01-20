package icons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Composable
fun rememberSensorOccupied(): ImageVector {
  return remember {
    ImageVector.Builder(
      name = "sensor_occupied",
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
        moveTo(20f, 18.25f)
        quadToRelative(-2.042f, 0f, -3.479f, -1.438f)
        quadToRelative(-1.438f, -1.437f, -1.438f, -3.479f)
        quadToRelative(0f, -2.041f, 1.438f, -3.5f)
        quadTo(17.958f, 8.375f, 20f, 8.375f)
        reflectiveQuadToRelative(3.5f, 1.458f)
        quadToRelative(1.458f, 1.459f, 1.458f, 3.5f)
        quadToRelative(0f, 2.042f, -1.458f, 3.479f)
        quadToRelative(-1.458f, 1.438f, -3.5f, 1.438f)
        close()
        moveToRelative(0f, -2.625f)
        quadToRelative(0.958f, 0f, 1.625f, -0.667f)
        quadToRelative(0.667f, -0.666f, 0.667f, -1.625f)
        quadToRelative(0f, -0.958f, -0.667f, -1.625f)
        quadToRelative(-0.667f, -0.666f, -1.625f, -0.666f)
        reflectiveQuadToRelative(-1.625f, 0.666f)
        quadToRelative(-0.667f, 0.667f, -0.667f, 1.625f)
        quadToRelative(0f, 0.959f, 0.667f, 1.625f)
        quadToRelative(0.667f, 0.667f, 1.625f, 0.667f)
        close()
        moveTo(11.375f, 28.25f)
        quadToRelative(-0.542f, 0f, -0.917f, -0.375f)
        reflectiveQuadToRelative(-0.375f, -0.917f)
        verticalLineToRelative(-1.791f)
        quadToRelative(0f, -1.084f, 0.605f, -1.875f)
        quadToRelative(0.604f, -0.792f, 2.02f, -1.542f)
        quadToRelative(1.709f, -0.833f, 3.521f, -1.271f)
        quadToRelative(1.813f, -0.437f, 3.771f, -0.437f)
        reflectiveQuadToRelative(3.771f, 0.437f)
        quadToRelative(1.812f, 0.438f, 3.521f, 1.271f)
        quadToRelative(1.416f, 0.708f, 2.041f, 1.521f)
        quadToRelative(0.625f, 0.812f, 0.625f, 1.896f)
        verticalLineToRelative(1.791f)
        quadToRelative(0f, 0.542f, -0.396f, 0.917f)
        quadToRelative(-0.395f, 0.375f, -0.937f, 0.375f)
        close()
        moveTo(20f, 22.708f)
        quadToRelative(-1.958f, 0f, -3.792f, 0.521f)
        quadToRelative(-1.833f, 0.521f, -3.5f, 1.563f)
        verticalLineToRelative(0.833f)
        horizontalLineToRelative(14.584f)
        verticalLineToRelative(-0.833f)
        quadToRelative(-1.667f, -1.042f, -3.5f, -1.563f)
        quadToRelative(-1.834f, -0.521f, -3.792f, -0.521f)
        close()
        moveToRelative(7.25f, -19.875f)
        quadToRelative(0.25f, -0.541f, 0.792f, -0.708f)
        quadToRelative(0.541f, -0.167f, 1.041f, 0.125f)
        quadToRelative(2.75f, 1.417f, 4.896f, 3.563f)
        quadToRelative(2.146f, 2.145f, 3.646f, 4.812f)
        quadToRelative(0.25f, 0.542f, 0.125f, 1.125f)
        reflectiveQuadToRelative(-0.667f, 0.875f)
        quadToRelative(-0.458f, 0.25f, -0.979f, 0.042f)
        quadToRelative(-0.521f, -0.209f, -0.812f, -0.75f)
        quadToRelative(-1.25f, -2.334f, -3.104f, -4.167f)
        quadTo(30.333f, 5.917f, 28f, 4.667f)
        quadToRelative(-0.5f, -0.25f, -0.729f, -0.771f)
        quadToRelative(-0.229f, -0.521f, -0.021f, -1.063f)
        close()
        moveToRelative(-14.625f, 0.084f)
        quadToRelative(0.25f, 0.458f, 0.021f, 1f)
        quadToRelative(-0.229f, 0.541f, -0.729f, 0.791f)
        quadToRelative(-2.334f, 1.25f, -4.146f, 3.084f)
        quadToRelative(-1.813f, 1.833f, -3.063f, 4.166f)
        quadToRelative(-0.25f, 0.5f, -0.77f, 0.709f)
        quadToRelative(-0.521f, 0.208f, -1.021f, -0.042f)
        quadToRelative(-0.542f, -0.292f, -0.667f, -0.896f)
        quadToRelative(-0.125f, -0.604f, 0.167f, -1.146f)
        quadTo(3.833f, 8f, 5.917f, 5.917f)
        quadTo(8f, 3.833f, 10.583f, 2.458f)
        quadToRelative(0.542f, -0.333f, 1.146f, -0.208f)
        quadToRelative(0.604f, 0.125f, 0.896f, 0.667f)
        close()
        moveTo(2.917f, 27.375f)
        quadToRelative(0.5f, -0.292f, 1.041f, -0.042f)
        quadToRelative(0.542f, 0.25f, 0.834f, 0.792f)
        quadToRelative(1.166f, 2.25f, 2.937f, 4.042f)
        quadToRelative(1.771f, 1.791f, 4.021f, 3f)
        quadToRelative(0.542f, 0.291f, 0.792f, 0.833f)
        reflectiveQuadToRelative(0.041f, 1.042f)
        quadToRelative(-0.291f, 0.541f, -0.854f, 0.687f)
        quadToRelative(-0.562f, 0.146f, -1.104f, -0.146f)
        quadTo(8f, 36.208f, 5.917f, 34.125f)
        quadToRelative(-2.084f, -2.083f, -3.459f, -4.708f)
        quadToRelative(-0.333f, -0.542f, -0.187f, -1.146f)
        quadToRelative(0.146f, -0.604f, 0.646f, -0.896f)
        close()
        moveToRelative(34.166f, 0f)
        quadToRelative(0.542f, 0.292f, 0.646f, 0.917f)
        quadToRelative(0.104f, 0.625f, -0.187f, 1.166f)
        quadToRelative(-1.417f, 2.584f, -3.48f, 4.646f)
        quadToRelative(-2.062f, 2.063f, -4.645f, 3.438f)
        quadToRelative(-0.584f, 0.333f, -1.167f, 0.208f)
        quadToRelative(-0.583f, -0.125f, -0.875f, -0.667f)
        quadToRelative(-0.25f, -0.5f, -0.021f, -1.021f)
        quadToRelative(0.229f, -0.52f, 0.771f, -0.812f)
        quadToRelative(2.292f, -1.208f, 4.104f, -3.021f)
        quadToRelative(1.813f, -1.812f, 3.021f, -4.104f)
        quadToRelative(0.292f, -0.5f, 0.833f, -0.75f)
        quadToRelative(0.542f, -0.25f, 1f, 0f)
        close()
        moveTo(20f, 13.333f)
        close()
        moveToRelative(0f, 12.292f)
        horizontalLineToRelative(7.292f)
        horizontalLineToRelative(-14.584f)
        horizontalLineTo(20f)
        close()
      }
    }.build()
  }
}