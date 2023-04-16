package Data

import com.beust.klaxon.Json

class Resource {
    var id = 0
    var name: String? = null
    var year = 0
    var color: String? = null
    @Json(name = "pantone_value")
    var pantoneValue: String? = null
}