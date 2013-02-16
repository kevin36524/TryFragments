package com.example.tryfragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class CallScreenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_screen);
		Log.i("KevinDebug","Seems to be working");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i("KevinDebug","OnPause to be working");
		recreate();
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i("KevinDebug","OnStop to be working");
	}

	
}
