package models.activity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.db.ebean.Model;

@Entity
public class ActivityChoice extends Model{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	public String id;
	
	@JsonIgnore
	@ManyToOne(optional=false)
	@JoinColumn(name="activity_content_id", nullable=false)
	public ActivityContent content;
	
	@Column(name="activity_content_id", insertable=false, updatable=false)
	public String activity_content_id;
	
	@Column(nullable=false)
	public int type;
	
	@Column(nullable=false)
	public String choice;
	
	public String description;
	
	public static Finder<String, ActivityChoice> find = 
			new Finder<String, ActivityChoice>(String.class, ActivityChoice.class);

}
