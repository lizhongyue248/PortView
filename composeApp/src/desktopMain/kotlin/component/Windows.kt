package component

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.awt.ComposeDialog
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import java.awt.*
import java.awt.event.*
import java.util.*
import javax.swing.JDialog
import kotlin.math.roundToInt
internal val LocalWindow = compositionLocalOf<Window?> { null }

/**
 * Add alwaysOnTop for DialogWindow.
 *
 * @see [DialogWindow]
 */
@Composable
fun MyDialogWindow(
  onCloseRequest: () -> Unit,
  onWindowDeactivated: () -> Unit,
  state: DialogState = rememberDialogState(),
  visible: Boolean = true,
  title: String = "Untitled",
  icon: Painter? = null,
  undecorated: Boolean = false,
  transparent: Boolean = false,
  resizable: Boolean = true,
  enabled: Boolean = true,
  focusable: Boolean = true,
  alwaysOnTop: Boolean = false,
  onPreviewKeyEvent: ((KeyEvent) -> Boolean) = { false },
  onKeyEvent: ((KeyEvent) -> Boolean) = { false },
  content: @Composable DialogWindowScope.() -> Unit
) {
  val owner = LocalWindow.current

  val currentState by rememberUpdatedState(state)
  val currentTitle by rememberUpdatedState(title)
  val currentIcon by rememberUpdatedState(icon)
  val currentUndecorated by rememberUpdatedState(undecorated)
  val currentTransparent by rememberUpdatedState(transparent)
  val currentResizable by rememberUpdatedState(resizable)
  val currentEnabled by rememberUpdatedState(enabled)
  val currentFocusable by rememberUpdatedState(focusable)
  val currentOnCloseRequest by rememberUpdatedState(onCloseRequest)
  val currentOnAlwaysOnTop by rememberUpdatedState(alwaysOnTop)

  val updater = remember(::ComponentUpdater)

  // the state applied to the dialog. exist to avoid races between DialogState changes and the state stored inside the native dialog
  val appliedState = remember {
    object {
      var size: DpSize? = null
      var position: WindowPosition? = null
    }
  }

  val listeners = remember {
    object {
      var windowListenerRef = windowListenerRef()
      var componentListenerRef = componentListenerRef()

      fun removeFromAndClear(window: ComposeDialog) {
        windowListenerRef.unregisterFromAndClear(window)
        componentListenerRef.unregisterFromAndClear(window)
      }
    }
  }

  DialogWindow(
    visible = visible,
    onPreviewKeyEvent = onPreviewKeyEvent,
    onKeyEvent = onKeyEvent,
    create = {
      val graphicsConfiguration = WindowLocationTracker.lastActiveGraphicsConfiguration
      val dialog = if (owner != null) {
        ComposeDialog(owner, Dialog.ModalityType.DOCUMENT_MODAL, graphicsConfiguration = graphicsConfiguration)
      } else {
        ComposeDialog(graphicsConfiguration = graphicsConfiguration)
      }
      dialog.apply {
        // close state is controlled by DialogState.isOpen
        defaultCloseOperation = JDialog.DO_NOTHING_ON_CLOSE
        listeners.windowListenerRef.registerWithAndSet(
          this,
          object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
              currentOnCloseRequest()
            }

            override fun windowDeactivated(e: WindowEvent?) {
              onWindowDeactivated()
              println("windowDeactivated")
            }

            override fun windowActivated(e: WindowEvent?) {
              println("windowActivated")
            }

            override fun windowLostFocus(e: WindowEvent?) {
              println("windowLostFocus")
            }

            override fun windowGainedFocus(e: WindowEvent?) {
              println("windowGainedFocus")
            }
          }
        )
        listeners.componentListenerRef.registerWithAndSet(
          this,
          object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
              currentState.size = DpSize(width.dp, height.dp)
              appliedState.size = currentState.size
            }

            override fun componentMoved(e: ComponentEvent) {
              currentState.position = WindowPosition(x.dp, y.dp)
              appliedState.position = currentState.position
            }
          }
        )
        WindowLocationTracker.onWindowCreated(this)
      }
    },
    dispose = {
      WindowLocationTracker.onWindowDisposed(it)
      // We need to remove them because AWT can still call them after dispose()
      listeners.removeFromAndClear(it)
      it.dispose()
    },
    update = { dialog ->
      updater.update {
        set(currentTitle, dialog::setTitle)
        set(currentIcon, dialog::setIcon)
        set(currentUndecorated, dialog::setUndecoratedSafely)
        set(currentTransparent, dialog::isTransparent::set)
        set(currentResizable, dialog::setResizable)
        set(currentEnabled, dialog::setEnabled)
        set(currentFocusable, dialog::setFocusableWindowState)
        set(currentOnAlwaysOnTop, dialog::setAlwaysOnTop)
      }
      if (state.size != appliedState.size) {
        dialog.setSizeSafely(state.size, WindowPlacement.Floating)
        appliedState.size = state.size
      }
      if (state.position != appliedState.position) {
        dialog.setPositionSafely(
          state.position,
          WindowPlacement.Floating,
          platformDefaultPosition = { WindowLocationTracker.getCascadeLocationFor(dialog) }
        )
        appliedState.position = state.position
      }
    },
    content = content
  )
}

