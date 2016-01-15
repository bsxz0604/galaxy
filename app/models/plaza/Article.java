package models.plaza;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import models.application.Application;
import models.common.Account;
import models.users.AccountAppId;
import play.db.ebean.Model;

@Entity
public class Article extends Model {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public String id;
	
	@Column(name="article_id", nullable=false)
	public String  article_id;
	
	public String article_name;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="author_id",  nullable=false)
	public Account author;
	public String user_id;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="app_id", nullable=false)
	public Application application;	

	
	public String author_name;
	public Boolean is_verified;
	public String author_image;
//	public String author_image_base64;
	
	@Column(name="article_content", length=512)
	public String content;
	
	public Integer image_number;
	public String image_url1; 
	public String image_url2;
	public String image_url3;
	public String image_url4;
	public String image_url5;
	public String image_url6;
	public String image_url7;
	public String image_url8;
	public String image_url9;
	
	@Column(name="create_time", nullable=false)
	public Date create_time;
	
	@Column(name="update_time", nullable=false)
	public Date update_time;
	
	@Column(name="last_reply")
	public Date last_reply;
	
	public String theme_id;
	
	@Column(name="theme_name", nullable=false)
	public String theme_name;
	
	public String theme_image;
	
	public Integer theme_class;
	
	public Boolean is_elite;
	
	public Boolean is_top;
	
	public Boolean is_public;
	
	public Boolean is_new_comments;
	
	public Integer comments_number;
	
	public Integer thumbs_number;  //up
	
	public Integer thumbs_number2;  //down
	
	public Boolean isSpecial;
	
	public static Finder<AccountAppId, Article> find = 
			new Finder<AccountAppId, Article>(AccountAppId.class, Article.class);
}
