package com.example.tryfragments;

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
	public static final String[] CONTACT_IFO = new String[] {"foo", "bar"};
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
			}
		});
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(ARG_ID, mCurrentID);
		outState.putString(ARG_NAME, mCurrentName);
	}

}