private val GraphicsConfiguration.density: Density get() = Density(
  defaultTransform.scaleX.toFloat(),
  fontScale = 1f
)
internal val Component.density: Density get() = graphicsConfiguration.density
private val iconSize = Size(32f, 32f)
internal val ComponentOrientation.layoutDirection: LayoutDirection
  get() = when {
    isLeftToRight -> LayoutDirection.Ltr
    isHorizontal -> LayoutDirection.Rtl
    else -> LayoutDirection.Ltr
  }

internal fun Window.setIcon(painter: Painter?) {
  setIconImage(painter?.toAwtImage(density, layoutDirectionFor(this), iconSize))
}

internal val Locale.layoutDirection: LayoutDirection
  get() = ComponentOrientation.getOrientation(this).layoutDirection

/**
 * Compute the [LayoutDirection] the given AWT/Swing component should have, based on its own,
 * non-Compose attributes.
 */
internal fun layoutDirectionFor(component: Component): LayoutDirection {
  val orientation = component.componentOrientation
  return if (orientation != ComponentOrientation.UNKNOWN) {
    orientation.layoutDirection
  } else {
    // To preserve backwards compatibility we fall back to the locale
    return component.locale.layoutDirection
  }
}

internal class ListenerOnWindowRef<T>(
  private val register: Window.(T) -> Unit,
  private val unregister: Window.(T) -> Unit
) {
  private var value: T? = null

  fun registerWithAndSet(window: Window, listener: T) {
    window.register(listener)
    value = listener
  }

  fun unregisterFromAndClear(window: Window) {
    value?.let {
      window.unregister(it)
      value = null
    }
  }
}

internal fun windowListenerRef() = ListenerOnWindowRef<WindowListener>(
  register = Window::addWindowListener,
  unregister = Window::removeWindowListener
)

internal fun componentListenerRef() = ListenerOnWindowRef<ComponentListener>(
  register = Component::addComponentListener,
  unregister = Component::removeComponentListener
)

/**
 * Sets the position of the window, given its placement.
 * If the window is already visible, then change the position only if it's floating, in order to
 * avoid resetting the maximized / fullscreen state.
 * If the window is not visible yet, we _do_ set its size so that it will have an "un-maximized"
 * position to go to when the user un-maximizes the window.
 */
internal fun Window.setPositionSafely(
  position: WindowPosition,
  placement: WindowPlacement,
  platformDefaultPosition: () -> Point
) {
  if (!isVisible || (placement == WindowPlacement.Floating)) {
    setPositionImpl(position, platformDefaultPosition)
  }
}


