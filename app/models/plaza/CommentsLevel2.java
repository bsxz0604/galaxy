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
public class CommentsLevel2
extends Model {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@Id
	public String id;
	
	@Column(name="article_id", nullable=false)
	public String  article_id;
	
	public String comments;
	
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
	public String author_image;
	
	@Column(name="comments_content", length=2000)
	public String content;
	
	@Column(name="create_time", nullable=false)
	public Date create_time;
	
	@Column(name="update_time", nullable=false)
	public Date update_time;
	
	@Column(name="last_reply")
	public Date last_reply;
	
	public Boolean is_hidden;

	public Boolean is_top;
	
	
	public static Finder<AccountAppId, CommentsLevel2> find = 
			new Finder<AccountAppId, CommentsLevel2>(AccountAppId.class, CommentsLevel2.class);
}
