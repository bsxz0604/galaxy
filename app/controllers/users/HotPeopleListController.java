package controllers.users;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import models.users.CharmSortResult;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;

import controllers.AppController;

public class HotPeopleListController extends AppController{
    private static HotPeopleListController m_instance = null;
    
    public static HotPeopleListController getInstance() {
        if (null == m_instance) {
            m_instance = new HotPeopleListController();
        }
        
        return m_instance;
    }
    
    private static class ListData {
        public Date date = null;
        public List<CharmSortResult> list = null;
    }
        
    private static HashMap<PERIOD_TYPE, ListData> m_manTime = new HashMap<PERIOD_TYPE, ListData>();
    private static HashMap<PERIOD_TYPE, ListData> m_womanTime = new HashMap<PERIOD_TYPE, ListData>();
    private static HashMap<PERIOD_TYPE, ListData> m_newbieTime = new HashMap<PERIOD_TYPE, ListData>();
    private static HashMap<PERIOD_TYPE, ListData> m_richTime = new HashMap<PERIOD_TYPE, ListData>();
    private static long UPDATE_INTERVAL = 300000;
    
    private HotPeopleListController() {
    }
        
	public static Result dayManList(int pageNumber, int sizePerPage){
	    
	    ListData data = null;
	    synchronized (m_manTime) {
	        try {
	            data = m_manTime.get(PERIOD_TYPE.E_DAY);
	            
	            if (null == data) {
	                //need to get the real data
	                data = new ListData();
	                data.date = new Date();
	                data.list = HotPeopleListController.getInstance().
	                        getManList(PERIOD_TYPE.E_DAY, pageNumber, sizePerPage);
	                m_manTime.put(PERIOD_TYPE.E_DAY, data);
	            }
	            else {
	                if ((new Date().getTime() - data.date.getTime()) > UPDATE_INTERVAL) {
	                    data.date = new Date();
	                    data.list = HotPeopleListController.getInstance().
	                            getManList(PERIOD_TYPE.E_DAY, pageNumber, sizePerPage);
	                }               
	            }	            
	        }
	        catch (Throwable e) {
	            Logger.info("list Exception: " + e.getMessage());
	        }
	    }
	    
	    if (data == null || data.list == null) {
	        return ok();
	    }
	    else {
	        return ok(Json.toJson(data.list));
	    }
	    	    
		//List<CharmSortResult> list = getManList(PERIOD_TYPE.E_DAY, pageNumber, sizePerPage);
		//return ok(Json.toJson(list));
	}
	public static Result weekManList(int pageNumber, int sizePerPage){
        ListData data = null;
        synchronized (m_manTime) {
            try {
                data = m_manTime.get(PERIOD_TYPE.E_WEEK);
                
                if (null == data) {
                    //need to get the real data
                    data = new ListData();
                    data.date = new Date();
                    data.list = HotPeopleListController.getInstance().
                            getManList(PERIOD_TYPE.E_WEEK, pageNumber, sizePerPage);
                    m_manTime.put(PERIOD_TYPE.E_WEEK, data);
                }
                else {
                    if ((new Date().getTime() - data.date.getTime()) > UPDATE_INTERVAL) {
                        data.date = new Date();
                        data.list = HotPeopleListController.getInstance().
                                getManList(PERIOD_TYPE.E_WEEK, pageNumber, sizePerPage);
                    }               
                }                
            }
            catch (Throwable e) {
                Logger.info("list Exception: " + e.getMessage());
            }
        }

        if (data == null || data.list == null) {
            return ok();
        }
        else {
            return ok(Json.toJson(data.list));
        }
	}
	public static Result monthManList(int pageNumber, int sizePerPage){
        ListData data = null;
        synchronized (m_manTime) {
            try {
                data = m_manTime.get(PERIOD_TYPE.E_MONTH);
                
                if (null == data) {
                    //need to get the real data
                    data = new ListData();
                    data.date = new Date();
                    data.list = HotPeopleListController.getInstance().
                            getManList(PERIOD_TYPE.E_MONTH, pageNumber, sizePerPage);
                    m_manTime.put(PERIOD_TYPE.E_MONTH, data);
                }
                else {
                    if ((new Date().getTime() - data.date.getTime()) > UPDATE_INTERVAL) {
                        data.date = new Date();
                        data.list = HotPeopleListController.getInstance().
                                getManList(PERIOD_TYPE.E_MONTH, pageNumber, sizePerPage);
                    }               
                }
                
            }
            catch (Throwable e) {
                Logger.info("list Exception: " + e.getMessage());
            }            
        }

        if (data == null || data.list == null) {
            return ok();
        }
        else {
            return ok(Json.toJson(data.list));
        }

	}
	
