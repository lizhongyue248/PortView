package core

import core.mac.MacPort
import core.win.WindowsAction
import core.win.WindowsPort

object  PlatformInfo {
  val name: String = "Java ${System.getProperty("java.version")}"
  val osName: String = System.getProperty("os.name")
  fun isWindows(): Boolean = osName.lowercase().contains("windows")
  fun isMac(): Boolean = osName.lowercase().contains("mac")
}

object Platform {
  val portStrategy: PortStrategy
    get() {
      val platform = PlatformInfo
      if (platform.isWindows()) {
        return WindowsPort
      }
      return MacPort
    }
  val actionStrategy: ActionStrategy
    get() {
      return WindowsAction
    }
}
