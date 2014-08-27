package com.dangle.audplayer;

import java.io.Serializable;

public class MyBundle implements Serializable {
	private static final long serialVersionUID = 1L;
	public int currentSongIndex = 0; 
	public boolean isShuffle = false;
	public boolean isRepeat = false;
	public boolean isSpeak = false;
	public int lyricIndex=0;
	public long currentDuration=0;
	public boolean isPause = false;
	public MyBundle(){		
	}
}
