package com.wangqin.globalshop.biz1.app.dal.dataObject;

public class BuyerStorageDetailDO extends BaseModel {
    private Long id;

    private String storageNo;

    private String warehouseNo;

    private String warehouseName;

    private String shelfNo;

    private String skuCode;

    private Double price;

    private Double totalPrice;

    private Byte currency;

    private String upc;

    private Integer quantity;

    private Integer transQuantity;

    private String purchaseStorageNo;

    private String buyerTaskDetailNo;

    private String itemCode;

    private String skuBuysite;

    private String creator;

    private String modifier;

    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStorageNo() {
        return storageNo;
    }

    public void setStorageNo(String storageNo) {
        this.storageNo = storageNo == null ? null : storageNo.trim();
    }

    public String getWarehouseNo() {
        return warehouseNo;
    }

    public void setWarehouseNo(String warehouseNo) {
        this.warehouseNo = warehouseNo == null ? null : warehouseNo.trim();
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName == null ? null : warehouseName.trim();
    }

    public String getShelfNo() {
        return shelfNo;
    }

    public void setShelfNo(String shelfNo) {
        this.shelfNo = shelfNo == null ? null : shelfNo.trim();
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode == null ? null : skuCode.trim();
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Byte getCurrency() {
        return currency;
    }

    public void setCurrency(Byte currency) {
        this.currency = currency;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc == null ? null : upc.trim();
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getTransQuantity() {
        return transQuantity;
    }

    public void setTransQuantity(Integer transQuantity) {
        this.transQuantity = transQuantity;
    }

    public String getPurchaseStorageNo() {
        return purchaseStorageNo;
    }

    public void setPurchaseStorageNo(String purchaseStorageNo) {
        this.purchaseStorageNo = purchaseStorageNo == null ? null : purchaseStorageNo.trim();
    }

    public String getBuyerTaskDetailNo() {
        return buyerTaskDetailNo;
    }

    public void setBuyerTaskDetailNo(String buyerTaskDetailNo) {
        this.buyerTaskDetailNo = buyerTaskDetailNo == null ? null : buyerTaskDetailNo.trim();
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode == null ? null : itemCode.trim();
    }

    public String getSkuBuysite() {
        return skuBuysite;
    }

    public void setSkuBuysite(String skuBuysite) {
        this.skuBuysite = skuBuysite == null ? null : skuBuysite.trim();
    }

    public String getCreator() {
        return creator;
    }

    @Override
    public void setCreator(String creator) {
        this.creator = creator == null ? null : creator.trim();
    }

    public String getModifier() {
        return modifier;
    }

    @Override
    public void setModifier(String modifier) {
        this.modifier = modifier == null ? null : modifier.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}