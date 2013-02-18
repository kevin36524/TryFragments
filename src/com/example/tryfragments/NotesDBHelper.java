package com.example.tryfragments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
	public static final String COLUMN_PH_NUMBER = "phNum";
	public static final String COLUMN_EMAIL = "email";
	public static final String COLUMN_NAME = "name";
	public static Boolean DATABASE_SET = true;
	
	private static final String DATABASE_NAME = "notes.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE = "create TABLE " + TABLE_NOTES + "(" 
			+ COLUMN_ID + " long primary key, " + COLUMN_NAME + " text, " +  COLUMN_EMAIL + " text, " + COLUMN_PH_NUMBER + " text, "  + COLUMN_NOTES + " text);"; 

	
	public NotesDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		Log.i("KevinDebug", "Creating the DataBase");
		DATABASE_SET = false;
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
	
	public void initialAdd (JSONArray dbArray) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values;
		JSONObject tempObj;
		
		for (int i = 0; i < dbArray.length(); i++) {
			values = new ContentValues();
		   try {
			   tempObj = dbArray.getJSONObject(i);
			   JSONArray tempNameArr = tempObj.names();
			   String key;
			   
			   for (int j=0; j < tempNameArr.length(); j++) {
				   key = tempNameArr.getString(j);
				   
				   if (key.equals(COLUMN_ID)){
					   values.put(key, tempObj.getLong(key));
				   } else {
					   values.put(key, tempObj.getString(key));
				   }
			   }
		   } catch (JSONException e) {
			   e.printStackTrace();
		   }	
		   
		   if (values.size() > 1) {
			   db.insert(TABLE_NOTES, null, values);
		   }
		   
		}
		
		db.close();
		DATABASE_SET = true;
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
	
	public void updateNote(long id, String note) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		Log.i("KevinDebug", "Updating the note " + note + " to id " + id);
		
		values.put(COLUMN_NOTES, note);
		db.update(TABLE_NOTES, values, COLUMN_ID + "=?", new String [] {"" + id});
		db.close();
	}

}
