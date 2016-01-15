package models.gift;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import models.application.Application;
import play.db.ebean.Model;

@Entity
public class Gift extends Model{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	public Integer id;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="app_id", insertable=false, updatable=false, nullable=false)
	public Application application;
	
	public String app_id;
	
	public String gift_image;
	
	public String gift_name;
	
	public Integer gift_free_time;
		
	public Integer price;
	
	public Integer effect;
	
	public static Finder<Integer, Gift> find = 
			new Finder<Integer, Gift>(Integer.class, Gift.class);
}
