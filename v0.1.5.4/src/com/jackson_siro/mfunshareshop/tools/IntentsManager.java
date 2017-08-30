package com.jackson_siro.mfunshareshop.tools;

import android.content.Context;

public class IntentsManager {

    Context mContext;
    MfssDatabase db;
    //= new MfssDatabase(this);
    
	public IntentsManager(Context mContext) {
        this.mContext = mContext;
    	this.db = new MfssDatabase(mContext);
    }
	
	public void NewPayment(String payment_agent, String payment_amount, String payment_time, String payment_code){
		db.createPayment(new MfPayment(payment_agent, payment_amount, payment_time, payment_code));
	}
	
}
