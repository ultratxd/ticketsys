package com.cj.ticketsys.svc.b2b.lvmm;

import lombok.Data;

import java.util.Date;

/**
 * @author wangliwei
 * @date 2020/4/8
 *
 * 驴妈妈接口对接-下单
 */

public class LvOrderParams {

    /**
     * id
     */
    private Integer id;

    /**
     * 驴妈妈给供应商的ID,用来标记哪些订单来自驴妈妈
     */
    private String uid;

    /**
     * 驴妈妈给供应商的密码,来校验uid合法性
     */
    private String password;

    /**
     * 接口调用时间:yyyyMMddHHmmss
     */
    private String timestamp;

    /**
     * 客户游玩时间:yyyyMMddHHmmss
     */
    private String visitTime;

    /**
     * 供应商商品ID,只有唯一商品,固定值
     */
    private String supplierGoodsId;

    /**
     * 驴妈妈的结算价
     */
    private String settlePrice;

    /**
     * 用户购买数量(你们返回对应数量的码)(最大50)
     */
    private String num;

    /**
     * 驴妈妈订单号(方便你们要和你们对应的订单做关联)
     */
    private String serialNo;

    /**
     * 本地订单号(与驴妈妈订单号对应)
     */
    private String orderId;

    /**
     * 联系人信息,Json格式字符串
     */
    private LvContact contacts;

    /**
     * 游玩人信息,Json格式字符串
     */
    private LvTraveller[] travellerList;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后修改时间
     */
    private Date updateTime;

    /**
     * 扩展数据
     */
    private String properties;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(String visitTime) {
        this.visitTime = visitTime;
    }

    public String getSupplierGoodsId() {
        return supplierGoodsId;
    }

    public void setSupplierGoodsId(String supplierGoodsId) {
        this.supplierGoodsId = supplierGoodsId;
    }

    public String getSettlePrice() {
        return settlePrice;
    }

    public void setSettlePrice(String settlePrice) {
        this.settlePrice = settlePrice;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public LvContact getContacts() {
        return contacts;
    }

    public void setContacts(LvContact contacts) {
        this.contacts = contacts;
    }

    public LvTraveller[] getTravellerList() {
        return travellerList;
    }

    public void setTravellerList(LvTraveller[] travellerList) {
        this.travellerList = travellerList;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }
}
