package com.wangqin.globalshop.company.service;

import com.wangqin.globalshop.biz1.app.dal.dataObject.AuthUserDO;

/**
 * 用户相关的 service
 *
 * @author angus
 * @date 2018/8/6
 */
public interface AuthUserService {

    /**
     * 新增用户
     *
     * @param authUserDO
     */
    void addAuthUser(AuthUserDO authUserDO);

    /**
     * 更新用户
     *
     * @param authUserDO
     */
    void updateAuthUser(AuthUserDO authUserDO);

    /**
     * 通过 login_name 获取 auth_user
     *
     * @param loginName
     * @return
     */
    AuthUserDO getByLoginName(String loginName);
}
