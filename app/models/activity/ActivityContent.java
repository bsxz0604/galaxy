package models.activity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Constraints;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.db.ebean.Model;

@Entity
public class ActivityContent extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public String id;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="activity_id", nullable=false)
	public Activity activity;
	
	@Column(name="activity_id", insertable=false, updatable=false)
	public String activity_id;
	
	public String img_url;
	
	public String introduction;
	
	public int max_selection = 1;
	
	@OneToMany
	public List<ActivityChoice> choiceList;
	
	public static Finder<String, ActivityContent> find = 
			new Finder<String, ActivityContent>(String.class, ActivityContent.class);
}
