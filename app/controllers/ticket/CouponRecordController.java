package controllers.ticket;

import java.util.Date;

import com.avaje.ebean.Ebean;

import models.common.Account;
import models.ticket.Coupon;
import models.ticket.CouponRecord;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import controllers.AppController;
import controllers.ErrDefinition;
import controllers.common.CodeGenerator;
import controllers.inceptors.AccessLevel;

public class CouponRecordController extends Controller {
    
    @AccessLevel(level=2)
    public Result create() {
        
        Form<CouponRecord> recordForm = 
                Form.form(CouponRecord.class).bindFromRequest();
        
        if (recordForm.hasErrors()) {
            return status(ErrDefinition.E_COUPON_RECORD_FORM_ERROR);
        }
        
        try {
            CouponRecord record = recordForm.get();
            record.id = CodeGenerator.GenerateUUId();
            
            record.account = Account.find.byId(record.user_id);
            record.coupon = Coupon.find.byId(record.coupon_id);
            record.create_time = new Date();
            
            Ebean.save(record);
            
            return ok(Json.toJson(record));
        }
        catch (Throwable e) {
            return status(ErrDefinition.E_COUPON_RECORD_CREATE_ERROR);
        }
    }
    
    
    public Result read() {
        DynamicForm form = Form.form().bindFromRequest();
        
        return ok();
    }
    
    public Result update() {
        return ok();
    }
    
    public Result delete() {
        return ok();
    }
}
