package com.cj.ticketsys.cfg

import com.alibaba.druid.pool.DruidDataSource
import lombok.extern.slf4j.Slf4j
import org.springframework.boot.context.properties.bind.Bindable
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.context.EnvironmentAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.sql.SQLException
import javax.sql.DataSource

@EnableTransactionManagement
@Configuration
@Slf4j
class DruidDataSourceConfig : EnvironmentAware {

    private var prop: DruidProperties? = null

    override fun setEnvironment(e: Environment) {
        val binder = Binder.get(e)
        prop = binder.bind("spring.datasource", Bindable.of(DruidProperties::class.java)).get()
    }

    @Bean
    fun dataSource(): DataSource {
        val datasource = DruidDataSource()
        datasource.url = prop!!.url
        datasource.driverClassName = prop!!.driverClassName
        datasource.username = prop!!.username
        datasource.password = prop!!.password
        datasource.initialSize = prop!!.initialSize
        datasource.minIdle = prop!!.minIdle
        datasource.maxWait = prop!!.maxWait
        datasource.maxActive = prop!!.maxActive
        datasource.minEvictableIdleTimeMillis = prop!!.minEvictableIdleTimeMillis
        try {
            datasource.setFilters("stat,wall")
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return datasource
    }
}