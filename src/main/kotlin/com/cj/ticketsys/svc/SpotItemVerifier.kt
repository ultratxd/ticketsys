package com.cj.ticketsys.svc

import com.cj.ticketsys.controller.dto.ResultT
import com.cj.ticketsys.entities.spotItem.SpotItem

/**
 * 扫码核销解析器
 */
interface SpotItemVerifier {
    /**
     * 解析核销码，返回可用数据
     */
    fun resolve(code: String): ResultT<List<SpotItemOrderDetail>>

    /**
     * 核销
     */
    fun verifying(code: String, itemNums: List<VerifyItemNums>, verifier:String): ResultT<List<SpotItemOrderDetail>>
}

class SpotItemOrderDetail {
    var item: SpotItem? = null
    var nums: Int = 0
    var perNums: Int = 0
    var surplus: Int = 0
}

data class VerifyItemNums(val itemId: Int, val nums: Int) {
}

