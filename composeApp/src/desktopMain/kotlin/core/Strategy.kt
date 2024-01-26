package core

import org.apache.commons.lang3.StringUtils
import org.tinylog.kotlin.Logger
import oshi.SystemInfo
import oshi.software.os.InternetProtocolStats
import java.awt.image.BufferedImage
import java.net.InetAddress
import java.net.UnknownHostException

abstract class PortStrategy {
  private val systemInfo = SystemInfo().operatingSystem

  protected abstract fun getInternetProtocolStats(): InternetProtocolStats

  protected abstract fun getIcon(command: String): BufferedImage?

  fun portList(
    lastList: List<PortInfo>,
    newPortHook: (newPorts: Set<String>) -> Unit = {}
  ): List<PortInfo> {
    val processMap = systemInfo.processes.associateBy { it.processID }
    val macInternet = getInternetProtocolStats()
    val lastListMap = lastList.associateBy { it.pid }
    val newPortList = mutableSetOf<String>()
    val portInfoList = macInternet.connections
      .asSequence()
      .map { connection ->
        val pid = connection.getowningProcessId()
        val osProcess = processMap[pid] ?: return@map null
        if (lastListMap[pid] == null && StringUtils.isNotEmpty(osProcess.commandLine)) {
          newPortList.add("${osProcess.name}($pid)")
        }
        PortInfo(
          name = osProcess.name,
          command = osProcess.commandLine,
          pid = osProcess.processID,
          address = formatIPAddress(connection.localAddress),
          port = connection.localPort,
          remoteAddress = formatIPAddress(connection.foreignAddress),
          remotePort = connection.foreignPort,
          image = getIcon(osProcess.path)
        )
      }
      .filterNotNull()
      .distinctBy { "${it.port}-${it.name}" }
      .sortedBy { it.port }
      .toList()
    newPortHook(newPortList)
    return portInfoList
  }

  private fun formatIPAddress(ipAddressBytes: ByteArray): String {
    return try {
      InetAddress.getByAddress(ipAddressBytes).hostAddress
    } catch (e: UnknownHostException) {
      "Unknown"
    }
  }
}

abstract class ActionStrategy {
  fun closeProcess(pid: Int?): Pair<Boolean, String> {
    if (pid == null) {
      return Pair(false, "No find process $pid")
    }
    val process = ProcessHandle.of(pid.toLong())
    if (process.isEmpty) {
      Logger.warn("[Error] Can not find process $pid. Maybe you should use root.")
      return Pair(false, "Can not find process $pid.")
    }
    val result = process.get().destroyForcibly()
    Logger.info("Close process result $result.")
    return Pair(true, "Success")
  }

  abstract fun open(command: String): Boolean
}
