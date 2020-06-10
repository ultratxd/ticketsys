package com.cj.ticketsys.svc.b2b.lvmm;

import lombok.Data;

/**
 * @author wangliwei
 * @date 2020/4/8
 *
 * 驴妈妈接口对接
 * 下单审核结果通知接口请求参数
 */

public class LvAsynNoticeCallbackParams {

    /**
     * 空字符串
     * 必传
     */
    private String msg;

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
     * 驴妈妈提供的授权密码，校验uid的合法性
     * 必传
     */
    private String password;

    /**
     * 审核结果：0-出票成功，其他状态为出票失败
     * 必传
     */
    private String status;

    /**
     * 签名
     * 必传
     */
    private String sign;

    /**
     * 取票/入园凭证码。多个时以逗号分隔，状态不为0时为空
     * 必传
     */
    private String authCode;

    /**
     * 二维码超链接
     * 非必传
     */
    private String codeURL;

    /**
     * 供应商的id
     * 必传
     */
    private String orderId;


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getCodeURL() {
        return codeURL;
    }

    public void setCodeURL(String codeURL) {
        this.codeURL = codeURL;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
