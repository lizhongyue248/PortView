package i18n

import i18n.lang.Lang
import i18n.lang.LangEnum
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource
import org.tinylog.kotlin.Logger
import java.util.Locale
import java.util.Locale.CHINESE

object Locale {

  val langMap: Map<LangEnum, Lang> = initConfig()

  private fun initConfig(): Map<LangEnum, Lang> {
    val json = Json { ignoreUnknownKeys = true }
    return getLangString().associate { langFile ->
      val lang = json.decodeFromString<Lang>(langFile)
      Logger.info("Load lang [${lang.name}] file success.")
      LangEnum.toLang(lang.name) to lang
    }
  }

  @OptIn(ExperimentalResourceApi::class)
  private fun getLangString(): List<String> {
    return runBlocking {
      listOf(
        resource("lang/zh.json").readBytes().decodeToString(),
        resource("lang/en.json").readBytes().decodeToString()
      )
    }
  }

  fun getDefaultLang(): Lang {
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