package models.stock;

import javax.persistence.Entity;
import java.util.Date;

import play.db.ebean.Model;

@Entity
public class IncomeSortResult extends Model {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String account_id;
	
	public String name;
	
	public String head_image;
	
	public Long search_id;
	
	public double sum_income;
	
	public Boolean is_verified;
	
	public static Finder<String, IncomeSortResult> find = 
			new Finder<String, IncomeSortResult>(String.class, IncomeSortResult.class);
	
//	public Integer total_charm;
	
//	public Integer fan_number;

}