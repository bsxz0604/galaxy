package controllers.users;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;

import play.Logger;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;
import models.common.Constellation;
import models.program.ProgramContent;
import models.users.AccountAppId;
import models.users.AppProfile;
import models.common.Account;
import models.data.Data;
import controllers.AppController;
import controllers.ErrDefinition;
import controllers.application.ApplicationController;
import controllers.common.CodeGenerator;
import controllers.inceptors.AccessLevel;

public class AppProfileController extends AppController {

	@AccessLevel(level=2)
	public Result create() {
		
		Form<AppProfile> profileForm = 
				Form.form(AppProfile.class).bindFromRequest();
		
		if (profileForm.hasErrors()) {
			return status(ErrDefinition.E_APPPROFILE_FORM_HASERROR,
					Messages.get("appprofile.failure"));
		}

		AppProfile profile = profileForm.get();
	
		return internalCreate(profile);
	}
	
	@AccessLevel(level=2)
	public Result createAll() {
		
		Form<AppProfile> profileForm = 
				Form.form(AppProfile.class).bindFromRequest();
		
		if (profileForm.hasErrors()) {
			return status(ErrDefinition.E_APPPROFILE_FORM_HASERROR,
					Messages.get("appprofile.failure"));
		}

		String appId     = session("appId");
		AppProfile profile = profileForm.get();
	
	
	    Account account = new Account();
		account.id = CodeGenerator.GenerateUUId();
	    String accountId = account.id;
		account.role = 0;
		account.username = "ol-"+CodeGenerator.GenerateUUId();
        account.type = 1;
		account.create_time = new Date();
		Ebean.save(account);
		
		profile.id = new AccountAppId(accountId,appId);
		profile.is_verified = true;
		
		if(profile.constellation.id == 0){profile.constellation.id=1;}
		Ebean.save(profile);
		return ok(Json.toJson(profile));
	}
	
	public Result internalCreate(AppProfile profile) {
		try {			
			String accountId = session("userId");
			String appId     = session("appId");
			
			if (accountId == null || appId == null) {
				return status(ErrDefinition.E_APPPROFILE_SESSION_INCORRECT,
						Messages.get("appprofile.failure"));
			}
			
			profile.id = new AccountAppId(accountId, appId);
			if (AppProfile.find.byId(profile.id) != null) {
			    
			};
			//profile.search_id = CodeGenerator.GenerateRandomNumber();
			
			if (profile.name == null || profile.name.isEmpty()) {
				profile.name = CodeGenerator.GenerateRandomNumber();
			}
			
			profile.is_verified = false;
			Ebean.save(profile);
			
			return ok(Json.toJson(profile));
		}
		catch (Throwable e) {
		    Logger.info("Profile Error: " + e.getMessage());
			return status(ErrDefinition.E_APPPROFILE_CREATE_FAILED, 
					Messages.get("appprofile.failure"));
		}		
	}
	
	public Result read() {
		String userId = session("userId");
		
		return readById(userId);
	}
	
