package models.ticket;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import models.common.Account;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.db.ebean.Model;

@Entity
public class CouponRecord extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	public String id;
	
	@JsonIgnore
	@ManyToOne(optional=false)
	@JoinColumn(name="user_id")
	public Account account;
	
	@Column(name="user_id", insertable=false, updatable=false)
	public String user_id;
	
	@JsonIgnore
	@ManyToOne(optional=false)
	@JoinColumn(name="coupon_id")
	public Coupon coupon;
	
	@Column(name="coupon_id", insertable=false, updatable=false)
	public String coupon_id;
	
	@Column(nullable=false)
	public boolean is_drawed;
	
	@Column(nullable=false)
	public boolean is_used;
	
	public Date create_time;
	
	public static Finder<String, CouponRecord> find = 
	        new Finder<String, CouponRecord>(String.class, CouponRecord.class);
}
