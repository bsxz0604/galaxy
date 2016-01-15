package controllers.ticket;

import java.util.Date;
import java.util.List;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import models.application.Application;
import models.shop.Shop;
import models.ticket.Coupon;
import controllers.AppController;
import controllers.ErrDefinition;
import controllers.common.CodeGenerator;
import controllers.inceptors.AccessLevel;

public class CouponController extends AppController {
    
    @AccessLevel(level=2)
    public Result create() {
        Form<Coupon> couponForm = 
                Form.form(Coupon.class).bindFromRequest();
        
        if (couponForm.hasErrors()) {
            return status(ErrDefinition.E_COUPON_FORM_ERROR);
        }
        
        try {
            Coupon coupon = couponForm.get();
            
            coupon.id = CodeGenerator.GenerateUUId();
            coupon.app = Application.find.byId(coupon.app_id);
            coupon.create_time = new Date();
                        
            Ebean.save(coupon);
            
            return ok(Json.toJson(coupon));
        }
        catch (Throwable e) {
            return status(ErrDefinition.E_COUPON_CREATE_ERROR);
        }
    }
    
    public Result read(String id) {
        try {
            Coupon coupon = Coupon.find.byId(id);
            if(coupon == null) {
            	 return status(ErrDefinition.E_COUPON_READ_ERROR);
            }   
            return ok(Json.toJson(coupon));
        }
        catch (Throwable e) {
            return status(ErrDefinition.E_COUPON_READ_ERROR);
        }
    }
    
    public Result readAll(Integer pageNumber, Integer sizePerPage) {
        try {
            String appId = session("appId");
            List<Coupon> couponList = Coupon.find.where()
                    .eq("app_id", appId)
                    .setFirstRow(pageNumber*sizePerPage)
                    .setMaxRows(sizePerPage)
                    .orderBy("id")
                    .findList();
            
            Integer total = Coupon.find.where()
                    .eq("app_id", appId)
                    .findRowCount();
            
            ObjectNode node = Json.newObject();
            node.put("totalPages", total / sizePerPage + 1);
            node.put("rows", Json.toJson(couponList));
            
            return ok(node);
        }
        catch (Throwable e) {
            return status (ErrDefinition.E_COUPON_READ_ERROR);
        }
    }
    
    @AccessLevel(level=2)
    public Result update() {
        Form<Coupon> couponForm = 
                Form.form(Coupon.class).bindFromRequest();
        
        if (couponForm.hasErrors()) {
            return status(ErrDefinition.E_COUPON_FORM_ERROR);
        }
        
        try {
            Coupon coupon = couponForm.get();
                        
            Ebean.save(coupon);
            
            return ok(Json.toJson(coupon));
        }
        catch (Throwable e) {
            return status(ErrDefinition.E_COUPON_UPDATE_ERROR);
        }        
    }
    
    @AccessLevel(level=2)
    public Result delete(String id) {
        try {
            Ebean.delete(Coupon.class, id);
            
            return ok();
        }
        catch (Throwable e) {
            return status(ErrDefinition.E_COUPON_DELETE_ERROR);
        }
    }
}
