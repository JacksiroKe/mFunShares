package com.jackson_siro.mfunshareshop.tools;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import com.jackson_siro.mfunshareshop.*;

public class TimePreference extends DialogPreference {
  /** The widget for picking a time. */
  private TimePicker timePicker;

  private static final int mfss_default_hour = 6;

  private static final int mfss_default_minute = 30;

  public TimePreference(Context context, AttributeSet attributes) {
    super(context, attributes);
  }

  @Override
  public void onBindDialogView(View view) {
    timePicker = (TimePicker) view.findViewById(R.id.prefTimePicker); 
    timePicker.setCurrentHour(getSharedPreferences().getInt(getKey() + ".hour", mfss_default_hour)); 
    timePicker.setCurrentMinute(getSharedPreferences().getInt(getKey() + ".minute", mfss_default_minute));
  }

  @Override
  public void onClick(DialogInterface dialog, int button) {
    if (button == Dialog.BUTTON_POSITIVE) {
      SharedPreferences.Editor editor = getEditor();
      editor.putInt(getKey() + ".hour", timePicker.getCurrentHour());
      editor.putInt(getKey() + ".minute", timePicker.getCurrentMinute());
      
      editor.putLong("mfss_reminder_hour", timePicker.getCurrentHour());
      editor.putLong("mfss_reminder_minute", timePicker.getCurrentMinute());
      
      editor.commit();
    }
  }
}