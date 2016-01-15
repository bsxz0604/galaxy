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
import models.users.AccountAppId;
import models.users.AppProfile;
import models.plaza.Sign;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;

public class CommentLevel2Controller extends AppController {

	public Result post() {
		Form<CommentsLevel2> CommentForm = 
				Form.form(CommentsLevel2.class).bindFromRequest();
		
		if (CommentForm.hasErrors()) {
			return status(ErrDefinition.E_COMMENT_FORM_ERROR,
					Messages.get("comment.failure"));
		}
		
		try {
			CommentsLevel2 comment = CommentForm.get();
			
			AccountAppId id = new AccountAppId(session("userId"), session("appId"));
			
			Comments comments = Comments.find.where()
					.eq("comments", comment.comments)
					.findUnique();
			
			if (comments == null) {
				return status(ErrDefinition.E_COMMENT_POST_ERROR,
						Messages.get("commentcreate.failure"));
			}
		    Article article = Article.find.where()
				  	.eq("article_id", comments.article_id)
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
			comment.comments = comments.comments;
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
			Ebean.save(comment);
			
			return ok(Json.toJson(comment));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_COMMENT_POST_ERROR,
					Messages.get("commentcreate.failure"));
		}
	}
	
	public Result readAll(String comments, int pageNumber, int sizePerPage) {
		try {
				List<CommentsLevel2> list = CommentsLevel2.find.where()
						.eq("comments", comments)
						.setOrderBy("update_time desc")
						.setFirstRow(pageNumber*sizePerPage)
						.setMaxRows(sizePerPage)
						.findList();
				for(CommentsLevel2 itera:list) {
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
	
	public static Result delete(String comments) {
		try { 
			List<CommentsLevel2> deleteComment = CommentsLevel2.find.where() 
					.eq("comments", comments )
					.findList();
			
			for(CommentsLevel2 itera:deleteComment) {
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
