package core.win

import com.sun.jna.platform.win32.Shell32
import core.ActionStrategy
import org.tinylog.kotlin.Logger
import java.io.File

object WindowsAction : ActionStrategy() {
  override fun open(command: String): Boolean {
    val file = File(command)
    if (!file.exists()) {
      return false
    }
    try {
      Shell32.INSTANCE.ShellExecute(
        null,
        "open",
        "explorer.exe",
        "/select,\"${file.canonicalPath}\"",
        null,
        1
      )
      return true
    } catch (e: Exception) {
      Logger.error("Windows open file error.", e)
      return false
    }
  }

}