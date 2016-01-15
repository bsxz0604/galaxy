package controllers.users;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.avaje.ebean.Ebean;

import models.users.AccountAppId;
import models.users.AppProfile;
import models.users.InterestFan;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;
import controllers.AppController;
import controllers.ErrDefinition;
import controllers.application.ApplicationController;
import controllers.common.CodeGenerator;
import controllers.inceptors.AccessLevel;

public class InterestFanController extends AppController {
	
	public Result create() {
		Form<InterestFan> interestForm = 
				Form.form(InterestFan.class).bindFromRequest();
		
		if (interestForm.hasErrors()) {
			return status(ErrDefinition.E_INTERESTFAN_FORM_HASERROR, 
					Messages.get("interestfan.failure"));
		}
		
		try {
			InterestFan interest = interestForm.get();
						
			interest.id = CodeGenerator.GenerateUUId();
			interest.interesterId = session("userId");
			interest.appId = session("appId");
			
			/*
			interest.account = new Account();
			interest.account.id = session("userId");
			
			interest.application = new Application();
			interest.application.id = session("appId");
			
			interest.interestAccount = new Account();
			interest.interestAccount.id = interest.interestAccount.id;
			*/
			if (interest.interesteeId == null || 
					(interest.interesteeId == interest.interesterId)) {
				return status(ErrDefinition.E_INTERESTFAN_SAME_ACCOUNT,
						Messages.get("interestfan.failure"));
			}
			
			Ebean.save(interest);
			
//			CharmValue fan_charm = CharmValue.find.where()
//					.eq("user_id", interest.interesteeId)
//					.findUnique();
//			SimpleDateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			if (null == fan_charm) {
//				fan_charm = new CharmValue();
//			    fan_charm.application = Application.find.byId(interest.appId);
//			    fan_charm.account = Account.find.byId(interest.interesteeId);		    
//			    fan_charm.create_time = new Date();
//			    fan_charm.charm_value = 100;
//			    fan_charm.total_charm_value = 100;
//			    Ebean.save(fan_charm);
//			}
//			else {
//				fan_charm.create_time = new Date();
//			        fan_charm.charm_value = 100;
//				fan_charm.total_charm_value += 100;
//				Ebean.save(fan_charm);
//			}	
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_INTERESTFAN_CREATE_FAILED,
					Messages.get("interestfan.failure"));
		}
		
		
		
