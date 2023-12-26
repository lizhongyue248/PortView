package core

class TestTag {
  companion object {
    const val PORT_LIST = "port-list"
    fun PORT_ITEM(num: Int?): String = "port-${num}"
    const val SEARCH_INPUT = "search-input"
  }
}