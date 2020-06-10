package com.cj.ticketsys.svc.b2b.lvmm;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wangliwei
 * @date 2020/4/9
 *
 * 驴妈妈接口对接
 * 下单返回结果
 */

public class LvApplyCodeResult extends LvResult {

    /**
     * 取票、入园凭证码。多个时以逗号分隔开，状态码不为0的时候为空
     */
    private String authCode;

    /**
     * 二维码超链接。多个可以用逗号隔开
     */
    private String codeURL;

    /**
     * 供应商的订单号
     */
    private String orderId;

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
