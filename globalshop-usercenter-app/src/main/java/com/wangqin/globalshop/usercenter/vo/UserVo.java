package com.wangqin.globalshop.usercenter.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wangqin.globalshop.biz1.app.dal.dataObject.AuthRoleDO;
import com.wangqin.globalshop.biz1.app.dal.dataObject.AuthUserDO;
import com.wangqin.globalshop.common.utils.JsonUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @description：UserVo
 * @author：zhixuan.wang
 * @date：2015/10/1 14:51
 */
public class UserVo implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
//	@NotNull(message = "用户编号不能为空")
	private String userNo;
//	@Size(max = 64,min = 1,message = "登陆名不能为空也不能大于64位")
	private String loginName;
//	@Size(max = 64,min = 1,message = "用户名不能为空也不能大于64位")
	private String name;
//	@Size(max = 64, min = 1, message = "所属公司不能为空也不能大于64位")
	private String companyNo;

	@JsonIgnore
//	@Size(max = 64, min = 1, message = "密码不能为空也不能大于64位")
	private String password;
	private Integer sex;
	@Min(value = 0, message = "年龄最小只能写0")
	@Max(value = 120, message = "年龄最大只能写120")
	private Integer age;
	@NotNull(message = "用户类型不能为空")
	private Integer userType;
	
	private Integer status;
	private Integer organizationId;
	
	private Date createTime;
	@Size(max = 11, min = 0, message = "电话最多11位")
	private String phone;

	private List<AuthRoleDO> rolesList;
	
	private String organizationName;

	private String roleIds;
	
	private Date createdateStart;
	
	private Date createdateEnd;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName == null ? null : loginName.trim();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password == null ? null : password.trim();
	}

	public String getCompanyNo() {
		return companyNo;
	}

	public void setCompanyNo(String companyNo) {
		this.companyNo = companyNo;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Integer organizationId) {
		this.organizationId = organizationId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone == null ? null : phone.trim();
	}

	public List<AuthRoleDO> getRolesList() {
		return rolesList;
	}

	public void setRolesList(List<AuthRoleDO> rolesList) {
		this.rolesList = rolesList;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(String roleIds) {
		this.roleIds = roleIds;
	}

	public Date getCreatedateStart() {
		return createdateStart;
	}

	public void setCreatedateStart(Date createdateStart) {
		this.createdateStart = createdateStart;
	}

	public Date getCreatedateEnd() {
		return createdateEnd;
	}

	public void setCreatedateEnd(Date createdateEnd) {
		this.createdateEnd = createdateEnd;
	}
	
	/**
	 * 比较vo和数据库中的用户是否同一个user，采用id比较
	 * @param user 用户
	 * @return 是否同一个人
	 */
	public boolean equalsUser(AuthUserDO user) {
		if (user == null) {
			return false;
		}
		Long userId = user.getId();
		if (id == null || userId == null) {
			return false;
		}
		return id.equals(userId);
	}

	@Override
	public String toString() {
		return JsonUtils.toJson(this);
	}
}
