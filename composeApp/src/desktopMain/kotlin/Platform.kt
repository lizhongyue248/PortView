import core.MacPort
import core.PortStrategy
import core.WindowsPort

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
