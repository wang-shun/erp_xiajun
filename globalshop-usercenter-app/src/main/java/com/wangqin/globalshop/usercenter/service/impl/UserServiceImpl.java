package com.wangqin.globalshop.usercenter.service.impl;


import com.wangqin.globalshop.biz1.app.bean.dataVo.AuthUserVO;
import com.wangqin.globalshop.biz1.app.bean.dataVo.UserQueryVO;
import com.wangqin.globalshop.biz1.app.dal.dataObject.*;
import com.wangqin.globalshop.biz1.app.dal.mapper.MallSaleAgentDOMapper;
import com.wangqin.globalshop.biz1.app.dal.mapperExt.*;
import com.wangqin.globalshop.common.exception.ErpCommonException;
import com.wangqin.globalshop.common.utils.*;
import com.wangqin.globalshop.usercenter.service.IUserService;
import com.wangqin.globalshop.usercenter.vo.UserVo;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * AuthUserDO 表数据服务层接口实现类
 */
@Service
public class UserServiceImpl implements IUserService { //extends SuperServiceImpl<AuthUserDOMapperExt, AuthUserDO>

    @Autowired
    private AuthUserDOMapperExt userMapper;

    @Autowired
    private AuthUserRoleDOMapperExt userRoleMapper;

    @Autowired
    private WxUserDOMapperExt wxUserDOMapper;

    @Autowired
    private AuthRoleDOMapperExt authRoleDOMapper;

    @Autowired
    private AuthUserRoleDOMapperExt userRoleDOMapperExt;

    @Autowired
    private BuyerDOMapperExt buyerDOMapperExt;
    @Autowired
    private MallSaleAgentDOMapper mallSaleAgentDOMapper;

    /*
     * 查询用户信息推荐用这个，密码已脱敏。
     */
    @Override
    public AuthUserDO selectByLoginName(String userNo) {
        return userMapper.selectByLoginName(userNo);
    }

    /*
    * 查询包括了密码密文的用户信息，慎用
    */
    @Override
    public AuthUserDO selectSecureByLoginName(String userNo) {
        return userMapper.selectSecureByLoginName(userNo);
    }

    @Override
    @Transactional(rollbackFor = ErpCommonException.class)
    public void insertByVo(UserVo userVo) {
        AuthUserDO user = BeanUtils.copy(userVo, AuthUserDO.class);
        user.init();
        user.setName(userVo.getName());
        user.setSex(userVo.getSex().byteValue());
        user.setAge(userVo.getAge().byteValue());
        user.setUserType(userVo.getUserType().byteValue());
        user.setStatus(userVo.getStatus().byteValue());
        user.setIsDel(false);
        userMapper.insertByNoId(user);

    }

    @Override
    @Transactional(rollbackFor = ErpCommonException.class)
    public void insertByUserVo(UserVo userVo) {
        // TODO Auto-generated method stub
        Long id = userVo.getId();
        String[] roles = userVo.getRoleIds().split(",");
        AuthUserRoleDO userRole = new AuthUserRoleDO();
        for (String string : roles) {
            userRole.init();
            userRole.setUserId(id);
            userRole.setRoleId(Long.valueOf(string));
            userRoleMapper.insertByNoId(userRole);
        }
    }

    @Override
    public UserVo selectVoById(String loginName) {
        //TODO
        userMapper.selectUserVoById(loginName);
        return null;
    }

