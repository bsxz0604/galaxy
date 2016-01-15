package models.users;

import java.util.Date;

import play.db.ebean.Model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import models.common.Account;
import models.common.Constellation;
import models.application.Application;
import play.data.validation.*;


@Entity
@Table(uniqueConstraints = {
	    @UniqueConstraint(columnNames={"id", "search_id"})})
public class AppProfile extends Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public AccountAppId id;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="account_id", insertable=false, updatable=false)
	public Account account;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="app_id", insertable=false, updatable=false)
	public Application application;
	
	@Constraints.MaxLength(30)
	@Column(nullable=false)
	public String name;
	
	//It does not work here, need to manually add primary key and auto increment for search_id;
	@GeneratedValue(strategy = GenerationType.AUTO) 
	@Column(nullable=false)
	public Long search_id;
	
	public String head_image;
	
	@Transient
	public String head_imgbase64;
	
	public Integer sex;
	
	public Date birthday;
	
	@OneToOne
	public Constellation constellation;
	
	public String career;
	
	private String address;
	
	@Transient
	public String programContentId;
	
	@Transient
	public boolean isSelected;
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getAddress() {
		return this.address;
	}
	
	public String idiograph;
	
	public Boolean is_verified;
			
	public static Finder<AccountAppId, AppProfile> find = 
			new Finder<AccountAppId, AppProfile>(AccountAppId.class, AppProfile.class);
}
