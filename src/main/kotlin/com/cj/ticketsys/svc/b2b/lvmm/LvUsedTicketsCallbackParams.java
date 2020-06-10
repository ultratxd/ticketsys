package com.cj.ticketsys.svc.b2b.lvmm;

import lombok.Data;

/**
 * @author wangliwei
 * @date 2020/4/8
 * <p>
 * 驴妈妈接口对接
 * 消费通知接口请求参数
 */

public class LvUsedTicketsCallbackParams {

    /**
     * 空字符串
     * 必传
     */
    private final String msg = "";

    /**
     * 驴妈妈订单号
     * 必传
     */
    private String serialNo;

    /**
     * 驴妈妈提供的授权id
     * 必传
     */
    private String uid;

    /**
     * 驴妈妈提供的授权密码，用来校验uid的合法性
     * 必传
     */
    private String password;

    /**
     * 使用状态：1-未使用 ，2-部分使用 ，3-完全使用
     * 必传
     */
    private String status;

    /**
     * 已使用数量（已使用的总数量，不是当次消费数量）
     * 必传
     */
    private String usedCount;

    /**
     * 码被使用的时间，格式：yyyyMMddHHmmss
     * 必传
     */
    private String usedTime;

    /**
     * 签名
     * 必传
     */
    private String sign;

    public String getMsg() {
        return msg;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(String usedCount) {
        this.usedCount = usedCount;
    }

    public String getUsedTime() {
        return usedTime;
    }

    public void setUsedTime(String usedTime) {
        this.usedTime = usedTime;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
