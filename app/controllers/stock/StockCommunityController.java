package controllers.stock;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import java.util.ArrayList;

import models.application.Application;
import models.common.Account;
import models.plaza.Article;
import models.plaza.MyTheme;
import models.plaza.Sign;
import models.plaza.Theme;
import models.stock.Stock;
import models.users.AccountAppId;
import models.users.AppProfile;
import models.users.InterestFan;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;

import java.util.List;

import javax.imageio.ImageIO;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import controllers.AppController;
import controllers.ErrDefinition;
import controllers.application.ApplicationController;
import controllers.common.CodeGenerator;

public class StockCommunityController extends AppController{

	public Result themeCreateStock() {
	     List<Stock> stockList = Stock.find.all();
	    for(Stock stock:stockList){
    
		Theme theme = new Theme();
	    	theme.id =stock.id;
	    	theme.theme_name = stock.stockName;
	    	Application application = new Application();
	    	application.id = "7248d7fc-1fab-45a6-87fe-5b57e03ac425";
	    	theme.application = application;
	    	theme.theme_class = 2;
	    	Ebean.save(theme);
	
    }
	    	return ok();
	}
	
	public Result post(String Alltheme) {
		
        String parseTheme[] = Alltheme.split("\\.");
        
        List<Article> articleList = new ArrayList<Article>();
		for(int i=0; i<parseTheme.length; i++){
		
		Form<Article> ArticleForm = 
				Form.form(Article.class).bindFromRequest();
		
		if (ArticleForm.hasErrors()) {
			return status(ErrDefinition.E_ARTICLE_FORM_ERROR,
					Messages.get("article.failure"));
		}
		
		try {
			Article article = ArticleForm.get();
			article.theme_id = parseTheme[i]; 
			Theme theme = Theme.find.where()
					.eq("id", article.theme_id)
					.findUnique();
			
			if (theme==null) {
				return status(ErrDefinition.E_ARTICLE_NO_THEME_ERROR,
						Messages.get("articlepost.failure"));
			}
			
			AccountAppId id = new AccountAppId(session("userId"), session("appId"));
			AppProfile appProfile = AppProfile.find.byId(id);
			if(session("appId").equals("34536418-d6b2-451f-a400-4f0e284c9497")){
				Sign signItem = Sign.find.where().eq("account_id", session("userId")).eq("app_id", session("appId")).findUnique();
				signItem.sign_charm += 1;	
				Ebean.update(signItem);
			}
			
			article.id = CodeGenerator.GenerateUUId();
			article.article_id = CodeGenerator.GenerateUUId();
			article.author = Account.find.where().eq("id", (session("userId"))).findUnique();
			article.user_id = session("userId");
			article.application = Application.find.where().eq("id", (session("appId"))).findUnique();
			article.author_name = appProfile.name;
			article.author_image = appProfile.head_image;
//			article.author_image_base64 = appProfile.head_imgbase64;
			article.is_verified = appProfile.is_verified;
		    Date createTime = new Date();
			article.create_time = createTime;
		    article.update_time = createTime;
		    article.last_reply = createTime;
		    article.theme_name = theme.theme_name;
		    article.theme_image = theme.theme_image;
		    article.theme_class = theme.theme_class;
		    article.is_top = false;
		    article.is_elite = false;
		    article.is_public = false;
		    article.is_new_comments = false;
		    article.isSpecial = false;
		    article.thumbs_number = 0;
		    article.thumbs_number2 = 0;
		    article.comments_number = 0;
		    if(i!=0){article.isSpecial=true;}   //  add the first theme  to  plaza 
		    theme.last_update = new Date();  //update theme latest time
		    
		    // Only administrator can send is_top flag
//		    UserGroup authorGroup = UserGroup.find.where()
//		    		.eq("account_id", article.author)
//		    		.findUnique();
		    
//		    if (authorGroup != null) {
//		    	if (authorGroup.group_name == "admin") {
//		    	}
//		    	else {
//		    		article.is_top = false;
//		    	}
//		    } else {
//		    	article.is_top = false;
//		    }
		    
		    articleList.add(article);
			Ebean.save(article);
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_ARTICLE_POST_ERROR,
					Messages.get("articlepost.failure"));
		}
		}
		return ok(Json.toJson(articleList));
	}
	
	public Result likeSearch(String keyWords,Integer pageNumber, Integer sizePerPage){
   	 List<Theme> likeList = Theme.find.where()
   			 .like("id", "%"+keyWords+"%")
   			 .setFirstRow(pageNumber*sizePerPage)
   			 .setMaxRows(sizePerPage)
   			 .findList();
	 return ok(Json.toJson(likeList));
	}
	
	public Result isInOrNot(String themeId) {
		boolean inOrNot; 
		MyTheme mytheme = MyTheme.find.where()
				.eq("theme_id", themeId)
				.eq("account_id", Account.find.where().eq("id", (session("userId"))).findUnique().id)
				.findUnique();
		if (mytheme==null)
		{
			inOrNot = false;
		}
		else
		{
			inOrNot = true;
		}
		
		
		return ok(Json.toJson(inOrNot));			
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
		//	ObjectNode node = Json.newObject();
			List<AppProfile> profileList = new ArrayList<AppProfile>();
		//	List array = new ArrayList();
			for (InterestFan fan : fanList) {
				AccountAppId id = null; 
				boolean  isInterested = false;
			  if (interestType.compareTo("interesteeId") == 0) {
					id = new AccountAppId(fan.interesterId, appId);
				}
				else {
					continue;
				}
				InterestFan isOrNotFan = InterestFan.find.where()
						.eq("interesterId", userId)
						.eq("interesteeId", fan.interesterId)
						.findUnique();
				if(isOrNotFan != null) {isInterested = true; }
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
				profile.isSelected = isInterested;
				profileList.add(profile);
		//		array.add(isInterested);
			}
//			node.put("fans", Json.toJson(profileList));
//			node.put("isInOrNot", Json.toJson(array));
			return ok(Json.toJson(profileList));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_INTERESTFAN_READ_FAILED,
					Messages.get("interestfan.failure"));
		}
	}
	
}
