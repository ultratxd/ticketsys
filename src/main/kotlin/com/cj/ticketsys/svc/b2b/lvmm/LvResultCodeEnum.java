package com.cj.ticketsys.svc.b2b.lvmm;

import lombok.Getter;

/**
 * @author wangliwei
 * @date 2020/4/8
 *
 * 驴妈妈返回状态码
 */

public enum LvResultCodeEnum {

    /**
     * 驴妈妈返回状态码枚举
     */
    SUCCESS("0", "正确"),
    INVALID_PARAM_ERROR("1","参数错误某部分参数为空或者格式不合法"),
    VERIFY_SIGN_FAIL("2", "签名校验失败"),
    CREATE_CODE_FAILED("3", "创建码失败"),
    ORDER_NOT_EXIST("4", "找不到对应订单"),
    ORDER_IS_REFUND("5", "订单已退票"),
    ORDER_IS_USED("6", "订单已使用"),
    ORDER_IS_PART_USED("7", "订单已部分使用"),
    SERVER_IS_ERROR("8", "系统异常"),
    USER_NOT_MATCH_PASSWORD("9", "用户名和密码不匹配"),
    USER_NOT_EXIST("10", "用户名不存在"),
    OTHER_ERROR("11", "其他"),
    NOT_SUFFICIENT_FUNDS("12", "账户余额不足"),
    PRODUCT_ID_NOT_EXIST("13", "商品ID不存在"),
    SEND_MESSAGE_FAILED("14", "供应商发送短信失败"),
    AUDIT("AUDIT","审核中");

    private String status;
    private String message;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    LvResultCodeEnum(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