	public static Result dayWomanList(int pageNumber, int sizePerPage){
        ListData data = null;
        synchronized (m_womanTime) {
            try {
                data = m_womanTime.get(PERIOD_TYPE.E_DAY);
                
                if (null == data) {
                    //need to get the real data
                    data = new ListData();
                    data.date = new Date();
                    data.list = HotPeopleListController.getInstance().
                            getWomanList(PERIOD_TYPE.E_DAY, pageNumber, sizePerPage);
                    m_womanTime.put(PERIOD_TYPE.E_DAY, data);
                }
                else {
                    if ((new Date().getTime() - data.date.getTime()) > UPDATE_INTERVAL) {
                        data.date = new Date();
                        data.list = HotPeopleListController.getInstance().
                                getWomanList(PERIOD_TYPE.E_DAY, pageNumber, sizePerPage);
                    }               
                }                
            }
            catch (Throwable e) {
                Logger.info("list Exception: " + e.getMessage());
            }
        }	    
	    
        if (data == null || data.list == null) {
            return ok();
        }
        else {
            return ok(Json.toJson(data.list));
        }
	}
	public static Result weekWomanList(int pageNumber, int sizePerPage){
        ListData data = null;
        synchronized (m_womanTime) {
            try {
                data = m_womanTime.get(PERIOD_TYPE.E_WEEK);
                
                if (null == data) {
                    //need to get the real data
                    data = new ListData();
                    data.date = new Date();
                    data.list = HotPeopleListController.getInstance().
                            getWomanList(PERIOD_TYPE.E_WEEK, pageNumber, sizePerPage);
                    m_womanTime.put(PERIOD_TYPE.E_WEEK, data);
                }
                else {
                    if ((new Date().getTime() - data.date.getTime()) > UPDATE_INTERVAL) {
                        data.date = new Date();
                        data.list = HotPeopleListController.getInstance().
                                getWomanList(PERIOD_TYPE.E_WEEK, pageNumber, sizePerPage);
                    }               
                }                
            }
            catch (Throwable e) {
                Logger.info("list Exception: " + e.getMessage());
            }
        } 	    
	    
        if (data == null || data.list == null) {
            return ok();
        }
        else {
            return ok(Json.toJson(data.list));
        }
	}
	
	public static Result monthWomanList(int pageNumber, int sizePerPage){
        ListData data = null;
        synchronized (m_womanTime) {
            try {
                data = m_womanTime.get(PERIOD_TYPE.E_MONTH);
                
                if (null == data) {
                    //need to get the real data
                    data = new ListData();
                    data.date = new Date();
                    data.list = HotPeopleListController.getInstance().
                            getWomanList(PERIOD_TYPE.E_MONTH, pageNumber, sizePerPage);
                    m_womanTime.put(PERIOD_TYPE.E_MONTH, data);
                }
                else {
                    if ((new Date().getTime() - data.date.getTime()) > UPDATE_INTERVAL) {
                        data.date = new Date();
                        data.list = HotPeopleListController.getInstance().
                                getWomanList(PERIOD_TYPE.E_MONTH, pageNumber, sizePerPage);
                    }               
                }                
            }
            catch (Throwable e) {
                Logger.info("list Exception: " + e.getMessage());
            }
        }       
        
        if (data == null || data.list == null) {
            return ok();
        }
        else {
            return ok(Json.toJson(data.list));
        }
	}
	
	public static Result dayNewbieList(int pageNumber, int sizePerPage){
        ListData data = null;
        synchronized (m_newbieTime) {
            try {
                data = m_newbieTime.get(PERIOD_TYPE.E_DAY);
                
                if (null == data) {
                    //need to get the real data
                    data = new ListData();
                    data.date = new Date();
                    data.list = HotPeopleListController.getInstance().
                            getNewbieList(PERIOD_TYPE.E_DAY, pageNumber, sizePerPage);
                    m_newbieTime.put(PERIOD_TYPE.E_DAY, data);
                }
                else {
                    if ((new Date().getTime() - data.date.getTime()) > UPDATE_INTERVAL) {
                        data.date = new Date();
                        data.list = HotPeopleListController.getInstance().
                                getNewbieList(PERIOD_TYPE.E_DAY, pageNumber, sizePerPage);
                    }               
                }                
            }
            catch (Throwable e) {
                Logger.info("list Exception: " + e.getMessage());
            }
        }       	    
	    
        if (data == null || data.list == null) {
            return ok();
        }
        else {
            return ok(Json.toJson(data.list));
        }
	}

