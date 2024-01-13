import net.harawata.appdirs.AppDirsFactory
import org.junit.Test
import java.io.File
import javax.swing.KeyStroke

class OtherTest {
  @Test
  fun test() {
    val CONFIG_PATH: String = AppDirsFactory.getInstance().getUserConfigDir("PortView", null, "zyue") + File.separatorChar + "config.json"
    val file = File(CONFIG_PATH)
    if (!file.exists()) {
      File(file.parent).mkdirs()
      val newFile = file.createNewFile()
      println(newFile)
    }
    println(CONFIG_PATH)
  }

  @Test
  fun textKey() {
    val keyStroke = KeyStroke.getKeyStroke("control alt P")
    assert(keyStroke != null)
  }
}
