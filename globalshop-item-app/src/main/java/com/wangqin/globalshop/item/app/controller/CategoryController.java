package com.wangqin.globalshop.item.app.controller;


import com.wangqin.globalshop.biz1.api.dto.response.BaseResp;
import com.wangqin.globalshop.biz1.app.aop.annotation.Authenticated;
import com.wangqin.globalshop.biz1.app.dal.dataObject.ItemCategoryDO;
import com.wangqin.globalshop.biz1.app.bean.dto.ItemCategoryDTO;
import com.wangqin.globalshop.biz1.app.bean.dataVo.JsonResult;
import com.wangqin.globalshop.common.exception.ErpCommonException;
import com.wangqin.globalshop.common.utils.AppUtil;
import com.wangqin.globalshop.common.utils.LogWorker;
import com.wangqin.globalshop.common.utils.RandomUtils;
import com.wangqin.globalshop.common.utils.StringUtil;
import com.wangqin.globalshop.common.utils.StringUtils;
import com.wangqin.globalshop.common.utils.czh.Util;
import com.wangqin.globalshop.item.api.brand.ItemBrandFeignService;
import com.wangqin.globalshop.item.api.category.ItemCategoryFeignService;
import com.wangqin.globalshop.item.app.service.IItemCategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import javax.validation.Valid;


@Controller
@RequestMapping("/category")
@Authenticated
public class CategoryController  {
	
	protected static Logger log = LoggerFactory.getLogger("System");
	//根类目的pcode
	private static final String P_CODE_OF_ROOT_CATEGORY = "0000000";
	
//	@Autowired
//	private IItemCategoryService categoryService;
	
	@Autowired
	private ItemCategoryFeignService categoryService;//feign声明式服务的高级版

	/**
	 * 添加类目（fin)，提供name和pcode
	 *
	 * @param
	 * @return
	 */
	@RequestMapping("/add")
	@ResponseBody
	public Object add(@Valid ItemCategoryDO category, BindingResult bindResult) {
		LogWorker.logStart(log, "配置", "category:{}", category);
		if(bindResult.hasErrors()) {
        	StringBuffer sb = new StringBuffer();
        	for(ObjectError error : bindResult.getAllErrors()) {
        		sb.append(error.getDefaultMessage()).append(",");
        	}
        	return BaseResp.createFailure(sb.toString());
        }
        BaseResp resp = BaseResp.createSuccess("");
		JsonResult<ItemCategoryDO> result = new JsonResult<>();
		if(StringUtils.isBlank(AppUtil.getLoginUserCompanyNo()) || StringUtil.isBlank(AppUtil.getLoginUserId())) {
			return result.buildIsSuccess(false).buildMsg("请先登录");
		}
		if (category.getpCode() == null) {//添加一级类目
			category.setpCode(P_CODE_OF_ROOT_CATEGORY);
			category.setRootCode(P_CODE_OF_ROOT_CATEGORY);
			category.setLevel(1);		
		} else {
			ItemCategoryDO categoryP = categoryService.queryByCategoryCode(category.getpCode());
			if (categoryP == null) {
				return result.buildIsSuccess(false).buildMsg("未找到父类目!");
			} else {
				category.setLevel(categoryP.getLevel()+1);
				if(category.getLevel() > 3){
					return result.buildIsSuccess(false).buildMsg("不支持新增4级及以上的类目");
				}	
				if(!Util.isEmpty(categoryP.getAllPath())){
					category.setAllPath(categoryP.getAllPath()+"/"+category.getName());
				}else{
					category.setAllPath(categoryP.getName()+"/"+category.getName());
				}
				if(category.getLevel() == 2){
					category.setRootCode(categoryP.getCategoryCode());
				}else{
					category.setRootCode(categoryP.getRootCode());
				}
		   }
		}
		//判断同一个级别下类目的名字可以和已有的类目是否相同，@Author:ZhangZiYang
		List<ItemCategoryDO> itemCategoryList = categoryService.queryItemCategoryByPcode(category.getpCode());
		for(int i = 0; i < itemCategoryList.size(); i ++) {
			ItemCategoryDO itemcategory = itemCategoryList.get(i);
			if(itemcategory.getName().equals(category.getName())) {
				return result.buildIsSuccess(false).buildMsg("同一个级别下类目的名字不可以和已有的类目相同");
			}
		}
		category.setCategoryCode(RandomUtils.getTimeRandom());
		category.setCreator(AppUtil.getLoginUserId());
		category.setModifier(AppUtil.getLoginUserId());
		category.setStatus(1);//设置为有效状态
		categoryService.insertCategorySelective(category);
		LogWorker.logEnd(log, "配置", "category:{}", category);
		return result.buildIsSuccess(true);
	}

