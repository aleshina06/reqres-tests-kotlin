import Data.Resource
import Data.ResourceList
import api.BaseRequest
import api.RestMethod
import com.google.gson.*
import org.example.CommonAssertions
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.IOException

class GetResourcesTests {
    private val baseRequest: BaseRequest = BaseRequest(BASE_URL)
    private val gson = Gson()

    @Test
    @Throws(IOException::class)
    @DisplayName("Get Resources. Validation of fields in the response.")
    fun getResourcesSuccessfulTest() {
        //When
        val response = baseRequest.getResponse(RestMethod.GET, "/unknown", null)

        //Then
        Assertions.assertEquals(
            200, response.statusCode,
            "StatusCode is not 200."
        )

        CommonAssertions.checkBodySchemaListValid(response.body)
    }

    @Test
    @Throws(IOException::class)
    @DisplayName("Get Resources. Verification of the page parameter in the response.")
    fun getResourcesPageParamTest() {
        //Given
        val numberOfPage = 1

        //When
        val page1Response = baseRequest.getResponse(RestMethod.GET, "/unknown?page=$numberOfPage", null)

        //Then
        CommonAssertions.checkBodySchemaListValid(page1Response.body)
        val resourceListFromBody: ResourceList = gson.fromJson(page1Response.body, ResourceList::class.java)
        val dataResourceList: List<Resource>? = resourceListFromBody.data
        Assertions.assertEquals(
            dataResourceList?.size, resourceListFromBody.perPage,
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
        val response1 = baseRequest.getResponse(RestMethod.GET, "/unknown?page=1", null)

        val response2 = baseRequest.getResponse(RestMethod.GET, "/unknown?page=2", null)

        //Then
        Assertions.assertEquals(200, response1.statusCode, "StatusCode is not 200.")
        Assertions.assertEquals(200, response2.statusCode, "StatusCode is not 200.")
        CommonAssertions.checkBodySchemaListValid(response1.body)
        CommonAssertions.checkBodySchemaListValid(response2.body)
        val body1: ResourceList = gson.fromJson(response1.body, ResourceList::class.java)
        val body2: ResourceList = gson.fromJson(response2.body, ResourceList::class.java)
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
        val response = baseRequest.getResponse(RestMethod.GET, "/$twoLongResource", null)
        Assertions.assertEquals(
            414, response.statusCode,
            "StatusCode is not 414."
        )
    }

    @Test
    @Throws(IOException::class)
    @DisplayName("Get Resources. The page with an empty resource list.")
    fun getResourcesPageWithEmptyResourceListTest() {
        //Given
        val numberOfAllResources: Int = getResourceList().total

        //When
        val response = baseRequest.getResponse(RestMethod.GET, "/resource?page=$numberOfAllResources+1", null)

        //Then
        Assertions.assertEquals(
            200, response.statusCode, "StatusCode is not 200."
        )
        CommonAssertions.checkBodySchemaListValid(response.body)
        val jelement = JsonParser.parseString(response.body).asJsonObject
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
        val response = baseRequest.getResponse(RestMethod.GET, "/unknown/${resource.id}", null)

        //Then
        Assertions.assertNotNull(response.body, "Content shouldn't be null.")

        val jElement = JsonParser.parseString(response.body).asJsonObject
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
        val response = baseRequest.getResponse(RestMethod.GET, "/unknown/$numberOfAllResources+1", null)

        //Then
        Assertions.assertEquals(
            404, response.statusCode,
            "StatusCode is not 404."
        )
        Assertions.assertEquals("{}", response.body)
    }

    @Test
    @Throws(IOException::class)
    @DisplayName("Get Resources by Id. Resource Id is Null.")
    fun getResourceByIDNullValueTest() {
        //When
        val response = baseRequest.getResponse(RestMethod.GET, "/resource/null", null)

        //Then
        Assertions.assertEquals(
            404, response.statusCode,
            "StatusCode is not 404."
        )
        Assertions.assertEquals("{}", response.body)
    }

    private fun getResourceList(): ResourceList {
        val allResourcesResponse = baseRequest.getResponse(RestMethod.GET, "/unknown", null)
        val resourceList: ResourceList = gson.fromJson(allResourcesResponse.body, ResourceList::class.java)
        if (resourceList.data == null) {
            throw IllegalStateException("UserList is empty.")
        }
        return gson.fromJson(allResourcesResponse.body, ResourceList::class.java)
    }
}