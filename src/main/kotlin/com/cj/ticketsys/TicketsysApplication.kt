package com.cj.ticketsys

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
class TicketsysApplication

fun main(args: Array<String>) {
	runApplication<TicketsysApplication>(*args)
}
