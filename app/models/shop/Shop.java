package models.shop;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Id;

import models.application.Application;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.db.ebean.Model;

@Entity
public class Shop extends Model {

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
	
	public String address;
	
	public String ibeaconSn;

	public static Finder<String, Shop> find = 
			new Finder<String, Shop>(String.class, Shop.class);
}
