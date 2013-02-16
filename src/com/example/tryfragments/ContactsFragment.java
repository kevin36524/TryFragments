package com.example.tryfragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
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

}
