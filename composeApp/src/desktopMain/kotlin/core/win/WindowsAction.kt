package core.win

import core.ActionStrategy

class WindowsAction : ActionStrategy {
  override fun closeProcess(pid: Int?): Pair<Boolean, String> {
    if (pid == null) {
      return Pair(false, "No find process $pid")
    }
    val process = ProcessHandle.of(pid.toLong())
    if (process.isEmpty) {
      println("[Error] Can not find process $pid. Maybe you should use root.")
      return Pair(false, "Can not find process \$$pid.")
    }
    val result = process.get().destroyForcibly()
    println("Close process result $result.")
    return Pair(true, "Success")
  }
}