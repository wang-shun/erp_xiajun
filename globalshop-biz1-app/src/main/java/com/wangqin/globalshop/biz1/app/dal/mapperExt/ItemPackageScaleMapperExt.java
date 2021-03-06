package com.wangqin.globalshop.biz1.app.dal.mapperExt;

import java.util.List;

import com.wangqin.globalshop.biz1.app.dal.dataObject.ShippingPackingScaleDO;
import com.wangqin.globalshop.biz1.app.bean.dto.ItemPackageScaleDTO;
import com.wangqin.globalshop.biz1.app.bean.dataVo.ShippingPackingScaleQueryVO;





public interface ItemPackageScaleMapperExt {
	ItemPackageScaleDTO selectById(Long id);
	
	List<ItemPackageScaleDTO> queryAllPackageScale();
	
	void insertPackageScale(ItemPackageScaleDTO itemPackageScaleDTO);
	
	void delete(Long id);
	
	void updateScaleSelectiveById(ItemPackageScaleDTO itemPackageScaleDTO);
	
	List<ItemPackageScaleDTO> queryScaleList(ShippingPackingScaleQueryVO scaleVO);
	
	List<ShippingPackingScaleDO> queryScaleListSelective(ShippingPackingScaleQueryVO shippingPackingScaleQueryVO);
	String queryNoById(Long id);
}
