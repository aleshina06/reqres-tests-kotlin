package api

import com.google.gson.Gson
import org.apache.http.HttpResponse
import org.apache.http.util.EntityUtils
import java.io.IOException

class BaseResponse(private val response: HttpResponse) {
    private val gson = Gson()

    val code = response.statusLine.statusCode

    val body: String
        get() = try {
            EntityUtils.toString(response.entity)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
}