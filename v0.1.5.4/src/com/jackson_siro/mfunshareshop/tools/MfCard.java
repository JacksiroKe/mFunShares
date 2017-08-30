package com.jackson_siro.mfunshareshop.tools;

public class MfCard {

	private int id;
	private String card_cat, card_title, card_content, card_image;
	
	public MfCard(){}
	
	public MfCard(String card_cat, String card_title, String card_content, String card_image) {
		super();
		this.card_cat = card_cat;
		this.card_title = card_title;
		this.card_content = card_content;
		this.card_image = card_image;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCardcat() {
		return card_cat;
	}
	public void setCardcat(String card_cat) {
		this.card_cat = card_cat;
	}
	public String getCardtitle() {
		return card_title;
	}
	public void setCardtitle(String card_title) {
		this.card_title = card_title;
	}
	public String getCardcontent() {
		return card_content;
	}
	public void setCardcontent(String card_content) {
		this.card_content = card_content;
	}
	public String getCardimage() {
		return card_image;
	}
	public void setCardimage(String card_image) {
		this.card_image = card_image;
	}
		
	@Override
	public String toString() {
		return "Song [id=" + id + ", card_cat=" + card_cat
				+  ", card_title=" + card_title  
				+  ", card_content=" + card_content + 
				", card_image=" + card_image + "]";
	}
	
	
	
}
