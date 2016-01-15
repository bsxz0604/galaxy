/** Author: Michael Wang
 * Date: 2015-06-26
 * Description: This controller is the base controller for all controllers except AccountController.
 * All other controllers must derive from this controller to keep the header access and security.
 * Log information should also be implemented in this controller.
 */
package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import controllers.inceptors.*;

@With(HeaderAccessAction.class)
@Security.Authenticated(Secured.class)
public class BaseController extends Controller {
	//This is the basic controller for all controllers 
	
	public Result failure(int code) {
		ObjectNode node = Json.newObject();
		
		node.put("code", code);
		
		return ok(node);
		
	}
}
