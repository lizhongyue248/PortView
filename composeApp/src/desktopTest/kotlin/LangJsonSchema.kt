
import com.github.ricky12awesome.jss.encodeToSchema
import i18n.lang.Lang
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.junit.Test
import java.io.File

@OptIn(ExperimentalSerializationApi::class)
val myGlobalJson by lazy {
  Json {
    prettyPrintIndent = "  "
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
    coerceInputValues = true
    encodeDefaults = true
    classDiscriminator = "classDiscriminator"
  }
}

class LangJsonSchema {
  @Test
  fun generate() {
    val encodeToSchema = myGlobalJson.encodeToSchema(Lang.serializer())
    val file = File("src/desktopMain/resources/lang/lang-schema.json")
    file.writeText(encodeToSchema)
    println(file.absolutePath)
  }
}