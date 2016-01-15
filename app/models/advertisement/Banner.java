package models.advertisement;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import models.application.Application;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class Banner extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	public String id;
	
	@ManyToOne(optional=false)
	public Application app;
	
	@Constraints.Required
	@Column(nullable=false)
    public String url;
	
	/**
	 * type = 0 big banner, type = 1 small banner; type = 2 video banner;
	 */
	@Constraints.Required
	@Constraints.Min(0)
	@Constraints.Max(2)
	@Column(nullable=false)
	public Integer type;
	

	/**
	 * link to the banner if the banner is clicked.
	 */
	public String link;
	//  words    descripte the video
	public String words;  
	
	public static Finder<String, Banner> find = 
			new Finder<String, Banner>(String.class, Banner.class);
}
