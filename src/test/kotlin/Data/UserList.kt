package Data

import com.google.gson.annotations.SerializedName

class UserList(
    val page: Int = 0,
    @SerializedName("per_page")
    var perPage: Int = 0,
    var total: Int = 0,
    @SerializedName("total_pages")
    var totalPages: Int = 0,
    var data: List<User>) {
}