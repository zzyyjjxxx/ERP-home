package com.erp.purchase.dto;

import com.erp.purchase.entity.PurReturn;
import com.erp.purchase.entity.PurReturnItem;
import lombok.Data;
import java.util.List;

@Data
public class CreateReturnRequest {
    private PurReturn purReturn;
    private List<PurReturnItem> items;
}
