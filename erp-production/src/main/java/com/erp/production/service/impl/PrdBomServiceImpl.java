package com.erp.production.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.exception.BusinessException;
import com.erp.production.entity.PrdBom;
import com.erp.production.entity.PrdBomItem;
import com.erp.production.mapper.PrdBomItemMapper;
import com.erp.production.mapper.PrdBomMapper;
import com.erp.production.service.PrdBomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrdBomServiceImpl extends ServiceImpl<PrdBomMapper, PrdBom> implements PrdBomService {

    private final PrdBomItemMapper bomItemMapper;

    @Override
    public PrdBom getBomWithItems(Long bomId) {
        PrdBom bom = getById(bomId);
        if (bom == null) {
            throw new BusinessException("BOM不存在");
        }
        return bom;
    }

    @Override
    public List<PrdBomItem> explodeBom(Long bomId) {
        PrdBom bom = getById(bomId);
        if (bom == null) {
            throw new BusinessException("BOM不存在");
        }
        LambdaQueryWrapper<PrdBomItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PrdBomItem::getBomId, bomId)
                .orderByAsc(PrdBomItem::getSeq);
        return bomItemMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void createBom(PrdBom bom, List<PrdBomItem> items) {
        if (bom.getVersion() == null || bom.getVersion().isEmpty()) {
            bom.setVersion("1.0");
        }
        if (bom.getStatus() == null) {
            bom.setStatus(0);
        }
        save(bom);
        if (items != null && !items.isEmpty()) {
            for (int i = 0; i < items.size(); i++) {
                PrdBomItem item = items.get(i);
                item.setBomId(bom.getId());
                if (item.getSeq() == null) {
                    item.setSeq(i + 1);
                }
                bomItemMapper.insert(item);
            }
        }
    }

    @Override
    @Transactional
    public void updateBom(PrdBom bom, List<PrdBomItem> items) {
        PrdBom existing = getById(bom.getId());
        if (existing == null) {
            throw new BusinessException("BOM不存在");
        }
        updateById(bom);
        if (items != null) {
            // Remove old items and insert new ones
            LambdaQueryWrapper<PrdBomItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PrdBomItem::getBomId, bom.getId());
            bomItemMapper.delete(wrapper);
            for (int i = 0; i < items.size(); i++) {
                PrdBomItem item = items.get(i);
                item.setId(null);
                item.setBomId(bom.getId());
                if (item.getSeq() == null) {
                    item.setSeq(i + 1);
                }
                bomItemMapper.insert(item);
            }
        }
    }

    @Override
    public List<PrdBomItem> getBomItems(Long bomId) {
        LambdaQueryWrapper<PrdBomItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PrdBomItem::getBomId, bomId)
                .orderByAsc(PrdBomItem::getSeq);
        return bomItemMapper.selectList(wrapper);
    }

    @Override
    public void enableBom(Long id) {
        PrdBom bom = getById(id);
        if (bom == null) {
            throw new BusinessException("BOM不存在");
        }
        if (bom.getStatus() == 1) {
            throw new BusinessException("BOM已启用");
        }
        bom.setStatus(1);
        updateById(bom);
    }

    @Override
    public void disableBom(Long id) {
        PrdBom bom = getById(id);
        if (bom == null) {
            throw new BusinessException("BOM不存在");
        }
        if (bom.getStatus() == 2) {
            throw new BusinessException("BOM已停用");
        }
        bom.setStatus(2);
        updateById(bom);
    }
}
