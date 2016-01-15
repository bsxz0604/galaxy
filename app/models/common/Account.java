/** Author: Michael Wang
 * Date: 2015-06-26
 * Description: Account entity. 
 * Note: Ebean will auto generate getter and setter for the entity.
 */
package models.common;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.persistence.*;

import controllers.common.CodeGenerator;
import play.db.ebean.Model;
import play.data.validation.*;

@Entity
public class Account extends Model {

	/**
	 * Only need to change this value if the class has changed its structure and
	 * you do not want to back compatible to the old version.
	 */
	private static final long serialVersionUID = 1L;

	/*It must be a uuid*/
	@Id
	public String id;
	
	@Constraints.MinLength(5)
	@Constraints.MaxLength(100)
	@Constraints.Required
	@Column(unique=true,nullable=false)
	public String username;
	
	/**password is not required if type is not 0*/
	public String password;
	
	/**
	 * type = 0, normal login; type = 1, weixin; type = 2, qq; type = 3, weibo 
	 */
	@Constraints.Min(0)
	@Constraints.Max(3)
	@Constraints.Required
	@Column(nullable=false)
	public Integer type = 0;
	
	@Column(nullable=false)
	public Date create_time;
	
	/**
	 * role = 0, user; role = ?, administrator;
	 */
	@Column(nullable=false)
	public Integer role = 0;
	
	public static Finder<String, Account> find = 
			new Finder<String, Account>(String.class, Account.class);
	
	public boolean Authenticate(String username, String password) {
		try {
			if (this.username == username &&
					this.password == CodeGenerator.GenerateMD5(password)) {
				return true;
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
}
