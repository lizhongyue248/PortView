package core

import PortSupport
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.net.ServerSocket
import kotlin.properties.Delegates

class WindowsPortTest : PortSupport(){

  @Test
  fun portListTest() {
    val windowsPort = WindowsPort()
    val portList = windowsPort.portList()
    assertTrue(portList.isNotEmpty())
    assertTrue(port > 0)
    val find = portList.find { it.port == port }
    assertNotNull(find)
    portList.forEach(System.out::println)
  }

}