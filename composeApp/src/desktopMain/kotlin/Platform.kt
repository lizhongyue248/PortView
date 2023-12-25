import core.MacPort
import core.PortStrategy
import core.WindowsPort
import java.awt.GraphicsEnvironment

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


internal val GlobalDensity get() = GraphicsEnvironment.getLocalGraphicsEnvironment()
  .defaultScreenDevice
  .defaultConfiguration