    @Override
    @Transactional(rollbackFor = ErpCommonException.class)
    public void updateByVo(UserVo userVo) {

//        AuthUserDO authUser = userMapper.selectByPrimaryKey(userVo.getId());
        AuthUserDO authUser = userMapper.selectByLoginName(userVo.getLoginName());

//        authUser.setLoginName(userVo.getLoginName());
        authUser.setName(userVo.getName());
        if (StringUtils.isNotBlank(userVo.getPassword()) && "********".equals(userVo.getPassword())) {
            authUser.setPassword(userVo.getPassword().trim());
        }

//        authUser.setPassword(userVo.getPassword());
        authUser.setSex((byte) userVo.getSex().intValue());
        authUser.setAge((byte) userVo.getAge().intValue());
        authUser.setUserType((byte) userVo.getUserType().intValue());
        authUser.setOrganizationId(userVo.getOrganizationId());
        authUser.setPhone(userVo.getPhone());
        authUser.setStatus((byte) userVo.getStatus().intValue());
        authUser.setIsDel(false);


        userMapper.updateUserInfoByLoginName(authUser);
//        userMapper.updateByPrimaryKey(authUser);


        /*找出原先的角色id*/
        List<Long> oldRoles = userRoleMapper.selectAllRole(authUser.getId());
        /*得到新的角色id*/
        String roleIds = userVo.getRoleIds();
        List<String> newRoleIds;
        if (StringUtil.isBlank(roleIds)) {
            newRoleIds = Collections.EMPTY_LIST;
        } else {
            String[] strs = roleIds.split(",");
            newRoleIds = Arrays.asList(strs);
        }
        List<Long> subList = new ArrayList();

        /*记录角色的变化的map
        * 如果value为true  就是新增
        * 如果value为flase 就是修改
        * */
        Map<Long, Boolean> changeMap = new HashMap(16);
        for (Long roleId : oldRoles) {
            if (newRoleIds.contains(roleId.toString())) {
                subList.add(roleId);
                newRoleIds.remove(roleId);
            } else {
                changeMap.put(roleId, Boolean.FALSE);
            }

        }
        for (String role : newRoleIds) {
            Long id = Long.valueOf(role);
            if (!subList.contains(id)) {
                changeMap.put(id, Boolean.TRUE);
            }
        }

        Long id = userVo.getId();
        /*查询得到该公司的买手角色*/
        AuthRoleDO buyer = authRoleDOMapper.selectByNameAndCompanyNo("买手", AppUtil.getLoginUserCompanyNo());
        if(buyer!=null){
            Long buyerId = buyer.getId();
            /*判断买手是不是在changeMap中*/
            if (changeMap.containsKey(buyerId)) {
                String wxUnionId = authUser.getWxUnionId();
                if (StringUtil.isBlank(wxUnionId)) {
                    throw new ErpCommonException("该用户不是扫码新增的用户,不能够设置买手角色");
                }
                /*如果是true就新增买手*/
                if (changeMap.get(buyerId)) {
                    addAuthRoleByRoleNameAndUserId(buyerId, id);
                    BuyerDO buyerQueryDO = new BuyerDO();
                    buyerQueryDO.setCompanyNo(AppUtil.getLoginUserCompanyNo());
                    buyerQueryDO.setUnionId(wxUnionId);
                    buyerQueryDO.setNickName(authUser.getName());
                    buyerQueryDO.init();
                    buyerDOMapperExt.insertSelective(buyerQueryDO);
                } else {
                     /*如果是false就删除买手*/
                    buyerDOMapperExt.deleteByUnionId(wxUnionId);
                    userRoleMapper.deleteByUserIdAndRoleId(authUser.getId(), buyerId);
                }
                /*处理完买手，在changeMap中删除相关的记录*/
                changeMap.remove(buyerId);
            }
        }

        for (Long roleId : changeMap.keySet()) {
            if (changeMap.get(roleId)) {
                addAuthRoleByRoleNameAndUserId(roleId, id);
            } else {
                userRoleMapper.deleteByUserIdAndRoleId(authUser.getId(), roleId);
            }
        }



//        //先全删
//        userRoleMapper.deleteRoleByUserId(authUser.getId());
//        //再全加
//        userVo.setId(authUser.getId());
//        insertByUserVo(userVo);
//        //TODO 要判断公司是否为空，否则会误删其他公司的买手
//        // if(AppUtil.getLoginUserCompanyNo()!=null){
//        AuthRoleDO buyerRoleDO = authRoleDOMapper.selectByNameAndCompanyNo("买手", AppUtil.getLoginUserCompanyNo());
//
//        boolean asBuyer = checkIfNeedAddBuyer(userVo.getRoleIds(), (buyerRoleDO == null) ? null : buyerRoleDO.getId());
//        if (asBuyer && authUser.getWxUnionId() != null) {
//            BuyerDO buyerQueryDO = new BuyerDO();
//            buyerQueryDO.setCompanyNo(AppUtil.getLoginUserCompanyNo());
//            buyerQueryDO.setUnionId(authUser.getWxUnionId());
//            buyerQueryDO.setNickName(authUser.getName());
//            BuyerDO buyerDO = buyerDOMapperExt.searchBuyer(buyerQueryDO);
//            if (buyerDO != null) {
//                //no change
//                return;
//            }
//            //add buyer
//            buyerQueryDO.init();
//            buyerDOMapperExt.insertSelective(buyerQueryDO);
//        } else if (!asBuyer) {
//            BuyerDO buyerQueryDO = new BuyerDO();
//            buyerQueryDO.setCompanyNo(AppUtil.getLoginUserCompanyNo());
//            buyerQueryDO.setUnionId(authUser.getWxUnionId());
//            List<BuyerDO> buyerDOList = buyerDOMapperExt.searchBuyerList(buyerQueryDO);
//            if (buyerDOList != null) {
//                //delete buyer
//                for (BuyerDO buyerDO : buyerDOList) {
//                    buyerDOMapperExt.deleteByPrimaryKey(buyerDO.getId());
//                }
//            }
//            return;
//        }


    }

