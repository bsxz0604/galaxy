package models.hotpeople;

import javax.persistence.*;

import play.db.ebean.Model;

import java.util.Date;

@Entity
public class HotPeoplePic extends Model {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	public String id;
	
//	public String online;
	
	public String name;
	
	public Integer num;
	
	public String picUrl;
	
	public Date createTime;
	
	public static Finder<String, HotPeoplePic> find = 
			new Finder<String, HotPeoplePic>(String.class, HotPeoplePic.class);

}
