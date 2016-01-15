package models.activity;

import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
public class ResultGroup {

	@Transient
	public String name;
	
	@Transient
	public Integer number;
}
