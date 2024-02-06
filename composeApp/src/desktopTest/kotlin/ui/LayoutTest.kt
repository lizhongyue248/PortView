package ui

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import core.TestTag
import io.mockk.*
import model.AppStore
import org.junit.Rule
import org.junit.Test

class LayoutTest {

  @get:Rule
  val rule = createComposeRule()

  @Test
  fun `Top bar compose test`() {
    val store = mockk<AppStore>(relaxed = true)
    every { store.hidden() } just runs
    rule.setContent {
      TopBar(store)
    }
    rule.onNodeWithTag(TestTag.TITLE)
      .assertExists()
      .assertTextEquals("Port View")
    rule.onNodeWithTag(TestTag.CLOSE_BUTTON)
      .performClick()

    verify(exactly = 1) { store.hidden() }
  }

  @Test
  fun `Bottom nav compose test`() {
    val appStore = AppStore()
    rule.setContent {
      BottomNav(appStore)
    }

    // test icon action
    rule.onNodeWithTag(TestTag.NAV_HOME, useUnmergedTree = true)
      .assertExists()
    rule.onNodeWithTag(TestTag.NAV_SETTING, useUnmergedTree = true)
      .assertExists()
  }

}