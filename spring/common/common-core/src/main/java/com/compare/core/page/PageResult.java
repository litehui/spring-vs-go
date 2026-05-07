package com.compare.core.page;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {
    private long total;
    private List<T> records;
    private int pageNum;
    private int pageSize;

    public static <T> PageResult<T> of(List<T> records, long total, int pageNum, int pageSize) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }
}
