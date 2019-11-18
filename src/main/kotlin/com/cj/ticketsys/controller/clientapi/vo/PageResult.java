package com.cj.ticketsys.controller.clientapi.vo;

import lombok.Data;

import java.util.List;

/**
 *  @author wangliwei
 *  @date 2019/11/11
 *  分页数据返回封装类
 */

@Data
public class PageResult<T> {
    /**
     * 数据总条数
     */
    private Long total;
    /**
     * 总页码数
     */
    private Integer totalPage;
    /**
     * 分页数据
     */
    private List<T> items;

    public PageResult() {
    }

    public PageResult(Long total, List<T> items) {
        this.total = total;
        this.items = items;
    }

    public PageResult(Long total, Integer totalPage, List<T> items) {
        this.total = total;
        this.totalPage = totalPage;
        this.items = items;
    }
}
