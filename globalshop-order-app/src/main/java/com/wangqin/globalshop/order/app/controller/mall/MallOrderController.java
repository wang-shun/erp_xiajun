package com.wangqin.globalshop.order.app.controller.mall;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.wangqin.globalshop.biz1.app.dal.dataObject.MallOrderDO;
import com.wangqin.globalshop.biz1.app.dal.dataObject.MallSubOrderDO;
import com.wangqin.globalshop.biz1.app.dal.dataVo.MallOrderVO;
import com.wangqin.globalshop.biz1.app.vo.JsonResult;
import com.wangqin.globalshop.common.enums.OrderStatus;
import com.wangqin.globalshop.common.exception.ErpCommonException;
import com.wangqin.globalshop.common.exception.InventoryException;
import com.wangqin.globalshop.common.utils.DateUtil;
import com.wangqin.globalshop.common.utils.HaiJsonUtils;
import com.wangqin.globalshop.common.utils.ParseObj2Obj;
import com.wangqin.globalshop.common.utils.ShiroUtil;
import com.wangqin.globalshop.common.utils.excel.ExcelHelper;
import com.wangqin.globalshop.order.app.service.inventory.OrderIInventoryService;
import com.wangqin.globalshop.order.app.service.mall.IMallOrderService;
import com.wangqin.globalshop.order.app.service.mall.IMallSubOrderService;
import com.wangqin.globalshop.order.app.service.util_service.OrderISequenceUtilService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zhulu
 */
@Controller
@RequestMapping("/outerOrder")
public class MallOrderController {

    @Autowired
    private IMallOrderService mallOrderService;

    @Autowired
    private OrderISequenceUtilService sequenceUtilService;

    @Autowired
    private IMallSubOrderService mallSubOrderService;
    @Autowired
    private OrderIInventoryService inventoryService;
    @RequestMapping(value = "/uuu",method = RequestMethod.POST)
    public void add1() {
        MallOrderDO mallOrderDO = mallOrderService.selectById(1L);
        List<MallSubOrderDO> list = mallSubOrderService.selectByOrderNo(mallOrderDO.getOrderNo());
        MallOrderVO mallOrderVO = new MallOrderVO();
        ParseObj2Obj.parseObj2Obj(mallOrderDO,mallOrderVO);
        mallOrderVO.setOuterOrderDetails(list);
        mallOrderDO.setCompanyNo("海狐");
        System.out.println(JSON.toJSON(mallOrderVO));
    }
    /**
     * 增加订单
     */
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    public Object add(String outerOrder) {
        MallOrderVO mallOrderVO = JSON.parseObject(outerOrder, MallOrderVO.class);
        JsonResult<String> result = new JsonResult<>();
        if (mallOrderVO.getId() == null) {
            mallOrderVO.setGmtCreate(new Date());
            mallOrderVO.setGmtModify(new Date());
//			ShiroUser shiroUser = this.getShiroUser();
//			outerOrder.setUserCreate(shiroUser.getLoginName());
            Long ordSequence = sequenceUtilService.gainORDSequence();
            mallOrderVO.setOrderNo("P" + String.format("%0" + 2 + "d", mallOrderVO.getChannelNo()) + String.format("%0" + 4 + "d", mallOrderVO.getChannelName()) + "D" + DateUtil.formatDate(new Date(), DateUtil.DATE_PARTEN_YYMMDDHHMMSS)+ordSequence );//系统自动生成
//
//			//订单详情
            String outerOrderDetailList = mallOrderVO.getOrderDetailList();
            if (StringUtils.isNotBlank(outerOrderDetailList)) {
                String s = outerOrderDetailList.replace("&quot;", "\"");
                List<MallSubOrderDO> outerOrderDetails = HaiJsonUtils.toBean(s, new TypeReference<List<MallSubOrderDO>>() {
                });
                mallOrderVO.setOuterOrderDetails(outerOrderDetails);
                if (CollectionUtils.isEmpty(outerOrderDetails)) {
                    return JsonResult.buildFailed("没有商品信息");
                }
            }
            /**注入部门no*/
            mallOrderVO.setCompanyNo("MallOrderController86");
            //创建外部订单
            mallOrderService.addOuterOrder(mallOrderVO);
            if (mallOrderVO.getStatus() == null || mallOrderVO.getStatus() == 0) {
                //生成子订单并配货
                mallOrderService.review(mallOrderVO.getOrderNo());
            }

            result.buildIsSuccess(true);
        } else {
            result.buildData("错误数据").buildIsSuccess(false);
        }

        return result;
    }


