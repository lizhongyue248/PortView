import org.junit.After
import org.junit.Before
import java.net.ServerSocket
import kotlin.properties.Delegates

open class PortSupport {
  private lateinit var socket: ServerSocket
  private var port by Delegates.notNull<Int>()

  @Before
  fun before() {
    socket = ServerSocket(0)
    port = socket.localPort
    println("Start port: $port")
  }

  @After
  fun after() {
    socket.close()
    println("Close port: $port")
  }
}