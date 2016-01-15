package controllers.hotpeople;

import com.avaje.ebean.Ebean;

import controllers.AppController; 
import models.activity.ActivityChoice;
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

public class HotPeoplePicController extends AppController {
	
	public Result create() {
	    Form<HotPeoplePic> hotPic = 
			Form.form(HotPeoplePic.class).bindFromRequest();
	
	    if (hotPic.hasErrors()){
		    return status(ErrDefinition.E_HOT_PIC_HASERROR,
				Messages.get("hotPic.failure"));
	    }
	
	    try {
        	HotPeoplePic hotPeoplePic = hotPic.get();
	        hotPeoplePic.id = CodeGenerator.GenerateUUId();
	        hotPeoplePic.createTime = new Date();
	        Ebean.save(hotPeoplePic);
	
	        return ok(Json.toJson(hotPeoplePic));
	    }
	    catch (Throwable e) {
		    return status(ErrDefinition.E_HOT_PIC_HASERROR,
				    Messages.get("hotpic.failure"));
	    }
	}
	
	public Result readByNumName(Integer num){
		if(num != 0)
			{List<HotPeoplePic> picList = HotPeoplePic.find.where()
					.eq("num", num)
			        .orderBy("create_time")
			        .findList();	
	return ok(Json.toJson(picList));}
		else{			
			List<HotPeopleRank> rankList = HotPeopleRank.find.all();
		    int maxNum = rankList.get(rankList.size()-1).id;
		    List<HotPeoplePic> picList = HotPeoplePic.find.where()
					.eq("num", maxNum)
			        .orderBy("create_time")
			        .findList();	
	return ok(Json.toJson(picList));}
	}
	
//	public Result num(){
//		
//		List<HotPeoplePic> picList = HotPeoplePic.find.where()
//		        .orderBy("create_time")
//		        .findList();	
//        int n = picList.size();
//        String num=picList.get(n-1).num;
//		return ok(Json.toJson(num));
//    }
	
	public Result deletePic(Integer num, String name) {
		
		List<HotPeoplePic> deletePic = HotPeoplePic.find.where()
				.eq("num", num)
				.eq("name", name)
				.findList();
		try {
			Ebean.delete(deletePic);
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_HOT_PIC_DELETE_FAILED,
					Messages.get("hotpicDelete.failure"));
		}
		
		return ok();	
	}
	
	public Result update(){
		Form<HotPeoplePic> peoplePic = 
				Form.form(HotPeoplePic.class).bindFromRequest();
		HotPeoplePic pic = peoplePic.get();
		Ebean.update(pic);
		return ok(Json.toJson(pic));
	}
	public Result deleteOne(String picId){
		HotPeoplePic pic = HotPeoplePic.find.byId(picId);
		if(pic == null) {
			return status(ErrDefinition.E_HOT_PIC_DELETE_FAILED,
				Messages.get("hotpicDelete.failure"));
		}
		Ebean.delete(pic);
		return ok();
	}
}
