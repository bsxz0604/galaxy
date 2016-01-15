package controllers.poshow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.activity.Activity;
import models.activity.ActivityContent;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;
import controllers.AppController;
import controllers.ErrDefinition;
import controllers.activity.ActivityResultController;
import controllers.common.CodeGenerator;
import controllers.inceptors.AccessLevel;
import models.application.Application;
import models.data.Data;

public class ActController extends AppController {
	
	@AccessLevel(level=2)
	public Result create() {
		
		Form<Activity> activityForm = 
				Form.form(Activity.class).bindFromRequest();
		
		if (activityForm.hasErrors()) {
			return status(ErrDefinition.E_ACTIVITY_FORM_HASERROR,
					Messages.get("activity.failure"));
		}
		
		try {
			Activity activity = activityForm.get(); 
			
			String startDate = activityForm.data().get("startTime");
			String endDate = activityForm.data().get("endTime");
			activity.startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(startDate);
			activity.endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(endDate);
			activity.id = CodeGenerator.GenerateUUId();
			activity.app = new Application();
			activity.app.id = session("appId");
			if (activity.startTime.compareTo(activity.endTime) >= 0) {
				return failure(ErrDefinition.E_ACTIVITY_INVALID_TIME);
						//Messages.get("activity.failure"));
			}
			 
			Ebean.save(activity);
			
			return ok(Json.toJson(activity));
		}
		catch (Exception e) {
			return status(ErrDefinition.E_ACTIVITY_CREATE_FAILED,
					Messages.get("activity.failure"));
		}
	}
	
	public Result isVoted(String activityId) {
		ActivityContent content = ActivityContent.find.where()
				.eq("activity_id", activityId)
				.findUnique();
		
		if (content != null) {
			return new ActivityResultController().isVoted(content.id);
		}
		
		return status(ErrDefinition.E_ACTIVITY_READ_FAILED,
				Messages.get("activity.failure"));
	}
	
