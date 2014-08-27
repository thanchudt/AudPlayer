package com.dangle.audplayer;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.net.ParseException;

public class Lyric {
	private long id;
	private long songId;
	private long position;
	private String fromTime;
	private String toTime;	
	private String lyric;
	
	public long getId(){
		return id;
	}
	
	public void setId(long id){
		this.id = id;
	}
	
	public long getSongId(){
		return songId;
	}
	
	public void setSongId(long id){
		this.songId = id;
	}
	
	public long getPosition(){
		return position;
	}
	
	public void setPosition(long pos){
		this.position = pos;
	}
		
	public String getFromTime(){
		return fromTime;
	}
	
	public long getFromTimeAsMilliSecond(){
		return stringToMillisecond(fromTime);
	}
	
	public void setFromTime(String time){
		this.fromTime = time;
	}
	
	public String getToTime(){
		return toTime;
	}
	
	public long getToTimeAsMilliSecond(){
		return stringToMillisecond(toTime);
	}
	
	public void setToTime(String time){
		this.toTime = time;
	}
	
	public String getLyric(){
		return lyric;
	}
	
	public void setLyric(String lyric){
		this.lyric = lyric;
	}
	
	private long stringToMillisecond(String str){		
		long milli = 0;
		try {   
			String strTime = str.split(" ")[1];
		    String[] arr1 = strTime.split(":");
		    long hour = Long.parseLong(arr1[0]);
		    long minute = Long.parseLong(arr1[1]);
		    String[] arr2 = arr1[2].split("\\.");
		    long second = Long.parseLong(arr2[0]);
		    long millisecond = Long.parseLong(arr2[1]) * 10;
		    milli = 1000*(hour*3600 + minute*60 + second) + millisecond;
		}catch (Exception e)
		{
			e.printStackTrace();
		}
		return milli;
	}
}
