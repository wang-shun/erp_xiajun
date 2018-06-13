package com.wangqin.globalshop.channel.service.jingdong;

import com.alibaba.fastjson.JSON;
import com.wangqin.globalshop.biz1.app.dal.dataObject.JdItemDO;
import com.wangqin.globalshop.biz1.app.dal.dataObject.JdShopOauthDO;
import com.wangqin.globalshop.biz1.app.dal.mapperExt.JdItemDOMapperExt;
import com.wangqin.globalshop.channelapi.dal.GlobalShopItemVo;
import com.wangqin.globalshop.common.utils.HttpClientUtil;
import com.wangqin.globalshop.common.utils.JsonResult;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by 777 on 2018/6/13
 */
@Service
public class JdItemServiceImpl implements JdItemService {

	protected Logger logger = LogManager.getLogger(getClass());

	@Autowired
	private JdItemDOMapperExt jdItemDOMapperExt;

	public int deleteByPrimaryKey(Long id){
		return jdItemDOMapperExt.deleteByPrimaryKey(id);
	}

	public int insert(JdItemDO record){
		return jdItemDOMapperExt.insert(record);
	}

	public int insertSelective(JdItemDO record){
		return jdItemDOMapperExt.insertSelective(record);
	}

	public JdItemDO selectByPrimaryKey(Long id){
		return jdItemDOMapperExt.selectByPrimaryKey(id);
	}

	public int updateByPrimaryKeySelective(JdItemDO record){
		return jdItemDOMapperExt.updateByPrimaryKeySelective(record);
	}

	public int updateByPrimaryKeyWithBLOBs(JdItemDO record){
		return jdItemDOMapperExt.updateByPrimaryKeyWithBLOBs(record);
	}

	public int updateByPrimaryKey(JdItemDO record){
		return jdItemDOMapperExt.updateByPrimaryKey(record);
	}

	public JdItemDO searchJdItem(JdItemDO jdItemDO){
		return jdItemDOMapperExt.searchJdItem(jdItemDO);
	}

	public List<JdItemDO> searchJdItemList(JdItemDO jdItemDO){
		return jdItemDOMapperExt.searchJdItemList(jdItemDO);
	}

	public Long searchJdItemCount(JdItemDO jdItemDO){
		return jdItemDOMapperExt.searchJdItemCount(jdItemDO);
	}

	/**
	 *
	 * 状态修改成request，等待下发
	 * @param jdItemDOS
	 */
	public void saveItems4Task(List<JdItemDO> jdItemDOS){
		for(JdItemDO Item : jdItemDOS){

			Item.setSendStatus(SendStatus.REQUEST);
			Item.setItemModifyTime(new Date());

			JdItemDO so = new JdItemDO();
			so.setChannelNo(Item.getChannelNo());
			so.setShopCode(Item.getShopCode());
			so.setChannelItemCode(Item.getChannelItemCode());
			JdItemDO result = jdItemDOMapperExt.searchJdItem(so);

			if(result == null){
				jdItemDOMapperExt.insertSelective(Item);
			}else {
				result.setModifier("-1");
				result.setGmtModify(new Date());
				result.setItemJson(Item.getItemJson());
				jdItemDOMapperExt.updateByPrimaryKey(result);
			}
		}
	}


	public void sendJdItem2globalshop4Task(JdItemDO jdItemDO, JdShopOauthDO shopOauth){
		GlobalShopItemVo globalShopItemVo = null;
		try {
			globalShopItemVo = JdShopFactory.getChannel(shopOauth).convertItemJd2Global(jdItemDO.getItemJson());
		} catch (Exception e) {
            logger.error("sendJdItem2globalshop4Task error: ",e);
		}

		if(globalShopItemVo == null){
			logger.error("sendJdItem2globalshop4Task error: ");
		}

		Map<String,String> pram = new HashMap<>();
		pram.put("channelNo",shopOauth.getChannelNo());
		pram.put("companyNo",shopOauth.getCompanyNo());
        pram.put("shopCode",shopOauth.getShopCode());
		pram.put("globalShopItemVo",JSON.toJSONString(globalShopItemVo));

		JSONObject jsonObject = null;
		try {
			jsonObject = HttpClientUtil.post(GlobalshopStatic.globalshop_dev_url+"/channel/",pram);
		} catch (Exception e) {
			logger.error("sendJdItem2globalshop4Task error: ",e);
		}

		JsonResult<String> result = JSON.parseObject(jsonObject.toString(),JsonResult.class);

		if(result.isSuccess()){
			jdItemDO.setSendStatus(SendStatus.SUCCESS);
		}else {
			jdItemDO.setSendStatus(SendStatus.FAILURE);
		}
		jdItemDOMapperExt.updateByPrimaryKey(jdItemDO);
	}



}