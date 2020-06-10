package com.cj.ticketsys.svc.b2b.lvmm;

import lombok.Getter;

/**
 * @author wangliwei
 * @date 2020/4/9
 * <p>
 * 驴妈妈接口对接
 * 证件类型以及描述
 */

public enum LvIdTypeEnum {

    /**
     * 证件枚举
     */
    ER_TONG("ERTONG", "儿童无证件"),
    GAN_GAO("GANGAO", "港澳通行证"),
    HUI_XIANG("HUIXIANG", "回乡证"),
    HU_ZHAO("HUZHAO", "护照"),
    ID_CARD("ID_CARD", "身份证"),
    SHI_BING("SHIBING", "士兵证"),
    JUN_GUAN("JUNGUAN", "军官证"),
    HU_KOU_BO("HUKOUBO", "户口薄"),
    CHU_SHENG_ZHENG_MING("CHUSHENGZHENGMING", "出生证明"),
    TAI_BAO("TAIBAO", "台湾通行证"),
    TAI_BAO_ZHENG("TAIBAOZHENG", "台胞证"),
    OTHER("OTHER", "其他");

    /**
     * 证件类型
     */
    private String idType;
    /**
     * 证件类型描述
     */
    private String des;

    public String getIdType() {
        return idType;
    }

    public String getDes() {
        return des;
    }

    LvIdTypeEnum(String idType, String des) {
        this.idType = idType;
        this.des = des;
    }
}
