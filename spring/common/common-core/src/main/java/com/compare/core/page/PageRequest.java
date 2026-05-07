package com.compare.core.page;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PageRequest {
    @Min(1)
    private int pageNum = 1;

    @Min(1)
    @Max(500)
    private int pageSize = 10;
}
