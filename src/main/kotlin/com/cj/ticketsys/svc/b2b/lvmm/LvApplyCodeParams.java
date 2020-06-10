package com.cj.ticketsys.svc.b2b.lvmm;

import lombok.Data;

/**
 * @author wangliwei
 * @date 2020/4/8
 *
 * 驴妈妈接口对接
 * 下单接口请求参数
 */

public class LvApplyCodeParams {

    /**
     * 驴妈妈给供应商的ID，用来标记，哪些订单来自驴妈妈
     * 必存
     */
    private String uid;

    /**
     * 驴妈妈给供应商的密码，来校验uid合法性
     * 必存
     */
    private String password ;

    /**
     * 接口调用时间：yyyyMMddHHmmss
     * 必存
     */
    private String timestamp;

    /**
     * 客户游玩时间：yyyyMMddHHmmss
     * 必存
     */
    private String visitTime;

    /**
     * 供应商商品ID，只有唯一商品,固定值
     * 必存
     */
    private String supplierGoodsId;

    /**
     * 驴妈妈的结算价
     * 非必存
     */
    private String settlePrice;

    /**
     * 用户购买数量(你们返回对应数量的码)(最大50)
     * 必存
     */
    private String num;

    /**
     * 驴妈妈订单号(方便你们要和你们对应的订单做关联)
     * 必存
     */
    private String serialNo;

    /**
     * 加密规则见上文“签名算法”
     * 必存
     */
    private String sign;

    /**
     * 联系人信息,Json格式字符串
     * 非必存
     */
    private String contacts;

    /**
     * 游玩人信息,Json格式字符串
     * 非必存
     */
    private String travellerList;


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

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getTravellerList() {
        return travellerList;
    }

    public void setTravellerList(String travellerList) {
        this.travellerList = travellerList;
    }
}
