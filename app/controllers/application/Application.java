package controllers.application;

//import controllers.AccessLevel;
import controllers.BaseController;
import controllers.common.CodeGenerator;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {
    
	//@AccessLevel(level=1)
    public static Result index() {
    	
        String result = null;
        try {
        	result = CodeGenerator.GenerateRandomNumber();
        }
        catch (Throwable e) {
        	result = null;
        }
        
        return ok(result);
    }

}
