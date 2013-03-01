package com.example.tryfragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class NotesFragment extends Fragment {

	public static final String ARG_ID = "id";
	public static final String ARG_NAME = "name";
	public int mCurrentID = -1;
	public String mCurrentName = "NoName";
	private NotesDBHelper mDBHelper;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mCurrentID = savedInstanceState.getInt(ARG_ID);
			mCurrentName = savedInstanceState.getString(ARG_NAME);
		}
		return inflater.inflate(R.layout.notes_view, container, false);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		Bundle args = getArguments();
		if (args != null) {
			updateNotesView (args.getInt(ARG_ID), args.getString(ARG_NAME));
			new getNotesFromRemoteDB().execute(Integer.toString(args.getInt(ARG_ID)));
		} else if (mCurrentID != -1) {
			updateNotesView(mCurrentID, mCurrentName);
		}
	}
	
	public void updateNotesView (int id, String name) {
		EditText et = (EditText) getActivity().findViewById(R.id.notes_content);
		mDBHelper = new NotesDBHelper(getActivity());
		String dbNotes = mDBHelper.getNote(id);
		
		((TextView) getActivity().findViewById(R.id.notes_header)).setText(name);
		
		if (dbNotes != null){
			et.setText(dbNotes);
		} 
		
		mCurrentID = id;
		mCurrentName = name;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Button save_btn = (Button)getActivity().findViewById(R.id.save_button);
		save_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View btnView) {
				Log.i("KevinDebug", "will have to add or edit the notes here");
				String updatedNote = ((EditText)getActivity().findViewById(R.id.notes_content)).getText().toString();
				mDBHelper.updateNote(mCurrentID, updatedNote);
				
				new updateRemoteDB().execute(Integer.toString(mCurrentID), updatedNote);
			}
		});
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(ARG_ID, mCurrentID);
		outState.putString(ARG_NAME, mCurrentName);
	}

	private class updateRemoteDB extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			Log.i("KevinDebug", params[0] + params[1]);
			JSONObject tempObj = new JSONObject();
			try {
				tempObj.put(NotesDBHelper.COLUMN_ID, params[0]);
				tempObj.put(NotesDBHelper.COLUMN_NOTES, params[1]);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			

			Log.i("KevinDebug", "tempObj string is :- " + tempObj.toString());
			
			HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost("http://kevinpatel.000space.com/try.php?updateEntry=1");
		    
		    try {
		        // Add your data
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		        nameValuePairs.add(new BasicNameValuePair("data", tempObj.toString()));
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		        
		        // Execute HTTP Post Request
		        httpclient.execute(httppost);
		        
		    } catch (ClientProtocolException e) {
		    } catch (IOException e) {
		    }
			
		   
			return null;
		}
		
	}

	private class getNotesFromRemoteDB extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			Log.i("KevinDebug","from do in background :- ");
			
			HttpClient httpclient = new DefaultHttpClient(); // Create HTTP Client
			HttpGet httpget = new HttpGet("http://kevinpatel.000space.com/try.php?fetchItem=1&id=" + params[0]); // Set the action you want to do
			HttpResponse response = null;
			String resString = "";
			
			try {
				response = httpclient.execute(httpget);
				int responseCode = response.getStatusLine().getStatusCode();
				Log.i("KevinDebug", "Response Code is :- " + responseCode);
				if (responseCode == 200) {
					HttpEntity entity = response.getEntity();
					if(entity != null) {
						resString = EntityUtils.toString(entity);
					}
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Log.i("KevinDebug", "Res String is :- " + resString);
			return resString;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			if (result.length() > 0) {
			    ((EditText) getActivity().findViewById(R.id.notes_content)).setText(result);
			}
		}
		
		
	}
}
