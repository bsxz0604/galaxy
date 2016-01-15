package models.users;

import javax.persistence.Entity;

import play.db.ebean.Model;

@Entity
public class follow extends Model {
	public String url;
}
