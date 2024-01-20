package core

import core.mac.MacPort
import core.win.WindowsAction
import core.win.WindowsPort

object Platform {
  val name: String = "Java ${System.getProperty("java.version")}"
  val osName: String = System.getProperty("os.name")
  val isWindows: Boolean
    get() = osName.lowercase().contains("windows")
  val isMac: Boolean
    get() = osName.lowercase().contains("mac")
  val x64: Boolean
    get() = System.getProperty("os.arch").contains("64")
  val x86: Boolean
    get() = !x64

  var isDebug: Boolean = false
  val portStrategy: PortStrategy
    get() {
      if (isWindows) {
        return WindowsPort
      }
      return MacPort
    }
  val actionStrategy: ActionStrategy
    get() {
      return WindowsAction
    }
}