    /**
     * 更新订单
     */
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    public Object update(String outerOrder) {
        MallOrderVO mallOrderVO = JSON.parseObject(outerOrder.trim(), MallOrderVO.class);
        JsonResult<String> result = new JsonResult<>();
        if (mallOrderVO.getId() != null) {
            //只有状态为新建的订单才能修改
            MallOrderDO selOuterOrder = mallOrderService.selectById(mallOrderVO.getId());
            if (selOuterOrder == null || selOuterOrder.getStatus() != OrderStatus.INIT.getCode()) {
                return JsonResult.buildFailed("订单不存在或者状态错误");
            }
            //查询是否有发货的订单，有的话订单不能修改
            MallSubOrderDO erpOrderQuery = new MallSubOrderDO();
            erpOrderQuery.setOrderNo(mallOrderVO.getOrderNo());
            erpOrderQuery.setStatus(OrderStatus.SENT.getCode());
            int sendOrder = mallSubOrderService.selectCountWithStateAndOrderNo(erpOrderQuery);
            if (sendOrder > 0) {
                return JsonResult.buildFailed("有子订单发货不能修改");
            }

            mallOrderVO.setGmtModify(new Date());
//			ShiroUser shiroUser = this.getShiroUser();
//			outerOrder.setUserModify(shiroUser.getLoginName());
            //订单详情
            String outerOrderDetailList = mallOrderVO.getOuterOrderDetailList();
            if (StringUtils.isNotBlank(outerOrderDetailList)) {
                String s = outerOrderDetailList.replace("&quot;", "\"");
                List<MallSubOrderDO> outerOrderDetails = HaiJsonUtils.toBean(s, new TypeReference<List<MallSubOrderDO>>() {
                });
                mallOrderVO.setOuterOrderDetails(outerOrderDetails);
            }
            mallOrderService.updateById(mallOrderVO);
//            EntityWrapper<ErpOrder> entityWrapper = new EntityWrapper<>();
//            entityWrapper.where("outer_order_id = {0} ", outerOrder.getId());
            List<MallSubOrderDO> erpOrders = mallSubOrderService.selectByOrderNo(mallOrderVO.getOrderNo());
            if (CollectionUtils.isNotEmpty(erpOrders)) {
                for (MallSubOrderDO erpOrder : erpOrders) {
                    try {
                        //1,释放子订单库存
                        inventoryService.releaseInventory(erpOrder);
                        //2,删除子订单
                        mallSubOrderService.delete(erpOrder);
                    } catch (InventoryException e) {
                        e.printStackTrace();
                    }
                }
                //3,重新生成子订单并分配库存。
                mallOrderService.review(mallOrderVO.getOrderNo());
            }
            result.buildIsSuccess(true);
        } else {
            result.buildData("错误数据").buildIsSuccess(false);
        }

        return result;
    }

    /**
     * 查询单个订单
     */
    @RequestMapping("/query")
    @ResponseBody
    public Object query(Long id) {
        JsonResult<MallOrderDO> result = new JsonResult<>();
        MallOrderDO outerOrder = mallOrderService.selectById(id);
        return result.buildData(outerOrder).buildIsSuccess(true);
    }

    /**
     * 批量调价查询订单
     *
     * @return
     */
    @RequestMapping(value = "/queryOuterOrderList",method = RequestMethod.POST)
    @ResponseBody
    public Object queryOuterOrderList(String outerOrderQueryVO) {
        MallOrderVO mallOrderVO = JSON.parseObject(outerOrderQueryVO.trim(), MallOrderVO.class);
        if (mallOrderVO.getStatus() != null && mallOrderVO.getStatus() == 10) {//10代表查询全部订单
            mallOrderVO.setStatus(null);
        }
        if (mallOrderVO.getEndGmtCreate() != null) {
            String endGmtCreateStr = DateUtil.ymdFormat(mallOrderVO.getEndGmtCreate());
            Date endGmtCreate = DateUtil.parseDate(endGmtCreateStr + " 23:59:59");
            mallOrderVO.setEndGmtCreate(endGmtCreate);
        }
        //如果是代理
//        ShiroUser shiroUser = this.getShiroUser();
//        Set<String> roles = shiroUser.getRoles();
//        if (roles.contains("irhdaili")) {
//            String[] logingNameArr = shiroUser.getLoginName().split("_");
//            if (logingNameArr.length < 2 || StringUtils.isBlank(logingNameArr[1])) {
//                throw new ErpCommonException("用户权限异常");
//            }
//            outerOrderQueryVO.setSalesId(Integer.parseInt(logingNameArr[1]));
//            Seller seller = sellerService.selectById(outerOrderQueryVO.getSalesId());
//            if (seller.getOpenId() != null) {
//                outerOrderQueryVO.setOpenId(seller.getOpenId());
//            }
//        }
        JsonResult<List<MallOrderDO>> result =new JsonResult<>();
        List<MallOrderDO> list = mallOrderService.queryOuterOrderList(mallOrderVO);
        result.setData(list);
//        if (roles.contains("irhdaili")) {
//            result.setAgentRoler(true);
//        }
        return result.buildIsSuccess(true);
    }

