package models.hotpeople;

import javax.persistence.*;

import play.db.ebean.Model;

import java.util.Date;

@Entity
public class HotPeopleMp3 extends Model {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	public String id;
	
//	public String online;
	
	public String name;
	
	public Integer num;
	
	public String musicUrl;
	
	public String mainUrl;
	
	public Date createTime;
	
	public static Finder<String, HotPeopleMp3> find = 
			new Finder<String, HotPeopleMp3>(String.class, HotPeopleMp3.class);

}

