package com.erp.common.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.response.PageResult;
import com.erp.common.response.Result;

public abstract class BaseController {

    protected <T> Result<PageResult<T>> pageResult(Page<T> page) {
        return Result.ok(PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize()));
    }
}