	public Result read(int pageNumber, int sizePerPage) {
		String appId = session("appId");
		
		try {
			List<Activity> activityAll = new ArrayList<Activity>();
			List<Activity> activityList = new ArrayList<Activity>();
			
			String sql = String.format("SELECT activity.id,activity.app_id,activity.type,activity.start_time,activity.end_time,activity.web,activity.img_url,activity.link FROM activity "
					+ "WHERE app_id = '%s' "
					+ "AND end_time-sysdate()>0 "
					+ "AND start_time-sysdate()<=0 "
					+ "ORDER BY start_time DESC ",session("appId"));
			RawSql rawSql=
					RawSqlBuilder.parse(sql)
					.columnMapping("activity.id", "id")
					.columnMapping("activity.app_id", "app_id")
					.columnMapping("activity.type", "type")
					.columnMapping("activity.start_time", "startTime")
					.columnMapping("activity.end_time", "endTime")
					.columnMapping("activity.web", "web")
					.columnMapping("activity.img_url", "imgUrl")
					.columnMapping("activity.link", "link")
					.create();

			Query<Activity> query = Ebean.find(Activity.class);
			
			query.setRawSql(rawSql);
			
			List<Activity> activityStart = query.findList();
			
			sql = String.format("SELECT activity.id,activity.app_id,activity.type,activity.start_time,activity.end_time,activity.web,activity.img_url,activity.link FROM activity "
					+ "WHERE app_id = '%s' "
					+ "AND end_time-sysdate()<=0 "
					+ "ORDER BY start_time DESC ",session("appId"));
			rawSql=
					RawSqlBuilder.parse(sql)
					.columnMapping("activity.id", "id")
					.columnMapping("activity.app_id", "app_id")
					.columnMapping("activity.type", "type")
					.columnMapping("activity.start_time", "startTime")
					.columnMapping("activity.end_time", "endTime")
					.columnMapping("activity.web", "web")
					.columnMapping("activity.img_url", "imgUrl")
					.columnMapping("activity.link", "link")
					.create();

			query.setRawSql(rawSql);
			
			List<Activity> activityEnd = query.findList();
			sql = String.format("SELECT activity.id,activity.app_id,activity.type,activity.start_time,activity.end_time,activity.web,activity.img_url,activity.link FROM activity "
					+ "WHERE app_id = '%s' "
					+ "AND start_time-sysdate()>0 "
					+ "ORDER BY start_time ",session("appId"));
			rawSql=
					RawSqlBuilder.parse(sql)
					.columnMapping("activity.id", "id")
					.columnMapping("activity.app_id", "app_id")
					.columnMapping("activity.type", "type")
					.columnMapping("activity.start_time", "startTime")
					.columnMapping("activity.end_time", "endTime")
					.columnMapping("activity.web", "web")
					.columnMapping("activity.img_url", "imgUrl")
					.columnMapping("activity.link", "link")
					.create();

			query.setRawSql(rawSql);
			
			List<Activity> activityWill = query.findList();
			
			activityAll.addAll(activityStart);
			activityAll.addAll(activityWill);
			activityAll.addAll(activityEnd);
			
			if(activityAll.size()>=pageNumber*sizePerPage&&activityAll.size()>=pageNumber*sizePerPage+sizePerPage){
				activityList=activityAll.subList(pageNumber*sizePerPage, pageNumber*sizePerPage+sizePerPage);
			}
			else if(activityAll.size()>pageNumber*sizePerPage&&activityAll.size()<pageNumber*sizePerPage+sizePerPage){
				activityList=activityAll.subList(pageNumber*sizePerPage, activityAll.size());
			}
			else {
				return ok();
			}
			
			for (Activity activity : activityList) {
				ActivityContent content = ActivityContent.find.where()
						.eq("activity_id", activity.id).findUnique();
				if (null != content) {
					activity.contentId = content.id;
				}
			}
//			
//			Collections.sort(activityList, new Comparator<Activity>() {
//				public int compare(Activity arg0, Activity arg1) {
//					if (arg0.getStatus() != arg1.getStatus()) {
//						return arg0.getStatus() < arg1.getStatus() ? 0 : 1;						
//					}
//					else {
//						return arg1.startTime.compareTo(arg0.startTime);
//					}
//				}
//			});

			return ok(Json.toJson(activityList));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_ACTIVITY_READ_FAILED,
					e.getMessage());
		}
	}
	
	@AccessLevel(level=2)
	public Result update() {
		Form<Activity> activityForm = 
				Form.form(Activity.class).bindFromRequest();
		
		if (activityForm.hasErrors()) {
			return status(ErrDefinition.E_ACTIVITY_FORM_HASERROR, 
					Messages.get("activity.failure"));
		}
		
		try {
			Activity activity = activityForm.get();
			String startDate = activityForm.data().get("startTime");
			String endDate = activityForm.data().get("endTime");
			activity.startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(startDate);
			activity.endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(endDate);
			if (activity.startTime.compareTo(activity.endTime) >= 0) {
				return failure(ErrDefinition.E_ACTIVITY_INVALID_TIME);
						//Messages.get("activity.failure"));
			}

			activity.app_id = session("appId");
			if (activity.app_id != session("appId")) {
				return status(ErrDefinition.E_ACTIVITY_NOT_SAME_APP,
						Messages.get("activity.failure"));
			}

						
			Ebean.update(activity);
			
			return ok(Json.toJson(activity));
		}
		catch (Exception e) {
			return status(ErrDefinition.E_ACTIVITY_UPDATE_FAILED,
					Messages.get("activity.failure"));
		}
	}
	
	public Result updateFcwm() {
		Form<Activity> activityForm = 
				Form.form(Activity.class).bindFromRequest();
		
		if (activityForm.hasErrors()) {
			return status(ErrDefinition.E_ACTIVITY_FORM_HASERROR, 
					Messages.get("activity.failure"));
		}
		
		try {
			Activity activity = activityForm.get();
			
            activity.app_id=session("appId");
						
			Ebean.update(activity);
			
			return ok(Json.toJson(activity));
		}
		catch (Exception e) {
			return status(ErrDefinition.E_ACTIVITY_UPDATE_FAILED,
					Messages.get("activity.failure"));
		}
	}
	
	@AccessLevel(level=2)
	public Result delete(String id) {
		try {
			Ebean.delete(Activity.class, id);
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_ACTIVITY_DELETE_FAILED,
					Messages.get("activity.failure"));
		}
		
		return ok();
	}
	
	public Result readById(String Id){
		Activity activity = Activity.find.byId(Id);
		return ok(Json.toJson(activity));
	}
}
