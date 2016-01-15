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
import play.db.ebean.Model.Finder;

@Entity
public class CharmValue extends Model{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public String id;
	
	/*delete function occurs to be wrong*/
	/*
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="interester_id", nullable=false)
	public Account account;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="app_id", nullable=false)
	public Application application;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="interestee_id", nullable=false)
	public Account interestAccount;
	*/
	@Column(name="create_time", nullable=false)
	public Date create_time;		//operation_time	
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="sender_id", nullable=false)
	public Account sender;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="receiver_id", nullable=false)
	public Account receiver;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="app_id", nullable=false)
	public Application application;
	
	public Integer charm_value;   //single charm_value
	
	public Integer total_charm_value;   // used for sort
	
	public long milisecond;
	
	public static Finder<String, CharmValue> find = 
			new Finder<String, CharmValue>(String.class, CharmValue.class);
}
