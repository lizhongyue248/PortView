package core.win

import com.sun.jna.Memory
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.*
import com.sun.jna.platform.win32.WinDef.HICON
import com.sun.jna.platform.win32.WinError.ERROR_NOT_ALL_ASSIGNED
import com.sun.jna.ptr.IntByReference
import core.Platform
import core.PortInfo
import core.PortStrategy
import model.UNKNOWN
import org.tinylog.kotlin.Logger
import java.awt.image.BufferedImage
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*


object WindowsPort : PortStrategy {
  private val ipHlpInstance: IPHlpAPI = IPHlpAPI.INSTANCE

  init {
    if (!enableDebugPrivilege()) {
      Platform.isDebug = false
      Logger.info("Current os doesn't enable debug privilege.")
    } else {
      Platform.isDebug = true
      Logger.info("Current os enable debug privilege.")
    }
  }

  override fun portList(lastList: List<PortInfo>): List<PortInfo> {
    val sizePtr = IntByReference()
    ipHlpInstance.GetExtendedTcpTable(null, sizePtr, false, IPHlpAPI.AF_INET, IPHlpAPI.TCP_TABLE_CLASS.TCP_TABLE_OWNER_PID_ALL, 0)
    var size: Int
    var buf: Memory
    do {
      size = sizePtr.value
      buf = Memory(size.toLong())
      val ret: Int = ipHlpInstance.GetExtendedTcpTable(
        buf, sizePtr, false, IPHlpAPI.AF_INET, IPHlpAPI.TCP_TABLE_CLASS.TCP_TABLE_OWNER_PID_ALL,
        0
      )
      if (WinError.NO_ERROR != ret) {
        Logger.info("GetExtendedTcpTable error $ret ")
      }
    } while (size < sizePtr.value)
    val lastListMap = lastList.associateBy { it.pid }
    val existList = mutableListOf<PortInfo>()
    return IPHlpAPI.MIB_TCPTABLE_OWNER_PID(buf).table.asSequence().filter {
      if (it.dwState != IPHlpAPI.MIB_TCP_STATE.MIB_TCP_STATE_LISTEN) {
        return@filter false
      }
      val value = lastListMap[it.dwOwningPid]
      if (value != null) {
        existList.add(value)
        return@filter false
      }
      return@filter true
    }
      .map { mibTcpRow ->
        val executablePath = getProcessExecutablePath(mibTcpRow.dwOwningPid)
        val image = getProcessExecutableImage(executablePath)
        PortInfo(
          Paths.get(executablePath).fileNameWithoutExtension(),
          executablePath, mibTcpRow.dwOwningPid,
          formatIPAddress(mibTcpRow.dwLocalAddr), formatPort(mibTcpRow.dwLocalPort),
          formatIPAddress(mibTcpRow.dwRemoteAddr), formatPort(mibTcpRow.dwRemotePort),
          image
        )
      }
      .plus(existList)
      .distinctBy { it.pid }
      .sortedBy { it.port }.toList()
  }

  private fun getProcessExecutableImage(executablePath: String) = if (executablePath != UNKNOWN) {
    val iconCount = Shell32.INSTANCE.ExtractIconEx(executablePath, 0, null, null, 0)
    Logger.debug("$executablePath get icon count $iconCount")
    if (iconCount == 0) {
      null
    } else {
      val icons = arrayOfNulls<HICON>(iconCount)
      Shell32.INSTANCE.ExtractIconEx(executablePath, 0, icons, null, iconCount)
      Logger.debug("$executablePath get icon count ${icons[0]}")
      toImage(icons[0])
    }
  } else {
    Logger.debug("$executablePath do not get icon.")
    null
  }

  private fun getProcessExecutablePath(pid: Int): String {
    return try {
      Kernel32Util.QueryFullProcessImageName(pid, 0)
    } catch (e: Exception) {
      UNKNOWN
    }
  }


  private fun formatIPAddress(rawAddress: Int): String {
    return try {
      val bytes = byteArrayOf(
        (rawAddress and 0xFF).toByte(), (rawAddress shr 8 and 0xFF).toByte(),
        (rawAddress shr 16 and 0xFF).toByte(), (rawAddress shr 24 and 0xFF).toByte()
      )
      InetAddress.getByAddress(bytes).hostAddress
    } catch (e: UnknownHostException) {
      UNKNOWN
    }
  }

