package com.wangqin.globalshop.usercenter.controller;

import java.util.HashMap;
import java.util.Map;

import com.wangqin.globalshop.common.utils.PageInfo;
import com.wangqin.globalshop.usercenter.service.ISysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @description：日志管理
 * @author：zhixuan.wang
 * @date：2015/10/30 18:06
 */
@Controller
@RequestMapping("/sysLog")
public class SysLogController {

    @Autowired
    private ISysLogService sysLogService;

    @GetMapping("/manager")
    public String manager() {
        return "admin/syslog";
    }


    @PostMapping("/dataGrid")
    @ResponseBody
    public PageInfo dataGrid(Integer page, Integer rows) {
        PageInfo pageInfo = new PageInfo(page, rows);
        Map<String, Object> condition = new HashMap<String, Object>();
        pageInfo.setCondition(condition);
        sysLogService.selectDataGrid(pageInfo);
        return pageInfo;
    }
}