package controllers.users;

import com.avaje.ebean.Ebean;

import models.users.AccountAppId;
import models.users.UserAddress;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import controllers.AppController;
import controllers.ErrDefinition;

public class UserAddressController extends AppController {

    public Result create() {
        Form<UserAddress> addressForm = 
                Form.form(UserAddress.class).bindFromRequest();
        
        if (addressForm.hasErrors()) {
            return status(ErrDefinition.E_USER_ADDRESS_FORM_ERROR);
        }
        
        try {
            UserAddress address = addressForm.get();
            
            address.id = new AccountAppId(session("userId"), session("appId"));
            
            Ebean.save(address);
            
            return ok(Json.toJson(address));
        }
        catch (Throwable e) {
            return status(ErrDefinition.E_USER_ADDRESS_CREATE_ERROR);
        }
    }
    
    public Result read() {
        try {
            AccountAppId id = new AccountAppId(session("userId"), session("appId"));
            
            UserAddress address = UserAddress.find.byId(id);
            
            return ok(Json.toJson(address));
        }
        catch (Throwable e) {
            return status(ErrDefinition.E_USER_ADDRESS_READ_ERROR);
        }
    }
    
    public Result update() {
        Form<UserAddress> addressForm = 
                Form.form(UserAddress.class).bindFromRequest();
        
        if (addressForm.hasErrors()) {
            return status(ErrDefinition.E_USER_ADDRESS_FORM_ERROR);
        }
        
        try {
            UserAddress address = addressForm.get();
            
            address.id = new AccountAppId(session("userId"), session("appId"));
            
            Ebean.update(address);
            
            return ok(Json.toJson(address));
        }
        catch (Throwable e) {
            return status(ErrDefinition.E_USER_ADDRESS_UPDATE_ERROR);
        }
    }
    
    public Result delete() {
        try {
            Ebean.delete(UserAddress.class, new AccountAppId(session("userId"), session("appId")));
            return ok();
        }
        catch (Throwable e) {
            return status(ErrDefinition.E_USER_ADDRESS_DELETE_ERROR);
        }
    }
    
    public Result saveAddress() {
        Form<UserAddress> addressForm = 
                Form.form(UserAddress.class).bindFromRequest();
        
        if (addressForm.hasErrors()) {
            return status(ErrDefinition.E_USER_ADDRESS_FORM_ERROR);
        }
        
        AccountAppId id = new AccountAppId(session("userId"), session("appId"));
        
        try {
            UserAddress address = UserAddress.find.byId(id);
            
            if (address == null) {
                return create();
            }
            else {
                return update();
            }
        }
        catch (Throwable e) {
            return status(ErrDefinition.E_USER_ADDRESS_CREATE_ERROR);
        }
        
        
    }
}
