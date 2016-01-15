package controllers.stock;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebean.RawSql.Sql;
import com.avaje.ebean.SqlUpdate;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.org.apache.bcel.internal.generic.NEW;

import controllers.AppController;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import scala.util.parsing.combinator.testing.Str;
import controllers.common.CodeGenerator;
import controllers.stock.StockManager;
import models.application.Application;
import models.common.Account;
import models.stock.StockRecommend;
import models.users.CharmValue;

public class StockInfoController extends AppController {

	public Result addRecommend() {

		StockManager.getInstance().addBegin();

		return ok();
	}

//public synchronized Result test(String gift_id, String star_id, Integer gift_number){
//	Account newaccount = Account.find.where()
//			.eq("id", session("userId")).findUnique();
//	Application newapplication = Application.find.where()
//			.eq("id", session("appId")).findUnique();
//	CharmValue latest_fan_charm = CharmValue.find.where()
//			.eq("receiver_id", star_id)
//			.setOrderBy("create_time desc").setFirstRow(0)
//			.setMaxRows(1).findUnique();
//   
//	CharmValue fan_charm = new CharmValue();
//	fan_charm.id = CodeGenerator.GenerateUUId();
//	fan_charm.sender = newaccount;
//	fan_charm.receiver = Account.find.byId(star_id);
//	fan_charm.application = newapplication;
//	fan_charm.create_time = new Date();
//	fan_charm.charm_value = gift_number;
//	if (latest_fan_charm != null)
//		{fan_charm.total_charm_value = latest_fan_charm.total_charm_value
//				+ gift_number;
//		 Logger.info("latest------"+latest_fan_charm.id);
//		 Logger.info("saveNew-++++++++++++++++++---"+fan_charm.id);
//		}
//	else
//		{
//		fan_charm.total_charm_value = gift_number;
//	 Logger.info("FirstOnesaveNew-++++++++++++++++++---"+fan_charm.id);
//		}
//	
//	Ebean.save(fan_charm);
//	return ok("congratulation~~");
//	
//}
//
//public synchronized Result test1(String gift_id, String star_id, Integer gift_number){
//
//	CharmValue latest_fan_charm = CharmValue.find.where()
//			.eq("receiver_id", star_id)
//			.setOrderBy("miliSecond desc").setFirstRow(0)
//			.setMaxRows(1).findUnique();
//	 
//	
//	Integer  total = 0;
//	Date date = new Date();
//    SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
//    long miliSecond = System.currentTimeMillis();
//    String newId = CodeGenerator.GenerateUUId();
//	if(latest_fan_charm == null) {total = gift_number;}
//	else{total = latest_fan_charm.total_charm_value+gift_number;}
//	String sqlString = String.format("INSERT INTO `galaxy`.`charm_value` (`id`, `create_time`," 
//            + "`sender_id`, `receiver_id`, `app_id`, `charm_value`, `total_charm_value`, `milisecond`)"
//            + " VALUES ('%s', '%s', '%s', '%s', "
//            + "'34536418-d6b2-451f-a400-4f0e284c9497', '1', '%s','%s');",newId,newDateFormat.format(date),
//            session("userId"),star_id,total,miliSecond);
//	SqlUpdate update = Ebean
//			.createSqlUpdate(sqlString);
//	Logger.info("latest------"+latest_fan_charm.id);
//	 Logger.info("saveNew-++++++++++++++++++---"+newId);
//	Ebean.execute(update);
//	return ok();
//}
//	public List<StockRecommend> foo() {
//		Date nowDate = new Date();
//		Calendar startDate = Calendar.getInstance();
//		startDate.setTime(nowDate);
//
//		startDate.add(Calendar.DAY_OF_YEAR, -1);
//		startDate.set(Calendar.HOUR_OF_DAY, 15);
//		startDate.set(Calendar.MINUTE, 30);
//		startDate.set(Calendar.SECOND, 0);
//		Date yesterdayClose = startDate.getTime();
//		startDate.setTime(nowDate);
//		// change open time
//		startDate.set(Calendar.HOUR_OF_DAY, 9);
//		startDate.set(Calendar.MINUTE, 15);
//		startDate.set(Calendar.SECOND, 0);
//		Date todayOpen = startDate.getTime();
//
//		return StockRecommend.find.where()
//				.eq("app_id", "7248d7fc-1fab-45a6-87fe-5b57e03ac425")
//				.between("create_time", yesterdayClose, todayOpen).findList();
//	}

//	public Result testUpdate() {
//		
//		for(int i =1; i<1000;i++){
//		
//			
//			double curr=new Random().nextDouble();
//			String sqlString = "UPDATE stock_recommend SET current=:curr where id =:id";
//					SqlUpdate update = Ebean.createSqlUpdate(sqlString);
//					update.setParameter("id", "cfbe9e4e-978e-42f0-91a1-fbcb4c228590");
//					update.setParameter("curr", curr);
//		  Ebean.execute(update);
//		}

//		
//		List<StockRecommend> todayList = foo();
//		HashMap<String, HashMap<String, StockRecommend>> m_stockMap = new HashMap<>();
//		for (StockRecommend recommend : todayList) {
//			if (!m_stockMap.containsKey(recommend.stock.id)) {
//				m_stockMap.put(recommend.stock.id,
//						new HashMap<String, StockRecommend>()); // first-level
//			}
//
//			m_stockMap.get(recommend.stock.id).put(recommend.id, recommend); // second-level
//		}
//
//		for (Entry<String, HashMap<String, StockRecommend>> entry : m_stockMap
//				.entrySet()) { // first-level traversal
//			try {
//				double open = 0.1;
//				double close = 0.2;
//				double income = 0.3;
//				double current = 0.4;
//				int num = 0;
//				String stockId = entry.getKey();
//				// Logger.info("whether weekEnd 0:yes  else: weekday"+
//				// open);
//				if (open == 0) {
//					continue;
//				} // weekend+++++++++++++ stock ---stop
//					// save the recommend
//
//				while (true) {
//					for (Entry<String, StockRecommend> recommend : entry
//							.getValue().entrySet()) {
//						Logger.info("existRecord----not the first time to fetch "
//								+ recommend.getValue().current);
//
//						StockRecommend stock = recommend.getValue();
//						int numAlready = stock.num;
//						double totalAlready = stock.total;
//						double incomeAlready = stock.income;
//
//						double totalIndex = totalAlready - incomeAlready
//								+ income;
//						StockRecommend stockRecommend = recommend.getValue();
//						stockRecommend.current = new Random().nextDouble();
////						Ebean.update(recommend.getValue());
//						stockRecommend.update();
//					}
//				}
//
//			} catch (Throwable e) {
//				Logger.info("update  error " + e);
//
//			}
//		}
//		return ok();
//	}
}
