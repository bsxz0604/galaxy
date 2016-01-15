package models.users;

import javax.persistence.Entity;

import play.db.ebean.Model;

@Entity
public class CharmSortResult extends Model {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String account_id;
	
	public String name;
	
	public String head_image;
	
	public Long search_id;
	
	public Integer sum_charm;
	
	public Boolean isVerified;
	
//	public Integer total_charm;
	
//	public Integer fan_number;

}
