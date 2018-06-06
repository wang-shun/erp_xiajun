package com.wangqin.globalshop.deal.app.service.impl;

import com.wangqin.globalshop.biz1.app.dal.dataObject.DealerDO;
import com.wangqin.globalshop.biz1.app.dal.mapperExt.DealerDOMapperExt;
import com.wangqin.globalshop.deal.app.service.IDealerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author biscuit
 * @data 2018/06/05
 */
@Service
public class DealerServiceImpl implements IDealerService {
    @Autowired
    private DealerDOMapperExt mapper;
    @Override
    public List<DealerDO> list() {
        return mapper.list();
    }

    @Override
    public void insert(DealerDO seller) {
        seller.setCompanyNo("1231");
        seller.init();
        mapper.insertSelective(seller);
    }
}