package core.mac

import com.dd.plist.NSDictionary
import com.dd.plist.PropertyListParser
import core.PortInfo
import core.PortStrategy
import oshi.SystemInfo
import oshi.software.os.mac.MacInternetProtocolStats
import java.awt.image.BufferedImage
import java.io.File
import java.net.InetAddress
import java.net.UnknownHostException
import javax.imageio.ImageIO

object MacPort : PortStrategy {
  private val systemInfo = SystemInfo()
  override fun portList(lastList: List<PortInfo>): List<PortInfo> {
    val processMap = systemInfo.operatingSystem.processes.associateBy { it.processID }
    val macInternet = MacInternetProtocolStats(true)
    val existList = mutableListOf<PortInfo>()
    val lastListMap = lastList.associateBy { it.pid }
    return macInternet.connections
      .asSequence()
      .map { connection ->
        val pid = connection.getowningProcessId()
        val osProcess = processMap[pid] ?: return@map null
        PortInfo(
          name = osProcess.name,
          command = osProcess.commandLine,
          pid = osProcess.processID,
          address = formatIPAddress(connection.localAddress),
          port = connection.localPort,
          remoteAddress = formatIPAddress(connection.foreignAddress),
          remotePort = connection.foreignPort,
          image = getIcon(osProcess.commandLine)
        )
      }
      .filterNotNull()
      .filter {
        val value = lastListMap[it.pid]
        if (value != null) {
          existList.add(value)
          return@filter false
        }
        if (it.port != null && it.port!! <= 0) {
          return@filter false
        }
        return@filter true
      }
      .plus(existList)
      .distinctBy { "${it.port}-${it.name}" }
      .sortedBy { it.port }
      .toList()
  }


  private fun formatIPAddress(ipAddressBytes: ByteArray): String {
    return try {
      InetAddress.getByAddress(ipAddressBytes).hostAddress
    } catch (e: UnknownHostException) {
      "Unknown"
    }
  }

  private fun getIcon(command: String): BufferedImage? {
    val path = command.substringBefore(".app")
    val infoPlist = File("$path.app/Contents/Info.plist")
    if (!infoPlist.exists()) {
      return null
    }
    val rootDict = PropertyListParser.parse(infoPlist) as NSDictionary
    val icnsFileName = rootDict["CFBundleIconFile"].toString()
    val icnsFilePath = "$path.app/Contents/Resources/"+ if (icnsFileName.endsWith(".icns")) {
      icnsFileName
    } else "$icnsFileName.icns"
    val icnsFile = File(icnsFilePath)
    if (icnsFile.exists()) {
      return ImageIO.read(icnsFile)
    }
    return null
  }
}