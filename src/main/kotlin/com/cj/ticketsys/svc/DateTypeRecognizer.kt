package com.cj.ticketsys.svc

import com.cj.ticketsys.entities.DateTypes
import java.util.*

interface DateTypeRecognizer {

    fun recognize(date: Date): DateTypes
}