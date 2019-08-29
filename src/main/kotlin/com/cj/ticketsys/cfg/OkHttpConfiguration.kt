package com.cj.ticketsys.cfg

import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OkHttpConfiguration {

    @Bean
    fun getOkHttpClient() : OkHttpClient {
        return OkHttpClient()
    }
}