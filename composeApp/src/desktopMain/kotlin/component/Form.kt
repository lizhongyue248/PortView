package component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.TextFieldDefaults.indicatorLine
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 2023/10/9 00:06:26
 * @author yue
 */
@Composable
fun MyIconButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  content: @Composable () -> Unit
) {
  // 这也是源码的一部分
  Box(
    modifier = modifier
      .clickable(
        onClick = onClick,
        enabled = enabled,
        role = Role.Button,
        interactionSource = interactionSource,
        indication = null
      )
      .then(
        Modifier.size(48.dp)
          .pointerHoverIcon(PointerIcon.Hand)
      ),
    contentAlignment = Alignment.Center
  ) { content() }
}



@Composable
fun MyTextField(
  value: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  readOnly: Boolean = false,
  textStyle: TextStyle = LocalTextStyle.current,
  label: @Composable (() -> Unit)? = null,
  placeholder: @Composable (() -> Unit)? = null,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  isError: Boolean = false,
  visualTransformation: VisualTransformation = VisualTransformation.None,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  keyboardActions: KeyboardActions = KeyboardActions(),
  singleLine: Boolean = false,
  maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
  minLines: Int = 1,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  shape: Shape =
    MaterialTheme.shapes.small.copy(bottomEnd = ZeroCornerSize, bottomStart = ZeroCornerSize),
  colors: TextFieldColors = TextFieldDefaults.textFieldColors(),
  contentPadding: PaddingValues = PaddingValues(),
  minWidth: Dp = TextFieldDefaults.MinWidth,
  minHeight: Dp = TextFieldDefaults.MinHeight
) {
  // If color is not provided via the text style, use content color as a default
  val textColor = textStyle.color.takeOrElse {
    colors.textColor(enabled).value
  }
  val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

  @OptIn(ExperimentalMaterialApi::class)
  (BasicTextField(
    value = value,
    modifier = modifier
      .background(colors.backgroundColor(enabled).value, shape)
      .indicatorLine(enabled, isError, interactionSource, colors)
      .defaultMinSize(
        minWidth = minWidth,
        minHeight = minHeight
      ),
    onValueChange = onValueChange,
    enabled = enabled,
    readOnly = readOnly,
    textStyle = mergedTextStyle,
    cursorBrush = SolidColor(colors.cursorColor(isError).value),
    visualTransformation = visualTransformation,
    keyboardOptions = keyboardOptions,
    keyboardActions = keyboardActions,
    interactionSource = interactionSource,
    singleLine = singleLine,
    maxLines = maxLines,
    minLines = minLines,
    decorationBox = @Composable { innerTextField ->
      Box(
        modifier = Modifier
      ) {
        TextFieldDefaults.TextFieldDecorationBox(
          value = value,
          visualTransformation = visualTransformation,
          innerTextField = innerTextField,
          placeholder = placeholder,
          label = label,
          leadingIcon = leadingIcon,
          trailingIcon = trailingIcon,
          singleLine = singleLine,
          enabled = enabled,
          isError = isError,
          interactionSource = interactionSource,
          colors = colors,
          contentPadding = contentPadding
        )
      }
    },

    ))
}
