package core.win

import com.sun.jna.Native
import com.sun.jna.platform.win32.*
import core.Platform
import core.PortStrategy
import org.tinylog.kotlin.Logger
import oshi.software.os.windows.WindowsInternetProtocolStats
import java.awt.image.BufferedImage
import java.io.File
import javax.swing.Icon
import javax.swing.filechooser.FileSystemView


object WindowsPort : PortStrategy() {
  private val stats = WindowsInternetProtocolStats()
  private val systemView = FileSystemView.getFileSystemView()

  init {
    if (!enableDebugPrivilege()) {
      Platform.isDebug = false
      Logger.info("Current os doesn't enable debug privilege.")
    } else {
      Platform.isDebug = true
      Logger.info("Current os enable debug privilege.")
    }
  }

  override fun getInternetProtocolStats() = stats

  override fun getIcon(command: String): BufferedImage? {
    val exeFile = File(command)
    if (!exeFile.exists()) {
      return null
    }
    val icon = systemView.getSystemIcon(exeFile)
    return iconToBufferedImage(icon)
  }

  private fun iconToBufferedImage(icon: Icon): BufferedImage {
    val bufferedImage = BufferedImage(icon.iconWidth, icon.iconHeight, BufferedImage.TYPE_INT_ARGB)
    val g2d = bufferedImage.createGraphics()
    icon.paintIcon(null, g2d, 0, 0)
    g2d.dispose()
    return bufferedImage
  }

  private fun enableDebugPrivilege(): Boolean {
    val hToken = WinNT.HANDLEByReference()
    var success = Advapi32.INSTANCE.OpenProcessToken(
      Kernel32.INSTANCE.GetCurrentProcess(),
      WinNT.TOKEN_QUERY or WinNT.TOKEN_ADJUST_PRIVILEGES, hToken
    )
    if (!success) {
      Logger.debug("OpenProcessToken 1 failed. Error: ${Native.getLastError()}")
      return false
    }
    try {
      val luid = WinNT.LUID()
      success = Advapi32.INSTANCE.LookupPrivilegeValue(null, WinNT.SE_DEBUG_NAME, luid)
      if (!success) {
        Logger.debug("OpenProcessToken 2 failed. Error: ${Native.getLastError()}")
        return false
      }
      val tkp = WinNT.TOKEN_PRIVILEGES(1)
      tkp.Privileges[0] = WinNT.LUID_AND_ATTRIBUTES(luid, WinDef.DWORD(WinNT.SE_PRIVILEGE_ENABLED.toLong()))
      success = Advapi32.INSTANCE.AdjustTokenPrivileges(hToken.value, false, tkp, 0, null, null)
      val err = Native.getLastError()
      if (!success) {
        Logger.debug("OpenProcessToken 3 failed. Error: $err")
        return false
      } else if (err == WinError.ERROR_NOT_ALL_ASSIGNED) {
        Logger.debug("Debug privileges not enabled.")
        return false
      }
    } finally {
      Kernel32.INSTANCE.CloseHandle(hToken.value)
    }
    Logger.debug("Debug privileges enabled.")
    return true
  }

}

