package controllers.plaza;

import java.util.Date;
import java.util.List;

import com.avaje.ebean.Ebean;

import controllers.AppController;
import controllers.ErrDefinition;
import controllers.common.CodeGenerator;
import models.application.Application;
import models.common.Account;
import models.plaza.Article;
import models.plaza.Comments;
import models.plaza.CommentsLevel2;
import models.plaza.MyTheme;
import models.plaza.Theme;
import models.plaza.Sign;
import models.users.AccountAppId;
import models.users.AppProfile;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;

public class CommentController extends AppController {

	public Result post() {
		Form<Comments> CommentForm = 
				Form.form(Comments.class).bindFromRequest();
		
		if (CommentForm.hasErrors()) {
			return status(ErrDefinition.E_COMMENT_FORM_ERROR,
					Messages.get("comment.failure"));
		}
		
		try {
			Comments comment = CommentForm.get();
			
			AccountAppId id = new AccountAppId(session("userId"), session("appId"));
			
			Article article = Article.find.where()
					.eq("article_id", comment.article_id)
					.findUnique();
			
			if (article == null) {
				return status(ErrDefinition.E_COMMENT_POST_ERROR,
						Messages.get("commentcreate.failure"));
			} else {
				article.last_reply = new Date();
				if (article.last_reply.getTime() > article.update_time.getTime())
					article.is_new_comments = true;
				if (article.comments_number == null)
					article.comments_number = 1;
				else
					article.comments_number += 1;
				Ebean.update(article);
			}
	
			comment.id = CodeGenerator.GenerateUUId();
			comment.comments = CodeGenerator.GenerateUUId();
			comment.author = Account.find.byId(session("userId"));
			comment.user_id = session("userId");
			comment.author_name = AppProfile.find.byId(id).name;
			comment.author_image = AppProfile.find.byId(id).head_image;
		    Date createTime = new Date();
			comment.create_time = createTime;
		    comment.update_time = createTime;
		    comment.last_reply = createTime;
		    comment.is_top = false;
		    comment.is_hidden = false;
		    Ebean.save(comment);
		    if(session("appId").equals("34536418-d6b2-451f-a400-4f0e284c9497")){
				Sign signItem = Sign.find.where().eq("account_id", session("userId")).eq("app_id", session("appId")).findUnique();
				if(signItem==null){
					signItem = new Sign();
					signItem.id = CodeGenerator.GenerateUUId();
					signItem.application = Application.find.where().eq("id", session("appId")).findUnique();
					signItem.user = Account.find.where().eq("id", session("userId")).findUnique();
					signItem.last_visit = new Date();
					signItem.sign_charm = 1;
					Ebean.save(signItem);
				}
				else {
					signItem.sign_charm += 1;	
					Ebean.update(signItem);
				}
			}
			
			return ok(Json.toJson(comment));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_COMMENT_POST_ERROR,
					Messages.get("commentcreate.failure"));
		}
	}
	
	public Result readAll(String article, int pageNumber, int sizePerPage) {
		try {
				List<Comments> list = Comments.find.where()
						.eq("article_id", article)
						.setOrderBy("update_time")
						.setFirstRow(pageNumber*sizePerPage)
						.setMaxRows(sizePerPage)
						.findList();
				for(Comments itera:list) {
					if (itera.user_id == null) {
						itera.user_id = itera.author.id;
					}
				}
				
				return ok(Json.toJson( list));			
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_COMMENT_READ_ERROR, 
					Messages.get("commenteread.failure"));
		}
	}
	
	public Result update() {
		Form<Comments> CommentForm = 
				Form.form(Comments.class).bindFromRequest();
		
		if (CommentForm.hasErrors()) {
			return status(ErrDefinition.E_COMMENT_FORM_ERROR,
					Messages.get("comment.failure"));
		}
		
		try { 
			Comments updateComment = CommentForm.get();
			// Check the updated comment is done by author
			Comments originComment = Comments.find.where()
					.eq("comment_id", updateComment.comments)
					.findUnique();
			AccountAppId id = new AccountAppId(session("userId"), session("appId"));
			
//			UserGroup authorGroup = UserGroup.find.where()
//		    		.eq("account_id", updateComment.author)
//		    		.findUnique();
			
//			if (authorGroup.group_name=="admin") { // administrator may update the article
//				updateComment.author = originComment.author;
//			} else
			if (AppProfile.find.byId(id).name != originComment.author_name) {
				return status(ErrDefinition.E_COMMENT_UPDATE_ERROR,
						Messages.get("commentupdate.failure"));
			}
			Article article = Article.find.where()
					.eq("article_id", updateComment.article_id)
					.findUnique();
			
			Date updateTime = new Date();
			if (article == null) {
				return status(ErrDefinition.E_COMMENT_POST_ERROR,
						Messages.get("commentcreate.failure"));
			} else {
				article.last_reply = updateTime;
				Ebean.update(article);
			}
   
		    updateComment.update_time = updateTime;			
			Ebean.update(updateComment);
			
			return ok(Json.toJson(updateComment));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_COMMENT_UPDATE_ERROR,
					Messages.get("commentupdate.failure"));
		}		
	}
	
	public static Result delete(String article) {
		try { 
			List<Comments> deleteComment = Comments.find.where() 
					.eq("article_id", article )
					.findList();
			
			for(Comments itera:deleteComment) {
				CommentLevel2Controller.delete(itera.comments);
				Ebean.delete(itera);
			}
				
			return ok(Json.toJson(""));
			
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_COMMENT_DELETE_ERROR,
					Messages.get("commentdelete.failure"));
		}		
	}
}
