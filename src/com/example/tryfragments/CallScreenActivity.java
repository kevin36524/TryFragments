package com.example.tryfragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class CallScreenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("KevinDebug","Seems to be working");
		
		new fetchDataClass().execute(new String [] {getIntent().getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)});
	}
	
	void renderUI(String renderString) {

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        setContentView(R.layout.activity_call_screen);

        String number = getIntent().getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        TextView text = (TextView)findViewById(R.id.text);
        text.setText("Incoming call from " + number + "Notes :-" + renderString);

	}
	
	private class fetchDataClass extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			Log.i("KevinDebug", "Params are :- " + params[0]);
			String result = new NotesDBHelper(getApplicationContext()).getNotesFromPhoneNumber(params[0]);
			
			Toast.makeText(getApplicationContext(), result , Toast.LENGTH_LONG).show();
		    return "";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			//http://stackoverflow.com/questions/3500197/how-to-display-toast-in-android
			// Toast worked on the emulator.
			
			
		}
		
	}
	
}
