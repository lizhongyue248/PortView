package i18n

import i18n.lang.Lang
import i18n.lang.LangEnum
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.tinylog.kotlin.Logger
import portview.composeapp.generated.resources.Res
import java.util.Locale
import java.util.Locale.CHINESE
import java.util.Locale.ENGLISH

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
        Res.readBytes("files/lang/zh.json").decodeToString(),
        Res.readBytes("files/lang/en.json").decodeToString(),
      )
    }
  }

  fun getDefaultLang(): Lang {
    if (langMap.isEmpty()) {
      throw RuntimeException("Not found any lang resource when get default lang.")
    }
    val systemDefault = Locale.getDefault()
    if (systemDefault == CHINESE && langMap.containsKey(LangEnum.ZH)) {
      return langMap[LangEnum.ZH]!!
    }
    if (systemDefault == ENGLISH && langMap.containsKey(LangEnum.EN)) {
      return langMap[LangEnum.EN]!!
    }
    return langMap[langMap.keys.first()]!!
  }
}