package core

import org.tinylog.kotlin.Logger

fun interface PortStrategy {
  fun portList(lastList: List<PortInfo>): List<PortInfo>
}

abstract class ActionStrategy {
  fun closeProcess(pid: Int?): Pair<Boolean, String> {
    if (pid == null) {
      return Pair(false, "No find process $pid")
    }
    val process = ProcessHandle.of(pid.toLong())
    if (process.isEmpty) {
      Logger.warn("[Error] Can not find process $pid. Maybe you should use root.")
      return Pair(false, "Can not find process $pid.")
    }
    val result = process.get().destroyForcibly()
    Logger.info("Close process result $result.")
    return Pair(true, "Success")
  }

  abstract fun open(command: String): Boolean
}
