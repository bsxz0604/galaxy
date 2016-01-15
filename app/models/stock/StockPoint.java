package models.stock;

import java.util.Date;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import models.application.Application;
import models.common.Account;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class StockPoint extends Model{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	public String id;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="account_id",  nullable=false)
	public Account user;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="app_id",  nullable=false)
	public Application application;
	
	public Date last_visit;
	
	public Integer point;
	
	public static Finder<String, StockPoint> find =
			new Finder<String, StockPoint>(String.class, StockPoint.class);

}
