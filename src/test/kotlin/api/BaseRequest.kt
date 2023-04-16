package api

import org.apache.http.HttpResponse
import org.apache.http.client.fluent.Request
import org.apache.http.entity.ContentType
import java.io.IOException
import api.RestMethod.*

class BaseRequest(private val baseUrl: String) {

    fun getResponse(restMethod: RestMethod, shortUrl: String, body: String?): BaseResponse {
        return getResponse(restMethod, shortUrl, body, null)
    }

    fun getResponse(
        restMethod: RestMethod,
        shortUrl: String,
        body: String?,
        headers: Map<String, String>?
    ): BaseResponse {
        val url = baseUrl + shortUrl
        val request = when (restMethod) {
            GET -> Request.Get(url)
            POST -> Request.Post(url).bodyString(body, ContentType.APPLICATION_JSON)
            PATCH -> Request.Patch(url).bodyString(body, ContentType.APPLICATION_JSON)
            PUT -> Request.Put(url).bodyString(body, ContentType.APPLICATION_JSON)
            DELETE -> Request.Delete(url)
        }

        //Add headers
        if (!headers.isNullOrEmpty()) {
            for ((key, value) in headers) {
                request.addHeader(key.toString(), value.toString())
            }
        }

        val httpResponse = try {
            request.execute().returnResponse()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        return BaseResponse(httpResponse)
    }
}