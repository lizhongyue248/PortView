
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import core.TestTag.Companion.SEARCH_INPUT
import model.AppStore
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import ui.SearchField

class UiTest : PortSupport() {
  @get:Rule
  val rule = createComposeRule()
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