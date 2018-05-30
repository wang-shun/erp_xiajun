package com.wangqin.globalshop.channel.service.channel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wangqin.globalshop.biz1.app.constants.enums.ItemStatus;
import com.wangqin.globalshop.biz1.app.dal.dataObject.ChannelAccountDO;
import com.wangqin.globalshop.biz1.app.dal.dataObject.ChannelListingItemDO;
import com.wangqin.globalshop.biz1.app.dal.dataObject.ChannelListingItemSkuDO;
import com.wangqin.globalshop.biz1.app.dal.dataObject.InventoryDO;
import com.wangqin.globalshop.biz1.app.dal.dataObject.ItemDO;
import com.wangqin.globalshop.channel.dal.dataObjectVo.ItemVo;
import com.wangqin.globalshop.channel.dal.mapperExt.MallOrderDOMapperExt;
import com.wangqin.globalshop.channel.dal.mapperExt.MallSubOrderDOMapperExt;
import com.wangqin.globalshop.channel.service.channelAccount.IChannelAccountService;
import com.wangqin.globalshop.channel.service.channelItem.IChannelListingItemService;
import com.wangqin.globalshop.channel.service.channelItem.IChannelListingItemSkuService;
import com.wangqin.globalshop.channel.service.inventory.IInventoryService;
import com.wangqin.globalshop.channel.service.item.IItemService;
import com.wangqin.globalshop.channel.service.item.IItemSkuService;
import com.wangqin.globalshop.channel.service.order.IMallOrderService;
import com.wangqin.globalshop.channel.service.order.IMallSubOrderService;
import com.wangqin.globalshop.channel.service.order.IShippingOrderService;
import com.wangqin.globalshop.channel.service.utils.ISequenceService;
import com.wangqin.globalshop.common.scan.SpringUtils;

public abstract class AbstractChannelService implements IChannelService, IChannelAdapter {
	// logger
	protected Logger logger = LogManager.getLogger(getClass());
	
	// 平台账号信息
	protected ChannelAccountDO channelAccount;
	
	protected IChannelAccountService channelAccountService = SpringUtils.getBean(IChannelAccountService.class);

	protected IItemService itemService = SpringUtils.getBean(IItemService.class);
	protected IItemSkuService itemSkuService = SpringUtils.getBean(IItemSkuService.class);

	protected IChannelListingItemService outerItemService = SpringUtils.getBean(IChannelListingItemService.class);
	protected IChannelListingItemSkuService outerItemSkuService = SpringUtils.getBean(IChannelListingItemSkuService.class);


	protected MallSubOrderDOMapperExt outerOrderDetailMapper = SpringUtils.getBean(MallSubOrderDOMapperExt.class);

	protected MallOrderDOMapperExt outerOrderMapper = SpringUtils.getBean(MallOrderDOMapperExt.class);

	protected IMallOrderService outerOrderService = SpringUtils.getBean(IMallOrderService.class);

	protected IMallSubOrderService mallSubOrderService = SpringUtils.getBean(IMallSubOrderService.class);
	
	protected IShippingOrderService shippingOrderService = SpringUtils.getBean(IShippingOrderService.class);
	protected IInventoryService inventoryService = SpringUtils.getBean(IInventoryService.class);

	public AbstractChannelService(ChannelAccountDO channelAccount) {
		this.channelAccount = channelAccount;
	}
	protected ISequenceService sequenceUtilService = SpringUtils.getBean(ISequenceService.class);


	//protected IUploadFileService uploadFileService = SpringUtils.getBean(IUploadFileService.class);



	static public class AdapterData {
		public ItemDO item;
		public ChannelListingItemDO outerItem;
		public List<ChannelListingItemSkuDO> outerItemSkus = new ArrayList<ChannelListingItemSkuDO>();
	}
	
	protected void auth() {
		adapterAuth();
		// 保存到channel_account中
		channelAccount.setGmtModify(new Date());
		channelAccountService.updateByPrimaryKey(channelAccount);
	}
	
	
	@Override
	public void createItem(Long itemId) {
		syncItem(itemId);
	}

