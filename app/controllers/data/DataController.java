package controllers.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.mvc.Action.Simple;
import play.mvc.Controller;
import play.mvc.Result;
import models.data.Data;
import modelslog.logger.LogData;

public class DataController extends Controller {
	public ObjectNode getStaticNode(String date){
//		String appId = session("appId");
		ObjectNode node = Json.newObject();
		try {
//			注册人数
			String sql = String.format("SELECT count(*) FROM account "
					+ "LEFT JOIN app_profile ON app_profile.account_id=account.id "
					+ "WHERE create_time >='%s' "
					+ "AND app_profile.app_id = '34536418-d6b2-451f-a400-4f0e284c9497' "
					+ "AND create_time <=date_add('%s', interval 1 day)",date,date);
			RawSql rawSql=
					RawSqlBuilder.parse(sql)
					.columnMapping("count(*)", "value")
					.create();
			
			Query<Data> query = Ebean.find(Data.class);
			
//			EbeanServer logEbean = Ebean.getServer("log");
			Query<LogData> querylog = Ebean.getServer("log").find(LogData.class);
			
			query.setRawSql(rawSql);
			Data data = query.findUnique();
			node.put("loginCount", data.value);
//			上平台人数
			sql = String.format("SELECT count(distinct(user_id)) FROM logs_bean "
					+ "WHERE method = 'loginWithApp' "
					+ "AND operation_time >='%s' "
					+ "AND operation_time <=date_add('%s', interval 1 day)",date,date);
			
            rawSql = 
                    RawSqlBuilder.parse(sql)                    
                    .columnMapping("count(distinct(user_id))", "value")
                    .create();
            
            querylog.setRawSql(rawSql);
            
            LogData datalog = querylog.findUnique();
            node.put("upCount", datalog.value);
//          聊天室人数
            sql = String.format("SELECT count(distinct(user_id)) FROM logs_bean "
					+ "WHERE method = 'getRoomPeopleNum' "
					+ "AND request like '%s' "
					+ "AND operation_time >='%s' "
					+ "AND operation_time <=date_add('%s', interval 1 day)","{\"Referer\":\"http://fcwm.24-7.com.cn%",date,date);
            rawSql = 
                    RawSqlBuilder.parse(sql)
                    .columnMapping("count(distinct(user_id))", "value")
                    .create();
            
            querylog.setRawSql(rawSql);
            
            datalog = querylog.findUnique();
            node.put("chatCount", datalog.value);
//          美美人数
            sql = String.format("SELECT count(distinct(user_id)) FROM logs_bean "
					+ "WHERE method = 'loginWithApp' "
            		+ "AND request like '%s' "
					+ "AND operation_time >='%s' "
					+ "AND operation_time <=date_add('%s', interval 1 day)","{\"Referer\":\"http://fcwm.24-7.com.cn/assets/family%",date,date);
            rawSql = 
                    RawSqlBuilder.parse(sql)
                    .columnMapping("count(distinct(user_id))", "value")
                    .create();
            
            querylog.setRawSql(rawSql);
            
            datalog = querylog.findUnique();
            node.put("mmNum", datalog.value);     
//          美美人次
            sql = String.format("SELECT count(*) FROM logs_bean "
					+ "WHERE method = 'loginWithApp' "
					+ "AND request like '%s' "
					+ "AND operation_time >='%s' "
					+ "AND operation_time <=date_add('%s', interval 1 day)","{\"Referer\":\"http://fcwm.24-7.com.cn/assets/family%",date,date);
            rawSql = 
                    RawSqlBuilder.parse(sql)
                    .columnMapping("count(*)", "value")
                    .create();
            
            querylog.setRawSql(rawSql);
            
            datalog = querylog.findUnique();
            node.put("mmCount", datalog.value);
//          发帖人数
            sql = String.format("SELECT count(*) FROM article "
					+ "where create_time >='%s' "
            		+ "AND app_id = '34536418-d6b2-451f-a400-4f0e284c9497' "
					+ "AND create_time <=date_add('%s', interval 1 day)",date,date);
            rawSql = 
                    RawSqlBuilder.parse(sql)
                    .columnMapping("count(*)", "value")
                    .create();
            
            query.setRawSql(rawSql);
            
            data = query.findUnique();
            node.put("postCount", data.value);
//          回帖人数
            sql = String.format("SELECT count(*) FROM comments LEFT JOIN article on article.article_id=comments.article_id "
					+ "where comments.create_time >='%s' "
            		+ "AND article.app_id ='34536418-d6b2-451f-a400-4f0e284c9497' "
					+ "AND comments.create_time <=date_add('%s', interval 1 day)",date,date);
            rawSql = 
                    RawSqlBuilder.parse(sql)
                    .columnMapping("count(*)", "value")
                    .create();
            
            query.setRawSql(rawSql);
            
            data = query.findUnique();
            node.put("commentCount", data.value);            
		} catch (Throwable e) {
			// TODO: handle exception
			node = null;
		}
		return node;
	}
	public Result getDateStatistics(String fromDate,String toDate){
		
		String header = request().getHeader("Origin");
		header = (header == null ? "*" : header);
		response().setHeader("Access-Control-Allow-Origin", header);
        response().setHeader("Access-Control-Allow-Credentials", "true");
        response().setHeader("Access-Control-Allow-Methods", "POST,GET,PUT,DELETE,OPTIONS");
        
        List<ObjectNode> dataList = new ArrayList<ObjectNode>();
        
        try {
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	
			Calendar start = Calendar.getInstance();
			start.setTime(sdf.parse(fromDate));
			
			Calendar end = Calendar.getInstance();
			end.setTime(sdf.parse(toDate));
			
			ObjectNode data = null;
			
			while(!start.after(end)){
				data = getStaticNode(sdf.format(start.getTime()));
				
				if(null != data){
					data.put("date", sdf.format(start.getTime()));
					dataList.add(data);
				}
				start.add(Calendar.DAY_OF_YEAR, 1);
			}
		} catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ok(Json.toJson(dataList));
	}

}
