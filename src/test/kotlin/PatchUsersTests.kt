import Data.UserList
import api.BaseRequest
import api.BaseResponse
import api.RestMethod
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import org.apache.http.HttpResponse
import org.apache.http.client.fluent.Request
import org.apache.http.entity.ContentType
import org.apache.http.util.EntityUtils
import org.example.CommonAssertions
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.IOException
import Data.UserWithAdditionalFields as UserWithAdditionalFields

class PatchUsersTests {
    private val baseRequest = BaseRequest(BASE_URL)
    private val gson = Gson()

    @Test
    @Throws(IOException::class)
    @DisplayName("Patch User. Verification of fields in the response.")
    fun patchUsersSuccessfulTest() {
        //Given
        val userId = getUserList().data[0].id
        val user = UserWithAdditionalFields()
        user.job = "engineer"
        user.name = "TestName"
        val requestBody = gson.toJson(user)

        //When
        val response = baseRequest.getResponse(RestMethod.PATCH, "/users/$userId", requestBody)

        //Then
        Assertions.assertEquals(
            200, response.statusCode,
            "StatusCode is not 200."
        )
        val jElement = JsonParser.parseString(response.body).asJsonObject
        val userJob: JsonPrimitive = jElement.getAsJsonPrimitive("job")
        Assertions.assertNotNull(userJob, "The job field should be exist")
        val userName: JsonPrimitive = jElement.getAsJsonPrimitive("name")
        Assertions.assertNotNull(userName, "The name field should be exist")
        val updatedAt: JsonPrimitive = jElement.getAsJsonPrimitive("updatedAt")
        Assertions.assertNotNull(updatedAt, "The updatedAt field should be exist")

        CommonAssertions.checkHeadersListValid(response)
    }

    @Test
    @Throws(IOException::class)
    @DisplayName("Patch User. Adding any field in the request.")
    fun patchUsersAddAnyFieldInRequestTest() {
        //Given
        val userId = getUserList().data[0].id
        val user = UserWithAdditionalFields()
        user.testField = "testField"
        val requestBody = gson.toJson(user)

        //When
        val response = baseRequest.getResponse(RestMethod.PATCH, "/users/$userId", requestBody)

        //Then
        Assertions.assertEquals(
            200, response.statusCode,
            "StatusCode is not 200."
        )
        val jElement = JsonParser.parseString(response.body).asJsonObject
        val testField: JsonPrimitive = jElement.getAsJsonPrimitive("testField")
        Assertions.assertNotNull(testField, "The testField field should be exist")
    }

        @Test
        @Throws(IOException::class)
        @DisplayName("Patch User. Empty request.")
        fun patchUsersEmptyRequestTest() {
            //Given
            val userId: Int = getUserList().data[0].id
            val requestBody = "{}"

            //When
            val response = baseRequest.getResponse(RestMethod.PATCH, "/users/$userId", requestBody)

            //Then
            Assertions.assertEquals(
                200, response.statusCode,
                "StatusCode is not 200."
            )
            val jElement = JsonParser.parseString(response.body).asJsonObject
            val testField: JsonPrimitive = jElement.getAsJsonPrimitive("updatedAt")
            Assertions.assertNotNull(testField, "The testField field should be exist")
        }

    @Throws(IOException::class)
    private fun getUserList(): UserList {
        val allUsersResponse = baseRequest.getResponse(RestMethod.GET, "/users", null)
        val userList: UserList = gson.fromJson(allUsersResponse.body, UserList::class.java)
        if (userList.data == null) {
            throw IllegalStateException("UserList is empty.")
        }
        return gson.fromJson(allUsersResponse.body, UserList::class.java)
    }
}