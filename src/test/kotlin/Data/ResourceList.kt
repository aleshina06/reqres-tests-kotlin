package Data

import com.google.gson.annotations.SerializedName

class ResourceList {
    var page = 0
    @SerializedName("per_page")
    var perPage = 0
    var total = 0
    @SerializedName("total_pages")
    var totalPages = 0
    var data: List<Resource>? = null
    var support: Support? = null
}