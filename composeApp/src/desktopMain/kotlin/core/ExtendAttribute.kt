package core

import androidx.compose.ui.input.key.*

internal val KeyEvent.isScrollNext: Boolean
  get() = KeyEventType.KeyUp == type && (Key.Tab == key || Key.DirectionDown == key)

internal val KeyEvent.isScrollLast: Boolean
  get() = KeyEventType.KeyUp == type && ((isShiftPressed && Key.Tab == key) || Key.DirectionUp == key)

internal fun KeyEvent.ctrlAnd(otherKey: Key): Boolean
  = isCtrlPressed && key == otherKey && type == KeyEventType.KeyUp
