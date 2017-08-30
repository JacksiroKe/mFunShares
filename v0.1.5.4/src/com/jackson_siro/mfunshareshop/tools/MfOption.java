package com.jackson_siro.mfunshareshop.tools;

public class MfOption {

	private int id;
	private String option_title;
	private String option_content;
	
	public MfOption(){}
	
	public MfOption(String option_title, String option_content) {
		super();
		this.option_title = option_title;
		this.option_content = option_content;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getOptiontitle() {
		return option_title;
	}
	public void setOptiontitle(String option_title) {
		this.option_title = option_title;
	}
	public String getOptioncontent() {
		return option_content;
	}
	public void setOptioncontent(String option_content) {
		this.option_content = option_content;
	}
		
	@Override
	public String toString() {
		return "Song [id=" + id +  ", option_title=" + option_title  
				+  ", option_content=" + option_content + "]";
	}
	
	
	
}
