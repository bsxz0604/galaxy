package models.activity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import models.application.Application;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.data.validation.Constraints;
import play.db.ebean.Model;


@Entity
public class Activity extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	public String id;
	
	@JsonIgnore
	@ManyToOne(optional=false)
	@JoinColumn(name="app_id")
	public Application app;
	
	@Column(name="app_id", insertable=false, updatable=false)
	public String app_id;
	
	@Constraints.Required
	@Column(name="start_time", nullable=false)
	public Date startTime;
	
	@Constraints.Required
	@Column(name="end_time", nullable=false)
	public Date endTime;

	public String title;

	@Column(name="img_url")
	public String imgUrl;
	
	@Column(name="link")
	public String link;
	
	@Column(name="repeatable", nullable=false)
	public Boolean repeatable = false;
	
	//if type = 0, it will check whether it is voted, else it will not check.
	@Column(nullable=false)
	public Integer type = 0;
	
	@Transient
	public int status;
	
	@Transient
	public String contentId;
	
	public String web;
	//@OneToMany
	//public List<ActivityContent> contentList;
	
	/**
	 * Returns 0 if it is started; 1 if it is not started, 2 if it is end.
	 * @return
	 */
	public int getStatus() {
		Date now = new Date();
		
		if (now.before(startTime)) {
			return ACTIVITY_STATUS.E_NOTSTART.ordinal();
		}
		
		if ((now.equals(startTime) || now.after(startTime)) && 
				(now.before(endTime) || now.equals(endTime))) {
			return ACTIVITY_STATUS.E_STARTING.ordinal();
		}
		
		return ACTIVITY_STATUS.E_ENDED.ordinal();
	}
	
	private enum ACTIVITY_STATUS {
		E_STARTING,
		E_NOTSTART,
		E_ENDED
	}
	
	public static Finder<String, Activity> find = 
			new Finder<String, Activity>(String.class, Activity.class);

}
