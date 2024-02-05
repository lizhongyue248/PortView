package i18n

import org.junit.Test

class LocaleTest {

  @Test
  fun `Get default lang test`() {
    val systemDefault = java.util.Locale.getDefault()
    val defaultLang = Locale.getDefaultLang()
    if (systemDefault == java.util.Locale.CHINESE) {
      assert(defaultLang.name == "简体中文")
    } else if (systemDefault == java.util.Locale.ENGLISH) {
      assert(defaultLang.name == "English")
    }
  }

}