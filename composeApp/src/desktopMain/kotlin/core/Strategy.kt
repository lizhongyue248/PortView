package core

fun interface PortStrategy {
  fun portList(): List<PortInfo>
}

fun interface ActionStrategy {
  fun closeProcess(pid: Int?): Pair<Boolean, String>
}
