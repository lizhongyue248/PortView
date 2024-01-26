package core.mac

import com.dd.plist.NSDictionary
import com.dd.plist.PropertyListParser
import core.PortStrategy
import oshi.software.os.mac.MacInternetProtocolStats
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object MacPort : PortStrategy() {

  private val stats = MacInternetProtocolStats(true)

  override fun getInternetProtocolStats() = stats

  override fun getIcon(command: String): BufferedImage? {
    val path = command.substringBefore(".app")
    val infoPlist = File("$path.app/Contents/Info.plist")
    if (!infoPlist.exists()) {
      return null
    }
    val rootDict = PropertyListParser.parse(infoPlist) as NSDictionary
    val icnsFileName = rootDict["CFBundleIconFile"].toString()
    val icnsFilePath = "$path.app/Contents/Resources/" + if (icnsFileName.endsWith(".icns")) {
      icnsFileName
    } else "$icnsFileName.icns"
    val icnsFile = File(icnsFilePath)
    if (icnsFile.exists()) {
      return ImageIO.read(icnsFile)
    }
    return null
  }
}