package core

class TestTag {
  companion object {
    const val BOTTOM_NAV: String = "BOTTOM_NAV"
    const val NAV_HOME: String = "NAV_HOME"
    const val NAV_SETTING: String = "NAV_SETTING"
    const val TITLE: String = "TITLE"
    const val CLOSE_BUTTON: String = "CLOSE_BUTTON"
    const val CLOSE_ALERT_CONFIRM = "CLOSE_ALERT_CONFIRM"
    const val CLOSE_ALERT_CANCEL = "CLOSE_ALERT_CANCEL"
    const val CLOSE_ALERT = "CLOSE_ALERT"
    const val PORT_LIST = "PORT_LIST"
    const val PORT_SCROLLBAR = "PORT_SCROLLBAR"
    fun PORT_ITEM(num: Int?): String = "PORT_ITEM_${num}"
    const val SEARCH_INPUT = "SEARCH_INPUT"
  }
}