package models.badge;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import models.application.Application;
import models.gift.Gift;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class Badge  extends Model{
	
private static final long serialVersionUID = 1L;
	
	@Id
	public String id;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="app_id", insertable=false, updatable=false, nullable=false)
	public Application application;
	
	public String app_id;
	
	public String badge_image;
	
	public String badge_grey_image;
	
	public String badge_class;
	
	public String badge_name;
	
	public Integer value;
	
	public static Finder<String, Badge> find = 
			new Finder<String, Badge>(String.class, Badge.class);
}

