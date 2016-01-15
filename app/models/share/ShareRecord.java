package models.share;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import models.application.Application;
import models.common.Account;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.db.ebean.Model;

@Entity
public class ShareRecord extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	public String id;
	
	@JsonIgnore
	@ManyToOne(optional=false)
	@JoinColumn(name="app_id", nullable=false)
	public Application app;
	@Column(name="app_id", insertable=false, updatable=false)
	public String appId;
	
	@JsonIgnore
	@ManyToOne(optional=false)
	@JoinColumn(name="user_id", nullable=false)
	public Account account;
	
	@Column(name="user_id", insertable=false, updatable=false)
	public String userId;

	public String share_url;
	
	public Date create_time;
	
	public static Finder<String, ShareRecord> find = 
			new Finder<String, ShareRecord>(String.class, ShareRecord.class);

}
