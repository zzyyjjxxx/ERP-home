package com.erp.sales.dto;

import com.erp.sales.entity.SalReturn;
import com.erp.sales.entity.SalReturnItem;
import lombok.Data;
import java.util.List;

@Data
public class CreateReturnRequest {
    private SalReturn salReturn;
    private List<SalReturnItem> items;
}
