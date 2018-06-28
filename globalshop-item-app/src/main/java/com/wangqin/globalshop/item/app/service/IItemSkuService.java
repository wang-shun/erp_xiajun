package com.wangqin.globalshop.item.app.service;

import java.util.List;

import com.wangqin.globalshop.biz1.app.dal.dataObject.InventoryDO;
import com.wangqin.globalshop.biz1.app.dal.dataObject.ItemSkuDO;
import com.wangqin.globalshop.biz1.app.dto.ISkuDTO;
import com.wangqin.globalshop.biz1.app.vo.ItemSkuAddVO;
import com.wangqin.globalshop.biz1.app.vo.ItemSkuQueryVO;
import com.wangqin.globalshop.biz1.app.vo.JsonPageResult;
import org.apache.ibatis.annotations.Param;


public interface IItemSkuService  {

	/**
	 * 按照条件分页查询商品(分页)
	 * @param itemQueryVO
	 * @return
	 */
	JsonPageResult<List<ISkuDTO>> queryItemSkus(ItemSkuQueryVO itemSkuQueryVO);
	
	
	
	/**
	 * 根据SKU初始化库存信息
	 * @param itemSkuList
	 * @return
	 */
	List<InventoryDO> initInventory(List<ItemSkuAddVO> itemSkuList);
	
	/**
	 * 新增SKU
	 * @param itemSku
	 */
	void addItemSku(ItemSkuDO itemSku);
	
	/**
	 * update SKU
	 * @param itemSku
	 */
	void updateItemSku(ItemSkuDO itemSku);
	
	/**
	 * 新增SKU
	 * @param itemSku
	 */
//	ItemSku queryById(Long skuId);
	
	/**
	 * itemid 查询skulist
	 * @param itemId
	 * @return
	 */
	List<ItemSkuDO> queryItemSkusByItemId(Long itemId );
	
	
	ItemSkuDO selectByPrimaryKey(Long id);
	
	/**
	 * 判断sku是否可以删除
	 * @param itemId
	 * @return
	 */
	boolean isCanDeleteSku(Long skuId);

	
	void deleteById(Long id);
	 
	 List<ItemSkuDO> queryItemSkusForExcel();
	 
	 List<ItemSkuDO> queryItemSkusByUpc(String upc);
	
	 void insertBatch(List<ItemSkuAddVO> skuList);
	 
	 ISkuDTO queryItemSkuBySkuCode(String skuCode);
	 
	 void updateById(ItemSkuQueryVO itemSkuUpdateVO);
	 
	 //查询可售的sku
	 List<ItemSkuDO> querySaleableSkus();
	 
	 List<ItemSkuDO> querySkuListByItemCode(String itemCode);
	 
	 List<ItemSkuDO> queryItemSkuListSelective(ItemSkuQueryVO itemSkuQueryVO);
	 
	 void deleteItemSkuBySkuCode(String skuCode);
	 
	 void insertItemSkuSelective(ItemSkuDO itemSkuDO);
	 
	 String querySkuCodeById(Long id);

    List<ItemSkuDO> queryByItemCodeAndCompanyNo( String itemCode,  String loginUserCompanyNo);

    ItemSkuDO queryBySkuCodeOrUpcAndCompanyNo(String code, String companyNo);
    
    Double querySalePriceByItemCode(String itemCode);
    
    //根据id查出该sku对应的商品在sku表里面映射了几个sku，如果只有一个，禁止删除这个sku
  	Integer querySkuNumberBySkuId(Long skuId);
  	
    //查询指定的upc对应的item_sku的sku_code,按公司划分,防止重复的upc
  	List<String> querySkuCodeListByUpc(String companyNo,String upc);
  	
    //询要更新的upc是否在别的商品名下已经存在,存在则表明出现了upc重复的问题
  	Integer queryRecordCountByUpcCompanyNotInSameItem(String companyNo,String upc,String itemCode);
}
