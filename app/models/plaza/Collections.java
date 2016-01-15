package models.plaza;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import models.common.Account;
import play.db.ebean.Model;

@Entity
public class Collections extends Model{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	public String id;
	
	@Column(name="article_id", nullable=false)
	public String  article_id;
	
	public String article_name;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="user_id",  nullable=false)
	public Account user;
	
	public static Finder<String, Collections> find = 
			new Finder<String, Collections>(String.class, Collections.class);
}
