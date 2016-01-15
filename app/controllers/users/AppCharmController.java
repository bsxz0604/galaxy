package controllers.users;

import controllers.AppController;
import controllers.ErrDefinition;
import models.users.CharmValue;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;

public class AppCharmController  extends AppController{
	
	public Result readCharm(String account_id){
		try {	
			CharmValue fan_charm = CharmValue.find.where()
			          .eq("receiver_id", account_id)
			          .setOrderBy("create_time desc")
			          .setFirstRow(0)
			          .setMaxRows(1)
			          .findUnique();
			if (fan_charm!=null)
			  return ok(Json.toJson(fan_charm.total_charm_value));
			else
			   return ok(Json.toJson(""));
		}catch (Throwable e) {
			return status(ErrDefinition.E_CHARM_READ_FAILED,
					Messages.get("charm.failure"));
		}		
	}
}
