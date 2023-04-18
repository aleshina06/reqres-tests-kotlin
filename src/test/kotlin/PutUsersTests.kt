import Data.UserList
import Data.UserWithAdditionalFields
import api.BaseRequest
import api.RestMethod
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import org.example.CommonAssertions
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.IOException

class PutUsersTests {
    private val baseRequest = BaseRequest(BASE_URL)
    private val gson = Gson()

    @Test
    @Throws(IOException::class)
    @DisplayName("Put User. Verification of fields in the response.")
    fun putUsersSuccessfulTest() {
        //Given
        val userId = 1
        val user = UserWithAdditionalFields()
        user.job = "UserJob"
        user.name = "TestName"
        val requestBody = gson.toJson(user)

        //When
        val response = baseRequest.getResponse(RestMethod.PUT, "/users/$userId", requestBody)

        //Then
        Assertions.assertEquals(
            200, response.statusCode,
            "StatusCode is not 200."
        )
        CommonAssertions.checkHeadersListValid(response)
        val jElement = JsonParser.parseString(response.body).asJsonObject
        val userJob = jElement.getAsJsonPrimitive("job")
        Assertions.assertNotNull(userJob, "The job field should be exist")
        val userName = jElement.getAsJsonPrimitive("name")
        Assertions.assertNotNull(userName, "The name field should be exist")
        val updatedAt = jElement.getAsJsonPrimitive("updatedAt")
        Assertions.assertNotNull(updatedAt, "The updatedAt field should be exist")
    }

    @Test
    @Throws(IOException::class)
    @DisplayName("Put User. Adding any field in the request.")
    fun putUsersAddAnyFieldInRequestTest() {
        //Given
        val userId: Int = getUserList().data[0].id
        val user = UserWithAdditionalFields()
        user.testField = "test"
        val requestBody = gson.toJson(user)

        //When
        val response = baseRequest.getResponse(RestMethod.PUT, "/users/$userId", requestBody)

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
    @DisplayName("Put User. Empty request.")
    fun putUsers_EmptyRequestTest() {
        //Given
        val userId: Int = getUserList().data[0].id
        val requestBody = "{}"

        //When
        val response = baseRequest.getResponse(RestMethod.PUT, "/users/$userId", requestBody)

        //Then
        Assertions.assertEquals(
            200, response.statusCode,
            "StatusCode is not 200."
        )
        val jElement = JsonParser.parseString(response.body).asJsonObject
        val testField = jElement.getAsJsonPrimitive("updatedAt")
        Assertions.assertNotNull(testField, "The testField field should be exist")
    }

    @Test
    @Throws(IOException::class)
    @DisplayName("Put User. Any userId in the url.")
    fun putUsers_AnyUserIdInUrlTest() {
        //Given
        val userId = 3333
        val requestBody = "{}"

        //When
        val response = baseRequest.getResponse(RestMethod.PUT, "/users/$userId", requestBody)

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