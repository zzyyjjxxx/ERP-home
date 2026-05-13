package com.erp.purchase.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.exception.BusinessException;
import com.erp.purchase.entity.PurSupplier;
import com.erp.purchase.mapper.PurSupplierMapper;
import com.erp.purchase.service.PurSupplierService;
import org.springframework.stereotype.Service;

@Service
public class PurSupplierServiceImpl extends ServiceImpl<PurSupplierMapper, PurSupplier> implements PurSupplierService {

    @Override
    public void addSupplier(PurSupplier supplier) {
        if (lambdaQuery().eq(PurSupplier::getCode, supplier.getCode()).count() > 0) {
            throw new BusinessException("供应商编码已存在");
        }
        save(supplier);
    }

    @Override
    public void updateSupplier(PurSupplier supplier) {
        if (lambdaQuery().eq(PurSupplier::getCode, supplier.getCode()).ne(PurSupplier::getId, supplier.getId()).count() > 0) {
            throw new BusinessException("供应商编码已存在");
        }
        updateById(supplier);
    }
}
