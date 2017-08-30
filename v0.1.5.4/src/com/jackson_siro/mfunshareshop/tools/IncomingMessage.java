package com.jackson_siro.mfunshareshop.tools;

import com.jackson_siro.mfunshareshop.*;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class IncomingMessage extends BroadcastReceiver
{
  
	NotificationCompat.Builder notification;
	PendingIntent pIntent;
	NotificationManager manager;
	Intent resultIntent;
	TaskStackBuilder stackBuilder;
    String notificationMsg;
    String notificationTitle;
    Uri ringToneUri;
    
    //MfssDatabase db = new MfssDatabase(this);
	
  final SmsManager sms = SmsManager.getDefault();

  public void onReceive(Context paramContext, Intent paramIntent)
  {
	  //SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(paramContext.getApplicationContext()).edit();
	  	Bundle localBundle = paramIntent.getExtras();
		    if (localBundle != null) {}
		    for (;;)
		    {
		      int i, pamount;
		      String sender, message, pagent, ptime, pcode;
		      try {
		        Object[] arrayOfObject = (Object[])localBundle.get("pdus");
		        i = 0;
		        if (i >= arrayOfObject.length) {
		          return;
		        }
		        
		        SmsMessage localSmsMessage = SmsMessage.createFromPdu((byte[])arrayOfObject[i]);
		        sender = localSmsMessage.getDisplayOriginatingAddress();
		        message = localSmsMessage.getDisplayMessageBody();
		        Log.i("SmsReceiver", "senderNum: " + sender + "; message: " + message);

		        if (message.contains("AMBROSE MBINDO MALUSI"))
			     {
	  	      	  	Toast.makeText(paramContext, "mFunShares has received your payment!", Toast.LENGTH_LONG).show();
	  	      	  	String[] smscode = TextUtils.split(message, "Confirmed");
	  	      	  	String[] smsamounti = TextUtils.split(smscode[1], ".00");
	  	      	  	String[] smsamountii = TextUtils.split(smsamounti[0], "Ksh");
	  	      	  	String[] smstimei = TextUtils.split(smsamounti[1], "New MPESA");
	  	      	  	String[] smstimeii = TextUtils.split(smstimei[0], "on");
	  	      	  	NewPayment(paramContext, "MPESA", smsamountii[1], smstimeii[1], smscode[0]);
	  	        }
		        
		        /*if (sender.equalsIgnoreCase("MPESA")) {
			        if (message.contains("AMBROSE MBINDO MALUSI"))
			        {
			      	  	Toast.makeText(paramContext, "mFunShares has received your payment!", Toast.LENGTH_LONG).show();
			  	  		
			        }
		         } else if (sender.contains("711474787")) {
		        	 if (message.contains("AMBROSE MBINDO MALUSI"))
				     {
		  	      	  	Toast.makeText(paramContext, "mFunShares has received your payment!", Toast.LENGTH_LONG).show();			  	  		
		  	        }
		          } */
		        
		      } catch (Exception localException) {
		        Log.e("SmsReceiver", "Exception smsReceiver" + localException);
		        return;
		      }
      
     }
  }
  
  public void NewPayment(Context p_context, String p_agent, String p_amount, String p_time, String p_code) {
		MfssDatabase db = new MfssDatabase(p_context);
		db.createPayment(new MfPayment(p_agent, p_amount, p_time, p_code));
  }

	public void NewNotification(Context context, String strtitle, String strmessage) {
		Intent intent = new Intent(context, AaSplash.class);
		intent.putExtra("title", strtitle);
		intent.putExtra("text", strmessage);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		this.ringToneUri = Uri.parse("android.resource://" + 
				context.getPackageName() + "/raw/tweeting");
		// Create Notification using NotificationCompat.Builder
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_launcher)
				.setTicker(strmessage)
				.setContentTitle(strtitle)
				.setSound(ringToneUri)
				.setContentText(strmessage)
				.addAction(R.drawable.ic_launcher, "Continue Singing", pIntent)
				.setContentIntent(pIntent)
				.setAutoCancel(true);

		// Create Notification Manager
		NotificationManager notificationmanager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// Build Notification with Notification Manager
		notificationmanager.notify(0, builder.build());

	}
  
}
