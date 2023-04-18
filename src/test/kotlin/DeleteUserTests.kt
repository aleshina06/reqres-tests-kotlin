import Data.ResourceList
import api.BaseRequest
import api.BaseResponse
import api.RestMethod
import com.google.gson.Gson
import org.apache.http.client.fluent.Request
import org.apache.http.util.EntityUtils
import org.example.CommonAssertions
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.IOException

class DeleteUserTests {

    private val baseRequest = BaseRequest(BASE_URL)
    private val gson = Gson()

    @Test
    @Throws(IOException::class)
    @DisplayName("Delete User. Successful deleting user.")
    fun deleteUsersSuccessfulTest() {
        //When
        val response = baseRequest.getResponse(RestMethod.DELETE, "/users/1", null)
        //Then
        Assertions.assertEquals(
            204, response.statusCode,
            "StatusCode is not 204."
        )
        Assertions.assertTrue(response.isEntityNull, "The response should be null.")

        Assertions.assertNotNull(response.getHeader("Date"))
        Assertions.assertNotNull(response.getHeader("Connection"))
        Assertions.assertNotNull(response.getHeader("X-Powered-By"))
        Assertions.assertNotNull(response.getHeader("Etag"))
        Assertions.assertNotNull(response.getHeader("Via"))
        Assertions.assertNotNull(response.getHeader("CF-Cache-Status"))
        Assertions.assertNotNull(response.getHeader("Server"))
        Assertions.assertEquals("Express", response.getHeader("X-Powered-By"))
        Assertions.assertEquals("keep-alive", response.getHeader("Connection"))
    }

    @Test
    @Throws(IOException::class)
    @DisplayName("Delete User. Non-existent user.")
    fun deleteUsersNonExistentUserTest() {
        //Given
        val nonExistentUserId = getResourceList().total + 1
        //When
        val response = baseRequest.getResponse(RestMethod.DELETE, "/users/$nonExistentUserId", null)
        //Then
        Assertions.assertEquals(
            204, response.statusCode,
            "StatusCode is not 204."
        )
        Assertions.assertTrue(response.isEntityNull, "The response should be null.")
    }

    private fun getResourceList(): ResourceList {
        val allResourcesResponse = baseRequest.getResponse(RestMethod.GET, "/unknown", null)

        val resourceList = gson.fromJson(allResourcesResponse.body, ResourceList::class.java)
        if (resourceList.data == null) {
            throw IllegalStateException("UserList is empty.")
        }
        return gson.fromJson(allResourcesResponse.body, ResourceList::class.java)
    }

}