package api

import org.apache.http.HttpResponse
import org.apache.http.util.EntityUtils

class BaseResponse(private val response: HttpResponse) {
    val statusCode = response.statusLine.statusCode

    val body: String
        get() = EntityUtils.toString(response.entity)

    val isEntityNull = response.entity == null
}