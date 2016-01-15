package models.users;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import models.application.Application;
import models.common.Account;
import models.gift.Gift;
import play.db.ebean.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class AppProfileGift extends Model{
	
	private static final long serialVersionUID = 1L;

    @Id
	public String id;
    
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="account_id",  nullable=false)
	public Account account;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="app_id", nullable=false)
	public Application application;
	
	@Column(name="create_time", nullable=false)
	public Date create_time;		//operation_time
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="gift_id", nullable=false)
	public Gift gift;
	
	@Column(name="gift_number")
	public Integer gift_number;
	
	
	
	public static Finder<AccountAppId, AppProfileGift> find = 
			new Finder<AccountAppId, AppProfileGift>(AccountAppId.class, AppProfileGift.class);
	
}