internal fun Window.align(alignment: Alignment) {
  val screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(graphicsConfiguration)
  val screenBounds = graphicsConfiguration.bounds
  val size = IntSize(size.width, size.height)
  val screenSize = IntSize(
    screenBounds.width - screenInsets.left - screenInsets.right,
    screenBounds.height - screenInsets.top - screenInsets.bottom
  )
  val location = alignment.align(size, screenSize, LayoutDirection.Ltr)

  setLocation(
    screenBounds.x + screenInsets.left + location.x,
    screenBounds.y + screenInsets.top + location.y
  )
}
internal fun Window.setPositionImpl(
  position: WindowPosition,
  platformDefaultPosition: () -> Point
) = when (position) {
  WindowPosition.PlatformDefault -> location = platformDefaultPosition()
  is WindowPosition.Aligned -> align(position.alignment)
  is WindowPosition.Absolute -> setLocation(
    position.x.value.roundToInt(),
    position.y.value.roundToInt()
  )
}

/**
 * We cannot change call [Dialog.setUndecorated] if window is showing - AWT will throw an exception.
 * But we can call [Dialog.setUndecoratedSafely] if isUndecorated isn't changed.
 */
internal fun Dialog.setUndecoratedSafely(value: Boolean) {
  if (this.isUndecorated != value) {
    this.isUndecorated = value
  }
}


/**
 * Stores the previous applied state, and provide ability to update component if the new state is
 * changed.
 */
internal class ComponentUpdater {
  private var updatedValues = mutableListOf<Any?>()

  fun update(body: UpdateScope.() -> Unit) {
    UpdateScope().body()
  }

  inner class UpdateScope {
    private var index = 0

    /**
     * Compare [value] with the old one and if it is changed - store a new value and call
     * [update]
     */
    fun <T : Any?> set(value: T, update: (T) -> Unit) {
      if (index < updatedValues.size) {
        if (updatedValues[index] != value) {
          update(value)
          updatedValues[index] = value
        }
      } else {
        check(index == updatedValues.size)
        update(value)
        updatedValues.add(value)
      }

      index++
    }
  }
}



/**
 * Sets the size of the window, given its placement.
 * If the window is already visible, then change the size only if it's floating, in order to
 * avoid resetting the maximized / fullscreen state.
 * If the window is not visible yet, we _do_ set its size so that:
 * - It will have an "un-maximized" size to go to when the user un-maximizes the window.
 * - To allow drawing the first frame (at the correct size) before the window is made visible.
 */
internal fun Window.setSizeSafely(size: DpSize, placement: WindowPlacement) {
  if (!isVisible || (placement == WindowPlacement.Floating)) {
    setSizeImpl(size)
  }
}


private fun Window.setSizeImpl(size: DpSize) {
  val availableSize by lazy {
    val screenBounds = graphicsConfiguration.bounds
    val screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(graphicsConfiguration)

    IntSize(
      width = screenBounds.width - screenInsets.left - screenInsets.right,
      height = screenBounds.height - screenInsets.top - screenInsets.bottom
    )
  }

  val isWidthSpecified = size.isSpecified && size.width.isSpecified
  val isHeightSpecified = size.isSpecified && size.height.isSpecified

  val width = if (isWidthSpecified) {
    size.width.value.roundToInt().coerceAtLeast(0)
  } else {
    availableSize.width
  }

  val height = if (isHeightSpecified) {
    size.height.value.roundToInt().coerceAtLeast(0)
  } else {
    availableSize.height
  }

  var computedPreferredSize: Dimension? = null
  if (!isWidthSpecified || !isHeightSpecified) {
    preferredSize = Dimension(width, height)
    pack()  // Makes it displayable

    // We set preferred size to null, and then call getPreferredSize, which will compute the
    // actual preferred size determined by the content (see the description of setPreferredSize)
    preferredSize = null
    computedPreferredSize = preferredSize
  }

  if (!isDisplayable) {
    // Pack to allow drawing the first frame
    preferredSize = Dimension(width, height)
    pack()
  }

  setSize(
    if (isWidthSpecified) width else computedPreferredSize!!.width,
    if (isHeightSpecified) height else computedPreferredSize!!.height,
  )
  revalidate()  // Calls doLayout on the ComposeLayer, causing it to update its size
}