package com.wangqin.globalshop.usercenter.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.wangqin.globalshop.usercenter.service.IOrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wangqin.globalshop.common.base.BaseController;
import com.wangqin.globalshop.biz1.app.dal.dataObject.AuthOrganizationDO;

/**
 * @description：部门管理
 */
@Controller
@RequestMapping("/organization")
public class OrganizationController extends BaseController {

    @Autowired
    private IOrganizationService organizationService;

    /**
     * 部门管理主页
     *
     * @return
     */
    @GetMapping(value = "/manager")
    public String manager() {
        return "admin/organization";
    }

    /**
     * 部门资源树
     *
     * @return
     */
    @PostMapping(value = "/tree")
    @ResponseBody
    public Object tree() {
            return organizationService.selectTree();
    }

    /**
     * 部门列表
     *
     * @return
     */
    @RequestMapping("/treeGrid")
    @ResponseBody
    public Object treeGrid() {
        return organizationService.selectTreeGrid();
    }

    /**
     * 添加部门页
     *
     * @return
     */
    @RequestMapping("/addPage")
    public String addPage() {
        return "admin/organizationAdd";
    }

    /**
     * 添加部门
     *
     * @param organization
     * @return
     */
    @RequestMapping("/add")
    @ResponseBody
    public Object add(AuthOrganizationDO organization) {
//        organization.setCreateTime(new Date());
        organizationService.insert(organization);
        return renderSuccess("添加成功！");
    }

    /**
     * 编辑部门页
     *
     * @param request
     * @param id
     * @return
     */
    @GetMapping("/editPage")

    public String editPage(HttpServletRequest request, Long id) {
        AuthOrganizationDO organization = organizationService.selectById(id);
        request.setAttribute("organization", organization);
        return "admin/organizationEdit";
    }

    /**
     * 编辑部门
     *
     * @param organization
     * @return
     */
    @RequestMapping("/edit")
    @ResponseBody
    public Object edit(AuthOrganizationDO organization) {
        organizationService.updateSelectiveById(organization);
        return renderSuccess("编辑成功！");
    }

    /**
     * 删除部门
     *
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Object delete(Long id) {
        organizationService.deleteById(id);
        return renderSuccess("删除成功！");
    }
}
