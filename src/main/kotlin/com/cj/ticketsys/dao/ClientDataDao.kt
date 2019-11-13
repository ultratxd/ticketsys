package com.cj.ticketsys.dao

import com.cj.ticketsys.entities.ClientGateLog
import com.cj.ticketsys.entities.ClientOrder
import com.cj.ticketsys.entities.ClientSubOrder
import org.apache.ibatis.annotations.*

@Mapper
interface ClientDataDao {

    /**
     * 插入ClientGateLog
     */
    @Insert(
        "insert into client_gate_logs(c_id,c_order_no,c_order_sid,code,ctype,scan_date,scan_time,in_time,out_time,per_nums,in_passes,out_passes,properties)" +
                " values(#{clientId},#{clientOrderNo},#{clientOrderSid},#{code},#{cType},#{scanDate},#{scanTime},#{inTime},#{outTime},#{perNums},#{inPasses},#{outPasses},#{properties})"
    )
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    fun insertGateLog(log: ClientGateLog): Long


    /**
     * 插入ClientOrder
     */
    @Insert(
        "insert into client_orders(c_id,cloud_id,c_order_no,nums,order_type,amount,per_nums,create_time,state,pay_type,real_pay,change_pay,should_pay,excode,remark,sale_client_no,ext1,ext2,ext3,properties) " +
                " values(#{clientId},#{cloudId},#{clientOrderNo},#{nums},#{orderType},#{amount},#{perNums},#{createTime},#{state},#{payType},#{realPay}," +
                "#{changePay},#{shouldPay},#{exCode},#{remark},#{saleClientNo},#{ext1},#{ext2},#{ext3},#{properties})"
    )
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    fun insertOrder(order: ClientOrder): Long


    /**
     * 插入ClientSubOrder
     */
    @Insert(
        "insert into client_suborders(c_id,cloud_id,c_order_no,order_type,tkt_id,tkt_name,amount,unit_price,nums,per_nums,create_time,prints,use_date,enter_time,properties,c_pid)" +
                " values(#{clientId},#{cloudId},#{clientOrderNo},#{orderType},#{ticketId},#{ticketName},#{amount},#{unitPrice},#{nums},#{perNums},#{createTime},#{prints},#{useDate},#{enterTime},#{properties},#{clientParentId})"
    )
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    fun insertSubOrder(subOrder: ClientSubOrder): Long


    /**
     * 修改ClientGateLog
     */
    //    @Update("update client_gate_logs set c_order_no=#{clientOrderNo}, c_order_sid=#{clientOrderSid}, code=#{code}, ctype=#{cType}, scan_date=#{scanDate}, scan_time=#{scanTime}, in_time=#{inTime}, out_time=#{outTime}, per_nums=#{perNums}, in_passes=#{inPasses}, out_passes=#{outPasses}, properties=#{properties} where c_id=#{clientId}")
    @Update(
            "<script>" +
                    "update client_gate_logs " +
                    "<set>" +
                    "<if test=\"clientOrderNo != ''\">,c_order_no=#{clientOrderNo}</if>" +
                    "<if test=\"clientOrderSid != 0\">, c_order_sid=#{clientOrderSid}</if>" +
                    "<if test=\"code !=''\">, code=#{code}</if>" +
                    "<if test=\"cType !=0\">, ctype=#{cType}</if>" +
                    "<if test=\"scanTime !=0\">, scan_time=#{scanTime}</if>" +
                    "<if test=\"inTime !=null\">, in_time=#{inTime}</if>" +
                    "<if test=\"outTime !=null\">, out_time=#{outTime}</if>" +
                    "<if test=\"perNums !=0\">, per_nums=#{perNums}</if>" +
                    "<if test=\"inPasses !=0\">, in_passes=#{inPasses}</if>" +
                    "<if test=\"outPasses !=0\">, out_passes=#{outPasses}</if>" +
                    "<if test=\"properties !=''\">, properties=#{properties}</if>" +
                    "</set> " +
                    " where c_id = #{clientId} " +
            "</script>"
    )
    fun updateGateLog(log: ClientGateLog): Long


    /**
     * 修改ClientOrder
     */
    @Update(
            "<script>" +
                    "update client_orders " +
                    "<set>" +
                    "<if test=\"cloudId !=null\">,cloud_id=#{cloudId}</if>" +
                    "<if test=\"clientOrderNo !=''\">,c_order_no=#{clientOrderNo}</if>" +
                    "<if test=\"nums !=0\">,nums=#{nums}</if>" +
                    "<if test=\"orderType !=0\">,order_type=#{orderType}</if>" +
                    "<if test=\"amount !=0.0\">,amount=#{amount}</if>" +
                    "<if test=\"perNums !=0\">,per_nums=#{perNums}</if>" +
                    "<if test=\"state !=0\">,state=#{state}</if>" +
                    "<if test=\"payType !=0\">,pay_type=#{payType}</if>" +
                    "<if test=\"realPay !=0.0\">,real_pay=#{realPay}</if>" +
                    "<if test=\"changePay !=0.0\">,change_pay=#{changePay}</if>" +
                    "<if test=\"shouldPay !=0.0\">,should_pay=#{shouldPay}</if>" +
                    "<if test=\"exCode !=null\">,excode=#{exCode}</if>" +
                    "<if test=\"remark !=null\">,remark=#{remark}</if>" +
                    "<if test=\"saleClientNo !=''\">,sale_client_no=#{saleClientNo}</if>" +
                    "<if test=\"ext1 !=null\">,ext1=#{ext1}</if>" +
                    "<if test=\"ext2 !=null\">,ext2=#{ext2}</if>" +
                    "<if test=\"ext3 !=null\">,ext3=#{ext3}</if>" +
                    "<if test=\"properties !=''\">,properties=#{properties}</if>" +
                    "</set>" +
                    " where c_id=#{clientId}" +
            "</script>"
    )
    fun updateOrder(order: ClientOrder): Long

