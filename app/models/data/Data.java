package models.data;

import javax.persistence.Entity;

import play.db.ebean.Model;

@Entity
public class Data extends Model {
	public Integer value;
	public Integer num;
}