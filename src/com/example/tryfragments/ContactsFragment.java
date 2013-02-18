package com.example.tryfragments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class ContactsFragment extends ListFragment {


	OnContactsSelectedListener mCallBack;
    public interface OnContactsSelectedListener {
    	public void onContactSelected(int id, String name);
    }

	private String[] projection = new String[] { Contacts.DISPLAY_NAME_PRIMARY, Contacts._ID};

	private SimpleCursorAdapter mAdapter;
	private Cursor mCursor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        mCursor = getActivity().getContentResolver().query(Contacts.CONTENT_URI, projection , null, null, null);
     
/*     String[] contactsName = new String[mCursor.getCount()];
     Log.i("KevinDebug","Am I called twice");
     if (mCursor.moveToFirst()) {
    	 for (int i=0; i<mCursor.getCount(); i++) {
    		 contactsName[i] = mCursor.getString(mCursor.getColumnIndex(RawContacts.DISPLAY_NAME_PRIMARY));
    		 Log.i("KevinDebug",mCursor.getString(mCursor.getColumnIndex(RawContacts.CONTACT_ID)));
    		 Log.i("KevinDebug",mCursor.getString(mCursor.getColumnIndex(RawContacts.DISPLAY_NAME_PRIMARY)));
    		 mCursor.moveToNext();
    	 }
     }*/
     
     setHasOptionsMenu(true);
     //setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1 , contactsName));
     

     String[] mDataColumns = { Contacts.DISPLAY_NAME_PRIMARY };
     int[] mViewIDs = { android.R.id.text1 };
  
     mAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, mCursor, mDataColumns, mViewIDs, 0);
     setListAdapter(mAdapter);
     
     new generateDB().execute(new String[]{});
		
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.search_menu, menu);
		SearchView mSearchView = (SearchView)menu.findItem(R.id.search_box).getActionView();
		mSearchView.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				Log.i("KevinDebug", "Submit button pressed with query :- " + query);
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				Log.i("KevinDebug", "text is Changed and the newText is :- " + newText);
				String selection = Contacts.DISPLAY_NAME + " LIKE ?";
				String [] selectionArgs = {"%" + newText + "%"};
				
				mCursor = getActivity().getContentResolver().query(Contacts.CONTENT_URI, projection, selection, selectionArgs, null);
				mAdapter.changeCursor(mCursor);
				
				return false;
			}
		});
	}
	
	@Override
	public void onStart() {
		super.onStart();
		// add the logic to highlight a particular selected row.
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		
		mCursor.moveToFirst();
		mCursor.move(position);
		
		mCallBack.onContactSelected((int)(id-1),mCursor.getString(mCursor.getColumnIndex(Contacts.DISPLAY_NAME_PRIMARY)));
		
		l.setItemChecked(position, true);
	}

	@Override
	public void onAttach(Activity activity) {
		// ensure that the activity is implementing the interface
		super.onAttach(activity);
		
		try {
			mCallBack = (OnContactsSelectedListener) activity;
		} catch (Exception e) {
			Log.e("ContactsFragment"," Must implement the OnContactsSelectedListener");
		}
	}
	
	private class generateDB extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			
			Log.i("KevinDebug","I am called from generateDB/doInBackground");
			
			String [] fetchFields = new String [] {Contacts._ID,Contacts.DISPLAY_NAME};
			Cursor retCursor = getActivity().getContentResolver().query(Contacts.CONTENT_URI, fetchFields, null, null, null);
			Cursor emailCursor = getActivity().getContentResolver().query(Email.CONTENT_URI, new String[] {Email.CONTACT_ID,Email.ADDRESS}, null, null, null);
			Cursor phCursor = getActivity().getContentResolver().query(Phone.CONTENT_URI, new String[] {Phone.CONTACT_ID,Phone.NUMBER},null,null,null);
			
			JSONArray postArr  = new JSONArray();
			JSONObject tempObj;
			

			retCursor.moveToFirst();
			for(int i=0; i < retCursor.getCount(); i++) {
				tempObj = new JSONObject();
				int index = retCursor.getInt(retCursor.getColumnIndex(Contacts._ID));
				try {
					tempObj.put("_id", retCursor.getString(retCursor.getColumnIndex(Contacts._ID)));
					tempObj.put("name", retCursor.getString(retCursor.getColumnIndex(Contacts.DISPLAY_NAME)));
					postArr.put(index, tempObj);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				retCursor.moveToNext();
			}
			

			emailCursor.moveToFirst();
			for (int i = 0 ; i < emailCursor.getCount(); i++) {
				
				int index = emailCursor.getInt(emailCursor.getColumnIndex(Email.CONTACT_ID));
				
				String email_id = "";
				try {
					email_id = emailCursor.getString(emailCursor.getColumnIndex(Email.ADDRESS));
					try {
						tempObj = (JSONObject) postArr.get(index);
						tempObj.put("email",email_id);
						postArr.put(index, tempObj);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				Log.i("KevinDebug","index :- " + index + "Email ID :-" + email_id);
				emailCursor.moveToNext();
			}
			
			phCursor.moveToFirst();
			for (int i = 0 ; i < phCursor.getCount(); i++) {
				int index = phCursor.getInt(phCursor.getColumnIndex(Phone.CONTACT_ID));
				
				String phNumber = "";
				try {
					phNumber = phCursor.getString(phCursor.getColumnIndex(Phone.NUMBER));
					try {
						tempObj = (JSONObject) postArr.get(index);
						tempObj.put("ph",phNumber);
						postArr.put(index, tempObj);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}	
				
				Log.i("KevinDebug","index :- " + index + "phone Number :-" + phNumber);
				phCursor.moveToNext();
			}
			
			Log.i("KevinDebug","postArr is :- " + postArr.toString());
			
			return null;
		}
		
	}

}
