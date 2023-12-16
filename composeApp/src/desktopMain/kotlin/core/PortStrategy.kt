package core

fun interface PortStrategy {
  fun portList(): List<PortInfo>
}