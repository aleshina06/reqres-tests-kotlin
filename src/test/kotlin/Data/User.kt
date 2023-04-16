package Data

import com.beust.klaxon.Json

class User {
    var id = 0
    var email: String? = null
    @Json(name = "first_name")
    var firstName: String? = null
    @Json(name = "last_name")
    var lastName: String? = null
    var password: String? = null
    var avatar: String? = null
    var token: String? = null
}