package models.ticket;

import java.util.Date;

import javax.persistence.Entity;

import play.db.ebean.Model;

@Entity
public class DetailData extends Model {
    public String time;
    public Integer value;
}
