package com.wangqin.globalshop.biz1.app.dal.mapperExt;


import com.wangqin.globalshop.biz1.app.dal.dataObject.ShippingOrderDO;
import com.wangqin.globalshop.biz1.app.dal.mapper.ShippingOrderDOMapper;
import com.wangqin.globalshop.biz1.app.vo.ShippingOrderVO;

import java.util.List;

/**
 * @author biscuits
 * @date 2018/5/24
 */

public interface IShippingOrderMapperExt extends ShippingOrderDOMapper {
    List<ShippingOrderDO> queryShippingOrders(ShippingOrderVO shippingOrderVO);

    Integer queryShippingOrderCount(ShippingOrderVO shippingOrderVO);

    List<ShippingOrderDO> selectBatchIds(List<Long> shippingOrderIdList);

    List<ShippingOrderDO> queryByShippingOrderPackageTime(ShippingOrderVO shippingOrderQueryVO);

    ShippingOrderDO selectByShippingNo(String shippingNO);

    int selectCount(String idCard, String logisticCompany);

    ShippingOrderDO selectByLogisticNo(ShippingOrderDO order);

    void updateStatusByShippingNo(String logisticNo);
}