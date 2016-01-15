package controllers.program;

import java.util.List;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.advertisement.Banner;
import models.program.Program;
import models.shop.Shop;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;
import controllers.AppController;
import controllers.BaseController;
import controllers.ErrDefinition;
import controllers.common.CodeGenerator;
import controllers.inceptors.AccessLevel;
import controllers.inceptors.ExistApp;

public class ProgramController extends AppController {
	
	@AccessLevel(level=2)
	public Result create() {
		Form<Program> programForm = 
				Form.form(Program.class).bindFromRequest();
		
		if (programForm.hasErrors()) {
			return status(ErrDefinition.E_PROGRAM_FORM_HASERROR,
					Messages.get("program.failure"));
		}
		
		try {
			Program program = programForm.get();
			
			program.id = CodeGenerator.GenerateUUId();
			program.app_id = session("appId");
			
			Ebean.save(program);
			
			return ok(Json.toJson(program));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_PROGRAM_CREATE_FAILED,
					Messages.get("program.failure"));
		}		
	}
	
	public Result readAll() {
		String appId = session("appId");
		return readByAppId(appId);
	}
	
	public Result read(Integer pageNumber, Integer sizePerPage) {
		
		try {
			List<Program> programList = 
					Program.find.where()
					.setFirstRow(pageNumber*sizePerPage)
					.setMaxRows(sizePerPage)
					.orderBy("show_time")
					.findList();
			
			double total = Program.find.where()
                    .findRowCount();
			
			ObjectNode node = Json.newObject();
			double page=Math.ceil(total/sizePerPage);
			
			node.put("totalPages", page);
			node.put("rows", Json.toJson(programList));

			return ok(node);
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_SHOP_READ_ERROR,
					Messages.get("shop.failure"));
		}
	}
	
	public Result readByAppId(String appId) {
		
		try {
			List<Program> programList = Program.find.where()
			.eq("app_id", appId)
			.eq("is_online", true)
			.orderBy("show_time desc")
			.setMaxRows(5)
			.findList();
			
			return ok(Json.toJson(programList));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_PROGRAM_READ_FAILED, 
					Messages.get("program.failure"));
		}
	}
	
	@AccessLevel(level=2)
	public Result update() {
		Form<Program> programForm = 
				Form.form(Program.class).bindFromRequest();
		
		if (programForm.hasErrors()) {
			return status(ErrDefinition.E_PROGRAM_FORM_HASERROR,
					Messages.get("program.failure"));
		}
		
		try {
			Program program = programForm.get();
			Ebean.update(program);
			
			return ok(Json.toJson(program));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_PROGRAM_UPDATE_FAILED,
					Messages.get("program.failure"));
		}		
	}
		
	@AccessLevel(level=2)
	public Result delete(String id) {
		try {			
			Ebean.delete(Program.class, id);
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_PROGRAM_DELETE_FAILED,
					Messages.get("program.failure"));
		}
		
		return ok();
	}
}
