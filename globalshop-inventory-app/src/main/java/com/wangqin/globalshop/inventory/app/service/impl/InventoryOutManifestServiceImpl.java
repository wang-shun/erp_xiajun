package com.wangqin.globalshop.inventory.app.service.impl;

import com.wangqin.globalshop.biz1.app.dal.dataObject.InventoryOutManifestDO;
import com.wangqin.globalshop.biz1.app.dal.dataVo.InventoryOutManifestVO;
import com.wangqin.globalshop.biz1.app.dal.mapperExt.InventoryOutManifestMapperExt;
import com.wangqin.globalshop.common.utils.AppUtil;
import com.wangqin.globalshop.common.utils.CodeGenUtil;
import com.wangqin.globalshop.inventory.app.service.InventoryOutManifestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author angus
 * @date 2018/7/12
 */
@Service
public class InventoryOutManifestServiceImpl implements InventoryOutManifestService {

    @Autowired
    private InventoryOutManifestMapperExt inventoryOutManifestMapper;

    /**
     * 查询出货单列表
     */
    @Override
    public List<InventoryOutManifestDO> listInventoryOutManifest(InventoryOutManifestVO inventoryOutManifestVO) {
        return inventoryOutManifestMapper.listInventoryOutManifest(inventoryOutManifestVO);
    }

    /**
     * 添加出货单
     */
    @Override
    public void insertInventoryOutManifest(InventoryOutManifestDO inventoryOutManifest) {
        inventoryOutManifestMapper.insert(inventoryOutManifest);
    }

    /**
     * 添加出货单
     */
    @Override
    public InventoryOutManifestDO insertInventoryOutManifest(String warehouseNo, String warehouseName, String remark) {
        InventoryOutManifestDO inventoryOutManifestDO = new InventoryOutManifestDO();

        inventoryOutManifestDO.setInventoryOutNo(CodeGenUtil.getInventoryOutNo());
        inventoryOutManifestDO.setCompanyNo(AppUtil.getLoginUserCompanyNo());
        inventoryOutManifestDO.setWarehouseNo(warehouseNo);
        inventoryOutManifestDO.setWarehouseName(warehouseName);
        inventoryOutManifestDO.setStatus(1);
        inventoryOutManifestDO.setRemark(remark);
        inventoryOutManifestDO.setModifier(AppUtil.getLoginUserId());
        inventoryOutManifestDO.setCreator(AppUtil.getLoginUserId());
        inventoryOutManifestDO.setIsDel(false);

        insertInventoryOutManifest(inventoryOutManifestDO);
        return inventoryOutManifestDO;
    }

}
