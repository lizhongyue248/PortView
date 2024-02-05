package model

import core.Platform
import core.PortInfo
import core.win.WindowsPort
import i18n.lang.LangEnum
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.time.Duration.Companion.seconds

class AppStoreTest {

  private val json = Json { ignoreUnknownKeys = true }

  @Before
  fun before() {
    val configFile = File(CONFIG_PATH)
    if (configFile.exists()) {
      assert(configFile.delete())
    }
  }

  @Test
  fun `Get default config test`() {
    val appStore = AppStore()
    val config = appStore.config
    assert(config.language == LangEnum.ZH)
    assert(config.theme == ThemeOption.SYSTEM)
    assert(config.keyboard == "ctrl shift P")
    assert(config.refreshTime == 60)
    assert(!config.showUnknown)
    assert(!config.showNotification)
  }

  @Test
  fun `Get initial state test`() {
    val appStore = AppStore()
    val state = appStore.state
    val config = appStore.config
    assert(state.keyboard == config.getKeyStrokeString())
    assert(state.showUnknown == config.showUnknown)
  }

  @Test
  @OptIn(ExperimentalCoroutinesApi::class)
  fun `Visible toggle test`() = runTest {
    val appStore = AppStore(this)
    val visible = appStore.state.isVisible
    appStore.visibleToggle()
    advanceUntilIdle()
    assert(appStore.state.items.isNotEmpty())
    assert(appStore.state.isVisible == !visible)
    assert(!appStore.isEmpty())
  }

  @Test
  fun `Hidden windows test`() = runTest {
    val appStore = AppStore()
    appStore.hidden()
    assert(!appStore.state.isVisible)
  }

  @Test
  fun `Change search text test`() = runTest {
    val appStore = AppStore()
    appStore.changeSearchText("Hello")
    assert(appStore.state.searchText == "Hello")
  }

  @Test
  @OptIn(ExperimentalCoroutinesApi::class)
  fun `Init port list test`() =
    runTest {
      val appStore = AppStore(this)
      appStore.updateItems()
      advanceUntilIdle()
      assert(appStore.state.items.isNotEmpty())
    }

  @Test
  @OptIn(ExperimentalCoroutinesApi::class)
  fun `Get new port list test`() =
    runTest(timeout = 60.seconds) {

      val firstList = listOf(
        PortInfo(
          "new port",
          "new port",
          "new port",
          1111111,
          "1111111",
          1111111,
          "111111",
          1111111,
          null
        )
      )
      val secondList = firstList.toMutableList()
      secondList.add(
        PortInfo(
          "2 new port",
          "2 new port",
          "2 new port",
          2222222,
          "2222222",
          2222222,
          "2222222",
          2222222,
          null
        )
      )

      mockkObject(Platform)
      every { Platform.portStrategy } returns WindowsPort
      mockkObject(WindowsPort)
      every { WindowsPort.portList(emptyList(), any()) } returns firstList
      every { WindowsPort.portList(firstList, any()) } answers {
        val newPortHook = secondArg<(newPorts: Set<PortInfo>) -> Unit>()
        newPortHook(secondList.toSet())
        secondList
      }

      val appStore = spyk(AppStore(this))

      every { appStore.sendNotification(any(), any(), any()) } returns Unit

      appStore.updateItems()
      advanceUntilIdle()
      assert(appStore.state.items.size == 1)

      // mock notification action
      appStore.configShowNotice(true)
      appStore.updateItems()
      advanceUntilIdle()
      assert(appStore.state.items.size == 2)

      // verify call times
      verify(atLeast = 1) { Platform.portStrategy }
      verify(exactly = 1) { WindowsPort.portList(emptyList(), any()) }
      verify(exactly = 1) { WindowsPort.portList(firstList, any()) }
      verify(exactly = 1) { appStore.sendNotification(any(), any(), any()) }
    }

  @Test
  fun `Change current tab test`() {
    val appStore = AppStore()
    appStore.changeCurrentTab(1)
    assert(appStore.state.currentTab == 1)
    appStore.changeCurrentTab(0)
    assert(appStore.state.currentTab == 0)
  }

  @Test
  fun `Update keyboard test`() {
    val appStore = AppStore()
    appStore.configKeyboard("ctrl P")
    appStore.updateKeyboard()
    assert(appStore.state.keyboard == appStore.config.getKeyStrokeString())
  }

  @Test
  fun `Config update test`() {
    val appStore = AppStore()
    val keyboard = "ctrl shift O"
    appStore.configKeyboard(keyboard)
    appStore.configTheme(ThemeOption.DARK)
    appStore.configLanguage(LangEnum.EN)
    appStore.configRefreshTime(1000)
    appStore.configShowUnknown(false)
    appStore.configShowNotice(false)
    assert(appStore.config.keyboard == keyboard)
    assert(appStore.config.theme == ThemeOption.DARK)
    assert(appStore.config.language == LangEnum.EN)
    assert(appStore.config.refreshTime == 1000)
    assert(!appStore.config.showUnknown)
    assert(!appStore.config.showNotification)
    val file = File(CONFIG_PATH)
    val config = json.decodeFromString<AppStore.ConfigState>(file.readText())
    assert(config.keyboard == keyboard)
    assert(config.theme == ThemeOption.DARK)
    assert(config.language == LangEnum.EN)
    assert(config.refreshTime == 1000)
    assert(!config.showUnknown)
    assert(!config.showNotification)
  }

}