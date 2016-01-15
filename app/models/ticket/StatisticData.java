package models.ticket;

import java.util.Date;

import javax.persistence.Entity;

import play.db.ebean.Model;

@Entity
public class StatisticData extends Model {
    
    public Integer value;

}
