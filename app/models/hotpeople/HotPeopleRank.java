package models.hotpeople;

import javax.persistence.*;

import play.db.ebean.Model;

@Entity
public class HotPeopleRank extends Model {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	public Integer id;
	
//	public String online;
	
	public String name;
	
	public String hotUrl;
	
	public String picUrl;
	
	public String words;
	
	
	public static Finder<Integer, HotPeopleRank> find = 
			new Finder<Integer, HotPeopleRank>(Integer.class, HotPeopleRank.class);

}

