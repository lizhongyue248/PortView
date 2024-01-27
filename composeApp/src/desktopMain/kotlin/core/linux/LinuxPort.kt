package core.linux

import core.PortStrategy
import oshi.software.os.linux.LinuxInternetProtocolStats

object LinuxPort : PortStrategy() {

  private val stats = LinuxInternetProtocolStats()

  override fun getInternetProtocolStats() = stats

}