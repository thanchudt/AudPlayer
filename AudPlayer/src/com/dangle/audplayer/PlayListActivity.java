package com.dangle.audplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;

public class PlayListActivity extends Activity {
	public final static String SONG_EXTRA_MESSAGE_SONG_INDEX = "com.dangle.audplayer.SONG_EXTRA_MESSAGE_SONG_INDEX";	
	private SongDataSource _songDataSource;
	private List<Song> songList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist);
		
		_songDataSource = new SongDataSource(this);
		_songDataSource.open();
		songList = _songDataSource.getAllSongs();
		_songDataSource.close();
		
		ListView lvSong = (ListView) findViewById(R.id.lvSong);			    

		// Adding menuItems to ListView
		ArrayList<HashMap<String, String>> songListData = new ArrayList<HashMap<String, String>>();
		for(int i=0;i<songList.size();i++)
		{
			HashMap<String, String> song = new HashMap<String, String>();
			String songName = songList.get(i).toString();
			song.put("songTitle", songName);
			songListData.add(song);
		}
		
		ListAdapter adapter = new SimpleAdapter(this, songListData,
				R.layout.playlist_item, new String[] { "songTitle" }, new int[] {
						R.id.songTitle });

		lvSong.setAdapter(adapter);
											    
	    lvSong.setOnItemClickListener(new OnItemClickListener()
	    {
	        @Override
			public void onItemClick(AdapterView<?> parent, final View view,
			          int position, long id) {
	        	int songIndex = position;				
				Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
				intent.putExtra(SONG_EXTRA_MESSAGE_SONG_INDEX, songIndex);
				setResult(100, intent);
				// Closing PlayListView
				finish();;
			}
	    });	    	
	}
}
