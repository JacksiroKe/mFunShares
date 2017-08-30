package com.jackson_siro.mfunshareshop;

import com.jackson_siro.mfunshareshop.adaptor.*;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import android.app.Activity;
import android.content.Intent;

public class SetApp extends ActionBarActivity {
	
	ListView list;
	
	private String[] mytext;
	private String[] mytexti;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_app);
		
		mytext = getResources().getStringArray(R.array.SetAppList);
		mytexti = getResources().getStringArray(R.array.SetAppListDesc);
		
		SettingsList adapter = new SettingsList(SetApp.this, mytext, mytexti);
		list=(ListView)findViewById(R.id.list);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mytext[+ position].equals("User Preferences")) {
        			Intent intent = new Intent(getBaseContext(), Settings.class);
        			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        			startActivity(intent);
        		}
        		
        		else if (mytext[+ position].equals("Display Preferences")) {
        			Intent intent = new Intent(getBaseContext(), Settings_I.class);
        			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        			startActivity(intent);
        		}
        		        		
            }
        });		
				
	}
			
}
