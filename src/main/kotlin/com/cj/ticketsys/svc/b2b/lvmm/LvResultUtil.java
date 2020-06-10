package com.cj.ticketsys.svc.b2b.lvmm;


/**
 * @author wangliwei
 * @date 2020/4/9
 *
 * 驴妈妈返回结果工具类
 */

public class LvResultUtil {

    /**
     * 返回
     * @param status 返回状态码
     * @param msg 状态信息
     * @return String
     */
    public static LvResult declare(String status,String msg) {
        LvResult result = new LvResult();
        result.setStatus(status);
        result.setMsg(msg);
        return result;
    }

    /**
     * 成功返回
     * @param msg 提示信息
     * @return String
     */
    public static LvResult success(String msg) {
        LvResult result = new LvResult();
        result.setStatus(LvResultCodeEnum.SUCCESS.getStatus());
        result.setMsg(msg);
        return result;
    }

    /**
     * 成功返回
     * @return String
     */
    public static LvResult success() {
        LvResult result = new LvResult();
        result.setStatus(LvResultCodeEnum.SUCCESS.getStatus());
        result.setMsg("");
        return result;
    }

    /**
     * 失败返回
     * @param status 失败状态码
     * @param msg 提示信息
     * @return String
     */
    public static LvResult fail(String status, String msg) {
        LvResult result = new LvResult();
        result.setStatus(status);
        result.setMsg(msg);
        return result;
    }

    /**
     * 失败返回
     * @param status 失败状态码
     * @return String
     */
    public static LvResult fail(String status) {
        LvResult result = new LvResult();
        result.setStatus(status);
        result.setMsg("");
        return result;
    }

}
