package controllers.common;

import models.common.Constellation;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;
import controllers.BaseController;
import controllers.ErrDefinition;

public class ConstellationController extends BaseController {

	public Result readList() {
		
		try {
			return ok(Json.toJson(Constellation.find.all()));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_CONSTELLATION_READ_FAILED,
					Messages.get("constellation.failure"));
		}
	}
	
	public Result readById(Integer id) {
		try {
			return ok(Json.toJson(Constellation.find.byId(id)));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_CONSTELLATION_READ_FAILED,
					Messages.get("constellation.failure"));			
		}
	}
}
