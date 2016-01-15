package controllers.activity;

import com.avaje.ebean.Ebean;

import models.activity.Activity;
import models.activity.ActivityChoice;
import models.activity.ActivityContent;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;
import controllers.AppController;
import controllers.ErrDefinition;
import controllers.common.CodeGenerator;
import controllers.inceptors.AccessLevel;

public class ActivityContentController extends AppController {
	
	@AccessLevel(level=2)
	public Result create() {
		Form<ActivityContent> contentForm = 
				Form.form(ActivityContent.class).bindFromRequest();
		
		if (contentForm.hasErrors()) {
			return status(ErrDefinition.E_ACTIVITY_CONTENT_FORM_HASERROR,
					Messages.get("activitycontent.failure"));
		}
		
		try {
			ActivityContent content = contentForm.get();
			
			content.id = CodeGenerator.GenerateUUId();
			content.activity = new Activity();
			content.activity.id = content.activity_id;
			
			Ebean.save(content);
			
			return ok(Json.toJson(content));
			
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_ACTIVITY_CONTENT_CREATE_FAILED,
					Messages.get("activitycontent.failure"));
		}
	}
	
	public Result read(String activityId) {
		
		try {
			ActivityContent content = ActivityContent.find.where()
					.eq("activity_id", activityId).findUnique();
			if(content == null){
				return status(ErrDefinition.E_ACTIVITY_CONTENT_READ_FAILED,
						Messages.get("activitycontent.failure"));
			}
			if (content.choiceList != null) {
	            for (ActivityChoice choice : content.choiceList) {
	                ActivityChoiceController.ConvertDescription(choice);
	            }			    
			}
			
			return ok(Json.toJson(content));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_ACTIVITY_CONTENT_READ_FAILED,
					Messages.get("activitycontent.failure"));
		}
	}
	
	@AccessLevel(level=2)
	public Result update() {
		Form<ActivityContent> contentForm = 
				Form.form(ActivityContent.class).bindFromRequest();
		
		if (contentForm.hasErrors()) {
			return status(ErrDefinition.E_ACTIVITY_CONTENT_FORM_HASERROR,
					Messages.get("activitycontent.failure"));
		}
		
		try {
			ActivityContent content = contentForm.get();
			
			content.activity = new Activity();
			content.activity.id = content.activity_id;
			
			Ebean.update(content);
			
			return ok(Json.toJson(content));
			
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_ACTIVITY_CONTENT_UPDATE_FAILED,
					Messages.get("activitycontent.failure"));
		}		
	}
	
	@AccessLevel(level=2)
	public Result delete(String id) {
		try {
			Ebean.delete(ActivityContent.class, id);
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_ACTIVITY_CONTENT_DELETE_FAILED,
					Messages.get("activitycontent.failure"));
		}
		
		return ok();
	}
}
