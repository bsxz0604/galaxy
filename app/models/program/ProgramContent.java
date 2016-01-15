package models.program;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.db.ebean.Model.Finder;

@Entity
public class ProgramContent {
	
	@Id
	public String id;
	
	@JsonIgnore
	@ManyToOne(optional=false)
	@JoinColumn(name="program_id", nullable=false)
	public Program program;
	
	//@Column(name="program_id", nullable=false)
	//public String program_id;
	
	@Column(nullable=false)
	public int type;
	
	@Column(nullable=false)
	public String content;
	
	public static Finder<String, ProgramContent> find = 
			new Finder<String, ProgramContent>(String.class, ProgramContent.class);
}
