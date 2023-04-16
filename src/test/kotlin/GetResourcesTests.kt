import Data.Resource
import Data.ResourceList
import com.google.gson.*
import org.apache.http.client.fluent.Request
import org.apache.http.util.EntityUtils
import org.example.CommonAssertions
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.IOException

class GetResourcesTests {
    private val baseUrl = "https://reqres.in/api"
    var gson = Gson()

    @Test
    @Throws(IOException::class)
    @DisplayName("Get Resources. Validation of fields in the response.")
    fun getResourcesSuccessfulTest() {
        //When
        val response = Request.Get("$baseUrl/unknown")
            .execute()
            .returnResponse()
        //Then
        Assertions.assertEquals(
            200, response.statusLine.statusCode,
            "StatusCode is not 200."
        )
        val body = EntityUtils.toString(response.entity)
        CommonAssertions.checkBodySchemaListValid(body)
    }

    @Test
    @Throws(IOException::class)
    @DisplayName("Get Resources. Verification of the page parameter in the response.")
    fun getResourcesPageParamTest() {
        //Given
        val numberOfPage = 1

        //When
        val page1Response = Request.Get("$baseUrl/unknown?page=$numberOfPage")
            .execute()
            .returnResponse()

        //Then
        val body = EntityUtils.toString(page1Response.entity)
        CommonAssertions.checkBodySchemaListValid(body)
        val resourceListFromBody: ResourceList = gson.fromJson(body, ResourceList::class.java)
        val dataResourceList: List<Resource>? = resourceListFromBody.data
        Assertions.assertEquals(
            dataResourceList?.size, resourceListFromBody.per_page,
            "Number of resources entities in the data list should be equals per_page."
        )
        Assertions.assertEquals(
            numberOfPage, resourceListFromBody.page,
            "The page in the request is not equals the page in the response."
        )
    }

    @Test
    @Throws(IOException::class)
    @DisplayName("Get Resources. Two pages with the different content")
    fun getResourcesTwoPagesWithDifferentContentTest() {
        //When
        val response1 = Request.Get("$baseUrl/unknown?page=1")
            .execute()
            .returnResponse()
        val response2 = Request.Get("$baseUrl/unknown?page=2")
            .execute()
            .returnResponse()

        //Then
        Assertions.assertEquals(200, response1.statusLine.statusCode, "StatusCode is not 200.")
        Assertions.assertEquals(200, response2.statusLine.statusCode, "StatusCode is not 200.")
        val bodyString1 = EntityUtils.toString(response1.entity)
        val bodyString2 = EntityUtils.toString(response2.entity)
        CommonAssertions.checkBodySchemaListValid(bodyString1)
        CommonAssertions.checkBodySchemaListValid(bodyString2)
        val body1: ResourceList = gson.fromJson(bodyString1, ResourceList::class.java)
        val body2: ResourceList = gson.fromJson(bodyString2, ResourceList::class.java)
        val userList1: List<Resource>? = body1.data
        userList1?.sortedBy { it.id }
        val userList2: List<Resource>? = body2.data
        userList2?.sortedBy { it.id }
        val firstResourceIdInList1: Int? = userList1?.get(0)?.id
        val firstResourceIdInList2: Int? = userList2?.get(0)?.id
        Assertions.assertNotEquals(
            firstResourceIdInList1, firstResourceIdInList2,
            "The first ResourceId on the first page shouldn't be equal to the first ResourceID on the second page."
        )
    }

    @Test
    @Throws(IOException::class)
    @DisplayName("Get Resources. Too long resource name.")
    fun getResourcesTooLongResourceTest() {
        //Given
        val symbols = "aaaaaaaaaaaaaaaaaaaaa"
        val twoLongResource = symbols.repeat(450)

        //When
        val response = Request.Get("$baseUrl/$twoLongResource")
            .execute()
            .returnResponse()
        Assertions.assertEquals(
            414, response.statusLine.statusCode,
            "StatusCode is not 414."
        )
        Assertions.assertEquals("URI Too Long", response.statusLine.reasonPhrase)
    }

    @Test
    @Throws(IOException::class)
    @DisplayName("Get Resources. The page with an empty resource list.")
    fun getResourcesPageWithEmptyResourceListTest() {
        //Given
        val numberOfAllResources: Int = getResourceList().total

        //When
        val response = Request.Get("$baseUrl/resource?page=$numberOfAllResources+1")
            .execute()
            .returnResponse()

        //Then
        Assertions.assertEquals(
            200, response.statusLine.statusCode,
            "StatusCode is not 200."
        )
        val body = EntityUtils.toString(response.entity)
        CommonAssertions.checkBodySchemaListValid(body)
        val jelement = JsonParser.parseString(body).asJsonObject
        val data: JsonArray = jelement.getAsJsonArray("data")
        Assertions.assertEquals(0, data.size(), "Number of resources should be 0.")
    }

    @Test
    @Throws(IOException::class)
    @DisplayName("Get Resources by Id. Validation of fields from response.")
    fun getResourceByIdSuccessfulTest() {
        //Given
        val resource: Resource = getResourceList().data!![0]

        //When
        val response = Request.Get("$baseUrl/unknown/${resource.id}")
            .execute()
            .returnResponse()

        //Then
        val body = EntityUtils.toString(response.entity)
        Assertions.assertNotNull(body, "Content shouldn't be null.")

        val jElement = JsonParser.parseString(body).asJsonObject
        val data: JsonObject = jElement.getAsJsonObject("data")
        Assertions.assertNotNull(data, "The data field should be exist")
        val color: JsonPrimitive = jElement.getAsJsonObject("data").getAsJsonPrimitive("color")
        Assertions.assertNotNull(color, "The color should be exist")
        val name: JsonPrimitive = jElement.getAsJsonObject("data").getAsJsonPrimitive("name")
        Assertions.assertNotNull(name, "The name should be exist")
        val year: JsonPrimitive = jElement.getAsJsonObject("data").getAsJsonPrimitive("year")
        Assertions.assertNotNull(year, "The year should be exist")
        val pantoneValue: JsonPrimitive =
            jElement.getAsJsonObject("data").getAsJsonPrimitive("pantone_value")
        Assertions.assertNotNull(pantoneValue, "The pantone_value should be exist")
        val support: JsonObject = jElement.getAsJsonObject("support")
        Assertions.assertNotNull(support, "The support field should be exist")
    }

    @Test
    @Throws(IOException::class)
    @DisplayName("Get Resources by Id. Resource is not found.")
    fun getResourceByIDNotFoundTest() {
        //Given
        val numberOfAllResources: Int = getResourceList().total

        //When
        val response = Request.Get("$baseUrl/unknown/$numberOfAllResources+1")
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
    @DisplayName("Get Resources by Id. Resource Id is Null.")
    fun getResourceByIDNullValueTest() {
        //When
        val response = Request.Get("$baseUrl/resource/null")
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

    private fun getResourceList(): ResourceList {
        val allResourcesResponse =
            Request.Get("$baseUrl/unknown")
                .execute()
                .returnResponse()
        val bodyString = EntityUtils.toString(allResourcesResponse.entity)

        val resourceList: ResourceList = gson.fromJson(bodyString, ResourceList::class.java)
        if (resourceList.data == null) {
            throw IllegalStateException("UserList is empty.")
        }
        return gson.fromJson(bodyString, ResourceList::class.java)
    }
}