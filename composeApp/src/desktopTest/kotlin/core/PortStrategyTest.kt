package core

import PortSupport
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test


class PortStrategyTest : PortSupport() {

  @Test
  fun portListTest() {
    val portList = Platform.portStrategy.portList(emptyList())
    assertTrue(portList.isNotEmpty())
    assertTrue(port > 0)
    val find = portList.find { it.port == port }
    assertNotNull(find)
    portList.forEach(System.out::println)
  }

}