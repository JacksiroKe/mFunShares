package com.jackson_siro.mfunshareshop;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class Settings_I extends ActionBarActivity{
	public static final String QOTD_SETTINGS = "Card_Settings";
	public static final String FONT_SIZE = "font_size";
	public static final String SET_THEME = "set_theme";
	
	private SeekBar seekBar;
	private TextView preview;
	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
	super .onCreate(savedInstanceState);
	setContentView(R.layout.settings_i);
	SharedPreferences settings = getSharedPreferences(QOTD_SETTINGS, 0);
    String font_size = settings.getString(FONT_SIZE, "25");
    	
	textView = (TextView) findViewById(R.id.textView2);
	preview = (TextView) findViewById(R.id.textView3);
	seekBar = (SeekBar) findViewById(R.id.seekBar1);
		
	final int myFonts = Integer.parseInt(font_size);
	
	preview.setTextSize(myFonts);
	textView.setText(font_size);
	seekBar.setProgress(myFonts);
	
	seekBar.setOnSeekBarChangeListener(new	OnSeekBarChangeListener() {	
		int progress = myFonts ;
		
			@Override	
			public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser){
				progress = progresValue;
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			
			}
			
			@Override	
			public void onStopTrackingTouch(SeekBar	seekBar) {	
				String myprogress = Integer.toString(progress);
				textView.setText(myprogress);	
				preview.setTextSize(progress);
				
				String font_size = Integer.toString(progress);
				SharedPreferences settings = getSharedPreferences(QOTD_SETTINGS, 25);
			    settings.edit().putString(FONT_SIZE, font_size).commit();
    
			}
		});
	
	}
		
}