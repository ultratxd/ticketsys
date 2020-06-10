package com.cj.ticketsys.svc.b2b.lvmm;

import lombok.Data;

/**
 * @author wangliwei
 * @date 2020/4/8
 *
 * 驴妈妈接口对接
 * 废单审核通知接口请求参数
 */

public class LvDiscardCodeCallbackParams {

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
     * 驴妈妈提供的授权密码,又来校验uid的合法性
     * 必传
     */
    private String password;

    /**
     * 审核结果 APPROVE-审核通过  REJECT-拒绝
     * 必传
     */
    private String status;

    /**
     * 拒绝原因
     * 非必传
     */
    private String msg;

    /**
     * 签名
     * 必传
     */
    private String sign;

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

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
