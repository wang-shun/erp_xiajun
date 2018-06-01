package com.wangqin.globalshop.item.app.service.impl;




import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.wangqin.globalshop.biz1.app.dal.dataObject.CountryDO;
import com.wangqin.globalshop.biz1.app.dal.dataObject.ItemSkuScaleDO;
import com.wangqin.globalshop.biz1.app.dal.mapperExt.CountryMapperExt;
import com.wangqin.globalshop.biz1.app.dal.mapperExt.ItemSkuScaleMapperExt;
import com.wangqin.globalshop.common.utils.JsonPageResult;
import com.wangqin.globalshop.common.utils.RandomUtils;
import com.wangqin.globalshop.item.app.service.ICountryService;
import com.wangqin.globalshop.item.app.service.IItemSkuScaleService;

/**
 * 
 * @author XiaJun
 *
 */

@Service
public class ItemSkuScaleServiceImpl implements IItemSkuScaleService {

	@Autowired
	private ItemSkuScaleMapperExt itemSkuScaleMapperExt;
	
	//查询sku对应的所有规格以及规格的值
	@Override
	public List<ItemSkuScaleDO> selectScaleNameValueBySkuCode(String skuCode) {
		return itemSkuScaleMapperExt.selectScaleNameValueBySkuCode(skuCode);
	}
}
