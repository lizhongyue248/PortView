package core

import PortSupport
import core.win.WindowsPort
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WindowsPortTest : PortSupport(){

  @Test
  fun portListTest() {
    val windowsPort = WindowsPort()
    val portList = windowsPort.portList(emptyList())
    assertTrue(portList.isNotEmpty())
    assertTrue(port > 0)
    val find = portList.find { it.port == port }
    assertNotNull(find)
    portList.forEach(System.out::println)
  }

}