package com.erp.common.base;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import java.util.Map;

/**
 * 动态排序工具。前端传 sortField/sortOrder，后端据此动态构建 ORDER BY。
 * 默认按 createTime DESC 排序。
 */
public class SortHelper {

    /**
     * 为 wrapper 添加动态排序。传 "" 或 null 的 sortField 将使用 defaultField DESC。
     * @param wrapper   查询 wrapper
     * @param sortField 排序字段名（camelCase，如 "createTime"）
     * @param sortOrder 排序方向（"asc" / "desc"）
     * @param defaultField 默认排序字段
     * @param fieldMap  字段名 -> SFunction 的映射（用于前端字段名到实体字段的转换）
     */
    public static <T> void applySort(LambdaQueryWrapper<T> wrapper,
                                      String sortField, String sortOrder,
                                      SFunction<T, ?> defaultField,
                                      Map<String, SFunction<T, ?>> fieldMap) {
        boolean asc = "asc".equalsIgnoreCase(sortOrder);
        SFunction<T, ?> useField = null;

        if (sortField != null && !sortField.isEmpty() && fieldMap != null) {
            useField = fieldMap.get(sortField);
        }

        if (useField != null) {
            wrapper.orderBy(true, asc, useField);
        } else {
            // 默认：按 defaultField DESC（除非指定了 asc）
            if (sortField != null && !sortField.isEmpty() && asc) {
                wrapper.orderByAsc(defaultField);
            } else {
                wrapper.orderByDesc(defaultField);
            }
        }
    }
}
