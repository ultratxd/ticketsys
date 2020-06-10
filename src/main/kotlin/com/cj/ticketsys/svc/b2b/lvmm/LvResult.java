package com.cj.ticketsys.svc.b2b.lvmm;

import lombok.Data;

import javax.lang.model.element.NestingKind;

/**
 * @author wangliwei
 * @date 2020/4/9
 *
 * 驴妈妈通用返回结果
 */

public class LvResult {

    /**
     * 返回状态码
     */
    private String status;

    /**
     * 返回失败信息，可以为空
     */
    private String msg;

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
}
