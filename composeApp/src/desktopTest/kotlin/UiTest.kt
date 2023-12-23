import TestTag.Companion.PORT_ITEM
import TestTag.Companion.PORT_LIST
import TestTag.Companion.SEARCH_INPUT
import androidx.compose.runtime.remember
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class UiTest : PortSupport() {

  @get:Rule
  val rule = createComposeRule()

  @Test
  fun `Get new port test`() {
    val store = AppStore()
    rule.setContent {
      Content(store)
    }
    rule.waitForIdle()

    rule.onNodeWithTag(PORT_LIST, true)
      .performTouchInput { swipeUp() }
      .onChildren()
      .filterToOne(hasTestTag(PORT_ITEM(port)))
      .onChildren()
      .onLast()
      .assertTextEquals(":$port")
  }

  @Test
  fun `Input search text test`() {
    val store = AppStore()
    rule.setContent {
      SearchField(store)
    }
    rule.onNodeWithTag(SEARCH_INPUT)
      .performTextInput("$port")
    Assert.assertTrue(store.state.list.size == 1)
    Assert.assertTrue(store.state.list[0].port == port)
  }


}