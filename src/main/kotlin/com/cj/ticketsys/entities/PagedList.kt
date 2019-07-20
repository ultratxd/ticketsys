
package com.cj.ticketsys.entities

import com.fasterxml.jackson.annotation.JsonProperty

class PagedList<T>(val page: Int, val size: Int, val total: Long, val list: Collection<T> = ArrayList()) {
    @JsonProperty("total_pages")
    var totalPages = 0

    init {
        this.calculatePages()
    }

    private fun calculatePages() {
        if (total <= 0 || size <= 0) {
            totalPages = 0
            return
        }
        val m = total % size.toLong()
        totalPages = Math.toIntExact(total / size.toLong())
        if (m > 0) {
            totalPages++
        }
    }
}