	/**
	 * 修改类目(fin)，需要传入pCode
	 * 
	 * @param category
	 * @return
	 */
	@RequestMapping("/update")
	@ResponseBody
	@Transactional(rollbackFor = ErpCommonException.class)
	public Object update(@Valid ItemCategoryDO category, BindingResult bindResult) {
		LogWorker.logStart(log, "配置", "category:{}", category);
		if(bindResult.hasErrors()) {
        	StringBuffer sb = new StringBuffer();
        	for(ObjectError error : bindResult.getAllErrors()) {
        		sb.append(error.getDefaultMessage()).append(",");
        	}
        	return BaseResp.createFailure(sb.toString());
        }
        BaseResp resp = BaseResp.createSuccess("");
		JsonResult<ItemCategoryDO> result = new JsonResult<>();
		if(StringUtils.isBlank(AppUtil.getLoginUserCompanyNo()) || StringUtil.isBlank(AppUtil.getLoginUserId())) {
			return result.buildIsSuccess(false).buildMsg("请先登录");
		}
		Long id = category.getId();
		if (id == null) {
			return result.buildIsSuccess(false).buildMsg("类目id不能为空");
		}
		if(category.getpCode()!=null){
			ItemCategoryDO categoryP = categoryService.queryByCategoryCode(category.getpCode());
			if (categoryP == null) {
				return result.buildIsSuccess(false).buildMsg("未找到父类目!");
			} else {
				category.setLevel(categoryP.getLevel() + 1);
				if(3 < category.getLevel()) {
					return result.buildIsSuccess(false).buildMsg("不支持修改为4级类目");
				}
				if(!Util.isEmpty(categoryP.getAllPath())){
					category.setAllPath(categoryP.getAllPath()+"/"+category.getName());
				}else{
					category.setAllPath(categoryP.getName()+"/"+category.getName());
				}
				if(category.getLevel()==2){
					category.setRootCode(categoryP.getCategoryCode());
				}else{
					category.setRootCode(categoryP.getRootCode());
				}
			}
		}

		//判断同一个级别下类目的名字可以和已有的类目是否相同，@Author:ZhangZiYang
		List<ItemCategoryDO> itemCategoryList = categoryService.queryItemCategoryByPcode(category.getpCode());
		for(int i = 0; i < itemCategoryList.size(); i ++) {
			ItemCategoryDO itemcategory = itemCategoryList.get(i);
			if(itemcategory.getName().equals(category.getName())) {
				return result.buildIsSuccess(false).buildMsg("同一个级别下类目的名字不可以和已有的类目相同");
			}
		}
				
		category.setModifier(AppUtil.getLoginUserId());
		categoryService.update(category);
		LogWorker.logEnd(log, "配置", "categoryDo:{}", category);
		return result.buildIsSuccess(true);
	}

	/**
	 * 删除类目，软删除(fin)
	 * 
	 * @param category
	 * @return
	 */
	@RequestMapping("/delete")
	@ResponseBody
	@Transactional(rollbackFor = ErpCommonException.class)
	public Object delete(@Valid ItemCategoryDO category, BindingResult bindResult) {
		LogWorker.logStart(log, "配置", "category:{}", category);
		if(bindResult.hasErrors()) {
        	StringBuffer sb = new StringBuffer();
        	for(ObjectError error : bindResult.getAllErrors()) {
        		sb.append(error.getDefaultMessage()).append(",");
        	}
        	return BaseResp.createFailure(sb.toString());
        }
        BaseResp resp = BaseResp.createSuccess("");
		JsonResult<ItemCategoryDO> result = new JsonResult<>();
		if(StringUtils.isBlank(AppUtil.getLoginUserCompanyNo()) || StringUtil.isBlank(AppUtil.getLoginUserId())) {
			return result.buildIsSuccess(false).buildMsg("请先登录");
		}
		Long id = category.getId();
		
		
		int categoryCodeCount = categoryService.countRelativeItem(categoryService.selectByPrimaryKey(category.getId()).getCategoryCode());
		if(categoryCodeCount > 0) {
			return result.buildIsSuccess(false).buildMsg("删除失败，该类目已关联商品");
		}
		int categoryCountByPcode = categoryService.queryChildCategoryCountByCategoryCode(categoryService.selectByPrimaryKey(category.getId()).getCategoryCode());
		if(categoryCountByPcode > 0) {
			return result.buildIsSuccess(false).buildMsg("删除失败，已关联子类目");
		}
			
		if (id == null) {
			return result.buildIsSuccess(false).buildMsg("类目id不能为空！");
		}
		
		categoryService.deleteItemCategoryById(id);
		LogWorker.logEnd(log, "配置", "category:{}", category);
		return result.buildIsSuccess(true);
	}
	
	

