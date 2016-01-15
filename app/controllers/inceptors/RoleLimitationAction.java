package controllers.inceptors;

import controllers.ErrDefinition;
import play.i18n.Messages;
import play.libs.Json;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.SimpleResult;

public class RoleLimitationAction extends Action<AccessLevel> {
	
	@Override
	public Promise<SimpleResult> call(Context context) throws Throwable {
		// TODO Auto-generated method stub
		String userRole = context.session().get("userRole");
		
		if (null == userRole) {
			SimpleResult result = ok(Json.newObject().put("code", ErrDefinition.E_ACCOUNT_NO_RIGHT)); 
			return Promise.pure(result);
		}
		
		int nUserRole = Integer.parseInt(userRole);
		
		if (configuration.level() > nUserRole) {
			SimpleResult result = ok(Json.newObject().put("code", ErrDefinition.E_ACCOUNT_NO_RIGHT)); 
			return Promise.pure(result);			
		}
		return delegate.call(context);
	}
}