    private void addAuthRoleByRoleNameAndUserId(Long roleId, Long id) {
        AuthUserRoleDO userRole = new AuthUserRoleDO();
        userRole.init();
        userRole.setUserId(id);
        userRole.setRoleId(roleId);
        userRoleMapper.insertByNoId(userRole);

    }

    private boolean checkIfNeedAddBuyer(String roleIds, Long roleId) {
        if (roleId == null && roleIds == null) {
            return false;
        }
        String[] roles = roleIds.split(",");
        for (String role : roles) {
            if (String.valueOf(roleId).equals(role)) {
                //exists
                return true;
            }
        }
        return false;
    }

    @Override
    public void changePasswordByLoginName(String loginName, String newPasswordInMd5Hex) {
        if (StringUtil.isEmpty(newPasswordInMd5Hex)) {
            return;
        }
        AuthUserDO user = new AuthUserDO();
//        user.setId(userId);
        user.setLoginName(loginName);
        user.setPassword(newPasswordInMd5Hex);
        userMapper.updatePasswordByLoginName(user);
    }

    @Override
    public void selectDataGrid(PageInfo pageInfo) {
//        Page<UserVo> page = new Page<UserVo>(pageInfo.getNowpage(), pageInfo.getSize());
//        List<UserVo> list = userMapper.selectUserVoPage(page, pageInfo.getCondition());
//        pageInfo.setRows(list);
//        pageInfo.setTotal(page.getTotal());
    }

    @Override
    public void deleteUserById(Long id) {

        userMapper.deleteByPrimaryKey(id);


    }

//	@Override
//	public List<AuthUserDO> queryBuyer() {
//		Map<String, Object> columnMap = new HashMap<>();
//		columnMap.put("organization_id", 7);
//		return userMapper.selectByMap(columnMap);
//	}

//	@Override
//    public List<Long> selectUserIds() {
//    	return userMapper.selectUserIds();
//    }

    @Override
    public UserQueryVO queryVoById(Long id) {
        return userMapper.queryUserQueryVOById(id);
    }

    @Override
    @Transactional(rollbackFor = ErpCommonException.class)
    public JsonPageResult<List<UserQueryVO>> queryUserQueryVOList(UserQueryVO userQueryVO) {
        JsonPageResult<List<UserQueryVO>> userResult = new JsonPageResult<>();

        Integer totalCount = userMapper.queryUsersCount(userQueryVO);

        if ((null != totalCount) && (0L != totalCount)) {
            userResult.buildPage(totalCount, userQueryVO);

            List<UserQueryVO> users = userMapper.queryUserQueryVOList(userQueryVO);
            userResult.setData(users);
        } else {
            List<UserQueryVO> users = new ArrayList<>();
            userResult.setData(users);
        }

        return userResult;
    }

    @Override
    public UserQueryVO selectUserVoByUserNo(String userNo) {
        // TODO Auto-generated method stub
        return userMapper.selectUserVoByUserNo(userNo);
    }

    //一键分享登录
    @Override
    public List<AuthUserDO> selectUserByWxUnionId(String wxUnionId) {
        return userMapper.selectUserByWxUnionId(wxUnionId);
    }


    //一键分享手机号登录
    @Override
    public AuthUserDO selectUserByPhone(String phone) {
        return userMapper.selectUserByPhone(phone);
    }


    @Override
    @Transactional(rollbackFor = ErpCommonException.class)
    public void addUserByqrcode(String companyNo, WxUserDO wxUser) throws ErpCommonException {

        WxUserDO existWxUser = wxUserDOMapper.queryByUnionId(wxUser.getUnionId());

        if (existWxUser == null) {
            wxUser.init4NoLogin();
            wxUserDOMapper.insertSelective(wxUser);
        } else {
            wxUser.setModifier("system");
            wxUserDOMapper.updateByPrimaryKey(existWxUser);
        }

        AuthUserDO user = new AuthUserDO();
        user.init4NoLogin();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        user.setPassword(Md5Util.getMD5(uuid));
        String userNo = CodeGenUtil.genUserNo();
        user.setUserNo(userNo);
        user.setCompanyNo(companyNo);
        user.setWxUnionId(wxUser.getUnionId());
        user.setWxOpenId(wxUser.getOpenId());
        user.setSex(wxUser.getGender().byteValue());
        user.setUserType((byte) 0);
        user.setStatus((byte) 0);
        user.setName(wxUser.getNickName());
        user.setLoginName("#init#" + companyNo + RandomUtils.nextInt(100000));
        userMapper.insertSelective(user);
        UserQueryVO userVo = userMapper.selectUserVoByUserNo(userNo);


        AuthRoleDO role = authRoleDOMapper.selectByNameAndCompanyNo("新成员", companyNo);
        if (role == null) {
            throw new ErpCommonException("找不到预置角色,请联系网站管理员");
        } else {
            AuthUserRoleDO authUserRole = new AuthUserRoleDO();
            authUserRole.setRoleId(role.getId());
            authUserRole.setCompanyNo(companyNo);
            authUserRole.setUserId(userVo.getId());
            authUserRole.init4NoLogin();
            userRoleDOMapperExt.insert(authUserRole);
        }


    }

