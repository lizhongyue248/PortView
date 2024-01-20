package core

import java.io.File

fun interface PortStrategy {
  fun portList(lastList: List<PortInfo>): List<PortInfo>
}

 interface ActionStrategy {
  fun closeProcess(pid: Int?): Pair<Boolean, String>
  fun open(file: File): Boolean
}
