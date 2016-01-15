/** Author: Michael Wang
 * Date: 2015-06-27
 * Description: Application entity. This entity is used to store application information
 * Note: Ebean will auto generate getter and setter for the entity.
 */
package models.application;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class Application extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public String id;
	
	@Constraints.MinLength(2)
	@Constraints.MaxLength(100)
	@Constraints.Required	
	@Column(unique=true,nullable=false)
	public String appName;
	
	public static Finder<String, Application> find = 
			new Finder<String, Application>(String.class, Application.class);
	

}
