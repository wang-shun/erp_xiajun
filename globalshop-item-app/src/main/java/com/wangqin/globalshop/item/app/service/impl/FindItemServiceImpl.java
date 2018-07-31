package com.wangqin.globalshop.item.app.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wangqin.globalshop.biz1.app.dal.dataObject.ItemFindDO;
import com.wangqin.globalshop.biz1.app.dal.mapperExt.ItemFindDOMapperExt;
import com.wangqin.globalshop.biz1.app.bean.dataVo.ItemQueryVO;
import com.wangqin.globalshop.common.exception.ErpCommonException;
import com.wangqin.globalshop.common.utils.JsonPageResult;
import com.wangqin.globalshop.item.app.service.IFindItemService;

@Service
public class FindItemServiceImpl implements IFindItemService {
//    protected Logger logger = LogManager.getLogger(getClass());
//    @Autowired
//	private IFindItemService findItemService;
//    @Autowired
//	private IItemSkuService itemSkuService;
//	
//	@Autowired
//	private IItemService itemService;
//	
//	@Autowired
//	private IFindItemSkuService findItemSkuService;
//	
//	@Autowired
//	private IInventoryService inventoryService;
	
	@Autowired
	private ItemFindDOMapperExt itemFindDOMapperExt;
	
	@Override
	@Transactional(rollbackFor = ErpCommonException.class)
	public JsonPageResult<List<ItemFindDO>> queryFindItems(ItemQueryVO itemQueryVO) {
		JsonPageResult<List<ItemFindDO>> itemResult = new JsonPageResult<>();
		//1、查询总的记录数量
		Integer totalCount =  itemFindDOMapperExt.queryItemsCount();
		
		//2、查询分页记录
		if(totalCount!=null&&totalCount!=0){
			//itemResult.buildPage(totalCount, itemQueryVO);
			List<ItemFindDO> items = itemFindDOMapperExt.queryFindItems();
			itemResult.setData(items);
		}else{
			List<ItemFindDO> items  = new ArrayList<>();
			itemResult.setData(items);
		}
		return itemResult;
	}

}
