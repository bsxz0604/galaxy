/** author zhicheng.wang
 * 11/8/2015
 */
package controllers.stock;

import java.io.File;
import java.util.Date;
import java.util.List;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.stock.StockEntry;
import models.gift.Gift;
import play.api.libs.Files;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.RequestBody;
import play.mvc.Http.MultipartFormData.FilePart;
import controllers.AppController;
import controllers.ErrDefinition;
import controllers.common.CodeGenerator;

public class StockEntryController extends AppController {
	
    public Result create() {           //  create  form
		
		Form<StockEntry> entryForm = 
				Form.form(StockEntry.class).bindFromRequest();
		
		if (entryForm.hasErrors()) {
			return status(ErrDefinition.E_ENTRY_CREAT_ERROR,
					Messages.get("entryform.failure"));
		}

		StockEntry entry = entryForm.get();
    	
	//	Ebean.save(entry);
	    return Create(entry);
	}
    
	public Result Create(StockEntry entry) {
		try {			
			String accountId = session("userId");
			String appId     = session("appId");               ///
			
			if (accountId == null || appId == null) {
				return status(ErrDefinition.E_ENTRY_CREAT_ERROR,
						Messages.get("entryform.failure"));
			}

			List<StockEntry> existaccount = 
					StockEntry.find.where()
					.eq("account_id", accountId)
					.findList();
			int num = 0;
			 num = existaccount.size();
			if (num < 3){
				entry.id=CodeGenerator.GenerateUUId();
				entry.account_id = session("userId");     // generate a uuid for every entry_form
		     	entry.application_id = session("appId");
		    	entry.create_time = new Date();
		    	entry.num=num;
		    	Ebean.save(entry);
				return ok(Json.toJson(entry));		
			
			}
			else return ok(Json.toJson(num));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_ENTRY_CREAT_ERROR, 
					Messages.get("entry.failure"));
		}		
	}
	
	public Result uploadVideo() {
		
		String appId = session("appId");
		String userId = session("userId");
		
		if (appId == null || appId.isEmpty()) {
			return failure(ErrDefinition.E_APP_IMG_INVALID_APP);
					//Messages.get("application.actionfailed"));			
		}

		if (userId == null || userId.isEmpty()) {
			return failure(ErrDefinition.E_APP_IMG_INVALID_USER);
					//Messages.get("application.actionfailed"));
		}
		String dir = "pictures" + 
				  File.separator + appId + File.separator + userId;
		return uploadEntryVideo(dir);
	}
	
	private Result uploadEntryVideo(String defaultdir){
		
		String domain = Messages.get("site.name");
		String linkPath = domain + File.separator;         // url
		String physicalPath = "public" + File.separator;    //  server   location
		
        ObjectNode json = Json.newObject();
		
        try {   // upload    and   transcode
        	
        	RequestBody requestBody = request().body();
			MultipartFormData body = requestBody.asMultipartFormData();
			FilePart videoData = body.getFile("submit");
			
			if(videoData == null) {
				return status(ErrDefinition.E_APP_IMG_NODATA, 
						Messages.get("application.actionfailed"));		
			}
			
    		File videoFile = videoData.getFile();// videoData.wait(timeout);
    		String realName =videoData.getFilename();
    		
    		String fileName = CodeGenerator.GenerateUUId() + 
    				realName.substring(realName.lastIndexOf('.'));
    		
            String phsyicalFile = physicalPath + defaultdir + File.separator + fileName;
            
            File f = new File(phsyicalFile);  //  server
            Files.copyFile(videoFile, f, false, true);

            String linkUrl = linkPath + defaultdir + File.separator + fileName;

            json.put("url", linkUrl);
        }
        catch (Throwable e){
        	return status(ErrDefinition.E_UPLOADVIDEO_FAILED,
        			Messages.get("uploadvideo.failed"));
        }
		return ok(json);
	}
	
	public  Result  readAll(Integer pageNumber,Integer sizePerPage){
		List<StockEntry> entryList = StockEntry.find.where()
				.setFirstRow(pageNumber*sizePerPage)
				.setMaxRows(sizePerPage)
				.findList();
		double total = StockEntry.find.where()
                .findRowCount();
		
		ObjectNode node = Json.newObject();
		double page=Math.ceil(total/sizePerPage);
		
		node.put("totalPages", page);
		node.put("rows", Json.toJson(entryList));
		return ok(Json.toJson(node));
	}
}