	public static Result weekNewbieList(int pageNumber, int sizePerPage){
        ListData data = null;
        synchronized (m_newbieTime) {
            try {
                data = m_newbieTime.get(PERIOD_TYPE.E_WEEK);
                
                if (null == data) {
                    //need to get the real data
                    data = new ListData();
                    data.date = new Date();
                    data.list = HotPeopleListController.getInstance().
                            getNewbieList(PERIOD_TYPE.E_WEEK, pageNumber, sizePerPage);
                    m_newbieTime.put(PERIOD_TYPE.E_WEEK, data);
                }
                else {
                    if ((new Date().getTime() - data.date.getTime()) > UPDATE_INTERVAL) {
                        data.date = new Date();
                        data.list = HotPeopleListController.getInstance().
                                getNewbieList(PERIOD_TYPE.E_WEEK, pageNumber, sizePerPage);
                    }               
                }                
            }
            catch (Throwable e) {
                Logger.info("list Exception: " + e.getMessage());
            }
        }               	    

        if (data == null || data.list == null) {
            return ok();
        }
        else {
            return ok(Json.toJson(data.list));
        }
	}
	public static Result monthNewbieList(int pageNumber, int sizePerPage){
        ListData data = null;
        synchronized (m_newbieTime) {
            try {                
                data = m_newbieTime.get(PERIOD_TYPE.E_MONTH);
                
                if (null == data) {
                    //need to get the real data
                    data = new ListData();
                    data.date = new Date();
                    data.list = HotPeopleListController.getInstance().
                            getNewbieList(PERIOD_TYPE.E_MONTH, pageNumber, sizePerPage);
                    m_newbieTime.put(PERIOD_TYPE.E_MONTH, data);
                }
                else {
                    if ((new Date().getTime() - data.date.getTime()) > UPDATE_INTERVAL) {
                        data.date = new Date();
                        data.list = HotPeopleListController.getInstance().
                                getNewbieList(PERIOD_TYPE.E_MONTH, pageNumber, sizePerPage);
                    }               
                } 
            }
            catch (Throwable e) {
                Logger.info("list Exception: " + e.getMessage());
            }
        }                       

        if (data == null || data.list == null) {
            return ok();
        }
        else {
            return ok(Json.toJson(data.list));
        }
	}
	
	public static Result dayRichList(int pageNumber, int sizePerPage){
	    
        ListData data = null;
        synchronized (m_richTime) {
            try {
                data = m_richTime.get(PERIOD_TYPE.E_DAY);
                
                if (null == data) {
                    //need to get the real data
                    data = new ListData();
                    data.date = new Date();
                    data.list = HotPeopleListController.getInstance().
                            getRichList(PERIOD_TYPE.E_DAY, pageNumber, sizePerPage);
                    m_richTime.put(PERIOD_TYPE.E_DAY, data);
                }
                else {
                    if ((new Date().getTime() - data.date.getTime()) > UPDATE_INTERVAL) {
                        data.date = new Date();
                        data.list = HotPeopleListController.getInstance().
                                getRichList(PERIOD_TYPE.E_DAY, pageNumber, sizePerPage);
                    }               
                }                
            }
            catch (Throwable e) {
                Logger.info("list Exception: " + e.getMessage());
            }
        }                       

        if (data == null || data.list == null) {
            return ok();
        }
        else {
            return ok(Json.toJson(data.list));
        }
	}
	public static Result weekRichList(int pageNumber, int sizePerPage){
        ListData data = null;
        synchronized (m_richTime) {
            try {
                data = m_richTime.get(PERIOD_TYPE.E_WEEK);
                
                if (null == data) {
                    //need to get the real data
                    data = new ListData();
                    data.date = new Date();
                    data.list = HotPeopleListController.getInstance().
                            getRichList(PERIOD_TYPE.E_WEEK, pageNumber, sizePerPage);
                    m_richTime.put(PERIOD_TYPE.E_WEEK, data);
                }
                else {
                    if ((new Date().getTime() - data.date.getTime()) > UPDATE_INTERVAL) {
                        data.date = new Date();
                        data.list = HotPeopleListController.getInstance().
                                getRichList(PERIOD_TYPE.E_WEEK, pageNumber, sizePerPage);
                    }               
                }                
            }
            catch (Throwable e) {
                Logger.info("list Exception: " + e.getMessage());
            }
        }                       

        if (data == null || data.list == null) {
            return ok();
        }
        else {
            return ok(Json.toJson(data.list));
        }
	}

