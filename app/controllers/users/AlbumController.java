package controllers.users;

import java.util.List;

import com.avaje.ebean.Ebean;

import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;
import models.application.Application;
import models.common.Account;
import models.users.Album;
import models.users.InterestFan;
import controllers.AppController;
import controllers.ErrDefinition;
import controllers.common.CodeGenerator;

public class AlbumController extends AppController {

	public Result create() {
		Form<Album> formAlbum = 
				Form.form(Album.class).bindFromRequest();
		
		if (formAlbum.hasErrors()) {
			return status(ErrDefinition.E_ALBUM_FORM_HASERROR,
					Messages.get("album.failure"));			
		}
		
		try {
			int albumNumber = Album.find.where()
					.eq("userId", session("userId")).findRowCount();
			
			if (albumNumber >= 200)
				return status(ErrDefinition.E_ALBUM_CREATE_EXCESS_MAX,
						Messages.get("album.failure"));
			
			Album album = formAlbum.get();
			
			album.id = CodeGenerator.GenerateUUId();
			album.account = Account.find.byId(session("userId"));
			album.userId = album.account.id;
			
			album.app = Application.find.byId(session("appId"));
			album.appId = album.app.id;
			
			Ebean.save(album);
			
			return ok(Json.toJson(album));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_ALBUM_CREATE_FAILED,
					Messages.get("album.failure"));
		}
	}
	
	public Result read() {
		String userId = session("userId");
		
		return readByUserId(userId);
	}
	
	public Result readByUserId(String userId) {
		try {
			String appId = session("appId");
			List<Album> albumList = Album.find.where()
					.eq("userId", userId)
					.eq("appId", appId)
					.findList();
			
			return ok(Json.toJson(albumList));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_ALBUM_READ_FAILED,
					Messages.get("album.failure"));
		}
	}
	
	public Result update() {
		Form<Album> formAlbum = 
				Form.form(Album.class).bindFromRequest();
		
		if (formAlbum.hasErrors()) {
			return status(ErrDefinition.E_ALBUM_FORM_HASERROR,
					Messages.get("album.failure"));			
		}
		
		try {
			Album album = formAlbum.get();
			
			album.account = new Account();
			album.account.id = album.userId;
			
			album.app = new Application();
			album.app.id = album.appId;
			
			Ebean.update(album);
			
			return ok(Json.toJson(album));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_ALBUM_UPDATE_FAILED,
					Messages.get("album.failure"));			
		}
	}
	
	public Result delete(String id) {
		try {
			String userId = session("userId");
			
			Album album = Album.find.byId(id);
			
			if (album != null) {
				if (album.userId.compareToIgnoreCase(userId) == 0) {
					Ebean.delete(Album.class, id);					
				}
			}
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_ALBUM_DELETE_FAILED,
					Messages.get("album.failure"));
		}
		
		return ok();
	}
}
