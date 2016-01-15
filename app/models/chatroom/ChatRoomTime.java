package models.chatroom;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import models.common.Account;
import play.db.ebean.Model;

@Entity
public class ChatRoomTime extends Model{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	public String  id;
	
	public String room_id;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="account_id",  nullable=false)
	public Account account;
	
	@Column(name="action_time", nullable=false)
	public Date action_time;		//action_time
	
	public String action;		// join or exit now
	
	public Integer send_gift_number;    // get the total number sent so far
	
	public static Finder<String, ChatRoomTime> find = 
			new Finder<String, ChatRoomTime>(String.class, ChatRoomTime.class);
}