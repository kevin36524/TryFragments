package com.example.tryfragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

public class IncomingReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent intent) {
		Bundle extras = intent.getExtras();
		if (extras != null) {
			String state = extras.getString(TelephonyManager.EXTRA_STATE);
			if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
				Log.i("KevinDebug", extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER));
				
				String result = new NotesDBHelper(ctx).getNotesFromPhoneNumber(extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER));
				//http://stackoverflow.com/questions/3500197/how-to-display-toast-in-android
				
				/* Creating custom toast */
				/*
				Toast toast = Toast.makeText(ctx, result, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
				toast.show();*/
				
				Toast toast = new Toast(ctx);
				TextView tv = new TextView(ctx);
				tv.setText(result);
				tv.setTextSize(22.0f);
				toast.setGravity(Gravity.CENTER_VERTICAL, 0, 23);
				toast.setView(tv);
				
				final Toast finaltoast = toast;
				
				finaltoast.show();
				
				new CountDownTimer(9000, 1000)
				{

				    public void onTick(long millisUntilFinished) {finaltoast.show();}
				    public void onFinish() {finaltoast.show();}

				}.start();
				
			}
		}
	}

}
