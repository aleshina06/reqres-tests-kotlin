import Data.User
import Data.UserList
import Data.UserPage
import com.google.gson.GsonBuilder
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
    private val baseUrl = "https://reqres.in/api"
    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    @Test
    @Throws(IOException::class)
    @DisplayName("Post Register. Validation of fields in the response.")
    fun postRegisterSuccessfulTest() {
        //Given
        val user: User = getUserList().data[0]
        val requestBody = setPasswordJson(user.id, "testPassword")
        //When
        val response = Request.Post("$baseUrl/register")
            .bodyString(requestBody, ContentType.APPLICATION_JSON)
            .execute()
            .returnResponse()
        //Then
        Assertions.assertEquals(200, response.statusLine.statusCode,
            "StatusCode is not 200.")
        val body = EntityUtils.toString(response.entity)
        Assertions.assertNotNull(body, "Body shouldn't be null.")
        val jelement = JsonParser.parseString(body)
        val tokenFromResponse: JsonPrimitive = jelement.asJsonObject.getAsJsonPrimitive("token")
        Assertions.assertNotNull(tokenFromResponse, "The token field should be exist")
        val userIdFromResponse: JsonPrimitive = jelement.asJsonObject.getAsJsonPrimitive("id")
        Assertions.assertNotNull(userIdFromResponse, "The id field should be exist")
        val userFromResponse: User = gson.fromJson(body, User::class.java)
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

        //When
        val test = gson.toJson(user)
        val response = Request.Post("$baseUrl/register")
            .bodyString(test, ContentType.APPLICATION_JSON)
            .execute()
            .returnResponse()
        //Then
        Assertions.assertEquals(
            400, response.statusLine.statusCode,
            "StatusCode is not 400."
        )
        val body = EntityUtils.toString(response.entity)
        Assertions.assertNotNull(body, "Body shouldn't be null.")
        val jelement = JsonParser.parseString(body).asJsonObject
        val errMessage: JsonPrimitive = jelement.getAsJsonPrimitive("error")
        Assertions.assertEquals("\"Note: Only defined users succeed registration\"", errMessage.toString())
    }

    @Test
    @Throws(IOException::class)
    @DisplayName("Post Register. The password field is missing.")
    fun postRegisterPasswordFieldIsMissingTest() {
        //Given
        val user: User = getUserList()!!.data!![0]

        //When
        val test = gson.toJson(user)
        val response = Request.Post("$baseUrl/register")
            .bodyString(test, ContentType.APPLICATION_JSON)
            .execute()
            .returnResponse()
        //Then
        Assertions.assertEquals(
            400, response.statusLine.statusCode,
            "StatusCode is not 400."
        )
        val body = EntityUtils.toString(response.entity)
        Assertions.assertNotNull(body, "Body shouldn't be null.")
        val jelement = JsonParser.parseString(body).asJsonObject
        val errMessage: JsonPrimitive = jelement.getAsJsonPrimitive("error")
        Assertions.assertEquals("\"Missing password\"", errMessage.toString())
    }


        @Test
        @Throws(IOException::class)
        @DisplayName("Post Register. The empty request body.")
        fun postRegisterEmptyRequestBodyTest() {
            //Given
            val user = User()

            //When
            val bodyString = gson.toJson(user)
            val response = Request.Post("$baseUrl/register")
                .bodyString(bodyString, ContentType.APPLICATION_JSON)
                .execute()
                .returnResponse()
            //Then
            Assertions.assertEquals(
                400, response.statusLine.statusCode,
                "StatusCode is not 400."
            )
            val body = EntityUtils.toString(response.entity)
            Assertions.assertNotNull(body, "Body shouldn't be null.")
            val jelement = JsonParser.parseString(body).asJsonObject
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
                val response1 = Request.Post("$baseUrl/register")
                    .bodyString(test1, ContentType.APPLICATION_JSON)
                    .execute()
                    .returnResponse()
                val response2 = Request.Post("$baseUrl/register")
                    .bodyString(test2, ContentType.APPLICATION_JSON)
                    .execute()
                    .returnResponse()
                //Then
                Assertions.assertEquals(
                    200, response1.statusLine.statusCode,
                    "StatusCode is not 200."
                )
                Assertions.assertEquals(
                    200, response2.statusLine.statusCode,
                    "StatusCode is not 200."
                )
                val body1 = EntityUtils.toString(response1.entity)
                val body2 = EntityUtils.toString(response2.entity)
                val token1: String? = gson.fromJson<User>(body1, User::class.java).token
                val token2: String? = gson.fromJson<User>(body2, User::class.java).token
                Assertions.assertNotSame(token1, token2, "Tokens should be different for the different users.")
            }

    @Throws(IOException::class)
    private fun setPasswordJson(userId: Int, password: String): String {
        val response = Request.Get("$baseUrl/users/$userId")
            .execute()
            .returnResponse()

        val responseBodyString = EntityUtils.toString(response.entity)
        val userEmail: String? = gson.fromJson<UserPage>(responseBodyString, UserPage::class.java).data?.email
        val data = User()
        data.email = userEmail
        data.password = password
        return gson.toJson(data)
    }

    @Throws(IOException::class)
    private fun getUserList(): UserList {
        val allUsersResponse: HttpResponse =
            Request.Get("$baseUrl/users")
                .execute()
                .returnResponse()
        val bodyString = EntityUtils.toString(allUsersResponse.entity)
        val userList: UserList = gson.fromJson(bodyString, UserList::class.java)
        if (userList.data == null) {
            throw IllegalStateException("UserList is empty.")
        }
        return gson.fromJson(bodyString, UserList::class.java)
    }
}