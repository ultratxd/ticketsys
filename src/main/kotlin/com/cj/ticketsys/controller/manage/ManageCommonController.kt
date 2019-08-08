package com.cj.ticketsys.controller.manage

import com.cj.ticketsys.controller.BaseController
import com.cj.ticketsys.controller.dto.RESULT_FAIL
import com.cj.ticketsys.controller.dto.RESULT_SUCCESS
import com.cj.ticketsys.controller.dto.ResultT
import com.cj.ticketsys.controller.manage.dto.MRecommendDto
import com.cj.ticketsys.dao.RecommendDao
import com.cj.ticketsys.dao.TicketDao
import com.cj.ticketsys.entities.PagedList
import com.cj.ticketsys.entities.Recommend
import com.cj.ticketsys.svc.DocTransformer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/ota/v1/manage/common")
class ManageCommonController : BaseController() {

    @Autowired
    private lateinit var recommendDao: RecommendDao

    @Autowired
    private lateinit var ticketDao: TicketDao

    @Autowired
    private lateinit var recommendTransformer: DocTransformer<Recommend, MRecommendDto>

    @PostMapping("recommend")
    fun createRecommend(
        @RequestParam("rel_id", required = false) relId: Int?,
        @RequestParam("type", required = false) type: Short?,
        @RequestParam("display_order", required = false) displayOrder: Int?
    ): com.cj.ticketsys.controller.dto.Result {

        if (relId == null || relId <= 0) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误")
        }
        if (type == null || type <= 0) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "参数错误")
        }

        if (recommendDao.getByRefType(relId, type) != null) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "数据已存在")
        }

        val rec = Recommend()
        rec.refId = relId
        rec.type = type
        rec.displayOrder = displayOrder ?: 0
        val c = recommendDao.insert(rec)
        if (c > 0) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_SUCCESS, "添加成功")
        }
        return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "添加失败")
    }

    @DeleteMapping("recommend/{id}")
    fun delRecommend(
        @PathVariable("id") id:Int
    ): com.cj.ticketsys.controller.dto.Result {
        val c = recommendDao.del(id)
        if (c > 0) {
            return com.cj.ticketsys.controller.dto.Result(RESULT_SUCCESS, "删除成功")
        }
        return com.cj.ticketsys.controller.dto.Result(RESULT_FAIL, "删除失败")
    }

    @GetMapping("recommends")
    fun getRecommends(
        @RequestParam("page", required = false) page: Int? = 1,
        @RequestParam("size", required = false) size: Int? = 20
    ): ResultT<PagedList<MRecommendDto>> {
        var tmpPage = 0
        var tmpSize = 20
        if (page == null || page <= 0) {
            tmpPage = 1
        } else {
            tmpPage = page
        }
        if (size != null && size > 0) {
            tmpSize = size!!
        }
        val offset = (tmpPage - 1) * tmpSize
        val recs = recommendDao.getsForAdmin(offset, tmpSize)
        val total = recommendDao.getsCountForAdmin()
        val list = ArrayList<MRecommendDto>()
        for (rec in recs) {
            val r = recommendTransformer.transform(rec)!!
            val ticket = ticketDao.get(r.refid)
            if(ticket != null) {
                r.title = ticket.name
            }
            list.add(r)
        }
        val pList = PagedList(tmpPage, tmpSize, total, list)
        return ResultT(RESULT_SUCCESS, "ok", pList)
    }
}