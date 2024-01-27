package core.linux

import core.ActionStrategy
import java.awt.Desktop
import java.io.File

object LinuxAction : ActionStrategy(){
  override fun open(path: String): Boolean {
    val file = File(path)
    if (!file.exists()) {
      return false
    }
    Desktop.getDesktop().open(file.parentFile)
    return true
  }
}