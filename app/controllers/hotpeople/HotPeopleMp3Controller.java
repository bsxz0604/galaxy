package controllers.hotpeople;

import com.avaje.ebean.Ebean;

import controllers.AppController; 
import models.hotpeople.HotPeopleMp3;
import models.hotpeople.HotPeoplePic;
import models.hotpeople.HotPeopleRank;
import play.data.Form;
import play.mvc.Result;
import play.i18n.Messages;
import play.libs.Json;
import controllers.ErrDefinition;
import controllers.common.CodeGenerator;

import java.util.Date;
import java.util.List;

import models.users.AppProfile;
import models.users.AccountAppId;

public class HotPeopleMp3Controller extends AppController {
	
	public Result create() {
	    Form<HotPeopleMp3> hotMp3 = 
			Form.form(HotPeopleMp3.class).bindFromRequest();
	
	    if (hotMp3.hasErrors()){
		    return status(ErrDefinition.E_HOT_MP3_HASERROR,
				Messages.get("hotMp3.failure"));
	    }
	
	    try {
	    	HotPeopleMp3 hotPeopleMp3 = hotMp3.get();
	        List<HotPeopleMp3> exist = HotPeopleMp3.find.where()
	        		.eq("num", hotPeopleMp3.num)
	        		.findList();
	        if (exist.size()!=0) {return status(ErrDefinition.E_HOT_MP3_HASERROR,
					Messages.get("hotMp3.exist"));}
	    	hotPeopleMp3.id = CodeGenerator.GenerateUUId();
	    	String getName = hotPeopleMp3.name;
			String peopleId = getId(getName);
			hotPeopleMp3.mainUrl = "http://fcwm.24-7.com.cn/assets/member.html?id=" + peopleId;
			hotPeopleMp3.createTime = new Date();
	        Ebean.save(hotPeopleMp3);
	
	        return ok(Json.toJson(hotPeopleMp3));
	    }
	    catch (Throwable e) {
		    return status(ErrDefinition.E_HOT_MP3_HASERROR,
				    Messages.get("hotMp3.failure"));
	    }
	}
	
  private String getId(String name) {
	
	AppProfile hotProfile = AppProfile.find.where()
			.eq("name", name)
			.eq("is_verified", 1)
			.findUnique();
    AccountAppId accountApp = hotProfile.id;
    String id = accountApp.accountId;
	return id;
}
	
	public Result readByNumName(Integer num){
		
			List<HotPeopleMp3> mp3List = HotPeopleMp3.find.where()
					.eq("num", num)
			        .orderBy("create_time")
			        .findList();	
	return ok(Json.toJson(mp3List));
	}
    public Result deleteMp3(Integer num) {
        List<HotPeopleMp3> deleteMp3 = HotPeopleMp3.find.where()
        		.eq("num", num)
        		.findList();
        try {
        	Ebean.delete(deleteMp3);
        }
        catch(Throwable e) {
        	return status(ErrDefinition.E_HOT_MP3_DELETE_FAILED,
        			Messages.get("hotMp3Delete.failure"));
        }
        return ok();
    }
	
	public Result update(){
		Form<HotPeopleMp3> peopleMp3 = 
				Form.form(HotPeopleMp3.class).bindFromRequest();
		HotPeopleMp3 mp3 = peopleMp3.get();
		Ebean.update(mp3);
		return ok(Json.toJson(mp3));
	}
    
}
