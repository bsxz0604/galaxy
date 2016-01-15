package controllers.hotpeople;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.hotpeople.HotPeoplePic;
import models.hotpeople.HotPeopleRank;
import models.users.AppProfile;
import controllers.AppController;
import play.libs.Json;
import play.mvc.Result;
import play.data.Form;
import play.i18n.Messages;
import controllers.ErrDefinition;

import java.util.ArrayList;
import java.util.List;

import models.users.AccountAppId;

public class HotPeopleRankController extends AppController {

	public Result create() {
		Form<HotPeopleRank> hotRank = 
				Form.form(HotPeopleRank.class).bindFromRequest();
		if(hotRank.hasErrors()) {
			return status(ErrDefinition.E_HOT_RANK_HASERROR,
					Messages.get("hotRank.failure"));
		}
		
		try{
			HotPeopleRank hotPeopleRank = hotRank.get();
			List<HotPeopleRank> rankList = HotPeopleRank.find.all();
            int maxNum;
			if (rankList.size()==0) { maxNum = 0;}
			else
			{ maxNum = rankList.get(rankList.size()-1).id;}
			hotPeopleRank.id = maxNum+1;
			// get hotpeople_id 
		//	String getName = hotPeopleRank.name;
		//	String peopleId = getId(getName);
		//	hotPeopleRank.peopleUrl = "http://fcwm.24-7.com.cn/assets/member.html?id=" + peopleId;
			Ebean.save(hotPeopleRank);
			
			return ok(Json.toJson(hotPeopleRank));
		}
		catch (Throwable e){
			return status(ErrDefinition.E_HOT_RANK_HASERROR,
					Messages.get("hotrank.failure"));
		}
	}

//    public String getId(String name) {
//    	
//    	AppProfile hotProfile = AppProfile.find.where()
//    			.eq("name", name)
//    			.eq("is_verified", 1)
//    			.findUnique();
//        AccountAppId accountApp = hotProfile.id;
//        String id = accountApp.accountId;
//    	return id;
//    }
    
    public Result readAll() {
    	List<HotPeopleRank>  rankList = HotPeopleRank.find.all();
    	return ok(Json.toJson(rankList));
    }
    
    public Result readByPage(Integer pageNumber, Integer sizePerPage) {

    	List<HotPeopleRank> hotRank = HotPeopleRank.find.where()
    			.setFirstRow(pageNumber*sizePerPage)
    			.setMaxRows(sizePerPage)
    			.orderBy("id")
    			.findList();
    	double total = HotPeopleRank.find.where()
    			.findRowCount();
    	ObjectNode node = Json.newObject();
    	double page=Math.ceil(total/sizePerPage);
    	
    	node.put("totalPages", page);
    	node.put("rows",Json.toJson(hotRank));
       return ok(node);
    }
    
//    private Result existRankNum(String num) {
//		HotPeopleRank exist = HotPeopleRank.find.byId(num);
//		
//		if (exist == null) {return ok();}
//		else  return status(ErrDefinition.E_HOT_RANK_EXIST,
//			   Messages.get("existRankNum.failure"));
//	}
    
    public Result deleteRank(Integer num) {
        HotPeopleRank deleteRank = HotPeopleRank.find.byId(num);
        if(deleteRank == null){
        	return status(ErrDefinition.E_HOT_RANK_DELETE_FAILED,
        			Messages.get("hotRankDelete.failure"));
        }
        try {
        	Ebean.delete(deleteRank);
        }
        catch(Throwable e) {
        	return status(ErrDefinition.E_HOT_RANK_DELETE_FAILED,
        			Messages.get("hotRankDelete.failure"));
        }
        return ok();
    }
    
    public Result update(){
		Form<HotPeopleRank> peopleRank = 
				Form.form(HotPeopleRank.class).bindFromRequest();
		HotPeopleRank rank = peopleRank.get();
		Ebean.update(rank);
		return ok(Json.toJson(rank));
	}
}
