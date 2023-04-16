import Data.User
import Data.UserList
import Data.UserPage
import api.BaseRequest
import api.RestMethod
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import org.apache.http.HttpResponse
import org.apache.http.client.fluent.Request
import org.apache.http.entity.ContentType
import org.apache.http.util.EntityUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.IOException

class PostRegisterTests {
    private val baseRequest = BaseRequest(BASE_URL)
    private val gson = Gson()

    @Test
    @Throws(IOException::class)
    @DisplayName("Post Register. Validation of fields in the response.")
    fun postRegisterSuccessfulTest() {
        //Given
        val user: User = getUserList().data[0]
        val requestBody = setPasswordJson(user.id, "testPassword")
        //When
        val response = baseRequest.getResponse(RestMethod.POST, "/register", requestBody)
        //Then
        Assertions.assertEquals(200, response.statusCode,
            "StatusCode is not 200.")
        Assertions.assertNotNull(response.body, "Body shouldn't be null.")
        val jelement = JsonParser.parseString(response.body)
        val tokenFromResponse = jelement.asJsonObject.getAsJsonPrimitive("token")
        Assertions.assertNotNull(tokenFromResponse, "The token field should be exist")
        val userIdFromResponse = jelement.asJsonObject.getAsJsonPrimitive("id")
        Assertions.assertNotNull(userIdFromResponse, "The id field should be exist")
        val userFromResponse = gson.fromJson(response.body, User::class.java)
        Assertions.assertEquals(userFromResponse.id, user.id,
            "UserId in the request and the response are not equals.")
        Assertions.assertNotNull(
            userFromResponse.token,
            "Token shouldn't be null"
        )
    }

    @Test
    @Throws(IOException::class)
    @DisplayName("Post Register. Non-existent email in the request.")
    fun postRegisterNonExistentEmailTest() {
        //Given
        val user = User()
        user.email = "taleshina@test.com"
        user.password = "testPassword"
        val requestBody = gson.toJson(user)

        //When
        val response = baseRequest.getResponse(RestMethod.POST, "/register", requestBody)

        //Then
        Assertions.assertEquals(
            400, response.statusCode,
            "StatusCode is not 400."
        )
        Assertions.assertNotNull(response.body, "Body shouldn't be null.")
        val jelement = JsonParser.parseString(response.body).asJsonObject
        val errMessage = jelement.getAsJsonPrimitive("error")
        Assertions.assertEquals("\"Note: Only defined users succeed registration\"", errMessage.toString())
    }

    @Test
    @Throws(IOException::class)
    @DisplayName("Post Register. The password field is missing.")
    fun postRegisterPasswordFieldIsMissingTest() {
        //Given
        val user: User = getUserList().data[0]
        val requestBody = gson.toJson(user)
        //When
        val response = baseRequest.getResponse(RestMethod.POST, "/register", requestBody)
        //Then
        Assertions.assertEquals(
            400, response.statusCode,
            "StatusCode is not 400."
        )
        Assertions.assertNotNull(response.body, "Body shouldn't be null.")
        val jelement = JsonParser.parseString(response.body).asJsonObject
        val errMessage = jelement.getAsJsonPrimitive("error")
        Assertions.assertEquals("\"Missing password\"", errMessage.toString())
    }


        @Test
        @Throws(IOException::class)
        @DisplayName("Post Register. The empty request body.")
        fun postRegisterEmptyRequestBodyTest() {
            //Given
            val user = User()
            val requestBody = gson.toJson(user)

            //When
            val response = baseRequest.getResponse(RestMethod.POST, "/register", requestBody)

            //Then
            Assertions.assertEquals(
                400, response.statusCode,
                "StatusCode is not 400."
            )
            Assertions.assertNotNull(response.body, "Body shouldn't be null.")
            val jelement = JsonParser.parseString(response.body).asJsonObject
            val errMessage: JsonPrimitive = jelement.getAsJsonPrimitive("error")
            Assertions.assertEquals("\"Missing email or username\"", errMessage.toString())
        }
            @Test
            @Throws(IOException::class)
            @DisplayName("Post Register. Different tokens for two users.")
            fun postRegisterDifferentTokensForTwoUsersTest() {
                //Given
                val user1: User = getUserList().data[0]
                val user2: User = getUserList().data[1]
                user1.password = "1234"
                user2.password  = "2345"
                val test1 = gson.toJson(user1)
                val test2 = gson.toJson(user2)

                //When
                val response1 = baseRequest.getResponse(RestMethod.POST, "/register", test1)
                val response2 = baseRequest.getResponse(RestMethod.POST, "/register", test2)
                //Then
                Assertions.assertEquals(
                    200, response1.statusCode,
                    "StatusCode is not 200."
                )
                Assertions.assertEquals(
                    200, response2.statusCode,
                    "StatusCode is not 200."
                )
                val token1: String? = gson.fromJson(response1.body, User::class.java).token
                val token2: String? = gson.fromJson(response2.body, User::class.java).token
                Assertions.assertNotSame(token1, token2, "Tokens should be different for the different users.")
            }

    @Throws(IOException::class)
    private fun setPasswordJson(userId: Int, password: String): String {
        val response = baseRequest.getResponse(RestMethod.GET, "/users/$userId", null)

        val userEmail: String? = gson.fromJson(response.body, UserPage::class.java).data?.email
        val data = User()
        data.email = userEmail
        data.password = password
        return gson.toJson(data)
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