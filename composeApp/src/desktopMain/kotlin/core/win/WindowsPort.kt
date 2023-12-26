package core.win

import com.sun.jna.Memory
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.*
import com.sun.jna.platform.win32.WinError.ERROR_NOT_ALL_ASSIGNED
import com.sun.jna.ptr.IntByReference
import core.PortInfo
import core.PortStrategy
import java.awt.image.BufferedImage
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

class WindowsPort : PortStrategy {
  private val ipHlpInstance: IPHlpAPI = IPHlpAPI.INSTANCE

  override fun portList(): List<PortInfo> {
    if (!enableDebugPrivilege()) {
      println("Error enable debug privilege.")
    }
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
        println("Error $ret")
      }
    } while (size < sizePtr.value)
    val tcpTable = IPHlpAPI.MIB_TCPTABLE_OWNER_PID(buf)
    val result = mutableListOf<PortInfo>()
    for (mibTcpRow in tcpTable.table) {
      if (mibTcpRow.dwState != IPHlpAPI.MIB_TCP_STATE.MIB_TCP_STATE_LISTEN) {
        continue
      }
      val pid = mibTcpRow.dwOwningPid
      val executablePath = getProcessExecutablePath(pid)
      val image = if (executablePath != "unknown") {
        val largeIcons = arrayOfNulls<WinDef.HICON>(1)
        val smallIcons = arrayOfNulls<WinDef.HICON>(1)
        Shell32.INSTANCE.ExtractIconEx(executablePath, 0, largeIcons, smallIcons, 1)
        toImage(largeIcons[0])
      } else {
        null
      }
      result.add(
        PortInfo(
          Paths.get(executablePath).fileNameWithoutExtension(),
          executablePath, pid,
          formatIPAddress(mibTcpRow.dwLocalAddr), formatPort(mibTcpRow.dwLocalPort),
          formatIPAddress(mibTcpRow.dwRemoteAddr), formatPort(mibTcpRow.dwRemotePort),
          image
        )
      )
    }
    result.sortBy { it.port }
    return result
  }

  private fun getProcessExecutablePath(pid: Int): String {
    return try {
      Kernel32Util.QueryFullProcessImageName(pid, 0)
    } catch (e: Exception) {
      "unknown"
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
      "Unknown"
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
        ) { "GetDIBits should not return 0" }

        bitmapInfo.read()

        val pixels = Memory(bitmapInfo.bmiHeader.biSizeImage.toLong())
        bitmapInfo.bmiHeader.biCompression = WinGDI.BI_RGB
        bitmapInfo.bmiHeader.biHeight = -height

        require(
          gdi32.GetDIBits(
            deviceContext, bitmapHandle, 0, bitmapInfo.bmiHeader.biHeight, pixels, bitmapInfo,
            WinGDI.DIB_RGB_COLORS
          ) != 0
        ) { "GetDIBits should not return 0" }

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
      println("OpenProcessToken failed. Error: " + Native.getLastError())
      return false
    }
    try {
      val luid = WinNT.LUID()
      success = Advapi32.INSTANCE.LookupPrivilegeValue(null, WinNT.SE_DEBUG_NAME, luid)
      if (!success) {
        println("OpenProcessToken failed. Error: " + Native.getLastError())
        return false
      }
      val tkp = WinNT.TOKEN_PRIVILEGES(1)
      tkp.Privileges[0] = WinNT.LUID_AND_ATTRIBUTES(luid, WinDef.DWORD(WinNT.SE_PRIVILEGE_ENABLED.toLong()))
      success = Advapi32.INSTANCE.AdjustTokenPrivileges(hToken.value, false, tkp, 0, null, null)
      val err = Native.getLastError()
      if (!success) {
        println("OpenProcessToken failed. Error: $err")
        return false
      } else if (err == ERROR_NOT_ALL_ASSIGNED) {
        println("Debug privileges not enabled.")
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
