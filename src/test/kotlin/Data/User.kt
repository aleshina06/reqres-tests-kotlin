package Data

import com.google.gson.annotations.SerializedName

class User {
    var id = 0
    var email: String? = null
    @SerializedName("first_name")
    var firstName: String? = null
    @SerializedName("last_name")
    var lastName: String? = null
    var password: String? = null
    var avatar: String? = null
    var token: String? = null
}