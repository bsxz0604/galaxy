package models.users;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

//import com.fasterxml.jackson.annotation.JsonIgnore;

//import models.application.Application;
//import models.common.Account;
import play.db.ebean.Model;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames={"interester_id", "app_id", "interestee_id"})})
public class InterestFan extends Model {

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
	
	@Column(name="interester_id", nullable=false)//, insertable=false, updatable=false)
	public String interesterId;
	
	@Column(name="app_id", nullable=false)//, insertable=false, updatable=false)
	public String appId;
	
	@Column(name="interestee_id", nullable=false)//, insertable=false, updatable=false)
	public String interesteeId;
	
	
	public static Finder<String, InterestFan> find = 
			new Finder<String, InterestFan>(String.class, InterestFan.class);

}
