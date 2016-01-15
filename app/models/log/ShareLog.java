package models.log;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class ShareLog extends Model {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    public Long id;
    
    public String domain;
    
    public String path;
    
    public String userId;
    
    public String shareBy;
    
    public Date create_time;
}
