package api

import com.google.gson.JsonObject
import com.google.gson.JsonParser

fun getJsonObject(jsonString: String?): JsonObject {
    val jelement = JsonParser.parseString(jsonString)
    return jelement.asJsonObject
}