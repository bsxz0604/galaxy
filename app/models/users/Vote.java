package models.users;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import models.application.Application;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.data.format.Formats.DateTime;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class Vote extends Model{
	
	@Id
	public String account_id;
	
    public String app_id;
	
	@GeneratedValue(strategy = GenerationType.AUTO) 
	@Column(nullable=false)
	public Long search_id;
	
	public String vote_name;
	
	public String vote_img;
	
	public String vote_phone;
	
	public Integer vote_total;
	
	@Transient
	public Integer value;
	
	public static Finder<String, Vote> find = 
            new Finder<String, Vote>(String.class, Vote.class);
}
