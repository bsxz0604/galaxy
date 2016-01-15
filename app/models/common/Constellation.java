package models.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class Constellation extends Model{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	public Integer id;
	
	@Column(unique=true, nullable=false)
	public String name;
	
	public String img_url;
	
	public static Finder<Integer, Constellation> find =
			new Finder<Integer, Constellation>(Integer.class, Constellation.class);
}
