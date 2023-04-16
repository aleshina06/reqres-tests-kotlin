package Data

import com.google.gson.annotations.SerializedName

class Resource {
    var id = 0
    var name: String? = null
    var year = 0
    var color: String? = null
    @SerializedName("pantone_value")
    var pantoneValue: String? = null
}