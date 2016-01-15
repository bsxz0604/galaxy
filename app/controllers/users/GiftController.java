package controllers.users;

import models.gift.Gift;
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
import controllers.inceptors.AccessLevel;

public class GiftController extends AppController {

	@AccessLevel(level=2)
	public Result createGift(){
		
		Form<Gift> gift = 
				Form.form(Gift.class).bindFromRequest();
		
		if (gift.hasErrors()) {
			return status(ErrDefinition.E_GIFT_READ_FAILED,
					Messages.get("git.failure"));
		}
		try{
			 String appId=session("appId");
			 List<Gift> num = Gift.find.where()
					 .orderBy("id")
					 .findList();
			 int total = num.size();
			 Integer maxNum = num.get(total-1).id;
		     Gift gif = gift.get(); 
		     gif.id = maxNum+1;
		     gif.app_id=appId;
		     Ebean.save(gif);
		  return ok(Json.toJson(gif));
		}
		catch (Throwable e){
			return status(ErrDefinition.E_GIFT_TABLE_ERROR,
					Messages.get("git.failure"));
		}
	}
	
	public Result readAll(Integer pageNumber, Integer sizePerPage) {
		try {
			List<Gift> giftList = 
					Gift.find.where()
					.setFirstRow(pageNumber*sizePerPage)
					.setMaxRows(sizePerPage)
					.findList();
			
			double total = Gift.find.where()
                    .findRowCount();
			
			ObjectNode node = Json.newObject();
			double page=Math.ceil(total/sizePerPage);
			
			node.put("totalPages", page);
			node.put("rows", Json.toJson(giftList));
			
			return ok(node);
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_SHOP_READ_ERROR,
					Messages.get("shop.failure"));
		}
	}
	
	public Result delete(Integer id) {
		Gift gift = Gift.find.byId(id);
		gift.delete();
		return ok();
	}
	
	public Result update() {
		Form<Gift> gift = 
				Form.form(Gift.class).bindFromRequest();
		
		if (gift.hasErrors()) {
			return status (ErrDefinition.E_GIFT_READ_FAILED,
					Messages.get("giftUpdate.failure"));
		}
		
		try{
			String appId=session("appId");
			Gift gif = gift.get();
			gif.app_id=appId;
			Ebean.update(gif);
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_GIFT_READ_FAILED,
					Messages.get("giftUpdate.failure"));
		}
		return ok();
	}

}
