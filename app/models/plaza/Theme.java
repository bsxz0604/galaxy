package models.plaza;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import models.application.Application;
import play.db.ebean.Model;

@Entity
public class Theme  extends Model{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	public String id;
	
	//@JsonIgnore
	@ManyToOne
	@JoinColumn(name="app_id", insertable=true, updatable=false, nullable=false)
	public Application application;
	
	@Column(name="theme_name")
	public String theme_name;
	
	public String theme_image;
	
	public String hot_people;
	
	@Transient
	public String theme_image_base64;
	
	public Integer theme_class; // 0: hot people; 1: other topics
	
	public Date last_update;
	
	public static Finder<String, Theme> find = 
			new Finder<String, Theme>(String.class, Theme.class);
}
