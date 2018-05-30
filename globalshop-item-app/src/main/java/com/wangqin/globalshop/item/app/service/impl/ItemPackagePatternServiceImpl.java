package com.wangqin.globalshop.item.app.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.xmlbeans.impl.xb.xsdschema.impl.PublicImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wangqin.globalshop.biz1.app.dal.dataObject.ItemBrandDO;
import com.wangqin.globalshop.biz1.app.dal.dataObject.ShippingPackingPatternDO;
import com.wangqin.globalshop.biz1.app.dal.mapperExt.ItemBrandDOMapperExt;
import com.wangqin.globalshop.biz1.app.dal.mapperExt.ShippingPackingPatternDOMapperExt;
import com.wangqin.globalshop.biz1.app.vo.ItemBrandQueryVO;
import com.wangqin.globalshop.biz1.app.vo.JsonPageResult;
import com.wangqin.globalshop.biz1.app.vo.ShippingPackingPatternQueryVO;
import com.wangqin.globalshop.item.app.service.IItemPackagePatternService;

@Service
public class ItemPackagePatternServiceImpl implements IItemPackagePatternService {

    @Autowired
    private ShippingPackingPatternDOMapperExt packageLevelMapper;
    @Autowired
    ItemBrandDOMapperExt                      itemBrandDOMapperExt;

    public Integer queryPackageLevelsCount(ShippingPackingPatternQueryVO packageLevelQueryVO) {
        return packageLevelMapper.queryPackageLevelsCount(packageLevelQueryVO);
    }

    @Override
    public JsonPageResult<List<ShippingPackingPatternDO>> queryPackageLevelList(ShippingPackingPatternQueryVO packageLevelQueryVO) {
        JsonPageResult<List<ShippingPackingPatternDO>> packageLevelResult = new JsonPageResult<>();

        Integer totalCount = packageLevelMapper.queryPackageLevelsCount(packageLevelQueryVO);

        if ((null != totalCount) && (0L != totalCount)) {
            packageLevelResult.buildPage(totalCount, packageLevelQueryVO);

            List<ShippingPackingPatternDO> packageLevels = packageLevelMapper.queryPackageLevels(packageLevelQueryVO);
            packageLevelResult.setData(packageLevels);
        } else {
            List<ShippingPackingPatternDO> packageLevels = new ArrayList<>();
            packageLevelResult.setData(packageLevels);
        }
        return packageLevelResult;
    }

    @Override
    public void addBrand(ItemBrandDO brand) {
        // String userCreate = ShiroUtil.getShiroUser().getLoginName();
        String userCreate = "admin";
        brand.setCreator(userCreate);
        brand.setGmtCreate(new Date());
        brand.setGmtModify(new Date());
        brand.setModifier(userCreate);
        itemBrandDOMapperExt.insert(brand);
    }

    @Override
    public void updateBrand(ItemBrandDO brand) {
        // String userModify = ShiroUtil.getShiroUser().getLoginName();
        String userModify = "admin";
        brand.setModifier(userModify);
        brand.setGmtModify(new Date());
        itemBrandDOMapperExt.updateByPrimaryKeySelective(brand);
    }

    @Override
    public JsonPageResult<List<ItemBrandDO>> queryAllBrand() {
        JsonPageResult<List<ItemBrandDO>> brandResult = new JsonPageResult<>();
        List<ItemBrandDO> brandList = itemBrandDOMapperExt.queryAllBrand();
        brandResult.setData(brandList);
        return brandResult;
    }

    @Override
    public JsonPageResult<List<ItemBrandDO>> queryBrands(ItemBrandQueryVO brandQueryVO) {
        JsonPageResult<List<ItemBrandDO>> brandResult = new JsonPageResult<>();
        // 查询总条数
        Integer totalCount = itemBrandDOMapperExt.queryItemBrandCount(brandQueryVO);
        // 2、查询分页记录
        if (totalCount != null && totalCount != 0L) {
            brandResult.buildPage(totalCount.intValue(), brandQueryVO);
            List<ItemBrandDO> queryBrands = itemBrandDOMapperExt.queryAllBrand();
            brandResult.setData(queryBrands);
        } else {
            List<ItemBrandDO> queryBrands = new ArrayList<>();
            brandResult.setData(queryBrands);
        }
        return brandResult;
    }

  

    @Override
    public ItemBrandDO selectByPrimaryKey(Long id) {
        return itemBrandDOMapperExt.selectByPrimaryKey(id);
    }

    @Override
    public void deleteByPrimaryKey(Long id) {
        itemBrandDOMapperExt.deleteByPrimaryKey(id);
    }

    @Override
    public void add(ItemBrandDO itemBrand) {

    } 
    
    
    @Override
    public List<ShippingPackingPatternDO> queryPatternsByScaleNo(String no) {
    	return packageLevelMapper.queryPatternsByScaleNo(no);
    }
    
   

}
