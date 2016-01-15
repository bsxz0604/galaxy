/** Author: Michael Wang
 * Date: 2015-06-26
 * Description: Security check of all the actions except account related.
 */
package controllers.inceptors;

import controllers.ErrDefinition;
import play.Logger;
import play.libs.Json;
import play.mvc.*;
import play.mvc.Http.*;
import play.i18n.Messages;

public class Secured extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
        //Logger.info("userId from secure:" + ctx.session().get("userId"));
        return ctx.session().get("userId");
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return  ok(Json.newObject().put("code", ErrDefinition.E_ACCOUNT_UNAUTHENTICATED));
        		//status(ErrDefinition.E_ACCOUNT_UNAUTHENTICATED, 
        		//Messages.get("account.unauthenticated"));
    }
}