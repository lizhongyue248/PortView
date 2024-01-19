package core

fun interface PortStrategy {
  fun portList(lastList: List<PortInfo>): List<PortInfo>
}

fun interface ActionStrategy {
  fun closeProcess(pid: Int?): Pair<Boolean, String>
}
