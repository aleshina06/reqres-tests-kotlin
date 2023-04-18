package api

import org.apache.http.HttpResponse
import org.apache.http.util.EntityUtils

class BaseResponse(private val response: HttpResponse) {
    val statusCode = response.statusLine.statusCode

    val body: String
        get() = EntityUtils.toString(response.entity)

    fun getHeader(header: String?): String? {
        return if (response.containsHeader(header)) {
            val headerValue = response.getHeaders(header)[0].value
            headerValue
        } else throw IllegalStateException("Response doesn't contain this header.")
    }

    val isEntityNull = response.entity == null
}