    /**
     * 修改ClientSubOrders
     */
    @Update(
            "<script>" +
                    "update client_suborders " +
                    "<set>" +
                    "<if test=\"cloudId !=null\">,cloud_id=#{cloudId}</if>" +
                    "<if test=\"clientOrderNo !=''\">,c_order_no=#{clientOrderNo}</if>" +
                    "<if test=\"orderType !=0\">,order_type=#{orderType}</if>" +
                    "<if test=\"ticketId !=0\">,tkt_id=#{ticketId}</if>" +
                    "<if test=\"ticketName !=''\">,tkt_name=#{ticketName}</if>" +
                    "<if test=\"amount !=0.0\">,amount=#{amount}</if>" +
                    "<if test=\"unitPrice !=0.0\">,unit_price=#{unitPrice}</if>" +
                    "<if test=\"nums !=0\">,nums=#{nums}</if>" +
                    "<if test=\"perNums !=0\">,per_nums=#{perNums}</if>" +
                    "<if test=\"prints !=0\">,prints=#{prints}</if>" +
                    "<if test=\"useDate !=0\">,use_date=#{useDate}</if>" +
                    "<if test=\"enterTime !=0\">,enter_time=#{enterTime}</if>" +
                    "<if test=\"clientParentId !=0\">,c_pid=#{clientParentId}</if>" +
                    "<if test=\"properties !=''\">,properties=#{properties}</if>" +
                    "</set>" +
                    " where c_id = #{clientId}" +
                    "</script>"
    )
    fun updateSubOrder(subOrder: ClientSubOrder): Long


    /**
     * 根据pid查询ClientSubOrders
     */
    @Select(
            "<script>" +
                    "select id,c_id,cloud_id,c_order_no,order_type,tkt_id,tkt_name,amount,unit_price,nums,per_nums,create_time,prints,use_date,enter_time,properties,c_pid from client_suborders" +
                    "<where>"+
                    "<if test=\"id !=null\"> and c_pid=#{id}</if>" +
                    "</where>" +
            "</script>"
    )
    @Results(
            Result(column = "c_id", property = "clientId"),
            Result(column = "c_order_no", property = "clientOrderNo"),
            Result(column = "tkt_id", property = "ticketId"),
            Result(column = "tkt_name", property = "ticketName"),
            Result(column = "c_pid", property = "clientParentId")
    )
    fun selectByPid(@Param("id") id: Int): List<ClientSubOrder>


    /**
     * 查询ClientOrders
     */
    @Select(
            "<script>" +
                    "select id,c_id,cloud_id,c_order_no,nums,order_type,amount,per_nums,create_time,state,pay_type,real_pay,change_pay,should_pay,excode,remark,sale_client_no,ext1,ext2,ext3,properties from client_orders" +
            "</script>"
    )
    @Results(
            Result(column = "c_id", property = "clientId"),
            Result(column = "c_order_no", property = "clientOrderNo")
    )
    fun selectClientOrderList(): List<ClientOrder>


    /**
     * 查询ClientGateLogs
     */
    @Select(
            "<script>" +
                    "select id,c_id,c_order_no,c_order_sid,code,ctype,scan_date,scan_time,in_time,out_time,per_nums,in_passes,out_passes,properties from client_gate_logs" +
            "</script>"
    )
    @Results(
            Result(column = "c_id", property = "clientId"),
            Result(column = "c_order_no", property = "clientOrderNo"),
            Result(column = "c_order_sid", property = "clientOrderSid"),
            Result(column = "ctype", property = "cType")
    )
    fun selectGateLogList(): MutableList<ClientGateLog>


    /**
     * 查询ClientSubOrders
     */
    @Select(
            "<script>" +
                    "select id,c_id,cloud_id,c_order_no,order_type,tkt_id,tkt_name,amount,unit_price,nums,per_nums,create_time,prints,use_date,enter_time,properties,c_pid from client_suborders" +
            "</script>"
    )
    @Results(
            Result(column = "c_id", property = "clientId"),
            Result(column = "c_order_no", property = "clientOrderNo"),
            Result(column = "tkt_id", property = "ticketId"),
            Result(column = "tkt_name", property = "ticketName"),
            Result(column = "c_pid", property = "clientParentId")
    )
    fun selectSubOrderList(): MutableList<ClientSubOrder>

    @Update(
            "<script>" +
                    "update client_suborders " +
                    "<set>" +
                    "<if test=\"pid !=0\">,c_pid=#{pid}</if>" +
                    "</set>" +
                    " where id = #{id}" +
                    "</script>"
    )
    fun updatePid(@Param("id")id: Int,@Param("pid")pid: Int): Long

    @Select(
            "<script>" +
                    "select id,c_id,cloud_id,c_order_no,nums,order_type,amount,per_nums,create_time,state,pay_type,real_pay,change_pay,should_pay,excode,remark,sale_client_no,ext1,ext2,ext3,properties from client_orders where c_id=#{cid}" +
            "</script>"
    )
    @Results(
            Result(column = "c_id", property = "clientId"),
            Result(column = "c_order_no", property = "clientOrderNo")
    )
    fun selectByCid(@Param("cid")clientParentId: Int): ClientOrder?

}