package core.win

import com.sun.jna.platform.win32.Shell32
import core.ActionStrategy
import org.tinylog.kotlin.Logger
import java.io.File

object WindowsAction : ActionStrategy {
  override fun closeProcess(pid: Int?): Pair<Boolean, String> {
    if (pid == null) {
      return Pair(false, "No find process $pid")
    }
    val process = ProcessHandle.of(pid.toLong())
    if (process.isEmpty) {
      Logger.warn("[Error] Can not find process $pid. Maybe you should use root.")
      return Pair(false, "Can not find process \$$pid.")
    }
    val result = process.get().destroyForcibly()
    Logger.info("Close process result $result.")
    return Pair(true, "Success")
  }

  override fun open(file: File): Boolean {
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