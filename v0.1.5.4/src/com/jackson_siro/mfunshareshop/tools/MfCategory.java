package com.jackson_siro.mfunshareshop.tools;

public class MfCategory {

	private int id;
	private String catid;
	private String cat_title;
	private String cat_content;
	private String cat_icon;
	
	public MfCategory(){}
	
	public MfCategory(String catid, String cat_title, String cat_content, String cat_icon) {
		super();
		this.catid = catid;
		this.cat_title = cat_title;
		this.cat_content = cat_content;
		this.cat_icon = cat_icon;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCatid() {
		return catid;
	}
	public void setCatid(String catid) {
		this.catid = catid;
	}
	public String getCattitle() {
		return cat_title;
	}
	public void setCattitle(String cat_title) {
		this.cat_title = cat_title;
	}
	public String getCatcontent() {
		return cat_content;
	}
	public void setCatcontent(String cat_content) {
		this.cat_content = cat_content;
	}

	public String getCaticon() {
		return cat_icon;
	}
	public void setCaticon(String cat_icon) {
		this.cat_icon = cat_icon;
	}
		
	@Override
	public String toString() {
		return "Song [id=" + id + ", catid=" + catid
				+  ", cat_title=" + cat_title + ", "
				+  ", cat_content=" + cat_content + ", "
				+ "cat_icon=" + cat_icon + "]";
	}
	
	
	
}