	public static Result monthRichList(int pageNumber, int sizePerPage){
        ListData data = null;
        synchronized (m_richTime) {
            try {
                data = m_richTime.get(PERIOD_TYPE.E_MONTH);
                
                if (null == data) {
                    //need to get the real data
                    data = new ListData();
                    data.date = new Date();
                    data.list = HotPeopleListController.getInstance().
                            getRichList(PERIOD_TYPE.E_MONTH, pageNumber, sizePerPage);
                    m_richTime.put(PERIOD_TYPE.E_MONTH, data);
                }
                else {
                    if ((new Date().getTime() - data.date.getTime()) > UPDATE_INTERVAL) {
                        data.date = new Date();
                        data.list = HotPeopleListController.getInstance().
                                getRichList(PERIOD_TYPE.E_MONTH, pageNumber, sizePerPage);
                    }               
                }                
            }
            catch (Throwable e) {
                Logger.info("list Exception: " + e.getMessage());
            }            
        }                       

        if (data == null || data.list == null) {
            return ok();
        }
        else {
            return ok(Json.toJson(data.list));
        }
	}
	
	private Date getStartDate(Date now, PERIOD_TYPE periodType) {
		Calendar nowDate = Calendar.getInstance();
		nowDate.setTime(now);
		
		Calendar startDate = Calendar.getInstance();
		//start from Monday
		//startDate.setFirstDayOfWeek(Calendar.MONDAY);
		
		switch (periodType) {
		case E_DAY:
			//nowDate.add(Calendar.DATE, -1);
			startDate.setTime(nowDate.getTime());
			break;
			
		case E_WEEK:
			//nowDate.add(Calendar.DATE, -7);
			startDate.setTime(nowDate.getTime());
			startDate.add(Calendar.DATE, -7);
			//startDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			break;
			
		case E_MONTH:
			//nowDate.add(Calendar.DATE,  -30);
			startDate.setTime(nowDate.getTime());
			startDate.add(Calendar.DATE, -30);
			//startDate.set(Calendar.DAY_OF_MONTH, 1);
			break;
		}

		startDate.set(Calendar.HOUR_OF_DAY, 0);
		startDate.set(Calendar.MINUTE, 0);
		startDate.set(Calendar.SECOND, 0);
		startDate.set(Calendar.MILLISECOND, 0);

		return startDate.getTime();
	}
	
  private List<CharmSortResult> getManList(PERIOD_TYPE periodType, int pageNumber, int sizePerPage){
		
		Date nowDate = new Date();		
		Date startDate = getStartDate(nowDate, periodType);
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String appId = session("appId");
		
		String sql = String.format(
				"select charm_value.receiver_id, name, head_image, search_id, sum(charm_value)sumValue, is_verified from charm_value"
						+ " join app_profile on app_profile.account_id = charm_value.receiver_id"
						+ " where charm_value.app_id = '%s' and app_profile.sex = 0 and charm_value.create_time >= '%s' and charm_value.create_time <= '%s'"
						+ " group by charm_value.receiver_id  order by if (isnull(head_image) or head_image = '', 1, 0), sumValue desc",
						appId, sdf.format(startDate), sdf.format(nowDate));

		RawSql rawSql = 
				RawSqlBuilder.parse(sql)
				.columnMapping("charm_value.receiver_id", "account_id")	
				.columnMapping("name", "name")
				.columnMapping("head_image", "head_image")
    			.columnMapping("search_id", "search_id")
				.columnMapping("sum(charm_value)sumValue", "sum_charm")
				.create();
		
		Query<CharmSortResult> query = Ebean.find(CharmSortResult.class);
		query.setRawSql(rawSql);
		
		List<CharmSortResult> list = 
				query
				.setFirstRow(pageNumber*sizePerPage)
				.setMaxRows(sizePerPage)				
				.findList();
		
		return list;
	}
	
