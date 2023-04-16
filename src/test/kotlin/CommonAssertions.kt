package org.example

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import org.junit.jupiter.api.Assertions

object CommonAssertions {
    fun checkBodySchemaListValid(body: String?) {
        Assertions.assertNotNull(body, "Content shouldn't be null.")
        val jelement = JsonParser.parseString(body).asJsonObject
        val page: JsonPrimitive = jelement.getAsJsonPrimitive("page")
        Assertions.assertNotNull(page, "The page field should be exist")
        val perPage: JsonPrimitive = jelement.getAsJsonPrimitive("per_page")
        Assertions.assertNotNull(perPage, "The per_page field should be exist")
        val total: JsonPrimitive = jelement.getAsJsonPrimitive("total")
        Assertions.assertNotNull(total, "The total field should be exist")
        val totalPages: JsonPrimitive = jelement.getAsJsonPrimitive("total_pages")
        Assertions.assertNotNull(totalPages, "The total_pages field should be exist")
        val data: JsonArray = jelement.getAsJsonArray("data")
        Assertions.assertNotNull(data, "The data field should be exist")
    }
}