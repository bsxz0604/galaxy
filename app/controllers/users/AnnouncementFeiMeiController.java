package controllers.users;

import java.util.Date;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;

import play.Logger;
import play.data.Form;
import models.users.AnnouncementFeiMei;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;
import controllers.AppController;
import controllers.ErrDefinition;
import controllers.common.CodeGenerator;

public class AnnouncementFeiMeiController extends AppController {

	public Result create() {
		Form<AnnouncementFeiMei> record = Form.form(AnnouncementFeiMei.class)
				.bindFromRequest();
		if(record.hasErrors()){
			return status(ErrDefinition.E_ENTRY_CREAT_ERROR,
					Messages.get("entryform.failure"));
		}
		AnnouncementFeiMei existAnnouncementFeiMei = AnnouncementFeiMei.find.where()
				.eq("account_id",session("userId"))
				.orderBy("create_time desc")
				.findUnique();
		if(existAnnouncementFeiMei==null){
		AnnouncementFeiMei announcementFeiMei = record.get();
		announcementFeiMei.id = CodeGenerator.GenerateUUId();
		announcementFeiMei.create_time = new Date();
		announcementFeiMei.accountId = session("userId");
		announcementFeiMei.appId = session("appId");
		Ebean.save(announcementFeiMei);
		return ok(Json.toJson(announcementFeiMei));
		}
		else{
			return ok();
		}
		
	}
	
	public Result readById(){
		AnnouncementFeiMei existAnnouncementFeiMei = AnnouncementFeiMei.find.where()
				.eq("account_id",session("userId"))
				.orderBy("create_time desc")
				.setFirstRow(0)
				.setMaxRows(1)
				.findUnique();
		if(existAnnouncementFeiMei==null) {
			return ok("0");
		}
		else {
			return ok(Json.toJson(existAnnouncementFeiMei));
		}
	}
	
	public Result update(){
		Form<AnnouncementFeiMei> record = Form.form(AnnouncementFeiMei.class)
				.bindFromRequest();
		if(record.hasErrors()){
			return status(ErrDefinition.E_ENTRY_CREAT_ERROR,
					Messages.get("entryform.failure"));
		}
		try {
			AnnouncementFeiMei announcementFeiMei = record.get();
			Ebean.update(announcementFeiMei);
			return ok(Json.toJson(announcementFeiMei));
		} catch (Throwable e) {
			return status(ErrDefinition.E_ACTIVITY_CHOICE_UPDATE_FAILED,
					Messages.get("activitychoice.failure"));
		}
		
	}

}
