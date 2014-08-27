package com.dangle.audplayer;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class PlayerActivity extends Activity implements OnCompletionListener, SeekBar.OnSeekBarChangeListener{
	private ImageButton btnPlay;
	private ImageButton btnForward;
	private ImageButton btnBackward;
	private ImageButton btnNext;
	private ImageButton btnPrevious;
	private ImageButton btnPlaylist;
	private ImageButton btnSpeak;
	private ImageButton btnRepeat;
	private ImageButton btnShuffle;
	private SeekBar songProgressBar;
	private TextView songTitleLabel;
	private TextView songCurrentDurationLabel;
	private TextView songTotalDurationLabel;
	private ImageView songImage;
	// Media Player
	private  MediaPlayer _player = null;
		
	// Handler to update UI timer, progress bar etc,.
	private Handler mHandler = new Handler();;
	private Utilities utils;
	private SongDataSource _songDataSource;
	private List<Song> songList;
	private LyricDataSource _lyricDataSource;
	private List<Lyric> lyricList = null;
	private MyBundle myBundle = null;		
	private boolean isSeekTo = false;
	private boolean isUpdateLyric = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);
		
		_songDataSource = new SongDataSource(this);
		_songDataSource.open();
		songList = _songDataSource.getAllSongs();
		_songDataSource.close();									
				
		// All player buttons
		btnPlay = (ImageButton) findViewById(R.id.btnPlay);
		btnForward = (ImageButton) findViewById(R.id.btnForward);
		btnBackward = (ImageButton) findViewById(R.id.btnBackward);
		btnNext = (ImageButton) findViewById(R.id.btnNext);
		btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
		btnPlaylist = (ImageButton) findViewById(R.id.btnPlaylist);
		btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
		btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
		btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
		songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
		songTitleLabel = (TextView) findViewById(R.id.songTitle);
		songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
		songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
		songImage = (ImageView) findViewById(R.id.songImage);
		
		// Mediaplayer
		_player = new MediaPlayer();
		utils = new Utilities();
						
		// Listeners
		songProgressBar.setOnSeekBarChangeListener(this); // Important
		_player.setOnCompletionListener(this); // Important
		
		if (savedInstanceState != null) {
			myBundle = (MyBundle) savedInstanceState.getSerializable("myBundle");
        }
		
		if(myBundle == null) {
			myBundle = new MyBundle();			
        }else {
        	isSeekTo = true;
        	isUpdateLyric = true;
        }
		playSong(myBundle.currentSongIndex);
		
		/**
		 * Play button click event
		 * plays a song and changes button to pause image
		 * pauses a song and changes button to play image
		 * */
		btnPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				myBundle.isSpeak = false;
				// check for already playing
				if(_player != null)
				{
					if(_player.isPlaying()){
						pauseAudio();
					}else{
						playAudio();						
					}
				}else
					playSong(myBundle.currentSongIndex);
				
			}				
		});
		
		/**
		 * Forward button click event
		 * Forwards song specified seconds
		 * */
		btnForward.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {				
				if(lyricList == null)
					return;
				if(myBundle.lyricIndex + 1 >= 0 && myBundle.lyricIndex + 1 < lyricList.size()){
					myBundle.lyricIndex = myBundle.lyricIndex + 1;
					_player.seekTo((int) lyricList.get(myBundle.lyricIndex).getFromTimeAsMilliSecond());					
				}else{
					// forward to end position
					myBundle.lyricIndex = lyricList.size()-1;
					if(myBundle.lyricIndex >= 0)
						_player.seekTo((int) lyricList.get(myBundle.lyricIndex).getFromTimeAsMilliSecond());
					else
						_player.seekTo(_player.getDuration());
				}				
				myBundle.isSpeak = false;
				if(!_player.isPlaying()){
					playAudio();						
				}
				myBundle.isSpeak = true;
				showLyric(myBundle.lyricIndex);
			}
		});
		
		/**
		 * Backward button click event
		 * Backward song to specified seconds
		 * */
		btnBackward.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {				
				if(lyricList == null)
					return;
				if(myBundle.lyricIndex -1 >= 0 && myBundle.lyricIndex - 1 < lyricList.size()){
					myBundle.lyricIndex = myBundle.lyricIndex - 1;
					_player.seekTo((int) lyricList.get(myBundle.lyricIndex).getFromTimeAsMilliSecond());					
				}else{
					// forward to end position
					myBundle.lyricIndex = 0;
					if(myBundle.lyricIndex < lyricList.size())
						_player.seekTo((int) lyricList.get(myBundle.lyricIndex).getFromTimeAsMilliSecond());
					else
						_player.seekTo(0);
				}
				myBundle.isSpeak = false;
				if(!_player.isPlaying()){
					playAudio();						
				}
				myBundle.isSpeak = true;
				showLyric(myBundle.lyricIndex);
			}
		});
		
		/**
		 * Next button click event
		 * Plays next song by taking currentSongIndex + 1
		 * */
		btnNext.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				myBundle.isPause = false;
				lyricList = null;
				// check if next song is there or not
				if(myBundle.currentSongIndex < (songList.size() - 1)){
					myBundle.currentSongIndex = myBundle.currentSongIndex + 1;
				}else{
					// play first song
					myBundle.currentSongIndex = 0;
				}
				isUpdateLyric = true;				
				playSong(myBundle.currentSongIndex);
			}
		});
		
		/**
		 * Back button click event
		 * Plays previous song by currentSongIndex - 1
		 * */
		btnPrevious.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				myBundle.isPause = false;
				lyricList = null;
				if(myBundle.currentSongIndex > 0){
					myBundle.currentSongIndex = myBundle.currentSongIndex - 1;
				}else{
					// play last song
					myBundle.currentSongIndex = songList.size() - 1;									
				}
				isUpdateLyric = true;				
				playSong(myBundle.currentSongIndex);
			}
		});
		
		/**
		 * Button Click event for Repeat button
		 * Enables repeat flag to true
		 * */
		btnSpeak.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(lyricList == null)
					return;
				if(myBundle.lyricIndex >= 0 && myBundle.lyricIndex < lyricList.size()){
					_player.seekTo((int) lyricList.get(myBundle.lyricIndex).getFromTimeAsMilliSecond());					
				}
				myBundle.isSpeak = false;
				if(!_player.isPlaying()){
					playAudio();						
				}
				myBundle.isSpeak = true;				
			}
		});
		
		/**
		 * Button Click event for Repeat button
		 * Enables repeat flag to true
		 * */
		btnRepeat.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(myBundle.isRepeat){
					myBundle.isRepeat = false;
					btnRepeat.setImageResource(R.drawable.btn_repeat);
				}else{
					// make repeat to true
					myBundle.isRepeat = true;
					// make shuffle to false
					myBundle.isShuffle = false;
					btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
					btnShuffle.setImageResource(R.drawable.btn_shuffle);
				}	
			}
		});
		
		/**
		 * Button Click event for Shuffle button
		 * Enables shuffle flag to true
		 * */
		btnShuffle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(myBundle.isShuffle){
					myBundle.isShuffle = false;
					btnShuffle.setImageResource(R.drawable.btn_shuffle);
				}else{
					// make repeat to true
					myBundle.isShuffle= true;
					// make shuffle to false
					myBundle.isRepeat = false;
					btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
					btnRepeat.setImageResource(R.drawable.btn_repeat);
				}	
			}
		});
		
		/**
		 * Button Click event for Play list click event
		 * Launches list activity which displays list of songs
		 * */
		btnPlaylist.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(), PlayListActivity.class);
				startActivityForResult(i, 100);			
			}
		}); 			
	}
	
	/**
	 * Receiving song index from playlist view
	 * and play the song
	 * */
	@Override
    protected void onActivityResult(int requestCode,
                                     int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
        	lyricList = null;
        	myBundle.currentSongIndex = data.getExtras().getInt(PlayListActivity.SONG_EXTRA_MESSAGE_SONG_INDEX);
        	 // play selected song
            playSong(myBundle.currentSongIndex);
       }
    }
	
	/**
	 * Function to play a song
	 * @param songIndex - index of song
	 * */
	public void  playSong(int songIndex){
		// Play song
		try {
			mHandler.removeCallbacks(mUpdateTimeTask);
			resetPlayer();
			if(songIndex >= songList.size())
				return;
        	//int resID=getResources().getIdentifier(songList.get(songIndex).getIdentification(), "raw", getPackageName());
			_lyricDataSource = new LyricDataSource(this);
			_lyricDataSource.open();
			lyricList = _lyricDataSource.getAllLyrics(songList.get(songIndex).getId());
			_lyricDataSource.close();
			Collections.sort(lyricList, new LyricComparator());
        	Uri path = Uri.parse("android.resource://" + getPackageName() + "/raw/" + songList.get(songIndex).getIdentification());
        	
	   		//_player = MediaPlayer.create(this, resID);
	   		//_player.setOnCompletionListener(this); // Important
        	_player.setDataSource(this, path);
        	_player.prepare();
        	if(isSeekTo) {
        		_player.seekTo((int) myBundle.currentDuration);
        		isSeekTo = false;
        	}
        	_player.start();
        	long currentDuration = _player.getCurrentPosition();
        	updateLyric(currentDuration);
    		// Changing button image to pause button
    		btnPlay.setImageResource(R.drawable.btn_pause);
        	if(myBundle.isPause)
        		pauseAudio();
        	
			// Displaying Song title
			String songTitle = songList.get(songIndex).getName();
        	songTitleLabel.setText(songTitle);
			
        	// set Progress bar values
			songProgressBar.setProgress(0);
			songProgressBar.setMax(100);
			
			// Updating progress bar
			updateProgressBar();			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Update timer on seekbar
	 * */
	public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);        
    }	
	
	/**
	 * Background Runnable thread
	 * */
	private Runnable mUpdateTimeTask = new Runnable() {
	   public void run() {
		   if(!myBundle.isPause)
		   {
			   long totalDuration = _player.getDuration();
			   long currentDuration = _player.getCurrentPosition();
			   myBundle.currentDuration = currentDuration;
			   if(myBundle.isSpeak){
				   if(isUpdateLyric){
					   isUpdateLyric = false;
					   updateLyric(currentDuration);					   
				   }
				   if(lyricList!=null){
					   if(myBundle.lyricIndex >= 0 && myBundle.lyricIndex < lyricList.size()){
						   if(lyricList.get(myBundle.lyricIndex).getToTimeAsMilliSecond() <= currentDuration){
							   pauseAudio();
						   }
					   }
				   }
			   }else 
				   updateLyric(currentDuration);
			   // Displaying Total Duration time
			   songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
			   // Displaying time completed playing
			   songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));
			   
			   // Updating progress bar
			   int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
			   //Log.d("Progress", ""+progress);
			   songProgressBar.setProgress(progress);
		   }
		   
		   // Running this thread after 100 milliseconds
	       mHandler.postDelayed(this, 100);
	   }
	};
		
	private void updateLyric(long currentDuration){
		//lyricLabel.setText("");		
		songImage.setImageResource(R.drawable.adele);
		myBundle.lyricIndex = findLyric(currentDuration);
		showLyric(myBundle.lyricIndex);
	}

	private int findLyric(long currentDuration) {
		if(lyricList == null || lyricList.size() < 1)
			return -1;
		if(currentDuration <= lyricList.get(0).getToTimeAsMilliSecond()){
			return 0;
		}
		for(int i=1;i<lyricList.size()-1;i++){
			if(currentDuration > lyricList.get(i-1).getToTimeAsMilliSecond() && currentDuration <= lyricList.get(i).getToTimeAsMilliSecond()){
				return i;
			}
		}
		return (lyricList.size()-1);
	}
	
	void showLyric(int lyricIndex){
		if(myBundle.currentSongIndex >= songList.size() || lyricList == null)
			return;
		if(lyricIndex >= 0 && lyricIndex < lyricList.size())
		{		
			//lyricLabel.setText(lyricList.get(lyricIndex).getLyric());			
			Lyric lyric = lyricList.get(lyricIndex);
			String path= "@drawable/" + songList.get(myBundle.currentSongIndex).getIdentification() + String.format("%05d", lyric.getPosition());
			int imageResource = getResources().getIdentifier(path, null, getPackageName());
			Drawable res = getResources().getDrawable(imageResource);		
			songImage.setImageDrawable(res);
		}
	}
	
	private void resetPlayer() {
		if (_player != null) {
			 _player.stop();
			 _player.reset();			 
		 }
	}
	
	/**
	 * On Song Playing completed
	 * if repeat is ON play same song again
	 * if shuffle is ON play random song
	 * */
	@Override
	public void onCompletion(MediaPlayer arg0) {		
		if(myBundle.isSpeak)
			return;		
		lyricList = null;
		// check for repeat is ON or OFF
		if(myBundle.isRepeat){			
			// repeat is on play same song again
		} else if(myBundle.isShuffle){
			// shuffle is on - play a random song
			Random rand = new Random();
			myBundle.currentSongIndex = rand.nextInt((songList.size() - 1) - 0 + 1) + 0;			
		} else{
			// no repeat or shuffle ON - play next song
			if(myBundle.currentSongIndex < (songList.size() - 1)){
				myBundle.currentSongIndex = myBundle.currentSongIndex + 1;
			}else{
				// play first song
				myBundle.currentSongIndex = 0;
			}
		}
		playSong(myBundle.currentSongIndex);
	}
	
	@Override
	public void onDestroy(){
	 super.onDestroy();
	 	mHandler.removeCallbacks(mUpdateTimeTask);
	 	resetPlayer();
	 	_player.release();
	 }

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
		int totalDuration = _player.getDuration();
		int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
		
		// forward or backward to certain seconds
		_player.seekTo(currentPosition);
		
		// update timer progress again
		updateProgressBar();
		
		myBundle.isSpeak = false;
		if(!_player.isPlaying()){
			playAudio();						
		}		
	}	
	
	@Override
    protected void onSaveInstanceState(final Bundle outState) {
        outState.putSerializable("myBundle", myBundle);
    }
	
	private void pauseAudio() {
		myBundle.isPause = true;
		_player.pause();
		// Changing button image to play button
		btnPlay.setImageResource(R.drawable.btn_play);
	}
	
	private void playAudio() {
		myBundle.isPause = false;
		// Resume song						
		_player.start();
		// Changing button image to pause button
		btnPlay.setImageResource(R.drawable.btn_pause);
	}	
}
