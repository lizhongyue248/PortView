package i18n

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.FileResourceLoader
import i18n.lang.Lang
import i18n.lang.LangEnum
import kotlinx.serialization.json.Json
import org.tinylog.kotlin.Logger
import java.io.File
import java.util.Locale
import java.util.Locale.CHINESE

object Locale {

  val langMap: Map<LangEnum, Lang> = initConfig()

  @OptIn(ExperimentalComposeUiApi::class)
  private fun initConfig(): Map<LangEnum, Lang> {
    val json = Json { ignoreUnknownKeys = true }
    val resource = FileResourceLoader(File("lang"))
    if (!resource.root.exists()) {
      throw Exception("Not found lang resource.")
    }
    val listFiles = resource.root.listFiles() ?: throw Exception("Not found lang resource.")
    return listFiles.filter { file ->
      Logger.info("Get file from lang dir ${file.absolutePath}.")
      file.isFile && file.extension == "json"
    }.associate { langFile ->
      val jsonString = langFile.readText()
      val lang = json.decodeFromString<Lang>(jsonString)
      Logger.info("Load lang [${lang.name}] file ${langFile.name} success.")
      LangEnum.toLang(lang.name) to lang
    }
  }

  fun getDefaultLang(): Lang  {
    if (langMap.isEmpty()) {
      throw Exception("Not found any lang resource when get default lang.")
    }
    val systemDefault = Locale.getDefault()
    if (systemDefault == CHINESE && langMap.containsKey(LangEnum.ZH)) {
      return langMap[LangEnum.ZH]!!
    }
    return langMap[langMap.keys.first()]!!
  }
}