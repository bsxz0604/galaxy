package controllers.plaza;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.node.ObjectNode;

import controllers.AppController;
import controllers.ErrDefinition;
import controllers.application.ApplicationController;
import controllers.common.CodeGenerator;
import models.advertisement.Banner;
import models.application.Application;
import models.common.Account;
import models.plaza.Article;
import models.plaza.Theme;
import models.users.AccountAppId;
import models.users.AppProfile;
import models.plaza.MyTheme;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;

public class ThemeController extends AppController {
	
	public Result themeView(int pageNumber, int sizePerPage) {	
		try {
			List<Theme> theme = Theme.find.where()
					.eq("app_id", null)
					.setFirstRow(pageNumber*sizePerPage)
					.setMaxRows(sizePerPage)
					.order("app_id")
					.findList();
			
			return ok(Json.toJson( theme));			
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_THEME_VIEW_ERROR, 
					Messages.get("themeview.failure"));
		}		
	}
	
	class IsInOrNotTable {
		  public Boolean inOrNot;
		  public Integer themeClass;
		  public String themeId;
		  public String themeName;
		  public String themeImage;
		  public String themeImageBase64;
		  public String hotPeople;
	}
	
	public Result isInOrNot(String themeId) {
	  try {
		  IsInOrNotTable returnResult = new IsInOrNotTable() ;
		  Theme theme = Theme.find.where()
				  .eq("id", themeId)
				  .findUnique();
		  if (theme!=null && theme.theme_image!=null) {
	            int idxOfSite = theme.theme_image.indexOf(Messages.get("site.name"));
	            if (idxOfSite >= 0) {
	                String physicalFile = "public" + theme.theme_image.substring(
	                        idxOfSite + Messages.get("site.name").length());
	                
	                File file = new File(physicalFile);
	                BufferedImage img;
	                try {
	                    img = ImageIO.read(file);
	                    BufferedImage blurImg = ApplicationController.setGaussianBlur(img, 20);
	                    theme.theme_image_base64 = ApplicationController.getImageBase64(blurImg);                 
	                } catch (IOException e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                } 
	            }
	   
		      {
			      returnResult.themeClass = theme.theme_class;
			      returnResult.themeId = themeId;
			      returnResult.themeName = theme.theme_name;
			      returnResult.themeImage = theme.theme_image;
			      returnResult.themeImageBase64 = theme.theme_image_base64;
			      returnResult.hotPeople = theme.hot_people;
		      }
		  }
		  else
		  {
			  return status(ErrDefinition.E_THEME_VIEW_ERROR, 
						Messages.get("themeview.failure"));
		  }
		  
		MyTheme mytheme = MyTheme.find.where()
				.eq("theme_id", themeId)
				.eq("account_id", Account.find.where().eq("id", (session("userId"))).findUnique().id)
				.findUnique();
		if (mytheme==null)
		{
			returnResult.inOrNot = false;
		}
		else
		{
			returnResult.inOrNot = true;
		}
		
		
		return ok(Json.toJson( returnResult));			
	}
	catch (Throwable e) {
		return status(ErrDefinition.E_THEME_VIEW_ERROR, 
				Messages.get("themeview.failure"));
	}		
}
	
	public Result themeEnter(String theme_id, int pageNumber, int sizePerPage) {	
		try {
		     List<Article> listBreak = Article.find.where()
					.eq("theme_id", theme_id)
					.findList();
		     double total = Article.find.where()
						.eq("theme_id",theme_id)
	                    .findRowCount();
				
				ObjectNode node = Json.newObject();
				double page=Math.ceil(total/sizePerPage);
				

			if (listBreak.size() == 0){return ok();}
			else{
			List<Article> listTop = Article.find.where()
					.eq("is_top", true)
					.eq("theme_id", theme_id)
					.setOrderBy("update_time desc")
					.findList();
			
			List<Article> listCommon = Article.find.where()
					.eq("is_top", false)
					.eq("theme_id", theme_id)
					.setOrderBy("update_time desc")
					.findList();
			
			MyTheme mytheme = MyTheme.find.where()
					.eq("account_id", Account.find.where().eq("id", (session("userId"))).findUnique().id)
					.eq("theme_id", theme_id)
					.findUnique();
			
			if (mytheme == null) {
		//		return status(ErrDefinition.E_THEME_ENTER_ERROR, 
		//				Messages.get("themeenter.failure"));
			}
			else
			{
				mytheme.latest_visit = new Date();
			}
			
			 List<Article> listFinal = new ArrayList<Article>();
			 listFinal.addAll(listTop);
			 listFinal.addAll(listCommon);
			 
			 Integer startIndex;			 
			 Integer endIndex;
			  
			 if (pageNumber*sizePerPage < listFinal.size())
			 {
				 startIndex = pageNumber*sizePerPage;
			 }
			 else
			 {
				 return status(ErrDefinition.E_THEME_ENTER_ERROR, 
							Messages.get("themeenter.failure"));				 
			 }
			 
			 if (sizePerPage>(listFinal.size()-pageNumber*sizePerPage))
			 {
				 endIndex = listFinal.size();
			 }
			 else
			 {
				 endIndex = startIndex+sizePerPage;
			 }

			 
			 List<Article> list = listFinal.subList(startIndex, endIndex);
			 node.put("totalPages", page);
				node.put("rows", Json.toJson(list));
				
				return ok(node);
			//return ok(Json.toJson( list));			
		}
			}
		catch (Throwable e) {
			return status(ErrDefinition.E_THEME_ENTER_ERROR, 
					Messages.get("themeenter.failure"));
		}		
	}
	
	public Result articleRead(String article_id) {
		try {
			Article article = Article.find. where()
					.eq("article_id", article_id)
					.findUnique();
			
			if (article == null) {
				return status(ErrDefinition.E_THEME_READ_ERROR, 
						Messages.get("themejoin.failure"));
			}
			else
			{
				if (article.user_id == null) {
					article.user_id = article.author.id;
					Ebean.update(article);
				}
				if (article.is_verified == null) {
					AccountAppId id = new AccountAppId(article.author.id, article.application.id);
					AppProfile appProfile = AppProfile.find.byId(id);
					article.is_verified = appProfile.is_verified;
					Ebean.update(article);
				}
				if (article.author.id.equals(session("userId"))) {  // Only for the owner
					article.is_new_comments = false;
				    Ebean.update(article);
				}
				return ok(Json.toJson( article));	
			}
		} 
		catch (Throwable e) {
			return status(ErrDefinition.E_THEME_READ_ERROR, 
					Messages.get("themejoin.failure"));
		}		
	}
	
	public Result themeJoin(String theme_id) {	 
		try {
			MyTheme themeClass = MyTheme.find.where()
					.eq("theme_id", theme_id)
					.eq("account_id", Account.find.where().eq("id", (session("userId"))).findUnique().id)
					.findUnique();
			
			if (themeClass != null) {
				return status(ErrDefinition.E_THEME_JOIN_ERROR, 
						Messages.get("themejoin.failure"));
			}
			
			Theme theme = Theme.find.where()
					.eq("id", theme_id)
					.findUnique();
			
			if (theme == null) {
				return status(ErrDefinition.E_THEME_JOIN_ERROR, 
						Messages.get("themejoin.failure"));
			}
			
			themeClass = new MyTheme();
			
			themeClass.id = CodeGenerator.GenerateUUId();
			themeClass.account = Account.find.where().eq("id", (session("userId"))).findUnique();
			themeClass.application = Application.find.where().eq("id", (session("appId"))).findUnique();
			themeClass.theme_name = theme.theme_name;
			themeClass.hot_people = theme.hot_people;
			themeClass.theme_id = theme_id;
			themeClass.theme_image = theme.theme_image;
			themeClass.theme_class = theme.theme_class;
			themeClass.create_time = new Date();
			themeClass.is_new = 0;
					
			Ebean.save(themeClass);
			
			return ok(Json.toJson( themeClass));			
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_THEME_JOIN_ERROR, 
					Messages.get("themejoin.failure"));
		}		
	}
	
	public Result themeCreate() {	 
		Form<Theme> ThemeForm = 
				Form.form(Theme.class).bindFromRequest();
		
		if (ThemeForm.hasErrors()) {
			return status(ErrDefinition.E_THEME_FORM_ERROR,
					Messages.get("theme.failure"));
		}
		try {
			
			if (Account.find.where().eq("id", (session("userId"))).findUnique()==null 
					|| Account.find.where().eq("id", (session("userId"))).findUnique().role<0) {
				return status(ErrDefinition.E_THEME_CREATE_ERROR, 
						Messages.get("themecreate.failure"));
			}
			Theme themeCreate = ThemeForm.get();
			
			Theme theme = Theme.find.where()
					.eq("theme_name", themeCreate.theme_name)
					.eq("theme_class", themeCreate.theme_class)
					.findUnique();
			
			if (theme != null)
			{
				return status(ErrDefinition.E_THEME_CREATE_ERROR, 
						Messages.get("themecreate.failure"));
			}
			
			themeCreate.id = CodeGenerator.GenerateUUId();
			themeCreate.application = Application.find.where().eq("id", session("userId")).findUnique();
			Ebean.save(themeCreate);
			
			return ok(Json.toJson( themeCreate));			
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_THEME_CREATE_ERROR, 
					Messages.get("themecreate.failure"));
		}		
	}
	
	public Result themeDelete() {	 
		Form<Theme> ThemeForm = 
				Form.form(Theme.class).bindFromRequest();
		
		if (ThemeForm.hasErrors()) {
			return status(ErrDefinition.E_THEME_FORM_ERROR,
					Messages.get("theme.failure"));
		}
		try {
			
			if (Account.find.where().eq("id", (session("userId"))).findUnique()==null 
					|| Account.find.where().eq("id", (session("userId"))).findUnique().role<1) {
				return status(ErrDefinition.E_THEME_DELETE_ERROR, 
						Messages.get("themedelete.failure"));
			}
			Theme themeDelete = ThemeForm.get();
			
			Theme theme = Theme.find.where()
					.eq("theme_name", themeDelete.theme_name)
					.eq("theme_class", themeDelete.theme_class)
					.findUnique();
			
			if (theme == null)
			{
				return status(ErrDefinition.E_THEME_DELETE_ERROR, 
						Messages.get("themecreate.failure"));
			}
			
			Ebean.delete(theme);
			
			return ok(Json.toJson(""));			
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_THEME_DELETE_ERROR, 
					Messages.get("themedelete.failure"));
		}		
	}
	
	
	
	public Result themeExit(String theme_id) {	
		try {
			MyTheme themeClass = MyTheme.find.where()
					.eq("theme_id", theme_id)
					.eq("account_id", Account.find.where().eq("id", (session("userId"))).findUnique().id)
					.findUnique();
			
			if (themeClass == null) {
				return status(ErrDefinition.E_THEME_EXIT_ERROR, 
						Messages.get("themejoin.failure"));
			}		
			Ebean.delete(themeClass);
			
			return ok(Json.toJson( themeClass));			
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_THEME_JOIN_ERROR, 
					Messages.get("themeexit.failure"));
		}		
	}
	
	public Result myTheme(String userId, int pageNumber, int sizePerPage) {	
		try {
			List<MyTheme> list = MyTheme.find.where()
					.eq("account_id", Account.find.where().eq("id", userId).findUnique().id)
					.eq("app_id", session("appId"))
					.setFirstRow(pageNumber*sizePerPage)
					.setMaxRows(sizePerPage)
					.findList();
			
			for(MyTheme itera:list) {
				Theme theme = Theme.find.where()
						.eq("id", itera.theme_id)
						.findUnique();
				if (theme == null) {
					return status(ErrDefinition.E_THEME_MYSELF_ERROR, 
							Messages.get("themeview.failure"));
				}
					
				if ((itera.latest_visit==null) || (itera.latest_visit.getTime()<=theme.last_update.getTime())) {
					itera.is_new = 1;
				}
				else
				{
					itera.is_new = 0;
				}
			}		
			return ok(Json.toJson(list));			
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_THEME_MYSELF_ERROR, 
					Messages.get("mytheme.failure"));
		}		
	}
}
