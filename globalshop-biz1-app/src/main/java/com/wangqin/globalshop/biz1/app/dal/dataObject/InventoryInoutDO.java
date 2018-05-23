package com.wangqin.globalshop.biz1.app.dal.dataObject;

import java.util.Date;

public class InventoryInoutDO {
    private Long id;

    private String companyNo;

    private String itemCode;

    private String skuCode;

    private String inventoryOnWarehouseNo;

    private String warehouseNo;

    private String shelfNo;

    private Long quantity;

    private Byte operatorType;

    private Date gmtCreate;

    private Date gmtModify;

    private Boolean isDel;

    private String creator;

    private String modifier;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyNo() {
        return companyNo;
    }

    public void setCompanyNo(String companyNo) {
        this.companyNo = companyNo == null ? null : companyNo.trim();
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode == null ? null : itemCode.trim();
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode == null ? null : skuCode.trim();
    }

    public String getInventoryOnWarehouseNo() {
        return inventoryOnWarehouseNo;
    }

    public void setInventoryOnWarehouseNo(String inventoryOnWarehouseNo) {
        this.inventoryOnWarehouseNo = inventoryOnWarehouseNo == null ? null : inventoryOnWarehouseNo.trim();
    }

    public String getWarehouseNo() {
        return warehouseNo;
    }

    public void setWarehouseNo(String warehouseNo) {
        this.warehouseNo = warehouseNo == null ? null : warehouseNo.trim();
    }

    public String getShelfNo() {
        return shelfNo;
    }

    public void setShelfNo(String shelfNo) {
        this.shelfNo = shelfNo == null ? null : shelfNo.trim();
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Byte getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(Byte operatorType) {
        this.operatorType = operatorType;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }

    public Boolean getIsDel() {
        return isDel;
    }

    public void setIsDel(Boolean isDel) {
        this.isDel = isDel;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator == null ? null : creator.trim();
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier == null ? null : modifier.trim();
    }
}