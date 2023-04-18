package org.example

import api.BaseResponse
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
    fun checkHeadersListValid(response: BaseResponse) {
        Assertions.assertNotNull(response.getHeader("Date"))
        Assertions.assertNotNull(response.getHeader("Content-Type"))
        Assertions.assertNotNull(response.getHeader("Transfer-Encoding"))
        Assertions.assertNotNull(response.getHeader("Connection"))
        Assertions.assertNotNull(response.getHeader("X-Powered-By"))
        Assertions.assertNotNull(response.getHeader("Etag"))
        Assertions.assertNotNull(response.getHeader("Via"))
        Assertions.assertNotNull(response.getHeader("CF-Cache-Status"))
        Assertions.assertNotNull(response.getHeader("Server"))
        Assertions.assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"))
        Assertions.assertEquals("Express", response.getHeader("X-Powered-By"))
        Assertions.assertEquals("keep-alive", response.getHeader("Connection"))
    }
}