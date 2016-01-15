package models.eattoday;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import models.application.Application;

import com.fasterxml.jackson.annotation.JsonIgnore;

import models.common.Account;
import play.db.ebean.Model;

@Entity
public class EatEntry extends Model{
	
	private static final long serialVersionUID = 1L;

	@Id
	public String id;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "app_id", insertable = true, updatable = false, nullable = false)
	public Application application;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "account_id", insertable = true, updatable = false, nullable = false)
	public Account account;

	public String name;

	public int sex;

	public int age;

	public String city;
	
	public String province;

	public int num;
	
	public String phone;
	
	public Date create_time;

	public static Finder<String, EatEntry> find = new Finder<String, EatEntry>(
			String.class, EatEntry.class);

}
