package com.erp.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.erp.inventory.entity.InvProduct;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InvProductMapper extends BaseMapper<InvProduct> {
}
