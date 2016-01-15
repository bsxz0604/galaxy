package models.plaza;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import models.application.Application;
import models.common.Account;
import models.users.AccountAppId;
import play.db.ebean.Model;

@Entity
public class MyTheme  extends Model {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public String  id;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="account_id",  nullable=false)
	public Account account;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="app_id", nullable=false)
	public Application application;	
	
	public String theme_id;
	
	@Column(name="theme_name", nullable=false)
	public String theme_name;
	
	public String hot_people;
	
	public String theme_image;
	
	public Integer theme_class;
	
	public Integer is_new;
	public Date latest_visit;
		
	public Date create_time;
	
	public static Finder<AccountAppId, MyTheme> find = 
			new Finder<AccountAppId, MyTheme>(AccountAppId.class, MyTheme.class);
}
