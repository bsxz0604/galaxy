package models.users;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.omg.CORBA.PUBLIC_MEMBER;

import models.activity.ActivityEntry;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class AnnouncementFeiMei extends Model{
	
	public static final long serialVersionUID = 1L;
	
	@Id
	public String id;
	
	public String name;
	
	public String accountId;
	public String appId;
	
	public String sex;
    public String headImage;
	public String phone;

	public String mail;
	
	public String career;
	
	public String words;
	public String address;
	
	@Column(nullable=false)
	public Date create_time;

	public boolean isCancelled;
	public Date bornDate;
		
	public static Finder<String, AnnouncementFeiMei> find = 
			new Finder<String, AnnouncementFeiMei>(String.class, AnnouncementFeiMei.class);
	
}

