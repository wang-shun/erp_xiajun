package com.wangqin.globalshop.purchase.app.controller;

import com.wangqin.globalshop.biz1.app.aop.annotation.Authenticated;
import com.wangqin.globalshop.biz1.app.dal.dataObject.BuyerReceiptDO;
import com.wangqin.globalshop.biz1.app.dal.dataObject.BuyerReceiptDetailDO;
import com.wangqin.globalshop.biz1.app.vo.JsonResult;
import com.wangqin.globalshop.purchase.app.service.IReceiptDetailService;
import com.wangqin.globalshop.purchase.app.service.IReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author biscuit
 * @data 2018/06/12
 */
@Controller
@Authenticated
@ResponseBody
@RequestMapping(value = {
        "/receipt"})
public class ReceiptController {
    @Autowired
    private IReceiptService service;
    @Autowired
    private IReceiptDetailService detailService;
    @RequestMapping("/queryReceipt")
    public Object queryReceipt(BuyerReceiptDO receipt){
        JsonResult<List<BuyerReceiptDO>> result = new JsonResult<>();
        List<BuyerReceiptDO> list = service.list(receipt);
        return result.buildIsSuccess(true).buildData(list);
    }
    @RequestMapping("/queryTaskReceipt")
    public Object queryReceiptDetail(BuyerReceiptDetailDO receipt){
        JsonResult<List<BuyerReceiptDetailDO>> result = new JsonResult<>();
        List<BuyerReceiptDetailDO> list = detailService.list(receipt);
        return result.buildIsSuccess(true).buildData(list);
    }

}
