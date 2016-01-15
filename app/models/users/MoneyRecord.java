package models.users;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import models.application.Application;
import models.common.Account;
import play.db.ebean.Model;

@Entity
public class MoneyRecord  extends Model{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public Long  id;
	
	@Column(name="create_time", nullable=false)
	public Date create_time;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="account_id",  nullable=false)
	public Account account;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="app_id", nullable=false)
	public Application application;
	
	public String action;
	
	public Integer money;
	
	public static Finder<AccountAppId, Money> find = 
			new Finder<AccountAppId, Money>(AccountAppId.class, Money.class);
}

