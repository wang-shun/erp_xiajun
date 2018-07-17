package com.wangqin.globalshop.biz1.app.dal.dataObject;

import java.util.Date;

public class MallReturnOrderDO extends BaseModel {
    private Long id;

    private String orderNo;

    private Long outerOrderNo;

    private String subOrderNo;

    private Byte status;

    private String returnReason;

    private String returnReasonDetail;

    private Integer returnQuantity;

    private Double returnPrice;

    private Byte isCivil;

    private Byte isCheckin;

    private String customerOpenId;

    private String telephone;

    private Date receiveTime;

    private Date returnPayTime;

    private String remark;

    private Integer returnRefer;

    private String proofImg;

    private Integer returnType;

    private String modifier;

    private String creator;

    private String mallReturnOrderNo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public Long getOuterOrderNo() {
        return outerOrderNo;
    }

    public void setOuterOrderNo(Long outerOrderNo) {
        this.outerOrderNo = outerOrderNo;
    }

    public String getSubOrderNo() {
        return subOrderNo;
    }

    public void setSubOrderNo(String subOrderNo) {
        this.subOrderNo = subOrderNo == null ? null : subOrderNo.trim();
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getReturnReason() {
        return returnReason;
    }

    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason == null ? null : returnReason.trim();
    }

    public String getReturnReasonDetail() {
        return returnReasonDetail;
    }

    public void setReturnReasonDetail(String returnReasonDetail) {
        this.returnReasonDetail = returnReasonDetail == null ? null : returnReasonDetail.trim();
    }

    public Integer getReturnQuantity() {
        return returnQuantity;
    }

    public void setReturnQuantity(Integer returnQuantity) {
        this.returnQuantity = returnQuantity;
    }

    public Double getReturnPrice() {
        return returnPrice;
    }

    public void setReturnPrice(Double returnPrice) {
        this.returnPrice = returnPrice;
    }

    public Byte getIsCivil() {
        return isCivil;
    }

    public void setIsCivil(Byte isCivil) {
        this.isCivil = isCivil;
    }

    public Byte getIsCheckin() {
        return isCheckin;
    }

    public void setIsCheckin(Byte isCheckin) {
        this.isCheckin = isCheckin;
    }

    public String getCustomerOpenId() {
        return customerOpenId;
    }

    public void setCustomerOpenId(String customerOpenId) {
        this.customerOpenId = customerOpenId == null ? null : customerOpenId.trim();
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone == null ? null : telephone.trim();
    }

    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }

    public Date getReturnPayTime() {
        return returnPayTime;
    }

    public void setReturnPayTime(Date returnPayTime) {
        this.returnPayTime = returnPayTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Integer getReturnRefer() {
        return returnRefer;
    }

    public void setReturnRefer(Integer returnRefer) {
        this.returnRefer = returnRefer;
    }

    public String getProofImg() {
        return proofImg;
    }

    public void setProofImg(String proofImg) {
        this.proofImg = proofImg == null ? null : proofImg.trim();
    }

    public Integer getReturnType() {
        return returnType;
    }

    public void setReturnType(Integer returnType) {
        this.returnType = returnType;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier == null ? null : modifier.trim();
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator == null ? null : creator.trim();
    }

    public String getMallReturnOrderNo() {
        return mallReturnOrderNo;
    }

    public void setMallReturnOrderNo(String mallReturnOrderNo) {
        this.mallReturnOrderNo = mallReturnOrderNo == null ? null : mallReturnOrderNo.trim();
    }
}