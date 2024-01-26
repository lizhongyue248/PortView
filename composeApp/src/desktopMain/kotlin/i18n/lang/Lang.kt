package i18n.lang

import kotlinx.serialization.Serializable

@Serializable
data class Tip(
  val welcome: String,
  val search: String,
  val unknownHelp: String,
  val killTitle: String,
  val kill: String,
  val killError: String,
  val openPath: String,
  val copy: String,
  val errorNoExecute: String,
  val errorNoElevate: String,
  val errorNoRun: String
)

@Serializable
data class Ui(
  val homeNav: String,
  val settingNav: String,
  val showLog: String,
  val language: String,
  val theme: String,
  val keyboard: String,
  val refreshTime: String,
  val unknown: String,
  val confirm: String,
  val cancel: String,
  val elevate: String,
  val themeOption: Map<String, String>,
  val trayExit: String,
  val trayShow: String,
  val trayHide: String,
)

@Serializable
data class Links(
  val sourceCode: String
)

@Serializable
data class Lang(
  val name: String,
  val tip: Tip,
  val ui: Ui,
  val links: Links
)

enum class LangEnum(
  val displayName: String
) {
  ZH("简体中文"), EN("English");

  companion object {
    fun toLang(displayName: String) =
      LangEnum.entries.find {
        it.displayName == displayName
      } ?: ZH
  }
}
