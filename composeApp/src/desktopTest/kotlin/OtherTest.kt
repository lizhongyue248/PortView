
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.FileResourceLoader
import com.jthemedetecor.OsThemeDetector
import i18n.lang.Lang
import kotlinx.serialization.json.Json
import net.harawata.appdirs.AppDirsFactory
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.junit.Test
import org.tinylog.kotlin.Logger
import java.io.File


class OtherTest {
  @Test
  fun test() {
    val CONFIG_PATH: String = AppDirsFactory.getInstance().getUserConfigDir("PortView", null, null) + File.separatorChar + "config.json"
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
    val appDirs = AppDirsFactory.getInstance()
    println("User data dir: " + appDirs.getUserDataDir("PortView", null, null))
    println(
      "User data dir (roaming): "
        + appDirs.getUserDataDir("PortView", null, null, true)
    )
    println(
      ("User config dir: "
        + appDirs.getUserConfigDir("PortView", null, null))
    )
    println(
      ("User config dir (roaming): "
        + appDirs.getUserConfigDir("PortView", null, null, true))
    )
    println(
      ("User cache dir: "
        + appDirs.getUserCacheDir("PortView", null, null))
    )
    println(
      ("User log dir: "
        + appDirs.getUserLogDir("PortView", null, null))
    )
    println(
      ("User downloads dir: "
        + appDirs.getUserDownloadsDir("PortView", null, null))
    )
    println(
      ("Site data dir: "
        + appDirs.getSiteDataDir("PortView", null, null))
    )
    println(
      ("Site data dir (multi path): "
        + appDirs.getSiteDataDir("PortView", null, null, true))
    )
    println(
      ("Site config dir: "
        + appDirs.getSiteConfigDir("PortView", null, null))
    )
    println(
      ("Site config dir (multi path): "
        + appDirs.getSiteConfigDir("PortView", null, null, true))
    )
    println(
      ("Shared dir: "
        + appDirs.getSharedDir("PortView", null, null))
    )
  }
}
