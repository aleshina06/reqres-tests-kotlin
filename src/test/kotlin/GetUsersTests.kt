import Data.User
import Data.UserList
import com.google.gson.*
import org.apache.http.HttpResponse
import org.apache.http.client.fluent.Request
import org.apache.http.util.EntityUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.IOException

class GetUsersTests {
    private val baseUrl = "https://reqres.in/api"
    var gson = Gson()

    @Test
    @Throws(IOException::class)
    @DisplayName("Get Users. Validation of fields in the response.")
    fun getUsersSuccessfulTest() {
        //When
        val response = Request.Get("$baseUrl/users")
            .execute()
            .returnResponse()

        //Then
        Assertions.assertEquals(
            200, response.statusLine.statusCode,
            "StatusCode is not 200."
        )
        val body = EntityUtils.toString(response.entity)
        checkUserListResponseValid(body)
    }

    @Test
    @Throws(IOException::class)
    @DisplayName("Get Users. Verification of the page parameter.")
    fun getUsersPageParamTest() {
        //Given
        val numberOfPage = 1

        //When
        val page1Response: HttpResponse =
            Request.Get("$baseUrl/users?page=$numberOfPage")
                .execute()
                .returnResponse()

        //Then
        val body = EntityUtils.toString(page1Response.entity)
        checkUserListResponseValid(body)

        val userList = gson.fromJson(body, UserList::class.java)
        Assertions.assertEquals(
            numberOfPage, userList.page,
            "The page in the request is not equals the page in the response."
        )
        val dataUserList: List<User> = userList.data
        Assertions.assertEquals(
            dataUserList.size, userList.data.size,
            "Number of user entities in the data list should be equals per_page fields."
        )
    }

    @Test
    @Throws(IOException::class)
    @DisplayName("Get Users. Two pages with the different content.")
    fun getUsersTwoPagesWithDifferentContentTest() {
        //When
        val response1: HttpResponse = Request.Get("$baseUrl/users?page=1")
            .execute()
            .returnResponse()
        val response2: HttpResponse = Request.Get("$baseUrl/users?page=2")
            .execute()
            .returnResponse()

        //Then
        Assertions.assertEquals(200, response1.statusLine.statusCode, "StatusCode is not 200.")
        Assertions.assertEquals(200, response2.statusLine.statusCode, "StatusCode is not 200.")
        val bodyString1 = EntityUtils.toString(response1.entity)
        val bodyString2 = EntityUtils.toString(response2.entity)
        checkUserListResponseValid(bodyString1)
        checkUserListResponseValid(bodyString2)
        val body1 = gson.fromJson(bodyString1, UserList::class.java)
        val body2 = gson.fromJson(bodyString2, UserList::class.java)
        val userList1: List<User> = body1.data
        userList1.sortedBy{it.id}
        val userList2: List<User> = body2.data
        userList2.sortedBy{it.id}
        val firstUserIdInList1: Int = userList1[0].id
        val firstUserIdInList2: Int = userList2[0].id
            Assertions.assertNotEquals(firstUserIdInList1, firstUserIdInList2,
            "The first userId on the first page shouldn't be equal to the first userId on the second page."
        )
    }

    @Test
    @Throws(IOException::class)
    @DisplayName("Get Users. A page with the empty user list.")
    fun getUsersPageWithEmptyUserListTest() {
        //Given
        val numberOfAllUsers: Int = getUserList().per_page

        //When
        val response: HttpResponse = Request.Get("$baseUrl/users?page=$numberOfAllUsers+1")
            .execute()
            .returnResponse()

        //Then
        Assertions.assertEquals(
            200, response.statusLine.statusCode,
            "StatusCode is not 200."
        )
        val body = EntityUtils.toString(response.entity)
        checkUserListResponseValid(body)
        val jelement = JsonParser.parseString(body).asJsonObject
        val data: JsonArray = jelement.getAsJsonArray("data")
        Assertions.assertEquals(0, data.size(), "Number of users should be 0.")
    }

