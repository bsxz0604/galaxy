package controllers.users;

import java.util.ArrayList;
import java.util.List;

import controllers.AppController;
import controllers.ErrDefinition;
import models.badge.Badge;
import models.users.CharmValue;
import models.users.InterestFan;
import models.users.Money;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;

class BadgeTable {
	public String badge_class;
	public String badge_name;
	public String badge_image;
}


public class AppHonorController  extends AppController {
	public Result readHonor(String account_id){
		try {	
			List<BadgeTable> badge_list = new ArrayList<BadgeTable>();
			
//			int interesteeNumber = InterestFan.find.where()
//					.eq("interesteeId", account_id)
//					.eq("app_id", session("appId"))
//					.findRowCount();
			
			List<Badge> badge_charmvalue = Badge.find.where()
					.eq("badge_class", "charmValue")
					.eq("app_id", session("appId"))
					.orderBy("value asc")
					.findList();	
			
			if (badge_charmvalue==null)
			{
				return ok(Json.toJson("[]"));	
			}
			
			CharmValue fan_charm = CharmValue.find.where()
			          .eq("receiver_id", account_id)
			          .setOrderBy("create_time desc")
			          .setFirstRow(0)
			          .setMaxRows(1)
			          .findUnique();

			for(Badge itera:badge_charmvalue) {
				BadgeTable newBadge = new BadgeTable();
				newBadge.badge_class = itera.badge_class;
				newBadge.badge_name = itera.badge_name;
				if (fan_charm!=null) {
					if (fan_charm.total_charm_value>=itera.value) {
						newBadge.badge_image =  itera.badge_image;
					}
					else {
					newBadge.badge_image =  itera.badge_grey_image;
					}
				}
				else {
					newBadge.badge_image = itera.badge_grey_image;
				}
			    badge_list.add(newBadge);
			}
					
			Money userMoney = Money.find.where()
					.eq("account_id", account_id)
					.eq("app_id", session("appId"))
					.findUnique();
		
			List<Badge> badge_money = Badge.find.where()
					.eq("badge_class",  "money")
					.eq("app_id",  session("appId"))
					.orderBy("value asc")
					.findList();

			   for(Badge itera:badge_money) {
				  BadgeTable newBadge = new BadgeTable();
				  newBadge.badge_class = itera.badge_class;
				  newBadge.badge_name = itera.badge_name;
				  if (userMoney!=null) {
						if (userMoney.money>=itera.value) {
							newBadge.badge_image =  itera.badge_image;
						}
						else {
						newBadge.badge_image =  itera.badge_grey_image;
						}
					}
					else {
						newBadge.badge_image = itera.badge_grey_image;
					}
				   badge_list.add(newBadge);
			   }
			return ok(Json.toJson(badge_list));		
		}catch (Throwable e) {
			return status(ErrDefinition.E_HONOR_READ_FAILED,
					Messages.get("honor.failure"));
		}		
	}
}
