package core

import org.junit.Test
import org.tinylog.kotlin.Logger

class ActionStrategyTest {

  @Test
  fun `Close process test`() {
    val process = getProcessId()
    val pid = process.pid().toInt()
    Logger.info("Start new process on $pid")
    val (nullResult, nullMessage) = Platform.actionStrategy.closeProcess(null)
    assert(!nullResult)
    assert(nullMessage == "No find process null")
    val (emptyResult, emptyMessage) = Platform.actionStrategy.closeProcess(-1)
    assert(!emptyResult)
    assert(emptyMessage == "Can not find process -1.")
    val (successResult, successMessage) = Platform.actionStrategy.closeProcess(pid)
    assert(successResult)
    assert(successMessage == "Success")
    assert(!process.isAlive)
  }

  private fun getProcessId(): Process {
    val countParam = if (Platform.isWindows) {
      "-n"
    } else {
      "-c"
    }
    val processBuilder = ProcessBuilder("ping", countParam, "10000", "127.0.0.1")
    return processBuilder.start()
  }

}