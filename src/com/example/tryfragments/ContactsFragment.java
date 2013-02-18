package com.example.tryfragments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

	private String[] projection = new String[] { NotesDBHelper.COLUMN_NAME, NotesDBHelper.COLUMN_ID};

	private SimpleCursorAdapter mAdapter;
	private Cursor mCursor;
	private SQLiteDatabase notesDB;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		notesDB = new NotesDBHelper(getActivity()).getReadableDatabase();
		
		if (NotesDBHelper.DATABASE_SET) {
			mCursor = notesDB.query(NotesDBHelper.TABLE_NOTES, projection, null, null, null, null, null);
			renderUI();
		} else {
			new generateDB().execute(new String[]{});	
		}
        //mCursor = getActivity().getContentResolver().query(Contacts.CONTENT_URI, projection , null, null, null);
	
	}
	
	public void renderUI () {
		 setHasOptionsMenu(true);
	     
	     String[] mDataColumns = { NotesDBHelper.COLUMN_NAME};
	     int[] mViewIDs = { android.R.id.text1 };
	  
	     mAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, mCursor, mDataColumns, mViewIDs, 0);
	     setListAdapter(mAdapter);
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
				String selection = NotesDBHelper.COLUMN_NAME + " LIKE ?";
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
		
		mCallBack.onContactSelected((int)(id),mCursor.getString(mCursor.getColumnIndex(NotesDBHelper.COLUMN_NAME)));
		
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
					tempObj.put(NotesDBHelper.COLUMN_ID, retCursor.getString(retCursor.getColumnIndex(Contacts._ID)));
					tempObj.put(NotesDBHelper.COLUMN_NAME, retCursor.getString(retCursor.getColumnIndex(Contacts.DISPLAY_NAME)));
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
						tempObj = postArr.getJSONObject(index);
						tempObj.put(NotesDBHelper.COLUMN_EMAIL,email_id);
						postArr.put(index, tempObj);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				emailCursor.moveToNext();
			}
			
			phCursor.moveToFirst();
			for (int i = 0 ; i < phCursor.getCount(); i++) {
				int index = phCursor.getInt(phCursor.getColumnIndex(Phone.CONTACT_ID));
				
				String phNumber = "";
				try {
					phNumber = phCursor.getString(phCursor.getColumnIndex(Phone.NUMBER));
					try {
						tempObj = postArr.getJSONObject(index);
						tempObj.put(NotesDBHelper.COLUMN_PH_NUMBER,phNumber);
						postArr.put(index, tempObj);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}	
				
				phCursor.moveToNext();
			}
			
			new NotesDBHelper(getActivity()).initialAdd(postArr);
			
			Log.i("KevinDebug","postArr is :- " + postArr.toString());
			
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			mCursor = notesDB.query(NotesDBHelper.TABLE_NOTES, projection, null, null, null, null, null);
			renderUI();
		}
		
	}

}
