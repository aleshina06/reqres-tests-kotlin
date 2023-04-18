import Data.User
import Data.UserList
import api.BaseRequest
import api.BaseResponse
import api.RestMethod
import api.getJsonObject
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.example.CommonAssertions
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.IOException

class GetUsersTests {
    private val baseRequest: BaseRequest = BaseRequest(BASE_URL)
    private val gson = Gson()
    @Test
    fun GetUsers_SuccessfulTest() {
        //When
        val response: BaseResponse = baseRequest.getResponse(RestMethod.GET, "/users", null)
        //Then
        Assertions.assertEquals(
            200, response.statusCode,
            "StatusCode is not 200."
        )
        CommonAssertions.checkBodySchemaListValid(response.body)
        CommonAssertions.checkHeadersListValid(response)
    }

    @Test
    fun GetUsers_PageParamTest() {
        //Given
        val numberOfPage = 1

        //When
        val response: BaseResponse = baseRequest
            .getResponse(RestMethod.GET, String.format("/users?page=%s", numberOfPage), null)

        //Then
        CommonAssertions.checkBodySchemaListValid(response.body)
        val userList: UserList = gson.fromJson(response.body, UserList::class.java)
        Assertions.assertEquals(
            numberOfPage, userList.page,
            "The page in the request is not equals the page in the response."
        )
        val dataUserList: List<*> = userList.data
        Assertions.assertEquals(
            dataUserList.size, userList.perPage,
            "Number of user entities in the data list should be equals per_page fields."
        )
    }

    @Test
    fun GetUsers_TwoPagesWithDifferentContentTest() {
        //When
        val response1: BaseResponse = baseRequest.getResponse(RestMethod.GET, "/users?page=1", null)
        val response2: BaseResponse = baseRequest.getResponse(RestMethod.GET, "/users?page=2", null)
        //Then
        Assertions.assertEquals(200, response1.statusCode, "StatusCode is not 200.")
        Assertions.assertEquals(200, response2.statusCode, "StatusCode is not 200.")
        CommonAssertions.checkBodySchemaListValid(response1.body)
        CommonAssertions.checkBodySchemaListValid(response2.body)
        val body1: UserList = gson.fromJson(response1.body, UserList::class.java)
        val body2: UserList = gson.fromJson(response2.body, UserList::class.java)
        val userList1: List<User> = body1.data
        userList1.sortedBy{it.id}
        val userList2: List<User> = body2.data
        userList2.sortedBy{it.id}
        val firstUserIdInList1: Int = userList1[0].id
        val firstUserIdInList2: Int = userList2[0].id
        Assertions.assertNotEquals(
            firstUserIdInList1, firstUserIdInList2,
            "The first userId on the first page shouldn't be equal to the first userId on the second page."
        )
    }

    @Test
    @Throws(IOException::class)
    fun GetUsers_PageWithEmptyUserListTest() {
        //Given
        val numberOfAllUsers: Int = userList.total

        //When
        val shortUrl = String.format("/users?page=%s", numberOfAllUsers + 1)
        val response: BaseResponse = baseRequest.getResponse(RestMethod.GET, shortUrl, null)

        //Then
        Assertions.assertEquals(
            200, response.statusCode,
            "StatusCode is not 200."
        )
        CommonAssertions.checkBodySchemaListValid(response.body)
        val data: JsonArray = getJsonObject(response.body).getAsJsonArray("data")
        Assertions.assertEquals(0, data.size(), "Number of users should be 0.")
    }

    @Test
    fun GetUsers_InvalidPageValueSuccessfulTest() {
        //When
        val response: BaseResponse = baseRequest.getResponse(RestMethod.GET, "/users?page=iue12@+", null)
        CommonAssertions.checkBodySchemaListValid(response.body)
    }

    @Test
    fun GetUsersByID_SuccessfulTest() {
        //Given
        val user: User = userList.data.get(0)

        //When
        val shortUrl = java.lang.String.format("/users/%s", user.id)
        val response: BaseResponse = baseRequest.getResponse(RestMethod.GET, shortUrl, null)

        //Then
        Assertions.assertEquals(
            200, response.statusCode,
            "StatusCode is not 200."
        )
        Assertions.assertNotNull(response.body, "Content shouldn't be null.")
        val data: JsonObject = getJsonObject(response.body).getAsJsonObject("data")
        Assertions.assertNotNull(data, "The data field should be exist")
        val id = data.getAsJsonPrimitive("id")
        Assertions.assertNotNull(id, "The id field should be exist")
        val email = data.getAsJsonPrimitive("email")
        Assertions.assertNotNull(email, "The email field should be exist")
        val firstName = data.getAsJsonPrimitive("first_name")
        Assertions.assertNotNull(firstName, "The first_name field should be exist")
        val lastName = data.getAsJsonPrimitive("last_name")
        Assertions.assertNotNull(lastName, "The last_name field should be exist")
        val support: JsonObject = getJsonObject(response.body).getAsJsonObject("support")
        Assertions.assertNotNull(support, "The support field should be exist")
    }

    @Test
    @Throws(IOException::class)
    fun GetUsersByID_NotFoundTest() {
        //Given
        val numberOfAllUsers: Int = userList.total

        //When
        val shortUrl = String.format("/users/%s", numberOfAllUsers + 1)
        val response: BaseResponse = baseRequest.getResponse(RestMethod.GET, shortUrl, null)

        //Then
        Assertions.assertEquals(
            404, response.statusCode,
            "StatusCode is not 404."
        )
        Assertions.assertEquals("{}", response.body)
    }

    @Test
    @Throws(IOException::class)
    fun GetUsersByID_NullValueTest() {
        //When
        val shortUrl = String.format("/users/null")
        val response: BaseResponse = baseRequest.getResponse(RestMethod.GET, shortUrl, null)

        //Then
        Assertions.assertEquals(
            404, response.statusCode,
            "StatusCode is not 404."
        )
        Assertions.assertEquals("{}", response.body)
    }

    private val userList: UserList
        private get() {
            val response: BaseResponse = baseRequest.getResponse(RestMethod.GET, "/users", null)
            return gson.fromJson(response.body, UserList::class.java)
        }
}