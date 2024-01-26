package core

import PortSupport
import core.win.WindowsPort
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import oshi.SystemInfo
import oshi.software.os.windows.WindowsInternetProtocolStats
import java.awt.BorderLayout
import java.io.File
import javax.swing.Icon
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.filechooser.FileSystemView


class WindowsPortTest : PortSupport() {

  @Test
  fun portListTest() {
    val windowsPort = WindowsPort
    val portList = windowsPort.portList(emptyList())
    assertTrue(portList.isNotEmpty())
    assertTrue(port > 0)
    val find = portList.find { it.port == port }
    assertNotNull(find)
    portList.forEach(System.out::println)
  }

  @Test
  fun testWin() {
    val systemInfo = SystemInfo()
    val processMap = systemInfo.operatingSystem.processes.associateBy { it.processID }
    val winInternet = WindowsInternetProtocolStats()
    val connections = winInternet.connections
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

  @Test
  fun test1() {
    // 指定一个.exe文件的路径
    val exeFilePath = "C:\\Program Files\\ToDesk\\uninst.exe"


    // 获取文件系统视图
    val fileSystemView = FileSystemView.getFileSystemView()


    // 创建一个文件对象
    val file = File(exeFilePath)


    // 获取文件的图标
    val icon = fileSystemView.getSystemIcon(file)


    // 将图标显示在一个窗口中
    showIcon(icon)
    runBlocking {
      delay(100000)
    }
  }

  private fun showIcon(icon: Icon) {
    val frame = JFrame("Executable Icon Example")
    frame.setSize(200, 200)
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

    // 创建一个标签，并设置图标
    val label = JLabel(icon)

    // 将标签添加到窗口中
    frame.add(label, BorderLayout.CENTER)

    // 显示窗口
    frame.isVisible = true
  }
}