package models.ticket;

import java.util.Date;

import javax.persistence.Entity;

import play.db.ebean.Model;

@Entity
public class CouponMark extends Model {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public String id;
    public Date available_time;
    public int expired_in_days;
    public Date expired_time;
    public int type;
    public String value;
    public String code;
    public boolean is_used;
}
