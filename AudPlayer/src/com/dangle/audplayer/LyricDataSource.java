package com.dangle.audplayer;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class LyricDataSource {
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;
	private String[] allColumns = { DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_LYRICS_SONGID,
			DatabaseHelper.COLUMN_LYRICS_POSITION, DatabaseHelper.COLUMN_LYRICS_FROMTIME, DatabaseHelper.COLUMN_LYRICS_TOTIME, DatabaseHelper.COLUMN_LYRICS_LYRIC
	};
	private static final int COLUMN_ID_INDEX = 0;
	private static final int COLUMN_LYRICS__SONGID_INDEX = 1;
	private static final int COLUMN_LYRICS_POSITION_INDEX = 2;
	private static final int COLUMN_LYRICS_FROMTIME_INDEX = 3;
	private static final int COLUMN_LYRICS_TOTIME_INDEX = 4;
	private static final int COLUMN_LYRICS_LYRIC_INDEX = 5;
	
	public LyricDataSource(Context context){
		dbHelper = new DatabaseHelper(context);
	}
	
	public void open() throws SQLException {
		//database = dbHelper.getWritableDatabase();
		database = dbHelper.getDb();
	}
	
	public void close() {
		dbHelper.close();
	}
	
	public List<Lyric> getAllLyrics(long songId){
		List<Lyric> lyrics = new ArrayList<Lyric>();
		
		Cursor cursor = database.query(DatabaseHelper.TABLE_LYRICS, allColumns, DatabaseHelper.COLUMN_LYRICS_SONGID + " = ?", new String[] { Long.toString(songId) }, null, null, null);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			Lyric lyric = cursorToLyric(cursor);
			lyrics.add(lyric);
			cursor.moveToNext();
		}
		
		//make sure to close the cursor
		cursor.close();
		return lyrics;
	}
	
	private Lyric cursorToLyric(Cursor cursor) {
		Lyric lyric = new Lyric();
		lyric.setId(cursor.getLong(COLUMN_ID_INDEX));
		lyric.setSongId(cursor.getLong(COLUMN_LYRICS__SONGID_INDEX));
		lyric.setPosition(cursor.getLong(COLUMN_LYRICS_POSITION_INDEX));
		lyric.setFromTime(cursor.getString(COLUMN_LYRICS_FROMTIME_INDEX));		
		lyric.setToTime(cursor.getString(COLUMN_LYRICS_TOTIME_INDEX));
		lyric.setLyric(cursor.getString(COLUMN_LYRICS_LYRIC_INDEX));			
		return lyric;
	}	
}