  private fun formatPort(value: Int): Int {
    return value and 0xFF shl 8 or (value shr 8 and 0xFF)
  }

  private fun toImage(hicon: WinDef.HICON?): BufferedImage? {
    if (hicon == null) {
      return null
    }
    var bitmapHandle: WinDef.HBITMAP? = null
    val user32 = User32.INSTANCE
    val gdi32 = GDI32.INSTANCE

    try {
      val info = WinGDI.ICONINFO()
      if (!user32.GetIconInfo(hicon, info)) return null

      info.read()
      bitmapHandle = Optional.ofNullable(info.hbmColor).orElse(info.hbmMask)

      val bitmap = WinGDI.BITMAP()
      if (gdi32.GetObject(bitmapHandle, bitmap.size(), bitmap.pointer) > 0) {
        bitmap.read()

        val width = bitmap.bmWidth.toInt()
        val height = bitmap.bmHeight.toInt()

        val deviceContext = user32.GetDC(null)
        val bitmapInfo = WinGDI.BITMAPINFO()

        bitmapInfo.bmiHeader.biSize = bitmapInfo.bmiHeader.size()
        require(
          gdi32.GetDIBits(
            deviceContext, bitmapHandle, 0, 0, Pointer.NULL, bitmapInfo,
            WinGDI.DIB_RGB_COLORS
          ) != 0
        ) {
          Logger.warn("GetDIBits 1 should not return 0.")
        }

        bitmapInfo.read()

        val pixels = Memory(bitmapInfo.bmiHeader.biSizeImage.toLong())
        bitmapInfo.bmiHeader.biCompression = WinGDI.BI_RGB
        bitmapInfo.bmiHeader.biHeight = -height

        require(
          gdi32.GetDIBits(
            deviceContext, bitmapHandle, 0, bitmapInfo.bmiHeader.biHeight, pixels, bitmapInfo,
            WinGDI.DIB_RGB_COLORS
          ) != 0
        ) {
          Logger.warn("GetDIBits 2 should not return 0.")
        }

        val colorArray = pixels.getIntArray(0, width * height)
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        image.setRGB(0, 0, width, height, colorArray, 0, width)

        return image
      }
    } finally {
      gdi32.DeleteObject(hicon)
      Optional.ofNullable(bitmapHandle).ifPresent { hObject: WinDef.HBITMAP? -> gdi32.DeleteObject(hObject) }
    }
    return null
  }

  private fun enableDebugPrivilege(): Boolean {
    val hToken = WinNT.HANDLEByReference()
    var success = Advapi32.INSTANCE.OpenProcessToken(
      Kernel32.INSTANCE.GetCurrentProcess(),
      WinNT.TOKEN_QUERY or WinNT.TOKEN_ADJUST_PRIVILEGES, hToken
    )
    if (!success) {
      Logger.warn("OpenProcessToken 1 failed. Error: ${Native.getLastError()}")
      return false
    }
    try {
      val luid = WinNT.LUID()
      success = Advapi32.INSTANCE.LookupPrivilegeValue(null, WinNT.SE_DEBUG_NAME, luid)
      if (!success) {
        Logger.warn("OpenProcessToken 2 failed. Error: ${Native.getLastError()}")
        return false
      }
      val tkp = WinNT.TOKEN_PRIVILEGES(1)
      tkp.Privileges[0] = WinNT.LUID_AND_ATTRIBUTES(luid, WinDef.DWORD(WinNT.SE_PRIVILEGE_ENABLED.toLong()))
      success = Advapi32.INSTANCE.AdjustTokenPrivileges(hToken.value, false, tkp, 0, null, null)
      val err = Native.getLastError()
      if (!success) {
        Logger.warn("OpenProcessToken 3 failed. Error: $err")
        return false
      } else if (err == ERROR_NOT_ALL_ASSIGNED) {
        Logger.info("Debug privileges not enabled.")
        return false
      }
    } finally {
      Kernel32.INSTANCE.CloseHandle(hToken.value)
    }
    return true
  }

}


private fun Path.fileNameWithoutExtension(): String {
  val fileNameWithExtension: String = this.fileName.toString()
  val lastDotIndex = fileNameWithExtension.lastIndexOf(".")
  return if (lastDotIndex != -1) {
    fileNameWithExtension.substring(0, lastDotIndex)
  } else {
    fileNameWithExtension
  }
}
