import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import core.PortInfo
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

class AppStore {
  var state: AppState by mutableStateOf(initialState())
    private set

  fun show() {
    setState {
      copy(isVisible = true)
    }
  }

  fun visibleToggle() {
    setState {
      copy(isVisible = !isVisible)
    }
  }

  fun hidden() {
    setState {
      copy(isVisible = false)
    }
  }

  fun changeSearchText(value: String) {
    setState {
      copy(searchText = value)
    }
  }

  suspend fun setTimer() {
    while (true) {
      delay(5.seconds)
      setState {
        copy(items = getPortStrategy().portList())
      }
    }
  }

  private fun initialState(): AppState {
    val portStrategy = getPortStrategy()
    return AppState(
      items = portStrategy.portList()
    )
  }

  private inline fun setState(update: AppState.() -> AppState) {
    state = state.update()
  }

  data class AppState(
    val items: List<PortInfo> = emptyList(),
    val searchText: String = "",
    val editingItemId: Long? = null,
    val isVisible: Boolean = false
  ) {
    val list: List<PortInfo> get() {
      val conditionInt = searchText.toIntOrNull()
      return items.filter {
        it.name.trim().contains(searchText.trim(), ignoreCase = true)
          || (conditionInt != null && it.port == conditionInt)
      }
    }
  }
}