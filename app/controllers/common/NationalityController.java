package controllers.common;

import java.util.List;

import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;
import models.common.Nationality;
import controllers.BaseController;
import controllers.ErrDefinition;

public class NationalityController extends BaseController {

	public Result readList() {
		try {
			List<Nationality> nationList = Nationality.find.all();

			return ok(Json.toJson(nationList));
		} catch (Throwable e) {
			return status(ErrDefinition.E_NATIONALITY_READ_FAILED,
					Messages.get("nationality.failure"));
		}
	}

	public Result readById(Integer id) {
		try {
			Nationality nation = Nationality.find.byId(id);
			if (nation == null) {
				return status(ErrDefinition.E_NATIONALITY_READ_FAILED,
						Messages.get("nationality.failure"));
			}
			return ok(Json.toJson(nation));
		} catch (Throwable e) {
			return status(ErrDefinition.E_NATIONALITY_READ_FAILED,
					Messages.get("nationality.failure"));
		}
	}
}
