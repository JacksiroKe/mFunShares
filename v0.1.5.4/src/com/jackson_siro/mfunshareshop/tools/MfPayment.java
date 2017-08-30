package com.jackson_siro.mfunshareshop.tools;

public class MfPayment {

	private int id;
	private String payment_agent, payment_time, payment_code,payment_amount;
	
	public MfPayment(){}
	
	public MfPayment(String payment_agent, String payment_amount, String payment_time, String payment_code) {
		super();
		this.payment_agent = payment_agent;
		this.payment_amount = payment_amount;
		this.payment_time = payment_time;
		this.payment_time = payment_code;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPaymentAgent() {
		return payment_agent;
	}
	public void setPaymentAgent(String payment_agent) {
		this.payment_agent = payment_agent;
	}
	public String getPaymentTime() {
		return payment_time;
	}
	public void setPaymentTime(String payment_time) {
		this.payment_time = payment_time;
	}
	public String getPaymentCode() {
		return payment_code;
	}
	public void setPaymentCode(String payment_code) {
		this.payment_code = payment_code;
	}
	public String getPaymentAmount() {
		return payment_amount;
	}
	public void setPaymentAmount(String payment_amount) {
		this.payment_amount = payment_amount;
	}
		
	@Override
	public String toString() {
		return "Song [id=" + id +  ", payment_agent=" + payment_agent + ", payment_code=" + payment_code 
				 +  ", payment_time=" + payment_time +  ", payment_amount=" + payment_amount + "]";
	}
	
	
	
}
