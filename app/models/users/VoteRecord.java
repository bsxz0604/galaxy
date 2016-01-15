package models.users;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class VoteRecord extends Model{
	
	@Id
	public String user_id;
	
	public String app_id;
	
	public Integer vote_num;
	
	public Date vote_time;
	
	public static Finder<String, VoteRecord> find = 
            new Finder<String, VoteRecord>(String.class, VoteRecord.class);
}