    @Test
    @Throws(IOException::class)
    @DisplayName("Get Users. Invalid a page value in the url.")
    fun getUsersInvalidPageValueSuccessfulTest() {
        //When
        val response: HttpResponse = Request.Get("$baseUrl/users?page=iue12@+")
            .execute()
            .returnResponse()
        val body = EntityUtils.toString(response.entity)
        checkUserListResponseValid(body)
    }

    @Test
    @Throws(IOException::class)
    @DisplayName("Get Users by Id. Validation of fields in the response.")
    fun getUsersByIdSuccessfulTest() {
        //Given
        val user: User = getUserList().data[0]

        //When
        val response: HttpResponse =
            Request.Get("$baseUrl/users/${user.id}")
                .execute()
                .returnResponse()

        //Then
        Assertions.assertEquals(
            200, response.statusLine.statusCode,
            "StatusCode is not 200."
        )
        val body = EntityUtils.toString(response.entity)
        Assertions.assertNotNull(body, "Content shouldn't be null.")
        val jelement = JsonParser.parseString(body)
        val data: JsonObject = jelement.asJsonObject.getAsJsonObject("data")
        Assertions.assertNotNull(data, "The data field should be exist")
        val id = data.getAsJsonPrimitive("id")
        Assertions.assertNotNull(id, "The id field should be exist")
        val email = data.getAsJsonPrimitive("email")
        Assertions.assertNotNull(email, "The email field should be exist")
        val firstName = data.getAsJsonPrimitive("first_name")
        Assertions.assertNotNull(firstName, "The first_name field should be exist")
        val lastName = data.getAsJsonPrimitive("last_name")
        Assertions.assertNotNull(lastName, "The last_name field should be exist")
        val support: JsonObject = jelement.asJsonObject.getAsJsonObject("support")
        Assertions.assertNotNull(support, "The support field should be exist")
    }

    @Test
    @Throws(IOException::class)
    @DisplayName("Get Users by Id. User is not found.")
    fun getUsersByIdNotFoundTest() {
        //Given
        val numberOfAllUsers: Int = getUserList().total

        //When
        val response: HttpResponse =
            Request.Get("$baseUrl/users/$numberOfAllUsers+1")
                .execute()
                .returnResponse()

        //Then
        Assertions.assertEquals(
            404, response.statusLine.statusCode,
            "StatusCode is not 404."
        )
        val body = EntityUtils.toString(response.entity)
        Assertions.assertEquals("{}", body)
    }

    @Test
    @Throws(IOException::class)
    @DisplayName("Get Users by Id. Id equals null in the url.")
    fun getUsersByIdNullValueTest() {
        //When
        val response: HttpResponse = Request.Get("$baseUrl/users/null")
            .execute()
            .returnResponse()

        //Then
        Assertions.assertEquals(
            404, response.statusLine.statusCode,
            "StatusCode is not 404."
        )
        val body = EntityUtils.toString(response.entity)
        Assertions.assertEquals("{}", body)
    }

        private fun checkUserListResponseValid(body: String) {
            Assertions.assertNotNull(body, "Content shouldn't be null.")

            val jelement = JsonParser.parseString(body).asJsonObject
            val page = jelement.getAsJsonPrimitive("page")
            Assertions.assertNotNull(page, "The page field should be exist")

            val perPage = jelement.getAsJsonPrimitive("per_page")
            Assertions.assertNotNull(perPage, "The per_page field should be exist")

            val total = jelement.getAsJsonPrimitive("total")
            Assertions.assertNotNull(total, "The total field should be exist")

            val totalPages = jelement.getAsJsonPrimitive("total_pages")
            Assertions.assertNotNull(totalPages, "The total_pages field should be exist")
            val data = jelement.getAsJsonArray("data")
            Assertions.assertNotNull(data, "The data field should be exist")
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
