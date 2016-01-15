package controllers.users;

import models.badge.Badge;
import models.advertisement.Banner;
import models.application.Application;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;

import java.util.List;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.node.ObjectNode;

import controllers.AppController;
import controllers.ErrDefinition;
import controllers.common.CodeGenerator;
import controllers.inceptors.AccessLevel;

public class BadgeController extends AppController {

	@AccessLevel(level=2)
	public Result createBadge(){
		
		Form<Badge> badge = 
				Form.form(Badge.class).bindFromRequest();
		
		if (badge.hasErrors()) {
			return status(ErrDefinition.E_GIFT_READ_FAILED,
					Messages.get("git.failure"));
		}
		try{
			 String appId=session("appId");
			 Badge bad = badge.get(); 
			 bad.id=CodeGenerator.GenerateUUId();
			 bad.app_id=appId;
		     Ebean.save(bad);
		  return ok(Json.toJson(bad));
		}
		catch (Throwable e){
			return status(ErrDefinition.E_GIFT_TABLE_ERROR,
					Messages.get("git.failure"));
		}
	}
	
	public Result readAll(Integer pageNumber, Integer sizePerPage) {
		try {
			List<Badge> badgeList = 
					Badge.find.where()
					.setFirstRow(pageNumber*sizePerPage)
					.setMaxRows(sizePerPage)
					.findList();
			
			double total = Badge.find.where()
                    .findRowCount();
			
			ObjectNode node = Json.newObject();
			double page=Math.ceil(total/sizePerPage);
			
			node.put("totalPages", page);
			node.put("rows", Json.toJson(badgeList));
			
			return ok(node);
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_SHOP_READ_ERROR,
					Messages.get("shop.failure"));
		}
	}
	
	public Result delete(String id) {
		Badge badge = Badge.find.byId(id);
		badge.delete();
		return ok();
	}
	
	public Result update() {
		Form<Badge> badge = 
				Form.form(Badge.class).bindFromRequest();
		
		if (badge.hasErrors()) {
			return status (ErrDefinition.E_GIFT_READ_FAILED,
					Messages.get("giftUpdate.failure"));
		}
		
		try{
			Badge bad = badge.get();
			Ebean.update(bad);
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_GIFT_READ_FAILED,
					Messages.get("giftUpdate.failure"));
		}
		return ok();
	}

}
