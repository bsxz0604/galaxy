package controllers.eatToday;


import java.util.Date;

import com.avaje.ebean.Ebean;
import play.Logger;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;
import controllers.AppController;
import controllers.ErrDefinition;
import controllers.common.CodeGenerator;
import models.application.Application;
import models.common.Account;
import models.eattoday.EatQuestion;
import models.eattoday.EatRecord;
import models.share.ShareRecord;

public class EatRecordController extends AppController{

	public Result create(){
		
		Form<EatRecord> recordForm = Form.form(EatRecord.class).bindFromRequest();
		if (recordForm.hasErrors()) {
			return status(ErrDefinition.E_ENTRY_CREAT_ERROR,
					Messages.get("entryform.failure"));
		}
		
		EatRecord record = recordForm.get();
		
		return internalCreate(record);
	}
	
	private Result internalCreate(EatRecord record){
		
		String userId = session("userId");
		String appId = session("appId");
		
		EatRecord existEatRecord = EatRecord.find.where()       // userId     current question  
				.eq("account_id", userId)
				.eq("app_id", appId)
				.eq("question_id",record.questionId)             //    time Judge    current question?
				.findUnique();
		
		EatQuestion question = EatQuestion.find.byId(record.questionId);
		if(question == null) {return status(ErrDefinition.E_QUESTION_FIND_ERROR,
				Messages.get("questionNotExist"));
		}
		
		if(existEatRecord == null){
			record.id = CodeGenerator.GenerateUUId();
			record.account = Account.find.where().eq("id", session("userId")).findUnique();
			record.application= Application.find.where().eq("id", session("appId")).findUnique();
			record.result = false;
			record.isShared = false;   //   share-----isShared = true
			record.num = 0;
			record.answerNum = 1;
		//	record.create_time = new Date();
			
		    if(record.answer.equals(question.answer)) {
				record.result = true;
				// first right  ---random gift
				
			}
			Ebean.save(record);
			return ok(Json.toJson(record));
		}
		else{
		    
			if(record.answer.equals(question.answer)){
				existEatRecord.answer = record.answer;
				existEatRecord.result = true;
	//			existEatRecord.create_time = new Date();
				// second right  -----random gift
			}
			existEatRecord.answerNum += 1;
			Ebean.update(existEatRecord);
			return ok(Json.toJson(existEatRecord));
		}
	}
	
	public Result currentStatus(String questionId){
		EatRecord current = EatRecord.find.where()
				.eq("account_id", session("userId"))
				.eq("app_id", session("appId"))
				.eq("question_id", questionId)             //    time Judge    current question?
				.findUnique();
		if(current == null) {return ok("0");}         // 0 :first play 
		else {
			if(current.isShared == true && current.answerNum == 1 && current.result == false )
			{
				return ok("1");         //   1: first-false    shared   first share
			}
			else if(current.isShared == false && current.result == false ){
				return ok("2");          //  2: first-false   no-share
			}
			else{
				if(current.result==true) {
					return ok("3");        //3 : first -right
					}
				return ok("4");        //4:    more than 1 false-----shared
				}
		}
	}
	
	public Result saveShareRecord(String questionId){
		ShareRecord shareRecord = new ShareRecord();
        shareRecord.id = CodeGenerator.GenerateUUId();
        shareRecord.app = Application.find.byId(session("appId"));
        shareRecord.appId = session("appId");
        shareRecord.account = Account.find.byId(session("userId"));
        Logger.info("eatRecordController "+session("userId"));
        shareRecord.userId = session("userId");
//          shareRecord.share_url = url;
        shareRecord.create_time = new Date();
        Ebean.save(shareRecord);
        
        EatRecord existRecord = EatRecord.find.where()
        		.eq("question_id", questionId)
        		.eq("account_id", session("userId"))
                .eq("app_id", session("appId"))
                .findUnique();
        
        if(existRecord == null) {return ok("noAnswerRecord");}
        else{
        	existRecord.isShared = true;
        	existRecord.num +=1;
        	Ebean.update(existRecord);
        }
        
		return ok(Json.toJson(existRecord));
	}
	
	public Result readQuestion(){
		
		EatQuestion question = EatQuestion.find.where()
				.setFirstRow(0)
				.setMaxRows(1)
				.orderBy("time desc")
				.findUnique();
		
		return ok(Json.toJson(question));
	}
	
}
