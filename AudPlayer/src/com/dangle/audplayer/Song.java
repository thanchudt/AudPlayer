package com.dangle.audplayer;

public class Song {
	private long id;
	private String name;
	private String author;
	private String description;
	private String identification;	
	
	public long getId(){
		return id;
	}
	
	public void setId(long id){
		this.id = id;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getAuthor(){
		return author;
	}
	
	public void setAuthor(String author){
		this.author = author;
	}
	
	public String getDescription(){
		return description;
	}
	
	public void setDescription(String desc){
		this.description = desc;
	}
	
	public String getIdentification(){
		return identification;
	}
	
	public void setIdentification(String identification){
		this.identification = identification;
	}
		
	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return name;
	}
}
