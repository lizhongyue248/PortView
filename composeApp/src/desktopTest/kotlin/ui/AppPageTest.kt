package ui

import PortSupport
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import core.Platform
import core.PortInfo
import core.TestTag
import core.win.WindowsAction
import io.mockk.*
import kotlinx.coroutines.test.runTest
import model.AppStore
import org.junit.Rule
import org.junit.Test

class AppPageTest: PortSupport() {

  @get:Rule
  val rule = createComposeRule()

  @Test
  fun `Get new port list and action compose test`() {
    runTest {
      val store = mockk<AppStore>(relaxed = true)
      val state = mockk<AppStore.AppState>(relaxed = true)
      every { store.state } returns state
      every { state.list } returns listOf(
        PortInfo(
          "test",
          "test",
          "test",
          10,
          "test",
          port,
          "test",
          10,
          null
        )
      )

      rule.setContent {
        Content(store)
      }
      rule.waitForIdle()

      // test scroll bar
      rule.onNodeWithTag(TestTag.PORT_SCROLLBAR)
        .assertExists()

      // test item show
      rule.onNodeWithTag(TestTag.PORT_LIST, true)
        .performTouchInput { swipeUp() }
        .onChildren()
        .filterToOne(hasTestTag(TestTag.PORT_ITEM(port)))
        .onChildren()
        .onLast()
        .assertTextEquals(":$port")

      // test close alert
      rule.onNodeWithTag(TestTag.CLOSE_ALERT)
        .assertDoesNotExist()
      rule.onNodeWithTag(TestTag.PORT_ITEM(port))
        .assertHasClickAction()
        .performClick()
      rule.onNodeWithTag(TestTag.CLOSE_ALERT)
        .assertExists()
        .assertIsDisplayed()

      // mock object
      mockkObject(Platform)
      mockkObject(WindowsAction)
      every { Platform.actionStrategy } returns WindowsAction
      every { WindowsAction.closeProcess(any()) } returns Pair(true, "success")
      every { store.updateItems() } just runs

      // test confirm action
      rule.onNodeWithTag(TestTag.CLOSE_ALERT_CONFIRM)
        .assertExists()
        .performClick()

      rule.onNodeWithTag(TestTag.CLOSE_ALERT)
        .assertDoesNotExist()

      verify(exactly = 1) { WindowsAction.closeProcess(any()) }
      verify(exactly = 1) { store.updateItems() }

      // test close action
      rule.onNodeWithTag(TestTag.PORT_ITEM(port))
        .assertHasClickAction()
        .performClick()
      rule.onNodeWithTag(TestTag.CLOSE_ALERT_CANCEL)
        .assertExists()
        .performClick()
      rule.onNodeWithTag(TestTag.CLOSE_ALERT)
        .assertDoesNotExist()
    }
  }


}