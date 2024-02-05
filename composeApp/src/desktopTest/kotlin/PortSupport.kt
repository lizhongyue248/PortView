import org.junit.After
import org.junit.Before
import org.tinylog.kotlin.Logger
import java.net.ServerSocket
import kotlin.properties.Delegates

open class PortSupport {
  private lateinit var socket: ServerSocket
  var port by Delegates.notNull<Int>()

  @Before
  fun before() {
    socket = ServerSocket(0)
    port = socket.localPort
    Logger.info("Start port: $port")
  }

  @After
  fun after() {
    socket.close()
    Logger.info("Close port: $port")
  }
}