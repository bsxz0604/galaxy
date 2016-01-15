package controllers.shop;

import java.util.List;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.application.Application;
import models.shop.Shop;
import play.Logger;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;
import controllers.AppController;
import controllers.ErrDefinition;
import controllers.common.CodeGenerator;
import controllers.inceptors.AccessLevel;

public class ShopController extends AppController {
	
	@AccessLevel(level=2)
	public Result create() {
		Form<Shop> shopForm = 
				Form.form(Shop.class).bindFromRequest();
		
		if (shopForm.hasErrors()) {
			return status(ErrDefinition.E_SHOP_FORM_ERROR,
					Messages.get("shop.failure"));
		}
		
		try {
			Shop shop = shopForm.get();
			
			shop.id = CodeGenerator.GenerateUUId();
			shop.app = Application.find.byId(session("appId"));
			
			Ebean.save(shop);
			
			return ok(Json.toJson(shop));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_SHOP_CREATE_ERROR,
					Messages.get("shop.failure"));
		}
	}
	
	public Result read(Integer pageNumber, Integer sizePerPage) {
		
		try {
			List<Shop> shopList = 
					Shop.find.where()
					.eq("app_id", session("appId"))
					.setFirstRow(pageNumber*sizePerPage)
					.setMaxRows(sizePerPage)
					.orderBy("name")
					.findList();
			
			Integer total = Shop.find.where()
                    .eq("app_id", session("appId"))
                    .findRowCount();
			
			ObjectNode node = Json.newObject();
			
			node.put("totalPages", total / sizePerPage + 1);
			node.put("rows", Json.toJson(shopList));
			
			return ok(node);
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_SHOP_READ_ERROR,
					Messages.get("shop.failure"));
		}
	}
	
	@AccessLevel(level=2)
	public Result update() {
		Form<Shop> shopForm = 
				Form.form(Shop.class).bindFromRequest();
		
		if (shopForm.hasErrors()) {
			return status(ErrDefinition.E_SHOP_FORM_ERROR,
					Messages.get("shop.failure"));
		}
		
		try {
			Shop shop = shopForm.get();
            Shop shopToUpdate = Shop.find.byId(shop.id);
            shop.app = shopToUpdate.app;
			
			Ebean.update(shop);
			
			return ok(Json.toJson(shop));
			
		}
		catch (Throwable e) {
		    Logger.info(e.getMessage());
			return status(ErrDefinition.E_SHOP_UPDATE_ERROR,
					Messages.get("shop.failure"));
		}
	}
	
	@AccessLevel(level=2)
	public Result delete(String id) {
		
		try {
			Ebean.delete(Shop.class, id);			
			return ok();
		}
		catch (Throwable e) {
		    Logger.info(e.getMessage());
			return status(ErrDefinition.E_SHOP_DELETE_ERROR,
					Messages.get("shop.failure"));
		}
	}
}
