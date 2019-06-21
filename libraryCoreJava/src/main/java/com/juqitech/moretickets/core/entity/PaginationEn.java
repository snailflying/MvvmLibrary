package com.juqitech.moretickets.core.entity;

import java.io.Serializable;

/**
 * @author zhanfeng
 * @date 2019-06-05
 * @desc
 */
public class PaginationEn implements Serializable {

    /**
     * count : 9
     * offset : 0
     * length : 20
     */

    private int count;
    private int offset;
    private int length;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

}
