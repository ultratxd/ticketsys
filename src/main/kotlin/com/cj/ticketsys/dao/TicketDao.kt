package com.cj.ticketsys.dao

import com.cj.ticketsys.entities.Tag
import com.cj.ticketsys.entities.Ticket
import org.apache.ibatis.annotations.*

@Mapper
interface TicketDao {

    @Select(
        "select * from ticket where id=#{id}"
    )
    @Results(
        Result(column = "create_time", property = "createTime"),
        Result(column = "refund_type", property = "refundType"),
        Result(column = "enter_remark", property = "enterRemark"),
        Result(column = "buy_remark",property = "buyRemark"),
        Result(column = "front_view", property = "frontView")
    )
    fun get(id: Int): Ticket?


    @Select(
        "<script>select t.* from ticket t join scenic_of_tickets st on st.ticket_id=t.id " +
                "<where>" +
                "<if test='sid != null'>" +
                " and st.scenic_sid=#{sid} " +
                "</if>" +
                "<if test='cid != null'>" +
                " and t.cid=#{cid} " +
                "</if>" +
                "</where>" +
                "</script>"
    )
    @Results(
        Result(column = "create_time", property = "createTime"),
        Result(column = "refund_type", property = "refundType"),
        Result(column = "enter_remark", property = "enterRemark"),
        Result(column = "buy_remark",property = "buyRemark"),
        Result(column = "front_view", property = "frontView")
    )
    fun gets(sid: Int,cid: Int? = null): List<Ticket>

    @Insert(
        "insert into ticket(`name`,pernums,create_time,enter_remark,buy_remark,stocks,state,front_view,cid,properties) " +
                "values(#{name},#{perNums},#{createTime},#{enterRemark},#{buyRemark},#{stocks},#{state},#{frontView},#{cid},#{properties})"
    )
    fun insert(ticket: Ticket): Long

    @Insert(
        "insert into scenic_of_tickets(scenic_sid,ticket_id) values(#{sid},#{ticketId})"
    )
    fun insertSpotTicket(sid: Int, ticketId: Int): Long

    @Update(
        "update ticket set pernums=#{perNums},`name`=#{name},enter_remark=#{enterRemark},buy_remark=#{buyRemark},stocks=#{stocks},state=#{state},front_view=#{frontView},cid=#{cid},properties=#{properties} " +
                "where id=#{id}"
    )
    fun update(ticket: Ticket): Long

    @Update(
        "update ticket set solds=solds+#{solds} where id=#{id}"
    )
    fun updateSolds(id: Int, solds: Int): Long


    @Select(
        "select t.* from tags t join ticket_tags tt on tt.tagid=t.id where tt.tid=#{ticketId}"
    )
    @Results(
        Result(column = "t", property = "type")
    )
    fun getTags(ticketId:Int): List<Tag>

    @Select(
        "select to_tid from ticket_tkts where from_tid=#{ticketId}"
    )
    fun getRelatedTicket(ticketId:Int): List<Int>
}