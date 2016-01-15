package controllers.stock;

import play.Logger;
import play.libs.Json;
import controllers.AppController;
import play.mvc.Result;
import models.stock.IncomeSortResult;
import models.stock.StockRecommend;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StockRankController extends AppController{

	// day
	public Result DayList(Integer pageNumber, Integer sizePerPage){
		List<IncomeSortResult> list =  getList(PERIOD_TYPE.E_DAY, pageNumber, sizePerPage);
		return ok(Json.toJson(list));
	}
	
	// week
	public Result WeekList(Integer pageNumber, Integer sizePerPage){
		List<IncomeSortResult> list =  getList(PERIOD_TYPE.E_WEEK, pageNumber, sizePerPage);
		return ok(Json.toJson(list));
	}
	
	// month
	public Result MonthList(Integer pageNumber, Integer sizePerPage){
		List<IncomeSortResult> list =  getList(PERIOD_TYPE.E_MONTH, pageNumber, sizePerPage);
		return ok(Json.toJson(list));
	}
	
	public Result DayCommonList(Integer pageNumber, Integer sizePerPage){
		List<IncomeSortResult> list =  getCommonList(PERIOD_TYPE.E_DAY, pageNumber, sizePerPage);
		return ok(Json.toJson(list));
	}
	
	public Result WeekCommonList(Integer pageNumber, Integer sizePerPage){
		List<IncomeSortResult> list =  getCommonList(PERIOD_TYPE.E_WEEK, pageNumber, sizePerPage);
		return ok(Json.toJson(list));
	}
	
	public Result MonthCommonList(Integer pageNumber, Integer sizePerPage){
		List<IncomeSortResult> list =  getCommonList(PERIOD_TYPE.E_MONTH, pageNumber, sizePerPage);
		return ok(Json.toJson(list));
	}
	
	// get rank list
	private List<IncomeSortResult> getList(PERIOD_TYPE periodType, int pageNumber, int sizePerPage){

		Date startDate = getStartDate(periodType);
		
		if (null == startDate) {
		    return null;
		}
		
		Calendar endCalendar = Calendar.getInstance();
		
		endCalendar.set(Calendar.HOUR_OF_DAY, 9);
		endCalendar.set(Calendar.MINUTE, 15);
		endCalendar.set(Calendar.SECOND, 0);
		
	//	and open != '0'
		
		Date endDate = endCalendar.getTime();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		String sql = String.format("select app_profile.head_image, app_profile.name, app_profile.is_verified, app_profile.search_id, "
				+ "stock_recommend.account_id, sum(income)sum_income from stock_recommend "
		        + "join app_profile on app_profile.account_id = stock_recommend.account_id and "
		        + "app_profile.app_id = stock_recommend.app_id "
		        + "where (open !='0' or (open='0' and is_closed='1')) and stock_recommend.create_time >= '%s' and stock_recommend.create_time <= '%s' and is_verified = '1' "
		        + " group by stock_recommend.account_id"
		        + " order by sum_income desc", 
		        sdf.format(startDate), sdf.format(endDate));
		
		RawSql rawSql = RawSqlBuilder.parse(sql)
  			  .columnMapping("app_profile.head_image", "head_image")
  			  .columnMapping("app_profile.name", "name")
  			  .columnMapping("app_profile.is_verified", "is_verified")
  			  .columnMapping("app_profile.search_id", "search_id")
  			  .columnMapping("stock_recommend.account_id", "account_id")
  			  .columnMapping("sum(income)sum_income", "sum_income")
  			  .create();

  	       List<IncomeSortResult> list = Ebean.find(IncomeSortResult.class)
  	            .setRawSql(rawSql)
  	     		.setFirstRow(pageNumber*sizePerPage)
				.setMaxRows(sizePerPage)
    	        .findList();
//  Logger.info("getlist  "+session("userId")+" "+session("appId")+" "+list.size());
              return list;
              
	}
	
	private List<IncomeSortResult> getCommonList(PERIOD_TYPE periodType, int pageNumber, int sizePerPage){

		Date startDate = getStartDate(periodType);
		
		if (null == startDate) {
		    return null;
		}
		
		Calendar endCalendar = Calendar.getInstance();
		
		endCalendar.set(Calendar.HOUR_OF_DAY, 9);
		endCalendar.set(Calendar.MINUTE, 15);
		endCalendar.set(Calendar.SECOND, 0);
		
		Date endDate = endCalendar.getTime();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		String sql = String.format("select app_profile.head_image, app_profile.name, app_profile.is_verified, app_profile.search_id, "
				+ "stock_recommend.account_id, sum(income)sum_income from stock_recommend "
		        + "join app_profile on app_profile.account_id = stock_recommend.account_id and "
		        + "app_profile.app_id = stock_recommend.app_id "
		        + "where stock_recommend.create_time >= '%s' and stock_recommend.create_time <= '%s' and is_verified = '0' and (open !='0' or (open='0' and is_closed='1'))"
		        + " group by stock_recommend.account_id"
		        + " order by sum_income desc", 
		        sdf.format(startDate), sdf.format(endDate));
		
		RawSql rawSql = RawSqlBuilder.parse(sql)
  			  .columnMapping("app_profile.head_image", "head_image")
  			  .columnMapping("app_profile.name", "name")
  			  .columnMapping("app_profile.is_verified", "is_verified")
  			  .columnMapping("app_profile.search_id", "search_id")
  			  .columnMapping("stock_recommend.account_id", "account_id")
  			  .columnMapping("sum(income)sum_income", "sum_income")
  			  .create();

  	       List<IncomeSortResult> list = Ebean.find(IncomeSortResult.class)
  	            .setRawSql(rawSql)
  	     		.setFirstRow(pageNumber*sizePerPage)
				.setMaxRows(sizePerPage)
    	        .findList();
  	//     Logger.info("getlist  "+session("userId")+" "+session("appId")+" "+list.size());
              return list;
	}
	
	private Date getStartDate(PERIOD_TYPE periodType) {
	    Calendar startDate = Calendar.getInstance(); 
	    
	    switch (periodType) {
	    case E_DAY:
	        Date lastExchangeDate = getLastExchangeDate();
	        if (null == lastExchangeDate) {
	            return null;
	        }
	        startDate.setTime(lastExchangeDate);	        
	        break;
	        
	    case E_WEEK:
	        startDate.setFirstDayOfWeek(Calendar.MONDAY);
	        startDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
	        startDate.add(Calendar.DAY_OF_YEAR, -1);
	        startDate.set(Calendar.HOUR_OF_DAY, 15);
	        startDate.set(Calendar.MINUTE, 30);
	        startDate.set(Calendar.MILLISECOND, 0);
	        break;
	        
	    case E_MONTH:
	        startDate.set(Calendar.DAY_OF_MONTH, 1);
	        startDate.add(Calendar.DAY_OF_YEAR, -1);
	        startDate.set(Calendar.HOUR_OF_DAY, 15);
	        startDate.set(Calendar.MINUTE, 30);
	        startDate.set(Calendar.MILLISECOND, 0);
	        break;
	    }
	    
	    return startDate.getTime();
	    
	}
		
	private Date getLastExchangeDate() {
	    Calendar now = Calendar.getInstance();
	    
	    //during 0:00 to 9:15, no need to show daily rank
//	    if (now.get(Calendar.HOUR_OF_DAY) >= 0 && 
//	            (now.get(Calendar.HOUR_OF_DAY) < 9 || ( now.get(Calendar.HOUR_OF_DAY) == 9 && now.get(Calendar.MINUTE) <= 15))) {
//	        return null;
//	    }
	    
	    now.add(Calendar.DAY_OF_YEAR, -1);
	    now.set(Calendar.HOUR_OF_DAY, 15);
	    now.set(Calendar.MINUTE, 30);
	    now.set(Calendar.SECOND, 0);
	    
	    return now.getTime();
	}
	
	
	/*
	private Date getStartDate(Date now, PERIOD_TYPE periodType) {
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(now);
		int nowHour = startDate.get(Calendar.HOUR_OF_DAY);
		int nowMinute = startDate.get(Calendar.MINUTE);
		
		switch (periodType) {
		case E_DAY:
			//nowDate.add(Calendar.DATE, -1);
			if(nowHour<15){
				startDate.add(Calendar.DAY_OF_YEAR, -2); 
				startDate.set(Calendar.HOUR_OF_DAY, 15);
				startDate.set(Calendar.MINUTE, 0);
				startDate.set(Calendar.SECOND, 0);
				Date todayStart = startDate.getTime();
				
//				startDate.setTime(now);
//				startDate.add(Calendar.DAY_OF_YEAR, -1); 
//				startDate.set(Calendar.HOUR_OF_DAY, 9); 
//				startDate.set(Calendar.MINUTE, 30);
//				startDate.set(Calendar.SECOND, 0);
//				Date todayClose = startDate.getTime();
				// String TodayStart = String.valueOf(nowYear) + "-"+ String.valueOf(nowMonth)+"-"+String.valueOf(nowDay-2)+" "+"15:00:00";
			 //String todayClose = String.valueOf(nowYear) + "-"+ String.valueOf(nowMonth)+"-"+String.valueOf(nowDay-1)+" "+"9:00:00";
			 }
			else {
				startDate.add(Calendar.DAY_OF_YEAR, -1); 
				startDate.set(Calendar.HOUR_OF_DAY, 15);
				startDate.set(Calendar.MINUTE, 0);
				startDate.set(Calendar.SECOND, 0);
				Date todayStart = startDate.getTime();
				
//				startDate.setTime(now);
//				startDate.set(Calendar.HOUR_OF_DAY, 9); 
//				startDate.set(Calendar.MINUTE, 30);
//				startDate.set(Calendar.SECOND, 0);
//				Date todayClose = startDate.getTime();
			//	String TodayStart = String.valueOf(nowYear) + "-"+ String.valueOf(nowMonth)+"-"+String.valueOf(nowDay-1)+" "+"15:00:00";
			//	 String todayClose = String.valueOf(nowYear) + "-"+ String.valueOf(nowMonth)+"-"+String.valueOf(nowDay)+" "+"9:00:00";
			}
			break;
			
		case E_WEEK:
			//nowDate.add(Calendar.DATE, -7);
			if(nowHour<15){
				startDate.add(Calendar.DAY_OF_YEAR, -8); 
				startDate.set(Calendar.HOUR_OF_DAY, 15);
				startDate.set(Calendar.MINUTE, 0);
				startDate.set(Calendar.SECOND, 0);
				Date todayStart = startDate.getTime();
				
//				startDate.setTime(now);
//				startDate.add(Calendar.DAY_OF_YEAR, -1); 
//				startDate.set(Calendar.HOUR_OF_DAY, 9); 
//				startDate.set(Calendar.MINUTE, 30);
//				startDate.set(Calendar.SECOND, 0);
//				Date todayClose = startDate.getTime();
				// String TodayStart = String.valueOf(nowYear) + "-"+ String.valueOf(nowMonth)+"-"+String.valueOf(nowDay-2)+" "+"15:00:00";
			 //String todayClose = String.valueOf(nowYear) + "-"+ String.valueOf(nowMonth)+"-"+String.valueOf(nowDay-1)+" "+"9:00:00";
			 }
			else {
				startDate.add(Calendar.DAY_OF_YEAR, -7); 
				startDate.set(Calendar.HOUR_OF_DAY, 15);
				startDate.set(Calendar.MINUTE, 0);
				startDate.set(Calendar.SECOND, 0);
				Date todayStart = startDate.getTime();
				
//				startDate.setTime(now);
//				startDate.set(Calendar.HOUR_OF_DAY, 9); 
//				startDate.set(Calendar.MINUTE, 30);
//				startDate.set(Calendar.SECOND, 0);
//				Date todayClose = startDate.getTime();
			//	String TodayStart = String.valueOf(nowYear) + "-"+ String.valueOf(nowMonth)+"-"+String.valueOf(nowDay-1)+" "+"15:00:00";
			//	 String todayClose = String.valueOf(nowYear) + "-"+ String.valueOf(nowMonth)+"-"+String.valueOf(nowDay)+" "+"9:00:00";
			}
			break;
			
		case E_MONTH:
			//nowDate.add(Calendar.DATE,  -30);
			if(nowHour<15){
				startDate.add(Calendar.DAY_OF_YEAR, -31); 
				startDate.set(Calendar.HOUR_OF_DAY, 15);
				startDate.set(Calendar.MINUTE, 0);
				startDate.set(Calendar.SECOND, 0);
				Date todayStart = startDate.getTime();
				
//				startDate.setTime(now);
//				startDate.add(Calendar.DAY_OF_YEAR, -1); 
//				startDate.set(Calendar.HOUR_OF_DAY, 9); 
//				startDate.set(Calendar.MINUTE, 30);
//				startDate.set(Calendar.SECOND, 0);
//				Date todayClose = startDate.getTime();
				// String TodayStart = String.valueOf(nowYear) + "-"+ String.valueOf(nowMonth)+"-"+String.valueOf(nowDay-2)+" "+"15:00:00";
			 //String todayClose = String.valueOf(nowYear) + "-"+ String.valueOf(nowMonth)+"-"+String.valueOf(nowDay-1)+" "+"9:00:00";
			 }
			else {
				startDate.add(Calendar.DAY_OF_YEAR, -30); 
				startDate.set(Calendar.HOUR_OF_DAY, 15);
				startDate.set(Calendar.MINUTE, 0);
				startDate.set(Calendar.SECOND, 0);
				Date todayStart = startDate.getTime();
				
//				startDate.setTime(now);
//				startDate.set(Calendar.HOUR_OF_DAY, 9); 
//				startDate.set(Calendar.MINUTE, 30);
//				startDate.set(Calendar.SECOND, 0);
//				Date todayClose = startDate.getTime();
//				
			//	String TodayStart = String.valueOf(nowYear) + "-"+ String.valueOf(nowMonth)+"-"+String.valueOf(nowDay-1)+" "+"15:00:00";
			//	 String todayClose = String.valueOf(nowYear) + "-"+ String.valueOf(nowMonth)+"-"+String.valueOf(nowDay)+" "+"9:00:00";
			}
			break;
		}
		return startDate.getTime();
	}
	*/
	private enum PERIOD_TYPE {
		E_DAY,
		E_WEEK,
		E_MONTH
	}	
}
