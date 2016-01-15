package controllers.activity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.activity.Activity;
import models.activity.ActivityChoice;
import models.activity.ActivityContent;
import models.activity.ActivityResult;
import models.activity.ResultGroup;
import models.common.Account;
import play.data.DynamicForm;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;
import controllers.AppController;
import controllers.ErrDefinition;
import controllers.common.CodeGenerator;
import controllers.inceptors.AccessLevel;

public class ActivityResultController extends AppController {
	
	public Result create() {
		Form<ActivityResult> resultForm =
				Form.form(ActivityResult.class).bindFromRequest();
		
		if (resultForm.hasErrors()) {
			return status(ErrDefinition.E_ACTIVITY_RESULT_FORM_HASERROR,
					Messages.get("activityresult.failure"));
		}
		
		ActivityResult result = resultForm.get();
		
		return saveResult(result, result.activity_content_id, result.choice_id);
	}
	
	public Result saveResult(ActivityResult result, String contentId, String choiceId) {
		try {			
			result.id = CodeGenerator.GenerateUUId();
			
			result.account = Account.find.byId(session("userId"));
			result.account_id = result.account.id;
			
			result.content = ActivityContent.find.byId(contentId);
			result.activity_content_id = result.content.id;
			
			result.choice = ActivityChoice.find.byId(choiceId);
			result.choice_id = result.choice.id;
			
			result.create_time = new Date();
			
			if (Math.abs(result.create_time.getTime() - result.account.create_time.getTime()) < 2000) {
				return status(ErrDefinition.E_ACTIVITY_RESULT_CREATE_FAILED,
						Messages.get("activityresult.failure"));				
			}
			
			//check whether it is duplicated in current date
			Calendar today = Calendar.getInstance();
			today.set(Calendar.HOUR_OF_DAY, 0);
			today.set(Calendar.MINUTE, 0);
			today.set(Calendar.SECOND, 0);
			today.set(Calendar.MILLISECOND, 0);
			
			Calendar tomorrow = (Calendar) today.clone();
			tomorrow.add(Calendar.DATE, 1);
			
			int count = ActivityResult.find.where()
				.eq("account_id", result.account_id)
				.eq("activity_content_id", result.activity_content_id)
				.between("create_time", today.getTime(), tomorrow.getTime())
				.findRowCount();
			
			if (count > result.content.max_selection) {
				return status(ErrDefinition.E_ACTIVITY_RESULT_CREATE_FAILED,
						Messages.get("activityresult.failure"));				
			}
			
			Activity activity = Activity.find.byId(result.content.activity_id);
			if (activity.endTime.getTime() < result.create_time.getTime()) {
				return status(ErrDefinition.E_ACTIVITY_RESULT_CREATE_FAILED,
						Messages.get("activityresult.failure"));				
			}
			
			Ebean.save(result);
			
			return ok(Json.toJson(result));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_ACTIVITY_RESULT_CREATE_FAILED,
					Messages.get("activityresult.failure"));
		}		
	}
		
	public Result createList() {
		
		DynamicForm form = Form.form().bindFromRequest();
		String contentId = form.data().get("contentId");
		String choiceId = form.data().get("choiceId");
		
		String[] choiceList = choiceId.split(",");
		
		Status simpleResult = null;
		for (String choice : choiceList) {
			simpleResult = (Status)saveResult(new ActivityResult(), contentId, choice);
			
			if (simpleResult.getWrappedSimpleResult().header().status() != 200) {
				return simpleResult;
			}
		}
		
		return ok();
	}
	
	public Result isVoted(String contentId) {
		String userId = session("userId");
		
		try {
			//find the repeatable in activity
			String sql = String.format(
					"select t0.id from activity t0 join activity_content t1 on t0.id = t1.activity_id  where t1.id = '%s'", contentId, userId);
			RawSql rawSql = 
					RawSqlBuilder.parse(sql)
					.columnMapping("t0.id", "id")
					.create();
			Query<Activity> query = Ebean.find(Activity.class);
			query.setRawSql(rawSql);
			
			Activity activity = query.findUnique();
						
			int count = 0;
			
			if (!activity.repeatable) {
				count = ActivityResult.find.where()
					.eq("account_id", userId)
					.eq("activity_content_id", contentId)
					.findRowCount();				
			}
			else {
				Calendar today = Calendar.getInstance();
				today.set(Calendar.HOUR_OF_DAY, 0);
				today.set(Calendar.MINUTE, 0);
				today.set(Calendar.SECOND, 0);
				today.set(Calendar.MILLISECOND, 0);
				
				Calendar tomorrow = (Calendar) today.clone();
				tomorrow.add(Calendar.DATE, 1);
				count = ActivityResult.find.where()
					.eq("account_id", userId)
					.eq("activity_content_id", contentId)
					.between("create_time", today.getTime(), tomorrow.getTime())
					.findRowCount();
			}
			
			ObjectNode node = Json.newObject();
			node.put("contentId", contentId);
			node.put("result", count != 0 ? "true" : "false");
			
			return ok(node);
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_ACTIVITY_RESULT_READ_FAILED,
					Messages.get("activityresult.failure"));
		}		
	}
	
	public Result queryResult(String contentId) {
		
		try {
			String sql = String.format(
					//"select t1.description description, count(*) number from activity_result t0 inner join activity_choice t1 on t0.choice_id = t1.id where t0.activity_content_id = '%s' group by t0.choice_id order by number desc;", contentId);
			        "select t1.description description, count(t0.choice_id) number from activity_choice t1 left join activity_result t0 on t0.choice_id = t1.id where t1.activity_content_id = '%s' group by t1.id order by number desc;", contentId);


			RawSql rawSql = 
					RawSqlBuilder.parse(sql)
					.columnMapping("t1.description", "name")
					.columnMapping("count(t0.choice_id)", "number")
					.create();
			
			Query<ResultGroup> query = Ebean.find(ResultGroup.class);
			
			query.setRawSql(rawSql);
			
			List<ResultGroup> list = query.findList();
			
			return ok(Json.toJson(list));			
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_ACTIVITY_RESULT_READ_FAILED,
					Messages.get("activityresult.failure"));
		}
		
	}
	
	public Result readByActivity(String contentId) {
		/*try {
			
			ActivityResult.find.where()
				.eq("activity_content_id", contentId)
				.
		}
		*/
		return ok();
	}
	
	public Result readById(String id) {
		try {
			ActivityResult result = ActivityResult.find.byId(id);
			if(result == null){
				return status(ErrDefinition.E_ACTIVITY_RESULT_READ_FAILED,
						Messages.get("activityresult.failure"));
			}
			return ok(Json.toJson(result));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_ACTIVITY_RESULT_READ_FAILED,
					Messages.get("activityresult.failure"));
		}
	}
	
	public Result readByAccountId(String accountId) {
		try {
			List<ActivityResult> resultList = ActivityResult.find.where()
					.eq("account_id", accountId).findList();
			
			return ok(Json.toJson(resultList));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_ACTIVITY_RESULT_READ_FAILED,
					Messages.get("activityresult.failure"));
		}
	}
	
	public Result readByContentId(String contentId) {
		try {
			List<ActivityResult> resultList = ActivityResult.find.where()
					.eq("activity_content_id", contentId).findList();
			
			return ok(Json.toJson(resultList));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_ACTIVITY_RESULT_READ_FAILED,
					Messages.get("activityresult.failure"));
		}
		
	}
	
	@AccessLevel(level=2)
	public Result update() {
		Form<ActivityResult> resultForm =
				Form.form(ActivityResult.class).bindFromRequest();
		
		if (resultForm.hasErrors()) {
			return status(ErrDefinition.E_ACTIVITY_RESULT_FORM_HASERROR,
					Messages.get("activityresult.failure"));
		}
		
		try {
			ActivityResult result = resultForm.get();
						
			result.account = new Account();
			result.account.id = result.account_id;
			
			result.content = new ActivityContent();
			result.content.id = result.activity_content_id;
			
			result.choice = new ActivityChoice();
			result.choice.id = result.choice_id;
			
			Ebean.update(result);
			
			return ok(Json.toJson(result));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_ACTIVITY_RESULT_UPDATE_FAILED,
					Messages.get("activityresult.failure"));
		}		
	}
	
	@AccessLevel(level=2)
	public Result delete(String id) {
		try {
			Ebean.delete(ActivityResult.class, id);
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_ACTIVITY_RESULT_DELETE_FAILED,
					Messages.get("activityresult.failure"));			
		}
		
		return ok();
	}
}
