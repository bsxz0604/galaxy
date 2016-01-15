package models.eattoday;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import models.application.Application;
import models.common.Account;



import com.fasterxml.jackson.annotation.JsonIgnore;

import play.db.ebean.Model;

@Entity
public class EatRecord extends Model {

	private static final long serialVersionUID = 1L;

	@Id
	public String id;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "app_id", insertable = true, updatable = false, nullable = false)
	public Application application;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "account_id", insertable = true, updatable = false, nullable = false)
	public Account account;

	public String questionId;

	public String answer;
	
	public boolean result;

	public boolean isShared;
	
	public int answerNum;
	
	public int num;
	
//	public Date create_time;

	public static Finder<String, EatRecord> find = new Finder<String, EatRecord>(
			String.class, EatRecord.class);
}
