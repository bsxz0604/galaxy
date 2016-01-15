package models.shake;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import models.common.Account;
import models.shop.Shop;
import models.ticket.Coupon;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.db.ebean.Model;

@Entity
public class ShakeRecord extends Model{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	public String id;

	@JsonIgnore
	@ManyToOne(optional=false)
	@JoinColumn(name="shop_id", nullable=false)
	public Shop shop;
	
	@Column(name="shop_id", insertable=false, updatable=false)
	public String shop_id;
	
	@JsonIgnore
	@ManyToOne(optional=false)
	@JoinColumn(name="user_id", nullable=false)
	public Account account;
	
	@Column(name="user_id", insertable=false, updatable=false)
	public String user_id;

	public Date create_time;
	
	public static Finder<String, ShakeRecord> find = 
			new Finder<String, ShakeRecord>(String.class, ShakeRecord.class);

}
