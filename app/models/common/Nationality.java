package models.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;
import play.data.validation.*;

@Entity
public class Nationality extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public Integer id;
	
	@Constraints.Required
	@Column(unique=true, nullable=false)
	public String nation;
	
	public String short_name;
	
	public static Finder<Integer, Nationality> find =
			new Finder<Integer, Nationality>(Integer.class, Nationality.class);
}
