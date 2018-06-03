package com.wangqin.globalshop.inventory.app.service.impl;

import com.wangqin.globalshop.biz1.app.dal.dataObject.WarehouseDO;
import com.wangqin.globalshop.biz1.app.dal.mapperExt.WarehouseDOMapperExt;
import com.wangqin.globalshop.inventory.app.service.IWarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WarehouseServiceImpl implements IWarehouseService {

	@Autowired
	private WarehouseDOMapperExt mapper;

	public Map<String,Integer> getWarehousePropeties(String companyNo){

		Map<String,Integer> propetiesMap = new HashMap<>();

		WarehouseDO so = new WarehouseDO();
		so.setCompanyNo(companyNo);
		List<WarehouseDO> warehouseDOS =  mapper.queryPoList(so);

		for(WarehouseDO warehouseDO : warehouseDOS){
			propetiesMap.put(warehouseDO.getWarehouseNo(),warehouseDO.getDeliveryPriority());
		}

		return propetiesMap;

	}

	@Override
	public WarehouseDO getWarehouseById(Long warehouseId) {
		return mapper.selectByPrimaryKey(warehouseId);
	}

	@Override
	public void addWarehouse(WarehouseDO warehouse) {
		mapper.insertSelective(warehouse);
	}

	@Override
	public void updateWarehouse(WarehouseDO warehouse) {
		mapper.updateByPrimaryKeySelective(warehouse);
	}

	@Override
	public Object selectById(Long id) {
		return mapper.selectByPrimaryKey(id);
	}

	@Override
	public List<WarehouseDO> queryWarehouses(String companyNo) {
		return mapper.selectByCompanyNo(companyNo);
	}
}
