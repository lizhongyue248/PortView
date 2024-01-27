package core

import core.linux.LinuxAction
import core.linux.LinuxPort
import core.mac.MacAction
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
      if (isMac) {
        return MacPort
      }
      return LinuxPort
    }
  val actionStrategy: ActionStrategy
    get() {
      if (isWindows) {
        return WindowsAction
      }
      if (isMac) {
        return MacAction
      }
      return LinuxAction
    }
}