	@Override
	public void syncItem(Long itemId) {
		ItemVo item = itemService.queryItem(itemId);

		//0正常，1关闭
		if (this.channelAccount.getStatus().equals(1)) {
			return;
		}

		
		// 查看outerItem是否存在
		ChannelListingItemDO outerItem = new ChannelListingItemDO();
		outerItem.setItemCode(item.getItemCode());
		outerItem.setChannelNo(this.channelAccount.getChannelNo());
		ChannelListingItemDO selOuterItem = this.outerItemService.queryPo(outerItem);
		// 未同步过
		if(selOuterItem == null) {
			AdapterData adapterData = this.adapterCreateItem(item);
			this.outerItemService.insert(adapterData.outerItem);
			
			for (ChannelListingItemSkuDO sku : adapterData.outerItemSkus) {
				ChannelListingItemSkuDO outerItemSku = new ChannelListingItemSkuDO();
				outerItemSku.setSkuCode(sku.getSkuCode());
				outerItemSku.setPlatformType(this.channelAccount.getType());
				ChannelListingItemSkuDO selOuterItemSku = this.outerItemSkuService.queryPo(outerItemSku);
				if (selOuterItemSku == null) {
					this.outerItemSkuService.insert(sku);
				} else {
					this.outerItemSkuService.updateByPrimaryKey(sku);
				}
			}
		} else {
			this.adapterUpdateItem(item, selOuterItem);
		}
	}

	// 上架
	public void syncListingItem(Long itemId) {
		ItemVo item = itemService.queryItem(itemId);
		syncListingItem(item);
	}
	public void syncListingItem(ItemVo item) {
		// 查询出第三方平台帐号，上架
		ChannelListingItemDO outerItemQuery = new ChannelListingItemDO();
		outerItemQuery.setItemCode(item.getItemCode());
		ChannelListingItemDO outerItemDb = this.outerItemService.queryPo(outerItemQuery);
		if (outerItemDb != null) {
		    adapterListingItem(item, outerItemDb);
		    // 更新外部商品状态
	        outerItemDb.setStatus(ItemStatus.LISTING.getCode());
	        this.outerItemService.updateByPrimaryKey(outerItemDb);
		}else{

			try {
				ChannelFactory.getChannel(channelAccount).createItem(item.getId());
			}catch (Exception e){
				logger.error("商品上架时同步到【"+channelAccount.getChannelName()+"】失败,shopCode："+channelAccount.getShopCode(), e);
			}

		}
	}

	// 下架
	public void syncDelistingItem(Long itemId) {
		ItemVo item = itemService.queryItem(itemId);
		syncDelistingItem(item);
	}
	public void syncDelistingItem(ItemVo item) {
		ChannelListingItemDO outerItemQuery = new ChannelListingItemDO();
		outerItemQuery.setItemCode(item.getItemCode());
		ChannelListingItemDO outerItemDb = this.outerItemService.queryPo(outerItemQuery);
		if(outerItemDb!=null){
		    adapterDelistingItem(item, outerItemDb);
		    //更新外部商品状态
	        outerItemDb.setStatus(ItemStatus.DELISTING.getCode());
	        this.outerItemService.updateByPrimaryKey(outerItemDb);
		}
	}
	
	public void syncDelistingSaleOutItems() {
		
	}
	
	public void syncSkuInventory(String itemCode,String skuCode) {
		//查询是否同步到有赞，并且为已上架状态
		ChannelListingItemDO outerItemQuery = new ChannelListingItemDO();
		outerItemQuery.setItemCode(itemCode);
		ChannelListingItemDO outerItemDb = this.outerItemService.queryPo(outerItemQuery);
		if(outerItemDb==null){
//			throw new ErpCommonException("更新outerItem 订单信息错误;");
//			this.logger.info("没有找到对应的有赞商品，itemId:"+itemId);
		}
		else{
			//上架中的商品
			if(outerItemDb.getStatus()!=null&&outerItemDb.getStatus()==ItemStatus.LISTING.getCode()){
				ChannelListingItemSkuDO outerItemSkuQuery = new ChannelListingItemSkuDO();
				outerItemSkuQuery.setItemCode(itemCode);
				outerItemSkuQuery.setSkuCode(skuCode);
				ChannelListingItemSkuDO outerItemSkuDb = this.outerItemSkuService.queryPo(outerItemSkuQuery);
				if(outerItemSkuDb!=null&&outerItemSkuDb.getChannelItemSkuCode()!=null){
					InventoryDO inventory=inventoryService.queryInventoryByCode(itemCode, skuCode);
					//同步
					if(inventory!=null){
						adapterUpdateSkuInventory(outerItemSkuDb, inventory);
					}else{
//						this.logger.info("没有找到库存信息，skuId:"+skuId);
					}
				}else{
//					this.logger.info("没有找到对应的有赞sku，skuId:"+skuId);
				}
			}else{
//				this.logger.info("商品未上架不能同步库存，skuId:"+skuId);
			}
		}
	}
	

}
