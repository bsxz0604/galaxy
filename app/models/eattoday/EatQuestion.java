package models.eattoday;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class EatQuestion extends Model {

	private static final long serialVersionUID = 1L;

	@Id
	public String id;

	public String question;

	public String answer;

	public Date time;

	public static Finder<String, EatQuestion> find = new Finder<String, EatQuestion>(
			String.class, EatQuestion.class);

}
