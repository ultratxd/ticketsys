package com.cj.ticketsys.svc.b2b.lvmm;

import lombok.Data;

/**
 * @author wangliwei
 * @date 2020/4/8
 *
 * 驴妈妈接口对接
 * 废单接口请求参数
 */

public class LvDiscardCodeParams {

    /**
     * 驴妈妈提供的供应商id
     * 必存
     */
    private String uid;

    /**
     * 驴妈妈提供的供应商密码，用来验证id的有效性
     * 必存
     */
    private String password;

    /**
     * 接口调用时间：yyyyMMddHHmmss
     * 必存
     */
    private String timestamp;

    /**
     * 供应商订单ID
     * 必存
     */
    private String extId;

    /**
     * 加密规则见签名算法
     * 必存
     */
    private String sign;

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

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
