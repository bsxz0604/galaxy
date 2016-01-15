package models.stock;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Entity;
import javax.persistence.Transient;

import models.application.Application;
import play.db.ebean.Model.Finder;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.db.ebean.Model;

@Entity
public class Stock extends Model{
	private static final long serialVersionUID = 1L;
	
	 @Id
	 public String id;

	 public String stockName;
		
	 public String spell;
	
	 @Transient
	 public int num;
	 
	 public String pinyin;
	
   
	
	public static Finder<String, Stock> find = 
			new Finder<String, Stock>(String.class, Stock.class);
}
