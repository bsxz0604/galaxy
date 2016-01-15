package models.ticket;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import play.db.ebean.Model;

@Entity
public class CouponDetail extends Model {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    @Id
    public String id;
    
    public String value;
    
    public int type;
    
    public Date available_time;
    
    public int expired_in_days;
    
    @Transient
    public Date expired_date;
    
    public String code;
    
    public String shop_name;
    
    public boolean is_used;

}
