package controllers.inceptors;

import com.fasterxml.jackson.databind.node.ObjectNode;

import controllers.ErrDefinition;
import play.i18n.Messages;
import play.libs.Json;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.SimpleResult;

public class AppCheckAction extends Action<ExistApp> {

	@Override
	public Promise<SimpleResult> call(Context context) throws Throwable {
		// TODO Auto-generated method stub
		String appId = context.session().get("appId");
		
		//there is no application id
		if (appId == null) {			
			SimpleResult result = ok(Json.newObject().put("code", ErrDefinition.E_APP_NOT_SELECTED));
			return Promise.pure(result);
		}
		
		return delegate.call(context);
	}

}
