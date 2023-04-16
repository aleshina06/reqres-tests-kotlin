package Data

import com.beust.klaxon.Json

class UserList(
    val page: Int = 0,
    @Json(name = "per_page")
    var per_page: Int = 0,
    var total: Int = 0,
    @Json(name = "total_pages")
    var total_pages: Int = 0,
    var data: List<User>) {
}