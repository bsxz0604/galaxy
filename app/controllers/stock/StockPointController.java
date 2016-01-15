package controllers.stock;

import java.util.Calendar;
import java.util.Date;

import javassist.runtime.Desc;
import models.application.Application;
import models.common.Account;
import models.share.ShareRecord;
import models.stock.StockPoint;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;

import com.avaje.ebean.Ebean;

import controllers.ErrDefinition;
import controllers.common.CodeGenerator;
import controllers.AppController;

public class StockPointController extends AppController{

	// sign  
	public Result signIn() {	
		try {
			Boolean isInToday = false;
			
			StockPoint signItem = StockPoint.find.where()
					.eq("account_id", session("userId"))
					.eq("app_id", session("appId"))
					.findUnique();
			
			if (signItem == null) { // first time
				signItem = new StockPoint();
				signItem.id = CodeGenerator.GenerateUUId();
				signItem.application = Application.find.where().eq("id", session("appId")).findUnique();
				signItem.user = Account.find.where().eq("id", session("userId")).findUnique();
				signItem.last_visit = new Date();
				signItem.point = 1;
				Ebean.save(signItem);
				
				isInToday = true;
			}
			else
			{
				Date nowDate = new Date();
				Calendar startDate = Calendar.getInstance();
				startDate.setTime(nowDate);
				int nowDay = startDate.get(Calendar.DATE);
				int nowMonth = startDate.get(Calendar.MONTH);
				startDate.setTime(signItem.last_visit);
				int pastDay = startDate.get(Calendar.DATE);
				int pastMonth = startDate.get(Calendar.MONTH);
				
				if (nowDay!=pastDay || nowMonth!=pastMonth) {
					signItem.last_visit = nowDate;
					signItem.point += 1;
					isInToday = true;
				}
				Ebean.update(signItem);
			}
			
			return ok(Json.toJson( isInToday));			
		}
		catch (Throwable e) {
			System.err.println(e.toString());
			return status(ErrDefinition.E_SIGN_IN_ERROR, 
					Messages.get("themeview.failure"));
		}	
	}
	
	
//	public Result shareToday() {	
//		try {
//			Boolean isShareToday = false;
//			
//			StockPoint signItem = StockPoint.find.where()
//					.eq("account_id", session("userId"))
//					.eq("app_id", session("appId"))
//					.findUnique();
//			ShareRecord share = ShareRecord.find.where()
//					.eq("account_id", session("userId"))
//					.eq("app_id", session("appId"))
//					.setFirstRow(0)
//					.setMaxRows(1)
//					.orderBy("create_time Desc")
//					.findUnique();
//			 ShareRecord shareRecord = new ShareRecord();
//           shareRecord.id = CodeGenerator.GenerateUUId();
//           shareRecord.app = Application.find.byId(session("appId"));
//           shareRecord.appId = session("appId");
//           shareRecord.account = Account.find.byId(session("userId"));
//           shareRecord.userId = session("userId");
// //          shareRecord.share_url = url;
//           shareRecord.create_time = new Date();
//           Ebean.save(shareRecord);
//           
//			if (signItem == null) { // first time
//				signItem = new StockPoint();
//				signItem.id = CodeGenerator.GenerateUUId();
//				signItem.application = Application.find.where().eq("id", session("appId")).findUnique();
//				signItem.user = Account.find.where().eq("id", session("userId")).findUnique();
//				signItem.last_visit = new Date();
//				signItem.point = 1;
//				Ebean.save(signItem);
//				
//				isShareToday = true;
//			}
//			else
//			{
//				Date nowDate = new Date();
//				Calendar startDate = Calendar.getInstance();
//				startDate.setTime(nowDate);
//				int nowDay = startDate.get(Calendar.DATE);
//				int nowMonth = startDate.get(Calendar.MONTH);
//				startDate.setTime(share.create_time);
//				int pastDay = startDate.get(Calendar.DATE);
//				int pastMonth = startDate.get(Calendar.MONTH);
//				
//				if (nowDay!=pastDay || nowMonth!=pastMonth) {
//					signItem.point += 1;
//					isShareToday = true;
//				}
//				Ebean.update(signItem);
//			}
//			
//			return ok(Json.toJson( isShareToday));			
//		}
//		catch (Throwable e) {
//			System.err.println(e.toString());
//			return status(ErrDefinition.E_SIGN_IN_ERROR, 
//					Messages.get("themeview.failure"));
//		}	
//	}
	// saveShareRecord   and   update point
	public Result saveShareRecord() {
        DynamicForm form = Form.form().bindFromRequest();
//        String userId = form.get("userId");
//        String appId = form.get("appId");
        String url = form.get("url");
        
//        try {
//            ShareRecord shareRecord = new ShareRecord();
//            shareRecord.id = CodeGenerator.GenerateUUId();
//            shareRecord.app = Application.find.byId(session("appId"));
//            shareRecord.appId = session("appId");
//            shareRecord.account = Account.find.byId(session("userId"));
//            shareRecord.userId = session("userId");
//  //          shareRecord.share_url = url;
//            shareRecord.create_time = new Date();
//          StockPoint userPoint = StockPoint.find.where()
//			          .eq("account_id", session("userId"))
//                      .eq("app_id", session("appId"))
//			          .findUnique();
//          if (userPoint == null)  {
//        	  userPoint = new StockPoint();
//        	  userPoint.id = CodeGenerator.GenerateUUId();
//        	  userPoint.application = Application.find.where().eq("id", session("appId")).findUnique();
//        	  userPoint.user = Account.find.where().eq("id", session("userId")).findUnique();
//        	  userPoint.last_visit = new Date();
//        	  userPoint.point = 1;
//			  Ebean.save(userPoint);
//				}
//            else{
//            	userPoint.point += 1;
//            	userPoint.last_visit = new Date();
//                Ebean.update(userPoint);
//            }
//            Ebean.save(shareRecord);
//            return ok();
		try {
			Boolean isShareToday = false;
			
			StockPoint signItem = StockPoint.find.where()
					.eq("account_id", session("userId"))
					.eq("app_id", session("appId"))
					.findUnique();
			ShareRecord share = ShareRecord.find.where()
					.eq("user_id", session("userId"))
					.eq("app_id", session("appId"))
					.setFirstRow(0)
					.setMaxRows(1)
					.orderBy("create_time Desc")
					.findUnique();
			if(null == share){
				ShareRecord shareRecord = new ShareRecord();
		           shareRecord.id = CodeGenerator.GenerateUUId();
		           shareRecord.app = Application.find.byId(session("appId"));
		           shareRecord.appId = session("appId");
		           shareRecord.account = Account.find.byId(session("userId"));
		           shareRecord.userId = session("userId");
		           shareRecord.share_url = url;
		           shareRecord.create_time = new Date();
		           Ebean.save(shareRecord);
		           isShareToday = true;
		           signItem.point += 1;
		           Ebean.update(signItem);
		           return ok(Json.toJson(isShareToday));
			}
			else{
			   ShareRecord shareRecord = new ShareRecord();
	           shareRecord.id = CodeGenerator.GenerateUUId();
	           shareRecord.app = Application.find.byId(session("appId"));
	           shareRecord.appId = session("appId");
	           shareRecord.account = Account.find.byId(session("userId"));
	           shareRecord.userId = session("userId");
	           shareRecord.share_url = url;
	           shareRecord.create_time = new Date();
	           Ebean.save(shareRecord);
			}
				Date nowDate = new Date();
				Calendar startDate = Calendar.getInstance();
				startDate.setTime(nowDate);
				int nowDay = startDate.get(Calendar.DATE);
				int nowMonth = startDate.get(Calendar.MONTH);
				startDate.setTime(share.create_time);
				int pastDay = startDate.get(Calendar.DATE);
				int pastMonth = startDate.get(Calendar.MONTH);
				
				if (nowDay!=pastDay || nowMonth!=pastMonth) {
					signItem.point += 1;
					isShareToday = true;
				}
				Ebean.update(signItem);
			
			return ok(Json.toJson(isShareToday));		
        }
        catch (Throwable e) {
            Logger.info("erroR:" + e.getMessage());
        }
        
        return ok();
    }
	
	public Result readById(String accountId){
		StockPoint sign = StockPoint.find.where()
				.eq("account_id", accountId)
				.findUnique();
		if (sign==null)  return ok();
		else {return ok(Json.toJson(sign));}
	}
}
