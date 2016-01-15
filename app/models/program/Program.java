package models.program;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import models.application.Application;
import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class Program extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public String id;
	
	//@ManyToOne
	@Column(name="app_id", nullable=false)
	//public Application app;
	public String app_id;
	
	@Constraints.Required	
	@Column(nullable=false)
	public String title;
	
	@Column(nullable=false)
	public Date show_time;
	
	public boolean is_online;
	
	@OneToMany
	public List<ProgramContent> contentList;
	
	public static Finder<String, Program> find = 
			new Finder<String, Program>(String.class, Program.class);
}