    @Override
    @Transactional(rollbackFor = ErpCommonException.class)
    public void addProxy(String state, WxUserDO wxUser) throws ErpCommonException {
        String companyNo = AppUtil.getLoginUserCompanyNo();
        WxUserDO existWxUser = wxUserDOMapper.queryByUnionId(wxUser.getUnionId());

        if (existWxUser == null) {
            wxUser.init4NoLogin();
            wxUserDOMapper.insertSelective(wxUser);
        } else {
            wxUser.setModifier("system");
            wxUserDOMapper.updateByPrimaryKey(existWxUser);
        }

        AuthUserDO user = new AuthUserDO();
        user.init4NoLogin();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        user.setPassword(Md5Util.getMD5(uuid));
        String userNo = CodeGenUtil.genUserNo();
        user.setUserNo(userNo);
        user.setCompanyNo(companyNo);
        user.setWxUnionId(wxUser.getUnionId());
        user.setWxOpenId(wxUser.getOpenId());
        user.setSex(wxUser.getGender().byteValue());
        user.setUserType((byte) 0);
        user.setStatus((byte) 0);
        user.setName(wxUser.getNickName());
        user.setLoginName("#init#" + companyNo + RandomUtils.nextInt(100000));
        userMapper.insertSelective(user);
        UserQueryVO userVo = userMapper.selectUserVoByUserNo(userNo);
        String roleName = "销售代理";
        MallSaleAgentDO proxy = new MallSaleAgentDO();
        proxy.init();
        proxy.setUserNo(user.getUserNo());
        proxy.setParentAgent(state);
        proxy.setAgentName(wxUser.getNickName());
        proxy.setOpenId(wxUser.getOpenId());
        proxy.setUnionId(wxUser.getUnionId());
        proxy.setHeadProtraitUrl(wxUser.getAvatarUrl());
        proxy.setGender(wxUser.getGender());
        proxy.setCity(wxUser.getCity());
        proxy.setProvince(wxUser.getProvince());
        proxy.setCountry(wxUser.getCountry());
        proxy.setJoinTime(new Date());

        mallSaleAgentDOMapper.insertSelective(proxy);


        AuthRoleDO role = authRoleDOMapper.selectByNameAndCompanyNo(roleName, companyNo);
        if (role == null) {
            throw new ErpCommonException("找不到预置角色,请联系网站管理员");
        } else {
            AuthUserRoleDO authUserRole = new AuthUserRoleDO();
            authUserRole.setRoleId(role.getId());
            authUserRole.setCompanyNo(companyNo);
            authUserRole.setUserId(userVo.getId());
            authUserRole.init4NoLogin();
            userRoleDOMapperExt.insert(authUserRole);
        }


    }


    @Override
    public List<AuthUserDO> selectByUnionid(String unionid) {
        return userMapper.selectByUnionid(unionid);
    }


    @Override
    public List<AuthUserDO> queryUserByCompanyNo(String companyNo) {
        // TODO Auto-generated method stub
        return userMapper.queryUserByCompanyNo(companyNo);
    }

    @Override
    public List<AuthUserVO> queryUserListByCompanyNo(String companyNo) {
        return userMapper.queryUserListByCompanyNo(companyNo);
    }

    @Override
    public List<AuthUserDO> selectByUnionidAndCompanyNo(String unionid, String companyNo) {
        return userMapper.selectByUnionidAndCompanyNo(unionid, companyNo);
    }

    @Override
    public AuthUserDO selectByUserNoAndCompanyNo(String userNo, String companyNo) {

        return userMapper.selectByUserNoAndCompanyNo(userNo, companyNo);
    }

    @Override
    public Set<String> queryAvailableResources(String loginName) {
         Set<String> resCodes = userMapper.queryAvailableResourcesByLoginName(loginName);

        return resCodes;
    }
}

