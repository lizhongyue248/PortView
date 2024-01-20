
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.FileResourceLoader
import com.jthemedetecor.OsThemeDetector
import i18n.lang.Lang
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import net.harawata.appdirs.AppDirsFactory
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource
import org.junit.Test
import org.tinylog.kotlin.Logger
import java.io.File

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
    println(OsThemeDetector.getDetector().isDark)
    Logger.info("OsThemeDetector.getDetector().isDark ${OsThemeDetector.getDetector().isDark}")
  }

  @OptIn(ExperimentalComposeUiApi::class)
  @Test
  fun i18nTest() {
    val json = Json { ignoreUnknownKeys = true }
    val resource = this.javaClass.classLoader.getResource("lang")?.toURI() ?: throw Exception("Not found lang resource.")
    val langDir = File(resource)
    val langList = mutableListOf<Lang>()
    langDir.listFiles()?.forEach { langFile ->
      if (!langFile.isFile || langFile.extension != "json") {
        Logger.info("${langFile.name} is not json file, skip.")
        return@forEach
      }
      val jsonString = langFile.readText()
      val lang = json.decodeFromString<Lang>(jsonString)
      langList.add(lang)
      Logger.info("Load lang [${lang.name}] file ${langFile.name} success.")
    }
    val fileResourceLoader = FileResourceLoader(File("lang"))
    println(fileResourceLoader.root.absolutePath)
    println(fileResourceLoader.root.isDirectory)
  }

  @OptIn(ExperimentalResourceApi::class)
  @Test
  fun getLocale() {
//    val currentLocale: Locale = Locale.getDefault()
//    println(currentLocale.language)
//    println(ThemeOption.SYSTEM.name.lowercase())
    runBlocking {
      val decodeToString = resource("lang/zh.json").readBytes().decodeToString()
      println(decodeToString)
    }

  }
}
