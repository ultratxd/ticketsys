package com.cj.ticketsys

import com.cj.ticketsys.svc.entrance.CreateOrderParameter
import com.cj.ticketsys.svc.entrance.SCApi
import com.cj.ticketsys.svc.entrance.VisitPerson
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class TicketsysApplicationTests {

	@Test
	fun contextLoads() {
	}

	@Test
	fun testApi() {
		val api = SCApi("10036","WMPBL3N2")

		val order = CreateOrderParameter()
		order.orderSerialId = "sz591bc8d91649c8733752002"
		order.productNo = "10899"
		order.payType = 1
		order.tickets = 1
		order.price = 3000
		order.contractPrice = 1000
		order.bookName = "系统测试"
		order.bookMobile = "18262035263"
		order.travelDate = "2019-10-17"
		val visitPerson = VisitPerson()
		visitPerson.name = "李超"
		visitPerson.mobile = "15938019932"
		visitPerson.idCard = "310225198211252859"
		order.visitPerson.add(visitPerson)

		api.createOrder(order)
	}

}
