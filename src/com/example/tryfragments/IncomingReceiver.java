package com.example.tryfragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

public class IncomingReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent intent) {
		Bundle extras = intent.getExtras();
		if (extras != null) {
			String state = extras.getString(TelephonyManager.EXTRA_STATE);
			if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
				Log.i("KevinDebug", extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER));
				Intent i = new Intent(ctx,CallScreenActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        ctx.startActivity(i);
			}
		}
	}

}
