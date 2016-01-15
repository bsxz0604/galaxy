package models.stock;

import java.util.Date;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.db.ebean.Model;
import models.application.Application;
import models.common.Account;

@Entity
public class StockEntry extends Model {

	private static final long serialVersionUID = 1L;
	@Id
	public String id;
		
//	@JsonIgnore
//	@ManyToOne(optional=false)
//	@JoinColumn(name="application_id",nullable=false)
//	public Application application;
//	
//	@JsonIgnore
//	@ManyToOne(optional=false)
//	@JoinColumn(name="account_id",nullable=false)
//	public Account account;
	
	public String account_id;
	public String application_id;
	
	public String name;
	
	public String sex;
	
	public String age;
	
	public String province;
	
	public String city;
	
	public String education;
	
	public String phone;
	
	public String mail;
	
	public String marriage;
	
	public String makeMoney;
	
	public String stockYear;
	
	public String selfIntroduce;
	
	public String views;
	
	public String experience;
	
	public Date  create_time;
	
	public Integer num;
		
	public static Finder<String, StockEntry> find = 
			new Finder<String, StockEntry>(String.class, StockEntry.class);
	
}
