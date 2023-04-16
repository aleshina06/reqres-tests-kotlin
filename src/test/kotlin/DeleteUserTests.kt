import Data.ResourceList
import com.google.gson.Gson
import org.apache.http.client.fluent.Request
import org.apache.http.util.EntityUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.IOException

class DeleteUserTests {

    private val baseUrl = "https://reqres.in/api"
    private val gson = Gson()

    @Test
    @Throws(IOException::class)
    @DisplayName("Delete User. Successful deleting user.")
    fun deleteUsersSuccessfulTest() {
        //When
        val response = Request.Delete("$baseUrl/users/1")
            .execute()
            .returnResponse()
        //Then
        Assertions.assertEquals(
            204, response.statusLine.statusCode,
            "StatusCode is not 204."
        )
        Assertions.assertNull(response.entity, "The response should be null.")
    }

    @Test
    @Throws(IOException::class)
    @DisplayName("Delete User. Non-existent user.")
    fun deleteUsersNonExistentUserTest() {
        //Given
        val nonExistentUserId = getResourceList().total + 1
        //When
        val response = Request.Delete("$baseUrl/users/$nonExistentUserId")
            .execute()
            .returnResponse()
        //Then
        Assertions.assertEquals(
            204, response.statusLine.statusCode,
            "StatusCode is not 204."
        )
        Assertions.assertNull(response.entity, "The response should be null.")
    }

    private fun getResourceList(): ResourceList {
        val allResourcesResponse =
            Request.Get("$baseUrl/unknown")
                .execute()
                .returnResponse()
        val bodyString = EntityUtils.toString(allResourcesResponse.entity)

        val resourceList = gson.fromJson(bodyString, ResourceList::class.java)
        if (resourceList.data == null) {
            throw IllegalStateException("UserList is empty.")
        }
        return gson.fromJson(bodyString, ResourceList::class.java)
    }

}