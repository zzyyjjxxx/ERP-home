package com.erp.production.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.erp.production.entity.PrdBom;
import com.erp.production.entity.PrdBomItem;

import java.util.List;

public interface PrdBomService extends IService<PrdBom> {

    /**
     * Get BOM with all its items (BOM tree)
     */
    PrdBom getBomWithItems(Long bomId);

    /**
     * BOM explosion: given a BOM ID, expand all materials with quantities
     */
    List<PrdBomItem> explodeBom(Long bomId);

    /**
     * Create BOM with items
     */
    void createBom(PrdBom bom, List<PrdBomItem> items);

    /**
     * Update BOM and its items
     */
    void updateBom(PrdBom bom, List<PrdBomItem> items);

    /**
     * Get items for a BOM
     */
    List<PrdBomItem> getBomItems(Long bomId);

    /**
     * Enable a BOM (status 0/2 -> 1)
     */
    void enableBom(Long id);

    /**
     * Disable a BOM (status 1 -> 2)
     */
    void disableBom(Long id);
}
