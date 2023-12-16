package core

import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.net.ServerSocket
import kotlin.properties.Delegates

class WindowsPortTest {

  private lateinit var socket: ServerSocket
  private var port by Delegates.notNull<Int>()

  @Before
  fun before() {
    socket = ServerSocket(0)
    port = socket.localPort
    println("Start port: $port")
  }

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

  @After
  fun after() {
    socket.close()
    println("Close port: $port")
  }

}