    /**
     * 删除单个订单
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Object delete(Long id) {
        JsonResult<String> result = new JsonResult<>();
        try {
            MallOrderDO outerOrder = mallOrderService.selectById(id);
            if (outerOrder == null || outerOrder.getStatus() != OrderStatus.CLOSE.getCode()) {
                return JsonResult.buildFailed("先关闭才能删除订单");
            }
            mallOrderService.delete(outerOrder);
        } catch (Exception e) {
            return result.buildIsSuccess(false).buildMsg(e.getMessage());
        }
        return result.buildIsSuccess(true);
    }


    /**
     * 批量关闭订单
     */
    @RequestMapping("/close")
    @ResponseBody
    public Object close(String orderIds) {
        List<String> errorMsg = Lists.newArrayList();
        int i = 0;
        if (StringUtils.isNotBlank(orderIds)) {
            String s = orderIds.replace("&quot;", "\"");
            List<Long> orderIdList = HaiJsonUtils.toBean(s, new TypeReference<List<Long>>() {
            });
            for (Long id : orderIdList) {
                i++;
                MallOrderDO outerOrder = mallOrderService.selectById(id);
                if (outerOrder == null || outerOrder.getStatus() != OrderStatus.INIT.getCode()) {
                    errorMsg.add("第" + i + "条不存在或者状态错误,");
                }
                //查询是否有发货的订单，有的话订单不能修改
                MallSubOrderDO erpOrderQuery = new MallSubOrderDO();
                erpOrderQuery.setOrderNo(outerOrder.getOrderNo());
                erpOrderQuery.setStatus( OrderStatus.SENT.getCode());
                int sendOrder = mallSubOrderService.selectCountWithStateAndOrderNo(erpOrderQuery);
                if (sendOrder > 0) {
                    return errorMsg.add("第" + i + "条有子订单发货不能关闭");
                }
                //1、释放子订单库存
//                EntityWrapper<ErpOrder> entityWrapper = new EntityWrapper<>();
//                entityWrapper.where("outer_order_id = {0} ", outerOrder.getId());
                List<MallSubOrderDO> erpOrders = mallSubOrderService.selectByOrderNo(outerOrder.getOrderNo());
                if (CollectionUtils.isNotEmpty(erpOrders)) {
                    for (MallSubOrderDO erpOrder : erpOrders) {
                        try {
                            //1,释放子订单库存
                            inventoryService.releaseInventory(erpOrder);
                            //2,更改子订单状态
                            erpOrder.setStatus( OrderStatus.CLOSE.getCode());
                            erpOrder.setGmtModify(new Date());
                            mallSubOrderService.update(erpOrder);
                        } catch (InventoryException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //2、更改主子订单状态
                outerOrder.setStatus( OrderStatus.CLOSE.getCode());
                outerOrder.setGmtModify(new Date());
                mallOrderService.updateById(outerOrder);
            }
            StringBuilder rmsg = new StringBuilder();
            if (CollectionUtils.isNotEmpty(errorMsg)) {
                for (String a : errorMsg) {
                    rmsg.append(a);
                }
                return JsonResult.buildFailed(rmsg.toString());
            } else {
                return JsonResult.buildSuccess(null);
            }

        } else {
            return JsonResult.buildFailed("没有订单ID");
        }
    }

    /**
     * 查询子订单备货信息
     */
    @RequestMapping("/erpStockup")
    @ResponseBody
    public Object erpStockup(String orderNo) {
        JsonResult<List<MallSubOrderDO>> result = new JsonResult<>();
        try {
            if (orderNo != null) {
                //查询未关闭子订单备货情况
                List<MallSubOrderDO> erpOrders = mallSubOrderService.selectUnClosedByOrderNo(orderNo);
                result.setData(erpOrders);
            } else {
                return JsonResult.buildFailed("没有传ID");
            }
        } catch (Exception e) {
            return result.buildIsSuccess(false).buildMsg(e.getMessage());
        }
        return result.buildIsSuccess(true);
    }

    //手动确认主订单
    @RequestMapping("/reviewOuterOrder")
    @ResponseBody
    public void reviewOuterOrder(String orderNo) {
        mallOrderService.review(orderNo);
    }

    //主订单导出
    @RequestMapping(value = "/OuterOrderExportExcel",method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<byte[]> OuterOrderExportExcel(String OuterOrderQueryVO) throws Exception {
        MallOrderVO mallOrderVO = JSON.parseObject(OuterOrderQueryVO.trim(), MallOrderVO.class);
        if (mallOrderVO.getStartGmtCreate() == null || mallOrderVO.getEndGmtCreate() == null) {
            throw new ErpCommonException("必须选择创建时间段");
        }
        String startGmtCreateStr = DateUtil.ymdFormat(mallOrderVO.getStartGmtCreate());
        Date startGmtCreate = DateUtil.parseDate(startGmtCreateStr + " 00:00:00");
        mallOrderVO.setStartGmtCreate(startGmtCreate);
        String endGmtCreateStr = DateUtil.ymdFormat(mallOrderVO.getEndGmtCreate());
        Date endGmtCreate = DateUtil.parseDate(endGmtCreateStr + " 23:59:59");
        mallOrderVO.setEndGmtCreate(endGmtCreate);
        mallOrderVO.setCompanyNo(ShiroUtil.getShiroUser().getCompanyNo());

        List<List<Object>> rowDatas = new ArrayList<>();
        List<MallOrderDO> outerOrderlist = mallOrderService.queryOuterOrderForExcel(mallOrderVO);
        if (outerOrderlist != null) {
            for (MallOrderDO outerOrder : outerOrderlist) {
                List<Object> list = new ArrayList<>();

                list.add(outerOrder.getOrderNo());            //主订单号
//                list.add(outerOrder.getS());        //销售员
                list.add(outerOrder.getTotalAmount());    //订单金额
                list.add(outerOrder.getGmtCreate());        //下单时间
                list.add(OrderStatus.of(outerOrder.getStatus()).getDescription());  //订单状态
                list.add(outerOrder.getIdcardPicReverse());            //收件人
//                list.add(outerOrder.getTelephone());        //手机
//                list.add(outerOrder.getSt());    //省
//                list.add(outerOrder.getReceiverCity());        //市
//                list.add(outerOrder.getReceiverDistrict());    //区
//                list.add(outerOrder.getAddressDetail());    //详细地址

                rowDatas.add(list);
            }
        }
        ExcelHelper excelHelper = new ExcelHelper();
        String[] columnTitles = new String[]{"主订单号", "销售员", "订单金额", "下单时间", "订单状态", "收件人", "手机", "省", "市", "区", "详细地址"};
        Integer[] columnWidth = new Integer[]{30, 15, 15, 20, 15, 15, 15, 15, 15, 15, 45};

        excelHelper.setOuterOrderToSheet("Father Order", columnTitles, rowDatas, columnWidth);
        //excelHelper.writeToFile("/Users/liuyang/Work/test.xls");

        ResponseEntity<byte[]> filebyte = null;
        ByteArrayOutputStream out = excelHelper.writeToByteArrayOutputStream();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String fileName = "主订单(" + DateUtil.formatDate(startGmtCreate, "yyyyMMdd") + ").xlsx";
        headers.setContentDispositionFormData("attachment", new String(fileName.getBytes("utf-8"), "ISO8859-1"));

        filebyte = new ResponseEntity<byte[]>(out.toByteArray(), headers, HttpStatus.OK);
        out.close();
        excelHelper.close();
        return filebyte;
    }

    //微信录单确认
    @RequestMapping("/outerOrderReview")
    @ResponseBody
    public Object outerOrderReview() {
        List<MallOrderDO> outerOrderList = mallOrderService.selectByStatus((byte) -2);
        if (outerOrderList.size() > 0) {
            outerOrderList.forEach((outerOrder) -> {
                mallOrderService.review(outerOrder.getOrderNo());

                outerOrder.setStatus( 0);
                mallOrderService.updateById(outerOrder);
            });
        }
        return JsonResult.buildSuccess(null);
    }
}