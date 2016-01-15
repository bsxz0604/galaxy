package models.users;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import models.application.Application;
import models.common.Account;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class UserAddress extends Model {

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

    @Constraints.MaxLength(100)
    @Constraints.Required
    @Column(nullable=false)
    public String name;
    
    @Constraints.MaxLength(20)
    @Constraints.Required
    @Column(nullable=false)
    public String phone_number;
    
    @Constraints.MaxLength(10)
    @Constraints.Required
    @Column(nullable=false)
    public String code;
    
    @Constraints.MaxLength(255)
    @Constraints.Required
    @Column(nullable=false)
    public String address;
    
    public static Finder<AccountAppId, UserAddress> find = 
            new Finder<AccountAppId, UserAddress>(AccountAppId.class, UserAddress.class);
}
