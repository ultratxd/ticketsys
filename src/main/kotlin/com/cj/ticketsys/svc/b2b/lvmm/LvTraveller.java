package com.cj.ticketsys.svc.b2b.lvmm;

import lombok.Data;

/**
 * <p>Create Time: 2020年04月26日</p>
 * <p>@author tangxd</p>
 **/
public class LvTraveller {
    private String idNum;
    private String idType;
    private String mobile;
    private String name;

    public String getIdNum() {
        return idNum;
    }

    public void setIdNum(String idNum) {
        this.idNum = idNum;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
