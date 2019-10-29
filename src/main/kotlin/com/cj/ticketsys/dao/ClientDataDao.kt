package com.cj.ticketsys.dao

import com.cj.ticketsys.entities.ClientGateLog
import com.cj.ticketsys.entities.ClientOrder
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper

@Mapper
interface ClientDataDao {

    @Insert(
        "insert into client_gate_logs(c_id,c_order_no,c_order_sid,code,ctype,scan_date,scan_time,in_time,out_time,per_nums,in_passes,out_passes,properties)" +
                " values(#{clientId},#{clientOrderNo},#{clientOrderSid},#{code},#{cType},#{scanDate},#{scanTime},#{inTime},#{outTime},#{perNums},#{inPasses},#{outPasses},#{properties})"
    )
    fun insertGateLog(log: ClientGateLog): Long

    @Insert(
        "insert into client_orders(c_id,cloud_id,c_order_no,nums,order_type,amount,per_nums,create_time,state,pay_type,real_pay,change_pay,should_pay,excode,remark,sale_client_no,ext1,ext2,ext3,properties) " +
                " values(#{clientId},,#{properties})"
    )
    fun insertOrder(order: ClientOrder): Long
}