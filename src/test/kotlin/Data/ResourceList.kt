package Data

import com.beust.klaxon.Json

class ResourceList {
    var page = 0
    @Json(name = "per_page")
    var per_page = 0
    var total = 0
    @Json(name = "total_pages")
    var total_pages = 0
    var data: List<Resource>? = null
    var support: Support? = null
}