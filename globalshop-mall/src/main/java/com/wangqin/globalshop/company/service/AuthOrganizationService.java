package com.wangqin.globalshop.company.service;

import com.wangqin.globalshop.biz1.app.dal.dataObject.AuthOrganizationDO;

/**
 * 组织机构/部门对应的 service
 *
 * @author angus
 * @date 2018/8/6
 */
public interface AuthOrganizationService {
    /**
     * 新增组织机构/部门
     *
     * @param authOrganizationDO
     */
    void addAuthOrganization(AuthOrganizationDO authOrganizationDO);
}