	public Result readAll() {
		try {
			List<AppProfile> hotList = 
					AppProfile.find.where()
					.eq("application.id", session("appId"))
					.eq("is_verified", 1)
					.findList();

	
			Collections.sort(hotList, new Comparator<AppProfile>() {
		           
				@Override
				public int compare(AppProfile arg0, AppProfile arg1) {
					// TODO Auto-generated method stub
					return arg0.sex == 0 ? 1 : 0;
				}				
			});
			
			return ok(Json.toJson(hotList));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_PROGRAM_CONTENT_READ_FAILED, 
					Messages.get("programcontent.failure"));
		}
	}
	
	public Result readProgram(String programId) {
		try {
			List<AppProfile> hotList = 
					AppProfile.find.where()
					.eq("application.id", session("appId"))
					.eq("is_verified", 1)
					.findList();
			for (AppProfile list : hotList) {
				ProgramContent programContent = ProgramContent.find.where()
						.eq("program_id", programId)
						.eq("content",list.id.accountId )
						.findUnique();
					
					if (null != programContent) {
						list.isSelected = true;
					}
				}
	
			
			Collections.sort(hotList, new Comparator<AppProfile>() {
		           
				@Override
				public int compare(AppProfile arg0, AppProfile arg1) {
					// TODO Auto-generated method stub
					return arg0.sex == 0 ? 1 : 0;
				}				
			});
			
			return ok(Json.toJson(hotList));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_PROGRAM_CONTENT_READ_FAILED, 
					Messages.get("programcontent.failure"));
		}
	}
	
	public Result readBySearchId(String keyword, Integer pageNumber, Integer sizePerPage) {
	    String appId = session("appId");
        List<AppProfile> profileList = AppProfile.find.where()
                .or(com.avaje.ebean.Expr.like("search_id", "%"+keyword+"%"), 
                        com.avaje.ebean.Expr.ilike("name", "%"+keyword+"%"))
                .eq("app_id", appId)
                .orderBy("search_id")
                .setFirstRow(pageNumber*sizePerPage)
                .setMaxRows(sizePerPage)
                .findList();
        
        for (AppProfile profile : profileList) {
            if (profile.birthday != null) {
                Calendar birthday = Calendar.getInstance();
                birthday.setTime(profile.birthday);
                
                birthday.add(Calendar.HOUR_OF_DAY, 1);
                profile.birthday = birthday.getTime();
            }
        }
        
        return ok(Json.toJson(profileList));
	}
	
	public Result readById(String userId) {
        String accountId = userId;
        String appId     = session("appId");
        
        AccountAppId id = new AccountAppId(accountId, appId);
        
        AppProfile profile = AppProfile.find.byId(id);
        
        return readUserProfile(profile);
	}
	
	private void SetImage(AppProfile profile) {
        //update the birthday for summer time;
        if (profile.birthday != null) {
            Calendar birthday = Calendar.getInstance();
            birthday.setTime(profile.birthday);
            
            birthday.add(Calendar.HOUR_OF_DAY, 1);
            profile.birthday = birthday.getTime();
        }
        
        if (profile.head_image != null) {
            int idxOfSite = profile.head_image.indexOf(Messages.get("site.name"));
            if (idxOfSite >= 0) {
                String physicalFile = "public" + profile.head_image.substring(
                        idxOfSite + Messages.get("site.name").length());
                
                File file = new File(physicalFile);
                BufferedImage img;
                try {
                    img = ImageIO.read(file);
                    BufferedImage blurImg = ApplicationController.setGaussianBlur(img, 20);
                    profile.head_imgbase64 = ApplicationController.getImageBase64(blurImg);  
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } 
            }
        }	    
	}
	
	private Result readUserProfile(AppProfile profile) {
	    try {
			if (null == profile) {
				return status(ErrDefinition.E_APPPROFILE_NOT_FOUND,
						Messages.get("appprofile.failure"));
			}
			
			SetImage(profile);
			
			return ok(Json.toJson(profile));
		}
		catch (Exception e) {
			return status(ErrDefinition.E_APPPROFILE_READ_FAILED, 
					e.getMessage());
		}
	}
	
	/**
	 * This is only for owner to update his profile.
	 * @return
	 */
	public Result update() {
		try {

			Form<AppProfile> profileForm =
					Form.form(AppProfile.class).bindFromRequest();
			
			if (profileForm.hasErrors()) {
				return status(ErrDefinition.E_APPPROFILE_FORM_HASERROR,
						Messages.get("appprofile.failure"));			
			}			
			
			String userId = session("userId");			
			String appId = session("appId");
			
			AccountAppId id = new AccountAppId(userId, appId);
			
			AppProfile profile = profileForm.get();
			AppProfile currentProfile = AppProfile.find.byId(id);
			
			if (null == currentProfile) {
				return status(ErrDefinition.E_APPPROFILE_NOT_FOUND,
						Messages.get("appprofile.failure"));
			}
			
			profile.id = currentProfile.id;
			//profile.name = currentProfile.name;
			profile.search_id = currentProfile.search_id;
			
			String cId = profileForm.data().get("constellation_id");
			if (cId != null) {
				profile.constellation = Constellation.find.byId(
						Integer.parseInt(cId));				
			}
			

			Ebean.update(profile);
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_APPPROFILE_UPDATE_FAILED,
					Messages.get("appprofile.failure"));
		}
		
		return ok();
	}
	
	/**
	 * Not sure whether the access level will be invoked if using it in update().
	 * So Just duplicate the code for administrator to do update.
	 * @param userId
	 * @return
	 */
	@AccessLevel(level=2)
	public Result updateById(String userId) {
		Form<AppProfile> profileForm =
				Form.form(AppProfile.class).bindFromRequest();
		
		if (profileForm.hasErrors()) {
			return status(ErrDefinition.E_APPPROFILE_FORM_HASERROR,
					Messages.get("appprofile.failure"));			
		}
		
		try {
			String appId = session("appId");
			
			AccountAppId id = new AccountAppId(userId, appId);
			
			AppProfile profile = profileForm.get();
			AppProfile currentProfile = AppProfile.find.byId(id);
			
			if (null == currentProfile) {
				return status(ErrDefinition.E_APPPROFILE_NOT_FOUND,
						Messages.get("appprofile.failure"));
			}
			
			profile.id = currentProfile.id;
			//profile.name = currentProfile.name;
			profile.search_id = currentProfile.search_id;
			profile.is_verified = true;

			Ebean.update(profile);
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_APPPROFILE_UPDATE_FAILED,
					Messages.get("appprofile.failure"));
		}
		
		return ok();		
	}

	/* No profile could be deleted
	@AccessLevel(level=2)
	public Result delete(String userId) {
		try {
			
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_APPPROFILE_DELETE_FAILED,
					Messages.get("appprofile.failure"));
		}
	}
	*/
	public Result phoneAttach(String searchId, String accountId){
		AppProfile profile = AppProfile.find.where()
				.eq("search_id", searchId)
				.findUnique();
		String phoneAccountId = profile.id.accountId;

		if (phoneAccountId.compareTo(accountId) == 0) {
            return ok();
        }

		Account accountExist = Account.find.byId(accountId);
        Account accountPhone = Account.find.byId(phoneAccountId);
        
        accountExist.username = accountPhone.username;
        accountPhone.username = "ol-"+CodeGenerator.GenerateUUId();
        Ebean.save(accountPhone);
        Ebean.save(accountExist);
       
		
		return ok();
	}
	public Result isfollow(){
		try {
			boolean isfollow = false;
			String sql=String.format("SELECT count(*) FROM logs_bean "
					+ "WHERE user_id = '%s' "
					+ "AND request like '%fcwm.24-7.com.cn/assets/index.html%' limit 1", session("userId"));
			RawSql rawSql=
					RawSqlBuilder.parse(sql)
					.columnMapping("count(*)", "num")
					.create();
			Query<Data> query = Ebean.getServer("log").find(Data.class);
			query.setRawSql(rawSql);
			Data data = query.findUnique();
			if (data.num==1) {
				isfollow=true;
			} else{
				isfollow=false;
			}
			return ok(Json.toJson(isfollow));
			
		} catch (Throwable e) {
			return status(ErrDefinition.E_IS_FOLLW_ERROR,
					Messages.get("isfollow.failure"));
		}
	}
	
}
