package com.cj.ticketsys.dao

import com.cj.ticketsys.entities.ClientGateLog
import com.cj.ticketsys.entities.ClientOrder
import com.cj.ticketsys.entities.ClientSubOrder
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Options

@Mapper
interface ClientDataDao {

    @Insert(
        "insert into client_gate_logs(c_id,c_order_no,c_order_sid,code,ctype,scan_date,scan_time,in_time,out_time,per_nums,in_passes,out_passes,properties)" +
                " values(#{clientId},#{clientOrderNo},#{clientOrderSid},#{code},#{cType},#{scanDate},#{scanTime},#{inTime},#{outTime},#{perNums},#{inPasses},#{outPasses},#{properties})"
    )
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    fun insertGateLog(log: ClientGateLog): Long

    @Insert(
        "insert into client_orders(c_id,cloud_id,c_order_no,nums,order_type,amount,per_nums,create_time,state,pay_type,real_pay,change_pay,should_pay,excode,remark,sale_client_no,ext1,ext2,ext3,properties) " +
                " values(#{clientId},#{cloudId},#{clientOrderNo},#{nums},#{orderType},#{amount},#{perNums},#{createTime},#{state},#{payType},#{realPay}," +
                "#{changePay},#{shouldPay},#{exCode},#{remark},#{saleClientNo},#{ext1},#{ext2},#{ext3},#{properties})"
    )
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    fun insertOrder(order: ClientOrder): Long

    @Insert(
        "insert into client_suborders(c_id,cloud_id,c_order_no,order_type,tkt_id,tkt_name,amount,unit_price,nums,per_nums,create_time,prints,use_date,enter_time,properties)" +
                " values(#{clientId},#{cloudId},#{clientOrderNo},#{orderType},#{ticketId},#{ticketName},#{amount},#{unitPrice},#{nums},#{perNums},#{createTime},#{prints},#{useDate},#{enterTime},#{properties})"
    )
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    fun insertSubOrder(subOrder: ClientSubOrder): Long
}