	/**
	 * 查询类目(fin)
	 * 
	 * @param category
	 * @return
	 */
	@RequestMapping("/query")
	@ResponseBody
	public Object query(@Valid ItemCategoryDO category, BindingResult bindResult) {
		LogWorker.logStart(log, "配置", "category:{}", category);
		if(bindResult.hasErrors()) {
        	StringBuffer sb = new StringBuffer();
        	for(ObjectError error : bindResult.getAllErrors()) {
        		sb.append(error.getDefaultMessage()).append(",");
        	}
        	return BaseResp.createFailure(sb.toString());
        }
        BaseResp resp = BaseResp.createSuccess("");
		JsonResult<ItemCategoryDO> result = new JsonResult<>();
		if(StringUtils.isBlank(AppUtil.getLoginUserCompanyNo()) || StringUtil.isBlank(AppUtil.getLoginUserId())) {
			return result.buildIsSuccess(false).buildMsg("请先登录");
		}
		Long id = category.getId();
		if (id == null) {
			return result.buildIsSuccess(false).buildMsg("类目id不能为空");
		}
		ItemCategoryDO categoryP = categoryService.findCategory(id);
		if (categoryP == null) {
			return result.buildIsSuccess(false).buildMsg("未找到类目");
		}
		LogWorker.logEnd(log, "配置", "category:{}", category);
		return result.buildData(categoryP).buildIsSuccess(true);
	}
	
	
	/**
	 * 查询所有类目(fin)
	 *
	 * @return
	 */
	@RequestMapping("/queryList")
	@ResponseBody
	public Object queryList(@Valid ItemCategoryDO category, BindingResult bindResult) {
		LogWorker.logStart(log, "配置", "category:{}", category);
		if(bindResult.hasErrors()) {
        	StringBuffer sb = new StringBuffer();
        	for(ObjectError error : bindResult.getAllErrors()) {
        		sb.append(error.getDefaultMessage()).append(",");
        	}
        	return BaseResp.createFailure(sb.toString());
        }
        BaseResp resp = BaseResp.createSuccess("");
		JsonResult<List<ItemCategoryDO>> result = new JsonResult<>();
		if(StringUtils.isBlank(AppUtil.getLoginUserCompanyNo()) || StringUtil.isBlank(AppUtil.getLoginUserId())) {
			return result.buildIsSuccess(false).buildMsg("请先登录");
		}
		result.setData(categoryService.selectAll());
		result.setSuccess(true);
		LogWorker.logEnd(log, "配置", "category:{}", category);
		return result;
	}
	
	/**
	 * 类目管理列表（fin)s
	 *
	 * @return
	 */
	@RequestMapping("/tree")
	@ResponseBody
	public Object tree(@Valid ItemCategoryDO category, BindingResult bindResult) {
		LogWorker.logStart(log, "配置", "category:{}", category);
		if(bindResult.hasErrors()) {
        	StringBuffer sb = new StringBuffer();
        	for(ObjectError error : bindResult.getAllErrors()) {
        		sb.append(error.getDefaultMessage()).append(",");
        	}
        	return BaseResp.createFailure(sb.toString());
        }
        BaseResp resp = BaseResp.createSuccess("");
		JsonResult<List<ItemCategoryDTO>> result = new JsonResult<>();
		if(StringUtils.isBlank(AppUtil.getLoginUserCompanyNo()) || StringUtil.isBlank(AppUtil.getLoginUserId())) {
			return result.buildIsSuccess(false).buildMsg("请先登录");
		}
		result.setData(categoryService.tree());
		result.setSuccess(true);
		LogWorker.logEnd(log, "配置", "category:{}", category);
		return result;
	}
	

}
