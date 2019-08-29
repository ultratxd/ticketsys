package com.cj.ticketsys

import com.cj.ticketsys.dao.SubOrderDao
import com.cj.ticketsys.svc.entrance.CreateOrderParameter
import com.cj.ticketsys.svc.entrance.SCApi
import com.cj.ticketsys.svc.entrance.VisitPerson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.text.SimpleDateFormat
import org.apache.ibatis.javassist.CtMethod.ConstParameter.string
import org.springframework.util.Assert


@RunWith(SpringRunner::class)
@SpringBootTest
class TicketsysApplicationTests {

	@Autowired
	private lateinit var subOrderDao: SubOrderDao

	@Autowired
	private lateinit var httpClient: OkHttpClient

	@Test
	fun contextLoads() {
	}

	@Test
	fun testApi() {
		val api = SCApi("10036","WMPBL3N2")

		val order = CreateOrderParameter()
		order.orderSerialId = "a123456789"
		order.productNo = "10899"
		order.payType = 1
		order.tickets = 1
		order.price = 3000
		order.contractPrice = 1000
		order.bookName = "系统测试"
		order.bookMobile = "15821322009"
		order.travelDate = "2019-07-29"
		val visitPerson = VisitPerson("李超","15938019932","310225198211252859")
		order.visitPerson.add(visitPerson)

		api.createOrder(order)
	}

	@Test
	fun testDb() {
		val sub = subOrderDao.get(99)!!
		val d = SimpleDateFormat("yyyy-MM-dd").format(sub.useDate!!)
		System.out.println(d)
	}

	@Test
	fun testFeign() {
		val request = Request.Builder()
			.url("http://www.163.com")
			.build()
		val content = httpClient.newCall(request).execute().body.toString()
		Assert.isTrue(content.isNotEmpty())
	}

}
