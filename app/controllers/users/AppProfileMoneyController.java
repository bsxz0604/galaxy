package controllers.users;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.avaje.ebean.Ebean;

import controllers.AppController;
import controllers.ErrDefinition;
import controllers.inceptors.AccessLevel;
import models.application.Application;
import models.common.Account;
import models.users.AccountAppId;
import models.users.Money;
import models.users.MoneyRecord;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;

public class AppProfileMoneyController extends AppController {
    
    public Result saveMoney(Integer amount){
        
        /* Real money */
        Integer mei_money = amount ;
        
        if (mei_money > 100)
            mei_money += 100;
        else if (mei_money>30 & mei_money<100)
            mei_money+=30;
        
        Money userMoney = Money.find.where()
                .eq("account_id", session("userId"))
                .eq("app_id", session("appId"))
                .findUnique();
        
        if(null==userMoney){
            Money newUser = new Money();            
            
            newUser.id = new AccountAppId(session("userId"), session("appId"));
            newUser.operation_time = new Date();
            newUser.account = Account.find.byId(session("userId"));
            newUser.application = Application.find.byId(session("appId"));
            newUser.money = mei_money;
            
            userMoney = newUser;
            userMoney.operation_time = new Date();
            
            Ebean.save(userMoney);
        }
        else {
            userMoney.operation_time = new Date();
            userMoney.money += mei_money;
            Ebean.update(userMoney);
            
        }
        
        /* Record each charge */
        MoneyRecord moneyRecord = new MoneyRecord();
        
        moneyRecord.create_time = new Date();
        moneyRecord.account = userMoney.account;
        moneyRecord.application = userMoney.application;
        moneyRecord.money = amount;
        moneyRecord.action = "charge";
        Ebean.save(moneyRecord);
        return ok(Json.toJson(userMoney)); 
    }
        
    public Result getMoney(){
            Money userMoney = Money.find.where()
                    .eq("account_id", session("userId"))
                    .eq("app_id", session("appId"))
                    .findUnique();
            
        if(null==userMoney){
            return status(ErrDefinition.E_MONEY_ACCOUNT_FAILED,
                    Messages.get("appprofilegift.failure"));
        }else{
            return ok(Json.toJson(userMoney)); 
        }    
    }
        @AccessLevel(level=2)
        public Result getChargeRecord(){
            return ok(Json.toJson(""));                    
        }

}
