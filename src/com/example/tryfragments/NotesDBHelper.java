package com.example.tryfragments;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NotesDBHelper extends SQLiteOpenHelper {

	public static final String TABLE_NOTES = "notesTable";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NOTES = "notes";
	
	private static final String DATABASE_NAME = "notes.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE = "create TABLE " + TABLE_NOTES + "(" 
			+ COLUMN_ID + " integer primary key, " + COLUMN_NOTES + " text not null);"; 

	
	public NotesDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		Log.i("KevinDebug", "Creating the DataBase");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(NotesDBHelper.class.getName(),
		        "Upgrading database from version " + oldVersion + " to "
		            + newVersion + ", which will destroy all old data");
		    db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
		    onCreate(db);
	}
	
	public void addNote (int id, String note) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		Log.i("KevinDebug", "Adding the note " + note + " to id " + id);
		
		values.put(COLUMN_ID, id);
		values.put(COLUMN_NOTES, note);
		
		db.insert(TABLE_NOTES, null, values);
		db.close();
	}
	
	public String getNote (int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		String retNotes = null;
		
		Cursor retCursor = db.query(TABLE_NOTES, new String [] {COLUMN_NOTES}, COLUMN_ID + "=?", new String [] {""+id}, null, null, null);
		
		retCursor.moveToFirst();

		Log.i("KevinDebug", "get the Note for id " + id);
		
		if (retCursor != null) { 
			try {
				retNotes = retCursor.getString(retCursor.getColumnIndex(COLUMN_NOTES));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		db.close();
		
		return retNotes;
	}
	
	public void updateNote(int id, String note) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		Log.i("KevinDebug", "Updating the note " + note + " to id " + id);
		
		values.put(COLUMN_NOTES, note);
		db.update(TABLE_NOTES, values, COLUMN_ID + "=?", new String [] {"" + id});
		db.close();
	}

}
