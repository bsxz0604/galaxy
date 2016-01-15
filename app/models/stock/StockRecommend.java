package models.stock;

import play.db.ebean.Model;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import models.common.Account;
import models.application.Application;
import models.stock.Stock;
import java.util.Date;


@Entity
public class StockRecommend extends Model{

	private static final long serialVersionUID = 1L;
	
	@Id
	public String id;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="account_id", nullable=false)
	public Account accountId;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="app_id", nullable=false)
	public Application application;
	
	public Date createTime;
	
	@ManyToOne
	@JoinColumn(name="stock", nullable=false)
	public Stock stock;
	
	public double open;
	
	public double close;
	
	public double current;
	
	public double income;
	
	public double averageIncome;
	
	public int num;
	
	public double total;
	
	public int up;
	public int down;
	
	public boolean isClosed;
	
	public static Finder<String, StockRecommend> find =
			new Finder<String, StockRecommend>(String.class, StockRecommend.class);
}
			
