package core

import java.awt.image.BufferedImage

/**
 * 2023/10/7 18:49:50
 * @author yue
 */
data class PortInfo(
  val name: String,
  val command: String,
  val path: String,
  val pid: Int?,
  val address: String?,
  var port: Int?,
  val remoteAddress: String?,
  val remotePort: Int?,
  val image: BufferedImage?
)
