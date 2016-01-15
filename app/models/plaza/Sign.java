package models.plaza;

import java.util.Date;

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
public class Sign extends Model{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	public String id;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="account_id",  nullable=false)
	public Account user;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="app_id",  nullable=false)
	public Application application;
	
	public Date last_visit;
	
	public Integer sign_charm;
	
	public static Finder<AccountAppId, Sign> find = 
			new Finder<AccountAppId, Sign>(AccountAppId.class, Sign.class);
}
