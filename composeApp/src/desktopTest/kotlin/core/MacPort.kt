package core

import com.sun.jna.Memory
import com.sun.jna.Native
import org.junit.Test
import oshi.SystemInfo
import oshi.jna.platform.mac.SystemB
import oshi.software.os.mac.MacInternetProtocolStats
import java.net.InetAddress
import java.net.UnknownHostException


/**
 * 2024/1/22 21:37:47
 * @author yue
 */
class MacPort {
  @Test
  fun testPid() {
    val pid = SystemB.INSTANCE.getpid()
    println(pid)
    val numberOfProcesses = SystemB.INSTANCE.proc_listpids(SystemB.PROC_ALL_PIDS, 0, null, 0)
    val pids = IntArray(numberOfProcesses)
    var bytesReturned = SystemB.INSTANCE.proc_listpids(SystemB.PROC_ALL_PIDS, 0, pids, numberOfProcesses * 4)
    println("numberOfProcesses $numberOfProcesses")
    if (bytesReturned <= 0) {
      println("Error $bytesReturned")
      return
    }
    pids.filter { it > 0 }.forEach {
      val taskAllInfo = com.sun.jna.platform.mac.SystemB.ProcTaskAllInfo()
      bytesReturned = SystemB.INSTANCE.proc_pidinfo(
        it, SystemB.PROC_PIDTASKALLINFO, 0, taskAllInfo,
        taskAllInfo.size()
      )

      val buf = Memory(SystemB.PROC_PIDPATHINFO_MAXSIZE.toLong())
      bytesReturned = SystemB.INSTANCE.proc_pidpath(pid, buf, SystemB.PROC_PIDPATHINFO_MAXSIZE)
      val path: String = buf.getString(0).trim()
      println("$it - ${Native.toString(taskAllInfo.pbsd.pbi_name)} $path")
    }
  }

  @Test
  fun testO() {
    val systemInfo = SystemInfo()
    val processMap = systemInfo.operatingSystem.processes.associateBy { it.processID }
    val macInternet = MacInternetProtocolStats(true)
    val connections = macInternet.connections
    connections.forEach { connection ->
      val pid = connection.getowningProcessId()
      val osProcess = processMap[pid] ?: return@forEach
      println("${osProcess.processID} ${osProcess.name} ${osProcess.path}")

      println(
        "${convertByteArrayToIpAddress(connection.localAddress)}:${connection.localPort} --- ${
          convertByteArrayToIpAddress(connection.foreignAddress)
        }:${connection.foreignPort}"
      )
    }
  }

}


internal fun convertByteArrayToIpAddress(ipAddressBytes: ByteArray): String {
  try {
    val inetAddress = InetAddress.getByAddress(ipAddressBytes)
    return inetAddress.hostAddress // This gives the IP address as a String
  } catch (e: UnknownHostException) {
    return "Unknown"
  }
}