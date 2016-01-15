package controllers.plaza;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.avaje.ebean.Ebean;
import controllers.AppController;
import controllers.ErrDefinition;
import controllers.common.CodeGenerator;
import models.application.Application;
import models.common.Account;
import models.plaza.Article;
import models.plaza.Collections;
import models.plaza.Comments;
import models.plaza.Theme;
import models.plaza.Thumbs;
import models.users.AccountAppId;
import models.users.AppProfile;
import models.plaza.Sign;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;

public class ArticleController extends AppController {

	public Result post() {
		Form<Article> ArticleForm = 
				Form.form(Article.class).bindFromRequest();
		
		if (ArticleForm.hasErrors()) {
			return status(ErrDefinition.E_ARTICLE_FORM_ERROR,
					Messages.get("article.failure"));
		}
		
		try {
			Article article = ArticleForm.get();
			if(article.theme_id == null) article.theme_id = "stockDefault"; 
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
		    	
			Ebean.save(article);
			
			return ok(Json.toJson(article));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_ARTICLE_POST_ERROR,
					Messages.get("articlepost.failure"));
		}
	}
	
	public Result top(String articleId){
		Article article = Article.find.where()
				.eq("article_id", articleId)
				.findUnique();
		if(article == null){
			return status(ErrDefinition.E_ARTICLE_POST_ERROR,
					Messages.get("articlepost.failure"));
		}
		article.isSpecial = true;
		Ebean.save(article);
		return ok();
	}
	
	public Result down(String articleId){
		Article article = Article.find.where()
				.eq("article_id", articleId)
				.findUnique();
		if(article == null){
			return status(ErrDefinition.E_ARTICLE_POST_ERROR,
					Messages.get("articlepost.failure"));
		}
		article.isSpecial = false;
		Ebean.save(article);
		return ok();
	}
	
	public Result readTop() {
		List<Article> article = Article.find.where()
				.eq("app_id", session("appId"))
				.eq("isSpecial", 1)
				.orderBy("create_time desc")
				.findList();
		return ok(Json.toJson(article));
	}
	
	public Result readAll(int pageNumber, int sizePerPage) {
		try {
				List<Article> listTop = Article.find.where()
	//					.eq("is_public", true)
						.eq("app_id",session("appId"))
						.eq("isSpecial", 0)
						.setOrderBy("update_time desc")
						.setFirstRow(pageNumber*sizePerPage)
	   				    .setMaxRows(sizePerPage)
						.findList();
	//			List<Article> listCommon = Article.find.where()
	//					.eq("is_public", false)
	//					.setOrderBy("update_time desc")
	//					.findList();
				
	//			 List<Article> listFinal = new ArrayList<Article>();
	//			 listFinal.addAll(listTop);
	 //   		 listFinal.addAll(listCommon);
	    		 
	  //  		 Integer startIndex;			 
		//		 Integer endIndex;
				  
		//		 if (pageNumber*sizePerPage < listFinal.size())
		//		 {
		//			 startIndex = pageNumber*sizePerPage;
		//		 }
		//		 else
		//		 {
		//			 return status(ErrDefinition.E_ARTICLE_READ_ERROR, 
		//						Messages.get("articleread.failure"));				 
		//		 }
				 
		//		 if (sizePerPage>(listFinal.size()-pageNumber*sizePerPage))
		//		 {
			//		 endIndex = listFinal.size();
		//		 }
			//	 else
			//	 {
			//		 endIndex = sizePerPage;
			//	 }

			//	 List<Article> list = listFinal.subList(startIndex, endIndex);
				return ok(Json.toJson( listTop));			
		}
		catch (Throwable e) {
			System.err.println(e.toString());
			return status(ErrDefinition.E_ARTICLE_READ_ERROR, 
					Messages.get("articleread.failure"));
		}
	}
	
	class myClassTable {
		public String article_id;
		public String article_name;
		public String author_name;
		public String author_image;
		public boolean is_new_comments;
	}
	public Result readMyself(String userId, int pageNumber, int sizePerPage) {
		try {
			List<myClassTable> myArticleList = new  ArrayList<myClassTable>();
				List<Article> listArticle = Article.find.where()
						.eq("author_id", userId)
						.eq("app_id", session("appId"))
						.setOrderBy("update_time desc")
						.setFirstRow(pageNumber*sizePerPage)
						.setMaxRows(sizePerPage)
						.findList();
				
				 for(Article itera:listArticle) {
			          myClassTable  myArticle = new myClassTable();
			          myArticle.article_id = itera.article_id;
			          myArticle.article_name = itera.article_name;
			          myArticle.author_name = itera.author_name;
			          myArticle.author_image = itera.author_image;
			          if (itera.is_new_comments == null)
			        	  itera.is_new_comments = false;
			          myArticle.is_new_comments = itera.is_new_comments;
			          myArticleList.add(myArticle);
			        }
	
				return ok(Json.toJson(myArticleList));			
		}
		catch (Throwable e) {
			System.err.println(e.toString());
			return status(ErrDefinition.E_ARTICLE_READ_ERROR, 
					Messages.get("articleread.failure"));
		}
	}
		
	public Result update() {
		Form<Article> ArticleForm = 
				Form.form(Article.class).bindFromRequest();
		
		if (ArticleForm.hasErrors()) {
			return status(ErrDefinition.E_ARTICLE_FORM_ERROR,
					Messages.get("article.failure"));
		}
		
		try { 
			Article updateArticle = ArticleForm.get();
			// Check the updated article is done by author
			Article originArticle = Article.find.where()
					.eq("article_id", updateArticle.article_id )
					.findUnique();
			AccountAppId id = new AccountAppId(session("userId"), session("appId"));
			
//			if (Account.find.where().eq("id", (session("userId"))).findUnique().role==2) { // administrator may update the article
//				updateArticle.author = originArticle.author;
//			} else 
				
			if (AppProfile.find.byId(id).name != originArticle.author_name && Account.find.where().eq("id", (session("userId"))).findUnique().role!=2) {
				return status(ErrDefinition.E_ARTICLE_UPDATE_ERROR,
						Messages.get("articleupdate.failure"));
			}
			
		    Date updateTime = new Date();
		    updateArticle.update_time = updateTime;
			
			Ebean.update(updateArticle);
			
			return ok(Json.toJson(updateArticle));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_ARTICLE_UPDATE_ERROR,
					Messages.get("articleupdate.failure"));
		}		
	}
	
	public Result delete() {
		Form<Article> ArticleForm = 
				Form.form(Article.class).bindFromRequest();
		
		if (ArticleForm.hasErrors()) {
			return status(ErrDefinition.E_ARTICLE_FORM_ERROR,
					Messages.get("article.failure"));
		}
			
		try { 
			Article deleteArticle = ArticleForm.get();
			// Check the updated article is by author
			Article originArticle = Article.find.where()
					.eq("article_id", deleteArticle.article_id )
					.findUnique();
			
			if (originArticle == null)
			{	
				return status(ErrDefinition.E_ARTICLE_DELETE_ERROR,
						Messages.get("commentdelete.failure"));
			}
			
			if (session("userId").equals(originArticle.author.id) ||(Account.find.where().eq("id", (session("userId"))).findUnique()!=null && Account.find.where().eq("id", (session("userId"))).findUnique().role>=1)) {
				CommentController.delete(originArticle.article_id);
				Ebean.delete(originArticle);
				
				
				return ok(Json.toJson(originArticle));
			}
			else
			{	
				return status(ErrDefinition.E_ARTICLE_DELETE_ERROR,
						Messages.get("articledelete.failure"));
			}					
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_ARTICLE_DELETE_ERROR,
					Messages.get("articledelete.failure"));
		}		
	}
	
	public Result thumb(Integer thumb_type, String type_id, String upOrDown) {
		
		try {		
			if (thumb_type == 0) { //article
			  Thumbs thumb = Thumbs.find.where()
					.eq("article_id", type_id)
					.eq("user_id", session("userId"))
					.findUnique();
			
			  if (thumb != null) {
				return status(ErrDefinition.E_THUMBS_HAS_DONE_ERROR,
						Messages.get("thumbs.failure"));
			  }
			
			  thumb = new Thumbs();
			
			  thumb.id = CodeGenerator.GenerateUUId();
		      thumb.article_id = type_id;
			  thumb.user = Account.find.byId(session("userId"));
			  thumb.thumbs = upOrDown;
			
			  Ebean.save(thumb);
			
			  Integer pos_num = Thumbs.find.where()
					.eq("article_id", type_id)
					.eq("thumbs", "up")
					.findRowCount();
			
			  Integer neg_num = Thumbs.find.where()
					.eq("article_id", type_id)
					.eq("thumbs", "down")
					.findRowCount();
			
			  Article article = Article.find.where()
					.eq("article_id", type_id)
					.findUnique();
			
			  if (article != null)
			  {
				article.thumbs_number = pos_num;
				article.thumbs_number2 = neg_num;
				Ebean.update(article);
			  }
			
			  List<Integer> list = new ArrayList<Integer>();
			  list.add(pos_num);
			  list.add(neg_num);
			  return ok(Json.toJson(list));
			}
			else if (thumb_type == 1) { // comment
				Thumbs thumb = Thumbs.find.where()
						.eq("comment_id", type_id)
						.eq("user_id", session("userId"))
						.findUnique();
				
				  if (thumb != null) {
					return status(ErrDefinition.E_THUMBS_HAS_DONE_ERROR,
							Messages.get("thumbs.failure"));
				  }
				
				  thumb = new Thumbs();
				
				  thumb.id = CodeGenerator.GenerateUUId();
			      thumb.comment_id = type_id;
				  thumb.user = Account.find.byId(session("userId"));
				  thumb.thumbs = upOrDown;
				
				  Ebean.save(thumb);
				
				
				
				  Integer pos_num = Thumbs.find.where()
						.eq("comment_id", type_id)
						.eq("thumbs", "up")
						.findRowCount();
				
				  Integer neg_num = Thumbs.find.where()
						.eq("comment_id", type_id)
						.eq("thumbs", "down")
						.findRowCount();
				
				  Comments comments = Comments.find.where()
						.eq("comments", type_id)
						.findUnique();
				
				  if (comments != null)
				  {
					comments.thumbs_number = pos_num;
					comments.thumbs_number2 = neg_num;
					Ebean.update(comments);
				  }
				
				  List<Integer> list = new ArrayList<Integer>();
				  list.add(pos_num);
				  list.add(neg_num);
				  return ok(Json.toJson(list));
			}
			else
			{
				return status(ErrDefinition.E_THUMBS_SUBMIT_ERROR,
						Messages.get("thumbssubmit.failure"));
			}
		}
		catch (Throwable e) {
			System.err.println(e.toString());
			return status(ErrDefinition.E_THUMBS_SUBMIT_ERROR,
					Messages.get("thumbssubmit.failure"));
		}		
	}
	
    public Result collectionPost(String article, String addOrDelete) {	
		try { 
			
			if (addOrDelete.equals("add")) {
				Collections collection = Collections.find.where()
						.eq("user_id", session("userId"))
						.eq("article_id", article)
						.findUnique();
				
				if (collection != null)
				{
					return status(ErrDefinition.E_COLLECTIONS_POST_ERROR,
							Messages.get("collectionspost.failure"));
				}
			    collection = new Collections();
			
			    collection.article_id = article;
			    collection.article_name = Article.find.where().eq("article_id", article).findUnique().article_name;
			    collection.id = CodeGenerator.GenerateUUId();
			    collection.user = Account.find.byId(session("userId"));
			
			    Ebean.save(collection);
    			return ok(Json.toJson(collection));
			} else if (addOrDelete.equals("delete")) {
				Collections collection = Collections.find.where()
						.eq("user_id", session("userId"))
						.eq("article_id", article)
						.findUnique();
				
				if (collection != null) {
					Ebean.delete(collection);
				}
	    		return ok(Json.toJson(collection));
			}
			else
			{
				return status(ErrDefinition.E_COLLECTIONS_POST_ERROR,
						Messages.get("collectionspost.failure"));
			}
				
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_COLLECTIONS_POST_ERROR,
					Messages.get("collectionspost.failure"));
		}		
	}
    
  public Result collectionGet(int pageNumber, int sizePerPage) {	
    try { 
    	List<Collections> listConnections = Collections.find.where()
				.eq("user_id", session("userId"))
				.setFirstRow(pageNumber*sizePerPage)
				.setMaxRows(sizePerPage)
				.findList();
    	
    	for(Collections itera:listConnections) {
	          if (itera.article_name == null) {
	              itera.article_name = Article.find.where().eq("article_id", itera.article_id).findUnique().article_name;
	              Ebean.update(itera);
	          }
	       }
			
		  return ok(Json.toJson(listConnections));
	  }
	  catch (Throwable e) {
		  System.err.println(e.toString());
		  return status(ErrDefinition.E_COLLECTIONS_GET_ERROR,
				  Messages.get("collectionsget.failure"));
	  }	
  }

public Result isCollected(String article_id) {	
    try { 
    	Collections collection = Collections.find.where()
				.eq("user_id", session("userId"))
				.eq("article_id", article_id)
				.findUnique();
		//  is not collected                    null    error	
    	if (collection != null) {
            return ok(Json.toJson(collection));    	    
    	}
    	else {
    	    return status(ErrDefinition.E_COLLECTIONS_GET_ERROR,
                    Messages.get("collectionsget.failure"));
    	}
	  }
	  catch (Throwable e) {
		  System.err.println(e.toString());
		  return status(ErrDefinition.E_COLLECTIONS_GET_ERROR,
				  Messages.get("collectionsget.failure"));
	  }	
  }
}
