package core

import core.mac.MacPort
import core.win.WindowsAction
import core.win.WindowsPort

class PlatformInfo {
  val name: String = "Java ${System.getProperty("java.version")}"
  val osName: String = System.getProperty("os.name")
  fun isWindows(): Boolean = osName.lowercase().contains("windows")
  fun isMac(): Boolean = osName.lowercase().contains("mac")
}

fun getPlatform() = PlatformInfo()

fun getPortStrategy(): PortStrategy {
  val platform = getPlatform()
  if (platform.isWindows()) {
    return WindowsPort()
  }
  return MacPort()
}

fun getActionStrategy(): ActionStrategy {
  return WindowsAction()
}