  private List<CharmSortResult> getWomanList(PERIOD_TYPE periodType, int pageNumber, int sizePerPage){
	
	  Date nowDate = new Date();		
	  Date startDate = getStartDate(nowDate, periodType);
	
	
	  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  String appId = session("appId");
	
	  String sql = String.format(
			  "select charm_value.receiver_id, name, head_image, search_id, sum(charm_value)sumValue, is_verified from charm_value"
						+ " join app_profile on app_profile.account_id = charm_value.receiver_id"
						+ " where charm_value.app_id = '%s' and app_profile.sex = 1 and charm_value.create_time >= '%s' and charm_value.create_time <= '%s'"
						+ " group by charm_value.receiver_id  order by if (isnull(head_image) or head_image = '', 1, 0), sumValue desc",
						appId, sdf.format(startDate), sdf.format(nowDate));

	  RawSql rawSql = 
			RawSqlBuilder.parse(sql)
			.columnMapping("charm_value.receiver_id", "account_id")	
			.columnMapping("name", "name")
			.columnMapping("head_image", "head_image")
			.columnMapping("search_id", "search_id")
			.columnMapping("sum(charm_value)sumValue", "sum_charm")
			.create();
	
	  Query<CharmSortResult> query = Ebean.find(CharmSortResult.class);
	  query.setRawSql(rawSql);
	
	  List<CharmSortResult> list = 
			query
			.setFirstRow(pageNumber*sizePerPage)
			.setMaxRows(sizePerPage)				
			.findList();
	
	  return list;
  }
	
	private List<CharmSortResult> getNewbieList(PERIOD_TYPE periodType, int pageNumber, int sizePerPage){
		
		Calendar newbieCalendar = Calendar.getInstance();
		newbieCalendar.setTime(new Date());
		 newbieCalendar.add(Calendar.DATE, -30);
		 Date newbieDate = newbieCalendar.getTime();
		
		Date nowDate = new Date();		
		Date startDate = getStartDate(nowDate, periodType);
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String appId = session("appId");
		
		String sql = String.format(
				"select charm_value.receiver_id, name, head_image, search_id, sum(charm_value)sumValue, is_verified from charm_value"
						+ " join app_profile on app_profile.account_id = charm_value.receiver_id"
						+ " join account on account.id = charm_value.receiver_id"
						+ " where charm_value.app_id = '%s' and charm_value.create_time >= '%s' and charm_value.create_time <= '%s' and  account.create_time >= '%s'"
						+ " group by charm_value.receiver_id  order by if (isnull(head_image) or head_image = '', 1, 0), sumValue desc",
						appId, sdf.format(startDate), sdf.format(nowDate), sdf.format(newbieDate));

		RawSql rawSql = 
				RawSqlBuilder.parse(sql)
				.columnMapping("charm_value.receiver_id", "account_id")	
				.columnMapping("name", "name")
				.columnMapping("head_image", "head_image")
    			.columnMapping("search_id", "search_id")
				.columnMapping("sum(charm_value)sumValue", "sum_charm")
				.create();
		
		Query<CharmSortResult> query = Ebean.find(CharmSortResult.class);
		query.setRawSql(rawSql);
		
		List<CharmSortResult> list = 
				query
				.setFirstRow(pageNumber*sizePerPage)
				.setMaxRows(sizePerPage)				
				.findList();
		
		return list;
	}
		
  private List<CharmSortResult> getRichList(PERIOD_TYPE periodType, int pageNumber, int sizePerPage){
		
		Date nowDate = new Date();		
		Date startDate = getStartDate(nowDate, periodType);
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String appId = session("appId");
		
		//use flower instead of money, please replace count(money_record.account_id) back to sum(money)
		String sql = String.format(
				"select money_record.account_id, name, head_image, search_id, count(money_record.account_id) sumValue, is_verified from money_record"
				+ " join app_profile on app_profile.account_id = money_record.account_id"
				+ " where money_record.app_id = '%s' and money_record.create_time >= '%s' and money_record.create_time <= '%s'"
				+ " group by money_record.account_id  order by if (isnull(head_image) or head_image = '', 1, 0), sumValue desc",
				appId, sdf.format(startDate), sdf.format(nowDate));

		RawSql rawSql = 
				RawSqlBuilder.parse(sql)
				.columnMapping("money_record.account_id", "account_id")	
				.columnMapping("name", "name")
				.columnMapping("head_image", "head_image")
    			.columnMapping("search_id", "search_id")
				.columnMapping("count(money_record.account_id)", "sum_charm")
				.create();
		
		Query<CharmSortResult> query = Ebean.find(CharmSortResult.class);
		query.setRawSql(rawSql);
		
		List<CharmSortResult> list = 
				query
				.setFirstRow(pageNumber*sizePerPage)
				.setMaxRows(sizePerPage)				
				.findList();
		
		return list;
	}
	
	private enum PERIOD_TYPE {
		E_DAY,
		E_WEEK,
		E_MONTH
	}
}
