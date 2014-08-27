package com.dangle.audplayer;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SongDataSource {
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;
	private String[] allColumns = { DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_SONGS_NAME,
			DatabaseHelper.COLUMN_SONGS_AUTHOR, DatabaseHelper.COLUMN_SONGS_DESCRIPTION, DatabaseHelper.COLUMN_SONGS_IDENTIFICATION 
	};
	private static final int COLUMN_ID_INDEX = 0;
	private static final int COLUMN_SONGS_NAME_INDEX = 1;
	private static final int COLUMN_SONGS_AUTHOR_INDEX = 2;
	private static final int COLUMN_SONGS_DESCRIPTION_INDEX = 3;
	private static final int COLUMN_SONGS_IDENTIFICATION_INDEX = 4;
	
	public SongDataSource(Context context){
		dbHelper = new DatabaseHelper(context);
	}
	
	public void open() throws SQLException {
		//database = dbHelper.getWritableDatabase();
		database = dbHelper.getDb();
	}
	
	public void close() {
		dbHelper.close();
	}
	
	public List<Song> getAllSongs(){
		List<Song> songs = new ArrayList<Song>();
		
		Cursor cursor = database.query(DatabaseHelper.TABLE_SONGS, allColumns, null, null, null, null, null);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			Song song = cursorToSong(cursor);
			songs.add(song);
			cursor.moveToNext();
		}
		
		//make sure to close the cursor
		cursor.close();
		return songs;
	}
	
	private Song cursorToSong(Cursor cursor) {
		Song song = new Song();
		song.setId(cursor.getLong(COLUMN_ID_INDEX));
		song.setName(cursor.getString(COLUMN_SONGS_NAME_INDEX));
		song.setAuthor(cursor.getString(COLUMN_SONGS_AUTHOR_INDEX));
		song.setDescription(cursor.getString(COLUMN_SONGS_DESCRIPTION_INDEX));
		song.setIdentification(cursor.getString(COLUMN_SONGS_IDENTIFICATION_INDEX));			
		return song;
	}	
}
