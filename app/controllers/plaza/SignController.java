package controllers.plaza;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;

import controllers.AppController;
import controllers.ErrDefinition;
import controllers.common.CodeGenerator;
import models.application.Application;
import models.common.Account;
import models.plaza.Sign;
import models.plaza.Theme;
import models.users.CharmValue;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;

public class SignController extends AppController {
	public Result signIn() {	
		try {
			Boolean isInToday = false;
			
			Sign signItem = Sign.find.where()
					.eq("account_id", session("userId"))
					.eq("app_id", session("appId"))
					.findUnique();
			
			if (signItem == null) { // first time
				signItem = new Sign();
				signItem.id = CodeGenerator.GenerateUUId();
				signItem.application = Application.find.where().eq("id", session("appId")).findUnique();
				signItem.user = Account.find.where().eq("id", session("userId")).findUnique();
				signItem.last_visit = new Date();
				signItem.sign_charm = 1;
				Ebean.save(signItem);
//				CharmValue latest_fan_charm = CharmValue.find.where()
//				          .eq("receiver_id", session("userId"))
//				          .setOrderBy("create_time desc")
//				          .setFirstRow(0)
//				          .setMaxRows(1)
//				          .findUnique();
//				CharmValue fan_charm = new CharmValue();
//		        fan_charm.id = CodeGenerator.GenerateUUId();
//		        fan_charm.receiver = Account.find.byId(session("userId"));
//		        fan_charm.sender = fan_charm.receiver;
//		        fan_charm.application = Application.find.byId(session("appId"));
//		        fan_charm.create_time = new Date();
//		        fan_charm.charm_value = signItem.sign_charm; // for sign only
//		        if (latest_fan_charm!=null)
//		            fan_charm.total_charm_value = latest_fan_charm.total_charm_value+signItem.sign_charm;
//		        else
//		        	fan_charm.total_charm_value =  signItem.sign_charm;
//		        Ebean.save(fan_charm);
				
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
					signItem.sign_charm += 1;
//					CharmValue latest_fan_charm = CharmValue.find.where()
//					          .eq("receiver_id", session("userId"))
//					          .setOrderBy("create_time desc")
//					          .setFirstRow(0)
//					          .setMaxRows(1)
//					          .findUnique();
//					CharmValue fan_charm = new CharmValue();
//			        fan_charm.id = CodeGenerator.GenerateUUId();
//			        fan_charm.receiver = Account.find.byId(session("userId"));
//			        fan_charm.sender = fan_charm.receiver;
//			        fan_charm.application = Application.find.byId(session("appId"));
//			        fan_charm.create_time = new Date();
//			        fan_charm.charm_value = signItem.sign_charm; // for sign only
//			        if (latest_fan_charm!=null)
//			            fan_charm.total_charm_value = latest_fan_charm.total_charm_value+signItem.sign_charm;
//			        else
//			        	fan_charm.total_charm_value =  signItem.sign_charm;
//			        Ebean.save(fan_charm);
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
	
	public Result readSign(String accountId){
		Sign sign = Sign.find.where()
				.eq("account_id", accountId)
				.findUnique();
		if (sign == null) {
			Sign sign2 = new Sign(); 
			sign2.sign_charm=0;
			return ok(Json.toJson(sign2));
			}
		else {return ok(Json.toJson(sign));}
				
	}
}
