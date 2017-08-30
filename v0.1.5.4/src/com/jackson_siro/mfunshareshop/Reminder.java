package com.jackson_siro.mfunshareshop;

import java.util.Calendar;

import com.jackson_siro.mfunshareshop.tools.*;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.widget.TimePicker;

public class Reminder extends ActionBarActivity {
	public static final String QOTD_SETTINGS = "Card_Settings";
	private static final String mfss_default_hour = "6";
	private static final String mfss_default_minute = "30";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
       setContentView(R.layout.reminder);
       	

       	SharedPreferences settings = getPreferences(MODE_PRIVATE);        
		int savedHour= settings.getInt(mfss_default_hour, 6);
		int savedMin = settings.getInt(mfss_default_minute, 30);
       
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, savedHour);

        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
            12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = 
            (AlarmManager)getSystemService(Activity.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                pendingIntent);
    }
    
    

}