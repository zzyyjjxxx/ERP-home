package com.erp.purchase.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.purchase.entity.PurSupplier;

public interface PurSupplierService extends IService<PurSupplier> {
    void addSupplier(PurSupplier supplier);
    void updateSupplier(PurSupplier supplier);
}
