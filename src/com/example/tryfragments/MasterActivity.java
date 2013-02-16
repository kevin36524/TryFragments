package com.example.tryfragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class MasterActivity extends FragmentActivity 
	implements ContactsFragment.OnContactsSelectedListener{
	
	public Cursor mCursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_master);
		
		ContactsFragment mContactsFragment = new ContactsFragment();
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mContactsFragment).commit();
	}

	@Override
	public void onContactSelected(int id, String name) {
		NotesFragment mNotesFragment = new NotesFragment();
		Bundle args = new Bundle();
		args.putInt(NotesFragment.ARG_ID, id);
		args.putString("name", name);
		mNotesFragment.setArguments(args);
		
		FragmentTransaction ftr = getSupportFragmentManager().beginTransaction();
		ftr.replace(R.id.fragment_container, mNotesFragment);
		ftr.addToBackStack(null);
		ftr.commit();
	}

}
