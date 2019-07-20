package com.cj.ticketsys.svc.impl

import com.cj.ticketsys.cfg.cache.Cache
import com.cj.ticketsys.dao.TicketPriceDao
import com.cj.ticketsys.svc.InventoryManagement
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PriceInventoryManagement : InventoryManagement {

    @Autowired
    private lateinit var cache: Cache

    @Autowired
    private lateinit var ticketPriceDao: TicketPriceDao

    override fun surplus(tpId: Int): Int {

        val price = ticketPriceDao.get(tpId) ?: return Int.MAX_VALUE
        if (price.stocks == 0) {
            return Int.MAX_VALUE
        }
        return price.stocks - price.solds
    }

    @Transactional
    override fun incrSolds(tpId: Int, c: Int) {
        ticketPriceDao.updateSolds(tpId, c)
    }

    @Transactional
    override fun decrSolds(tpId: Int, c: Int) {
        ticketPriceDao.updateSolds(tpId, -c)
    }
}