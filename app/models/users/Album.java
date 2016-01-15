package models.users;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import models.application.Application;
import models.common.Account;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.db.ebean.Model;
import play.data.validation.Constraints;

@Entity
public class Album extends Model{

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
	
	@Constraints.Required
	@Column(name="img_url", nullable=false)
	public String imgUrl;
	
	public String description;
	
	public static Finder<String, Album> find = 
			new Finder<String, Album>(String.class, Album.class);
}
