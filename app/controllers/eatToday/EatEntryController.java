package controllers.eatToday;

import java.util.Date;
import java.util.List;

import controllers.AppController;
import controllers.ErrDefinition;
import controllers.common.CodeGenerator;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;
import play.data.Form;
import models.common.Account;
import models.application.Application;
import models.eattoday.EatEntry;
import models.stock.Stock;
import models.stock.StockEntry;

import com.avaje.ebean.Ebean;


public class EatEntryController extends AppController {
	
	public Result create(){
		Form<EatEntry> eatEntryForm = Form.form(EatEntry.class)
				.bindFromRequest();
		if (eatEntryForm.hasErrors()) {
			return status(ErrDefinition.E_ENTRY_CREAT_ERROR,
					Messages.get("entryform.failure"));
		}

        EatEntry eatEntry = eatEntryForm.get();
		return internalCreate(eatEntry);
	}

    public Result internalCreate(EatEntry entry){

    	try {			
			String accountId = session("userId");
			String appId     = session("appId");               ///
			
			if (accountId == null || appId == null) {
				return status(ErrDefinition.E_ENTRY_CREAT_ERROR,
						Messages.get("entryform.failure"));
			}

			List<EatEntry> existaccount = 
					EatEntry.find.where()
					.eq("account_id", accountId)
					.findList();
			int num = 0;
			 num = existaccount.size();
			if (num < 3){
				entry.id=CodeGenerator.GenerateUUId();
				entry.account = Account.find.where().eq("id", (session("userId"))).findUnique();;     // generate a uuid for every entry_form
		     	entry.application = Application.find.where().eq("id",session("appId")).findUnique();;
		    	entry.create_time = new Date();
		    	entry.num=num;
		    	Ebean.save(entry);
				return ok(Json.toJson(entry));		
			
			}
			else return ok(Json.toJson(num));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_ENTRY_CREAT_ERROR, 
					Messages.get("entry.failure"));
		}		
    }

}
