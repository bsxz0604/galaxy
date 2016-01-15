package models.users;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import models.application.Application;
import models.common.Account;
import com.fasterxml.jackson.annotation.JsonIgnore;
import play.db.ebean.Model;

@Entity
public class Money extends Model{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public AccountAppId id;
	
	@Column(name="operation_time", nullable=false)
	public Date operation_time;		//operation_time
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="account_id", insertable=false, updatable=false, nullable=false)
	public Account account;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="app_id", insertable=false, updatable=false,  nullable=false)
	public Application application;
	
	public Integer money;
	
	public static Finder<AccountAppId, Money> find = 
			new Finder<AccountAppId, Money>(AccountAppId.class, Money.class);
}
