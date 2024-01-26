package core.mac

import core.ActionStrategy
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * 2024/1/26 16:17:14
 * @author yue
 */
object MacAction : ActionStrategy() {
  override fun open(command: String): Boolean {
    val filePath = getFilePath(command)
    val file = File(filePath)
    if (!file.exists()) {
      return false
    }
    val script = arrayOf("open", "-R", file.path)
    val process = Runtime.getRuntime().exec(script)
    return process.waitFor(3, TimeUnit.SECONDS)
  }

  private fun getFilePath(command: String): String {
    return if (command.contains(" -")) {
      command.substringBefore(" -")
    } else {
      command
    }
  }
}