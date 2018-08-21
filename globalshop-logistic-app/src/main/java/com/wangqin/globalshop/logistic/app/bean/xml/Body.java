package com.wangqin.globalshop.logistic.app.bean.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.wangqin.globalshop.logistic.app.bean.declare.GoodsDeclareModule;
import com.wangqin.globalshop.logistic.app.bean.order.OrderInfo;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author angus
 * @date 2018/8/21
 */
@Data
@Builder
public class Body {
    @XStreamAlias("orderInfoList")
    private List<OrderInfo> orderInfoList;

    @XStreamAlias("goodsDeclareModuleList")
    private List<GoodsDeclareModule> goodsDeclareModuleList;

}