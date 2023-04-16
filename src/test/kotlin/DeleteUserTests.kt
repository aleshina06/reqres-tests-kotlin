import Data.ResourceList
import api.BaseRequest
import api.BaseResponse
import api.RestMethod
import com.google.gson.Gson
import org.apache.http.client.fluent.Request
import org.apache.http.util.EntityUtils
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