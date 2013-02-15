package com.example.tryfragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class NotesFragment extends Fragment {

	public static final String ARG_POSITION = "position";
	public static final String[] CONTACT_IFO = new String[] {"foo", "bar"};
	public int mCurrentPosition = -1;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
		}
		return inflater.inflate(R.layout.notes_view, container, false);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		Bundle args = getArguments();
		if (args != null) {
			updateNotesView (args.getInt(ARG_POSITION));
		} else if (mCurrentPosition != -1) {
			updateNotesView(mCurrentPosition);
		}
	}
	
	public void updateNotesView (int position) {
		EditText et = (EditText) getActivity().findViewById(R.id.notes_content);
		try {
			et.setText(CONTACT_IFO[position]);
		} catch (Exception e) {
			et.setText("Default Text");
		}
		mCurrentPosition = position;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(ARG_POSITION, mCurrentPosition);
	}

}
