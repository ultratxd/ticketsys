package com.cj.ticketsys.controller.manage.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

class FileDto : Serializable {
    @JsonProperty("store_id")
    var storeId: String? = null
    @JsonProperty("file_url")
    var fileUrl: String? = null
    @JsonProperty("file_id")
    var fileId: Int = 0
}