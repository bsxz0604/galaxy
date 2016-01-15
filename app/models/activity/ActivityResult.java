package models.activity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import models.common.Account;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.db.ebean.Model;

@Entity
//@Table(uniqueConstraints = {
//	    @UniqueConstraint(columnNames={"activity_content_id", "account_id", "choice_id"})})
public class ActivityResult extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	public String id;
	
	@JsonIgnore
	@ManyToOne(optional=false)
	@JoinColumn(name="activity_content_id", nullable=false)
	public ActivityContent content;
	
	@Column(name="activity_content_id", insertable=false, updatable=false)
	public String activity_content_id;
	
	@JsonIgnore
	@ManyToOne(optional=false)
	@JoinColumn(name="account_id", nullable=false)
	public Account account;
	
	@Column(name="account_id", insertable=false, updatable=false)
	public String account_id;
	
	@JsonIgnore
	@ManyToOne(optional=false)
	@JoinColumn(name="choice_id", nullable=false)
	public ActivityChoice choice;
	
	@Column(name="choice_id", insertable=false, updatable=false)
	public String choice_id;
	
	@Column(name="create_time", nullable=false)
	public Date create_time;
	
	public static Finder<String, ActivityResult> find = 
			new Finder<String, ActivityResult>(String.class, ActivityResult.class);
}
