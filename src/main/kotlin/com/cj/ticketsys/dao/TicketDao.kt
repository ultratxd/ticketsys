package com.cj.ticketsys.dao

import com.cj.ticketsys.entities.ChannelTypes
import com.cj.ticketsys.entities.Tag
import com.cj.ticketsys.entities.Ticket
import org.apache.ibatis.annotations.*

@Mapper
interface TicketDao {

    @Select(
        "select * from ticket where id=#{id}"
    )
    @Results(
        Result(column = "cloud_id", property = "cloudId"),
        Result(column = "create_time", property = "createTime"),
        Result(column = "refund_type", property = "refundType"),
        Result(column = "enter_remark", property = "enterRemark"),
        Result(column = "buy_remark", property = "buyRemark"),
        Result(column = "front_view", property = "frontView"),
        Result(column = "pernums", property = "perNums"),
        Result(column = "display_order", property = "displayOrder")
    )
    fun get(id: Int): Ticket?


    @Select(
        "<script>select DISTINCT(t.id) as tid,t.* from ticket t join scenic_of_tickets st on st.ticket_id=t.id join ticket_price tp on tp.tid=t.id" +
                "<where>" +
                "<if test='sid != null'>" +
                " and st.scenic_sid=#{sid} " +
                "</if>" +
                "<if test='cid != null'>" +
                " and t.cid=#{cid} " +
                "</if>" +
                "<if test='frontView != null'>" +
                " and t.front_view=#{frontView} " +
                "</if>" +
                "<if test='channelType != null'>" +
                " and tp.channel_type=#{channelType} " +
                "</if>" +
                "</where>" +
                " order by t.display_order desc" +
                "</script>"
    )
    @Results(
        Result(column = "cloud_id", property = "cloudId"),
        Result(column = "create_time", property = "createTime"),
        Result(column = "refund_type", property = "refundType"),
        Result(column = "enter_remark", property = "enterRemark"),
        Result(column = "buy_remark", property = "buyRemark"),
        Result(column = "front_view", property = "frontView"),
        Result(column = "pernums", property = "perNums"),
        Result(column = "display_order", property = "displayOrder")
    )
    fun gets(sid: Int, cid: Int? = null, channelType: ChannelTypes? = null, frontView: Boolean? = true): List<Ticket>

    @Select(
        "<script>select count(DISTINCT(t.id)) from ticket t left join scenic_of_tickets st on st.ticket_id=t.id " +
                "<where>" +
                "<if test='sid != null'>" +
                " and st.scenic_sid=#{sid} " +
                "</if>" +
                "<if test='name != null'>" +
                " and t.name like concat('%', #{name}, '%') " +
                "</if>" +
                "<if test='state != null'>" +
                " and t.state=#{state}" +
                "</if>" +
                "<if test='cid != null'>" +
                " and t.cid=#{cid} " +
                "</if>" +
                "<if test='frontView != null'>" +
                " and t.front_view=#{frontView} " +
                "</if>" +
                "</where>" +
                "</script>"
    )
    fun searchCountForAdmin(query: TicketQuery): Long

    @Select(
        "<script>select DISTINCT(t.id),t.* from ticket t left join scenic_of_tickets st on st.ticket_id=t.id " +
                "<where>" +
                "<if test='sid != null'>" +
                " and st.scenic_sid=#{sid} " +
                "</if>" +
                "<if test='name != null'>" +
                " and t.name like concat('%', #{name}, '%') " +
                "</if>" +
                "<if test='state != null'>" +
                " and t.state=#{state}" +
                "</if>" +
                "<if test='cid != null'>" +
                " and t.cid=#{cid} " +
                "</if>" +
                "<if test='frontView != null'>" +
                " and t.front_view=#{frontView} " +
                "</if>" +
                "</where>" +
                " order by create_time desc" +
                " limit #{offset},#{size}" +
                "</script>"
    )
    @Results(
        Result(column = "cloud_id", property = "cloudId"),
        Result(column = "create_time", property = "createTime"),
        Result(column = "refund_type", property = "refundType"),
        Result(column = "enter_remark", property = "enterRemark"),
        Result(column = "buy_remark", property = "buyRemark"),
        Result(column = "front_view", property = "frontView"),
        Result(column = "pernums", property = "perNums"),
        Result(column = "display_order", property = "displayOrder")
    )
    fun searchForAdmin(query: TicketQuery): List<Ticket>

    @Insert(
        "insert into ticket(cloud_id,`name`,pernums,create_time,enter_remark,buy_remark,stocks,state,front_view,cid,properties) " +
                "values(#{cloudId},#{name},#{perNums},#{createTime},#{enterRemark},#{buyRemark},#{stocks},#{state},#{frontView},#{cid},#{properties})"
    )
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    fun insert(ticket: Ticket): Long

    @Insert(
        "insert into scenic_of_tickets(scenic_sid,ticket_id) values(#{scenicSid},#{tktId})"
    )
    fun insertScenicTicket(scenicSid: Int, tktId: Int): Long

    @Delete(
        "delete from scenic_of_tickets where ticket_id=#{tktId}"
    )
    fun delScenicTickets(tktId: Int): Long

    @Select(
        "select scenic_sid from scenic_of_tickets where ticket_id=#{tktId}"
    )
    fun getTktRelatedSpotIds(tktId: Int): List<Int>

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
        "select tg.* from tags tg join ticket_tags tt on tt.tagid=tg.id where tg.t=1 and tt.tid=#{ticketId}"
    )
    @Results(
        Result(column = "t", property = "type")
    )
    fun getTags(ticketId: Int): List<Tag>

    @Select(
        "select * from tags where name=#{name} and t=#{type}"
    )
    fun getTagByName(name: String, type: Short): Tag?

    @Insert(
        "insert into tags(name,t) values(#{name},#{type})"
    )
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    fun insertTag(tag: Tag): Long

    @Insert(
        "insert into ticket_tags(tid,tagid) values(#{ticketId},#{tagId})"
    )
    fun insertTicketTag(ticketId: Int, tagId: Int): Long

    @Delete(
        "delete from ticket_tags where tid=#{ticketId}"
    )
    fun delTicketTags(ticketId: Int): Long

    @Select(
        "select to_tid from ticket_tkts where from_tid=#{ticketId}"
    )
    fun getRelatedTicket(ticketId: Int): List<Int>

    @Insert(
        "insert into ticket_tkts(from_tid,to_tid) values(#{ticketId},#{toTicketId})"
    )
    fun insertRelatedTicket(ticketId: Int, toTicketId: Int): Long

    @Delete(
        "delete from ticket_tkts where from_tid=#{ticketId}"
    )
    fun delRelatedTickets(ticketId: Int)

//    @Select(
//        "select * from tags tg where tg.t=2"
//    )
//    fun getCategories(): List<Tag>


}