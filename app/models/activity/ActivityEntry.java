package models.activity;

import java.util.Date;

import javax.persistence.*;

import play.db.ebean.Model;


@Entity
public class ActivityEntry extends Model {

	private static final long serialVersionUID = 1L;
	@Id
	public String id;
	
	public String name;
	
	public String application;
	
	public String age;
    
	public String phone;

	public String mail;
	
	public String selfintroduce;
	
	public String career;
	
	public String education;
	
	public String body; 
	
	public String type;
    
	public String talent;
	
	public String habit;
	
	public String advantage;
	
	public String impress;

	public String jiabin;
	
	public String words;
	
	public String largepic;
	
	public String smallpic;
	
	public String video;
	
	@Column(nullable=false)
	public Date create_time;
	
	public String account_id;
	
	public int num;
	
	public String videoUrl;
		
	public static Finder<String, ActivityEntry> find = 
			new Finder<String, ActivityEntry>(String.class, ActivityEntry.class);
	
}
