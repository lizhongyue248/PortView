package core

import core.linux.LinuxAction
import core.linux.LinuxPort
import core.mac.MacAction
import core.mac.MacPort
import core.win.WindowsAction
import core.win.WindowsPort
import com.sun.jna.Platform as JNAPlatform

object Platform {
  val name: String = "Java ${System.getProperty("java.version")}"
  val osName: String = System.getProperty("os.name")
  val isWindows: Boolean
    get() = JNAPlatform.isWindows()
  val isMac: Boolean
    get() = JNAPlatform.isMac()

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
