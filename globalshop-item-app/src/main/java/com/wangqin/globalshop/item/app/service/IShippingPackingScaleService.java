package com.wangqin.globalshop.item.app.service;

import java.util.List;

import com.wangqin.globalshop.biz1.app.dal.dataObject.ShippingPackingScaleDO;
import com.wangqin.globalshop.common.utils.JsonPageResult;

public interface IShippingPackingScaleService {

	List<ShippingPackingScaleDO> queryPackageScales();
	
	JsonPageResult<List<ShippingPackingScaleDO>> queryPackageScaleTree();
}
