package core.win

import core.ActionStrategy
import org.tinylog.kotlin.Logger

val WindowsAction = ActionStrategy { pid: Int? ->
  if (pid == null) {
    return@ActionStrategy Pair(false, "No find process $pid")
  }
  val process = ProcessHandle.of(pid.toLong())
  if (process.isEmpty) {
    Logger.warn("[Error] Can not find process $pid. Maybe you should use root.")
    return@ActionStrategy Pair(false, "Can not find process \$$pid.")
  }
  val result = process.get().destroyForcibly()
  Logger.info("Close process result $result.")
  return@ActionStrategy Pair(true, "Success")
}