package com.cj.ticketsys.svc.impl

import com.cj.ticketsys.svc.ThreadPoolFactory
import java.util.concurrent.ExecutorService

object IssueTicketRunner {

    private var THREAD_POOL: ExecutorService = ThreadPoolFactory.newPool("DefaultIssueTicket-Pool", 10, 100, 0L, 2048)

    fun run(runner:Runnable) {
        THREAD_POOL.submit(runner)
    }

}