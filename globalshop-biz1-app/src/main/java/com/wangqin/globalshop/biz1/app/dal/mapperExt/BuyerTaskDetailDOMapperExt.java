package com.wangqin.globalshop.biz1.app.dal.mapperExt;

import com.wangqin.globalshop.biz1.app.dal.dataObject.BuyerTaskDetailDO;
import com.wangqin.globalshop.biz1.app.dal.mapper.BuyerTaskDetailDOMapper;

import java.util.List;

public interface BuyerTaskDetailDOMapperExt extends BuyerTaskDetailDOMapper {
    void inserBatch(List<BuyerTaskDetailDO> detailList);
}