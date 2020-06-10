package com.cj.ticketsys.svc.b2b.lvmm;

import lombok.Getter;

/**
 * @author wangliwei
 * @date 2020/4/9
 * <p>
 * 废单状态信息
 */

public enum LvOrderRefundEnum {

    /**
     * 废单状态枚举
     */
    AUDIT("AUDIT", "审核中"),
    APPROVE("APPROVE", "审核通过"),
    REJECT("REJECT", "拒绝"),;

    private String status;
    private String message;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    LvOrderRefundEnum(String status, String message) {
        this.status = status;
        this.message = message;
    }

}
