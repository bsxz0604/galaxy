package controllers.program;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.activity.Activity;
import models.common.Account;
import models.program.Program;
import models.program.ProgramContent;
import models.shop.Shop;
import models.users.AppProfile;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;
import controllers.AppController;
import controllers.ErrDefinition;
import controllers.common.CodeGenerator;
import controllers.inceptors.AccessLevel;

public class ProgramContentController extends AppController {

	@AccessLevel(level=2)
	public Result create() {
		Form<ProgramContent> contentForm = 
				Form.form(ProgramContent.class).bindFromRequest();
		
		if (contentForm.hasErrors()) {
			return status(ErrDefinition.E_PROGRAM_CONTENT_FORM_HASERROR, 
					Messages.get("programcontent.failure"));
		}
		
		try {
			ProgramContent content = contentForm.get();
			content.id = CodeGenerator.GenerateUUId();
			String program_id = contentForm.data().get("program_id");
			content.program = Program.find.byId(program_id);
			
			Ebean.save(content);
			
			return ok(Json.toJson(content));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_PROGRAM_CONTENT_CREATE_FAILED, 
					Messages.get("programcontent.failure"));
		}
	}
	
	public Result read(String programId) {
		
		try {
			List<ProgramContent> contentList = 
					ProgramContent.find.where()
					.eq("program_id", programId)
					.findList();
			
			return ok(Json.toJson(contentList));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_PROGRAM_CONTENT_READ_FAILED, 
					Messages.get("programcontent.failure"));
		}
	}
	
	public Result readAccounts(String programId) {
		try {
			List<ProgramContent> contentList = 
					ProgramContent.find.where()
					.eq("program_id", programId)
					.eq("type", 0).findList();
			
			//double total = contentList.size();
			
//			ObjectNode node = Json.newObject();
//			double page=Math.ceil(total/sizePerPage);
			
			List<AppProfile> profileList = new ArrayList<AppProfile>();
			for (ProgramContent content : contentList) {
				Account account = Account.find.byId(content.content);
				if (account != null) {
					AppProfile profile = AppProfile.find.where()
					.eq("account_id", account.id)
					.eq("app_id", session("appId"))
					.findUnique();
					
					if (null != profile) {
						profile.programContentId = content.id;
						profileList.add(profile);
					}
				}
			}
			
			Collections.sort(profileList, new Comparator<AppProfile>() {
           
				@Override
				public int compare(AppProfile arg0, AppProfile arg1) {
					// TODO Auto-generated method stub
					return arg0.sex == 0 ? 1 : 0;
				}				
			});
					
			//node.put("totalPages", page);
			//node.put("rows", Json.toJson(profileList));
			return ok(Json.toJson(profileList));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_PROGRAM_CONTENT_READ_FAILED, 
					Messages.get("programcontent.failure"));
		}		
	}
	

	
	public Result readNew(Integer pageNumber, Integer sizePerPage) {
		try {
			
			String sql = String.format(
					"select t1.account_id,t1.app_id from account t0 join app_profile t1 on t0.id = t1.account_id");
			
			RawSql rawSql = 
					RawSqlBuilder.parse(sql)
					.columnMapping("t1.account_id", "id.accountId")
					.columnMapping("t1.app_id","id.appId")
					.create();
			Query<AppProfile> query = Ebean.find(AppProfile.class);
			query.setRawSql(rawSql)
			        .where().eq("is_verified",1)
			        .setFirstRow(pageNumber*sizePerPage)
			        .setMaxRows(sizePerPage)
					.orderBy("create_time desc") 
					.findList();;
			
			List<AppProfile> newList = query.findList();
					
			// dao xu  
			
			return ok(Json.toJson(newList));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_PROGRAM_CONTENT_READ_FAILED, 
					Messages.get("programcontent.failure"));
		}
	}
	
	@AccessLevel(level=2)
	public Result update() {
		Form<ProgramContent> contentForm = 
				Form.form(ProgramContent.class).bindFromRequest();
		
		if (contentForm.hasErrors()) { 
			return status(ErrDefinition.E_PROGRAM_CONTENT_FORM_HASERROR, 
					Messages.get("programcontent.failure"));			
		}
		
		try {
			ProgramContent content = contentForm.get();
			
			Ebean.update(content);
			
			return ok(Json.toJson(content));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_PROGRAM_CONTENT_UPDATE_FAILED,
					Messages.get("programcontent.failure"));
		}
	}
	
	@AccessLevel(level=2)
	public Result delete(String id) {
		try {
			
			Ebean.delete(ProgramContent.class, id);
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_PROGRAM_CONTENT_DELETE_FAILED,
					Messages.get("programcontent.failure"));
		}
		
		return ok();
	}
}
