package com.wangqin.globalshop.biz1.app.dal.mapperExt;

import com.wangqin.globalshop.biz1.app.dal.dataObject.BuyerDO;
import com.wangqin.globalshop.biz1.app.dal.mapper.BuyerDOMapper;

import java.util.List;


public interface BuyerMapperExt extends BuyerDOMapper{

	//Integer queryWxPurchaseUserCount(BuyerVO wxPurchaseUserVO);
	
	//List<WxPurchaseUser> queryWxPurchaseUser(BuyerVO wxPurchaseUserVO);
	
	List<BuyerDO> queryAllBuyers();
}
