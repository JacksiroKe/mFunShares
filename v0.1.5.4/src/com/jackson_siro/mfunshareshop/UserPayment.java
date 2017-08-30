package com.jackson_siro.mfunshareshop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;

import com.jackson_siro.mfunshareshop.adaptor.MyListAdapter;
import com.jackson_siro.mfunshareshop.tools.JSONParser;
import com.jackson_siro.mfunshareshop.tools.MfCategory;
import com.jackson_siro.mfunshareshop.tools.MfPayment;
import com.jackson_siro.mfunshareshop.tools.MfssDatabase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class UserPayment extends ActionBarActivity {
	
	RelativeLayout MyCard;
	MfssDatabase db = new MfssDatabase(this);
	
	List<MfPayment> mylist;
	ArrayAdapter<String> myAdapter;
	
	JSONArray cards = null;
	MyListAdapter adapter;
	
	ListView list;	
	private String[] My_Text;
	private String[] My_Texti;
	private String[] My_Textii;
	private String[] My_Icon;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_payment);
		
		FillWithData();
	}
	

	private void FillWithData(){
		mylist = db.getAllPayments();
		List<String> listPid = new ArrayList<String>();
		List<String> listPagent = new ArrayList<String>();
		List<String> listPamount = new ArrayList<String>();
		List<String> listPtime = new ArrayList<String>();
		
		for (int i = 0; i < mylist.size(); i++) {
			listPid.add(i, mylist.get(i).getPaymentCode());
			listPagent.add(i, mylist.get(i).getPaymentAgent());
			listPamount.add(i, mylist.get(i).getPaymentAmount());
			listPtime.add(i, mylist.get(i).getPaymentTime());
		}

		My_Text = listPid.toArray(new String[listPid.size()]);		
		for (String string : My_Text) {	System.out.println(string);	}
		
		My_Texti = listPagent.toArray(new String[listPagent.size()]);		
		for (String stringi : My_Texti) {	System.out.println(stringi);	}

		My_Textii = listPagent.toArray(new String[listPagent.size()]);		
		for (String stringii : My_Textii) {	System.out.println(stringii);	}
		
		My_Icon = listPtime.toArray(new String[listPtime.size()]);		
		for (String stringiii : My_Text) {	System.out.println(stringiii);	}
		
        list = (ListView)findViewById(R.id.mfss_list);
        adapter = new MyListAdapter(this, My_Icon, My_Text, My_Texti, My_Textii);
        list.setAdapter(adapter);
        
	}
		
}
