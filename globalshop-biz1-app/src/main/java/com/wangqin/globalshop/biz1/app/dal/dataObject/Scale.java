package com.wangqin.globalshop.biz1.app.dal.dataObject;


public class Scale {
	private Long id;//类型id
	private String name;//名称
	private Long typeId;//类型ID
	private Integer seq;//排序
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getTypeId() {
		return typeId;
	}
	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	
}
