package com.example.tryfragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class IncomingReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent intent) {
		Bundle extras = intent.getExtras();
		if (extras != null) {
			String state = extras.getString(TelephonyManager.EXTRA_STATE);
			if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
				Log.i("KevinDebug", extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER));
				
				// No need for the Intent
				/*Intent i = new Intent(ctx,CallScreenActivity.class);
				i.putExtras(intent);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
				
				String result = new NotesDBHelper(ctx).getNotesFromPhoneNumber(extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER));
				Toast.makeText(ctx, result , Toast.LENGTH_LONG).show();
		        //http://stackoverflow.com/questions/3500197/how-to-display-toast-in-android
			}
		}
	}

}
