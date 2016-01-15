package models.ticket;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import models.application.Application;
import models.shop.Shop;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.db.ebean.Model;

@Entity
public class Coupon extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	public String id;
	
	@JsonIgnore
	@ManyToOne(optional=false)
	@JoinColumn(name="app_id")
	public Application app;
	
	@Column(name="app_id", insertable=false, updatable=false)
	public String app_id;
			
	@Column(nullable=false)
	public String name;
	
	//type = 0, voucher; type = 1, discount;
	@Column(nullable=false)
	public int type;
	
	@Column(nullable=false)
	public Date create_time;
	
	//date type = 0, absolute date; date type = 1, expired in
	@Column(nullable=false)
	public int date_type;
	
	public Date available_time;
	
	@Column(nullable=false)
	public int expired_in_days;
	
	@Column(nullable=false)
	public String code;
	
	@Column(nullable=false)
	public String picture;
	
	@Column(nullable=false)
	public int batch;
	
	public static Finder<String, Coupon> find = 
	        new Finder<String, Coupon>(String.class, Coupon.class);
	
}
