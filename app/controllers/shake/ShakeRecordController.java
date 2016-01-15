package controllers.shake;

import java.util.Date;
import java.util.List;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;

import models.common.Account;
import models.shake.ShakeRecord;
import models.shop.Shop;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import controllers.AppController;
import controllers.ErrDefinition;
import controllers.common.CodeGenerator;
import controllers.inceptors.AccessLevel;

public class ShakeRecordController extends Controller {

	public Result create() {
	    Logger.info("enter create");
		Form<ShakeRecord> shakeForm = 
				Form.form(ShakeRecord.class).bindFromRequest();
		
		if (shakeForm.hasErrors()) {
	        Logger.info("form has error");
			return status(ErrDefinition.E_SHAKE_RECORD_FORM_ERROR,
					Messages.get("shakerecord.failure"));
		}
        DynamicForm form = Form.form().bindFromRequest();
        
        String userId = form.get("userId");
        String appId = form.get("appId");   
        
		if (null == userId) {
            Logger.info("userId is null");
            return status(ErrDefinition.E_SHAKE_RECORD_CREATE_ERROR,
                    Messages.get("shakerecord.failure"));		    
		}
		
		try {
			ShakeRecord shakeRecord = shakeForm.get();
			
			shakeRecord.id = CodeGenerator.GenerateUUId();
			shakeRecord.shop = Shop.find.byId(shakeRecord.shop_id);
			shakeRecord.account = Account.find.byId(userId);
			
			shakeRecord.create_time = new Date();
			
			Ebean.save(shakeRecord);
			
            Logger.info("save it");
			return ok(Json.toJson(shakeRecord));
		}
		catch (Throwable e) {
            Logger.info("cannot save");
            Logger.info(e.getMessage());
			return status(ErrDefinition.E_SHAKE_RECORD_CREATE_ERROR,
					Messages.get("shakerecord.failure"));
		}
	}
	
	public Result read() {
		try {
			
			String sql = String.format(
					"select id, shop_id, user_id, create_time from shake_record" 
					+" join shop on shop.id = shop_id"
				    +" where shop.app_id = %s and user_id = %s",
				    session("appId"), session("userId"));

			RawSql rawSql = 
					RawSqlBuilder.parse(sql)
					.columnMapping("id", "id")
					.columnMapping("shop_id", "shop_id")
					.columnMapping("user_id", "user_id")
					.columnMapping("create_time", "create_time")
					.create();
			
			Query<ShakeRecord> query = Ebean.find(ShakeRecord.class);
			query.setRawSql(rawSql);
			
			List<ShakeRecord> shakeList = query.findList();
			
			return ok(Json.toJson(shakeList));			
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_SHAKE_RECORD_READ_ERROR, 
					Messages.get("shakerecord.failure"));
		}
	}
	
	public Result readAll() {
		try {
			
			String sql = String.format(
					"select id, shop_id, user_id, create_time from shake_record" 
					+" join shop on shop.id = shop_id"
				    +" where shop.app_id = %s",
				    session("appId"));

			RawSql rawSql = 
					RawSqlBuilder.parse(sql)
					.columnMapping("id", "id")
					.columnMapping("shop_id", "shop_id")
					.columnMapping("user_id", "user_id")
					.columnMapping("create_time", "create_time")
					.create();
			
			Query<ShakeRecord> query = Ebean.find(ShakeRecord.class);
			query.setRawSql(rawSql);
			
			List<ShakeRecord> shakeList = query.findList();
			
			return ok(Json.toJson(shakeList));			
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_SHAKE_RECORD_READ_ERROR, 
					Messages.get("shakerecord.failure"));
		}
	}
	
	@AccessLevel(level=2)
	public Result update() {
		Form<ShakeRecord> shakeForm = 
				Form.form(ShakeRecord.class).bindFromRequest();
		
		if (shakeForm.hasErrors()) {
			return status(ErrDefinition.E_SHAKE_RECORD_FORM_ERROR,
					Messages.get("shakerecord.failure"));
		}
		
		try {
			ShakeRecord shakeRecord = shakeForm.get();
			
			shakeRecord.shop = Shop.find.byId(shakeRecord.shop_id);
			shakeRecord.account = Account.find.byId(shakeRecord.user_id);
			
			Ebean.update(shakeRecord);
			
			return ok(Json.toJson(shakeRecord));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_SHAKE_RECORD_UPDATE_ERROR,
					Messages.get("shakerecord.failure"));
		}		
	}
	
	@AccessLevel(level=2)
	public Result delete(String id) {
		try {
			Ebean.delete(ShakeRecord.class, id);
			return ok();
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_SHAKE_RECORD_DELETE_ERROR,
					Messages.get("shakerecord.failure"));			
		}
	}

}
