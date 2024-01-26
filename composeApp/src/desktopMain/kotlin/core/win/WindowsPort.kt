package core.win

import core.PortStrategy
import oshi.software.os.windows.WindowsInternetProtocolStats
import java.awt.image.BufferedImage
import java.io.File
import javax.swing.Icon
import javax.swing.filechooser.FileSystemView


object WindowsPort : PortStrategy() {
  private val stats = WindowsInternetProtocolStats()
  private val systemView = FileSystemView.getFileSystemView()

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
}