		return ok();
	}
	
	public Result isInterested(String otherId) {
		try {
			String appId = session("appId");
			String userId = session("userId");
			
			InterestFan fan = InterestFan.find.where()
					.eq("interesterId", userId)
					.eq("appId", appId)
					.eq("interesteeId", otherId).findUnique();
			
			return fan == null ? ok(Json.toJson(false)) : ok(Json.toJson(true));						
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_INTERESTFAN_READ_FAILED,
					Messages.get("interestfan.failure"));
		}
	}
	
	public Result readNumberById(String userId) {
		try {
			int interesteeNumber = InterestFan.find.where()
			.eq("interesterId", userId).eq("app_id", session("appId")).findRowCount();
			
			int interesterNumber = InterestFan.find.where()
					.eq("interesteeId", userId).eq("app_id", session("appId")).findRowCount();
			
			return ok(Json.newObject().put("eId", interesterNumber).put("rId", interesteeNumber));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_INTERESTFAN_READ_FAILED,
					Messages.get("interestfan.failure"));
		}		
	}
	
	public Result readNumber() {
		return readNumberById(session("userId"));
	}

	/**
	 * Returns the userId list whom a user is interested in.
	 * @return
	 */
	public Result readInterester(String userId) {
		
		try {
			
			String appId = session("appId");
			List<InterestFan> fanList = 
					InterestFan.find.where()
					.eq("interesterId", userId)
					.eq("appId", appId).findList();
			
			return ok(Json.toJson(fanList));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_INTERESTFAN_READ_FAILED,
					Messages.get("interestfan.failure"));
		}
	}
	
	public Result readInteresterIdByPage(String userId, Integer pageNumber, Integer sizePerPage) {
		return readByPage("interesterId", userId, pageNumber, sizePerPage);
	}
	
	public Result readInteresteeIdByPage(String userId, Integer pageNumber, Integer sizePerPage) {
		return readByPage("interesteeId", userId, pageNumber, sizePerPage);  // fans of userId
	}
	
	private Result readByPage(String interestType, String userId, Integer pageNumber, Integer sizePerPage) {
		try {
			String appId = session("appId");			
			
			List<InterestFan> fanList = InterestFan.find.where()
					.eq(interestType, userId)
					.eq("appId", appId)
					.setFirstRow(pageNumber*sizePerPage)
					.setMaxRows(sizePerPage)
					.findList();
			
			List<AppProfile> profileList = new ArrayList<AppProfile>();
			
			for (InterestFan fan : fanList) {
				AccountAppId id = null; 
				if (interestType.compareTo("interesteeId") == 0) {
					id = new AccountAppId(fan.interesterId, appId);
				}
				else if (interestType.compareTo("interesterId") == 0) {
					id = new AccountAppId(fan.interesteeId, appId);
				}
				else {
					continue;
				}
				
				AppProfile profile = AppProfile.find.byId(id);
				
				if (profile != null && profile.head_image != null) {
					int idxOfSite = profile.head_image.indexOf(Messages.get("site.name"));
					if (idxOfSite >= 0) {
						String physicalFile = "public" + profile.head_image.substring(
								idxOfSite + Messages.get("site.name").length());
						
						File file = new File(physicalFile);
						BufferedImage img = null;
						try {
							img = ImageIO.read(file); 
				            BufferedImage blurImg = ApplicationController.resizeImage(img, 60);
							profile.head_imgbase64 = ApplicationController.getImageBase64(blurImg);					
						}
						catch (Throwable e) {
							img = null;
						}
					}
				}
				
				profileList.add(profile);
			}
			
			return ok(Json.toJson(profileList));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_INTERESTFAN_READ_FAILED,
					Messages.get("interestfan.failure"));
		}
	}
	
	/**
	 * Returns the userId list who are interested in the user.
	 * @return
	 */
	public Result readInterestee(String userId) {
		try {
			
			String appId = session("appId");
			List<InterestFan> fanList = 
					InterestFan.find.where()
					.eq("interesteeId", userId)
					.eq("appId", appId).findList();
			
			return ok(Json.toJson(fanList));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_INTERESTFAN_READ_FAILED,
					Messages.get("interestfan.failure"));
		}
	}
	
	/**
	 * Usually it is impossible to update for a normal user.
	 * @return
	 */
	@AccessLevel(level=2)
	public Result update() {
		Form<InterestFan> fanForm = 
				Form.form(InterestFan.class).bindFromRequest();
		
		if (fanForm.hasErrors()) {
			return status(ErrDefinition.E_INTERESTFAN_FORM_HASERROR, 
					Messages.get("interestfan.failure"));
		}
		
		try {
			InterestFan fan = fanForm.get();
			
			InterestFan targetFan = InterestFan.find.byId(fan.id);
			
			if (targetFan == null) {
				return status(ErrDefinition.E_INTERESTFAN_NOT_FOUND,
						Messages.get("interestfan.failure"));
			}
			
			if (fan.interesterId == null || fan.appId == null || fan.interesteeId == null) {
				return status(ErrDefinition.E_INTERESTFAN_FORM_HASERROR,
						Messages.get("interestfan.failure"));
			}
			Ebean.update(fan);
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_INTERESTFAN_UPDATE_FAILED,
					Messages.get("interestfan.failure"));
		}
		
		return ok();
	}
	
	public Result delete(String interesteeId) {
		try {
			String interesterId = session("userId");
			String appId        = session("appId");
			
			InterestFan fan = InterestFan.find.where()
					.eq("interesterId", interesterId)
					.eq("appId", appId)
					.eq("interesteeId", interesteeId).findUnique();
			Ebean.delete(fan);
			
//			CharmValue fan_charm = CharmValue.find.where()
//					.eq("user_id", interesteeId)
//					.findUnique();
//			;
//			fan_charm.create_time = new Date();
//			fan_charm.charm_value = -100;
//			fan_charm.total_charm_value -= 100;
//			Ebean.save(fan_charm);			
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_INTERESTFAN_DELETE_FAILED,
					Messages.get("interestfan.failure"));
		}
		
		return ok();
	}
}
