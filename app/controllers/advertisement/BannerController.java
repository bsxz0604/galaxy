/** Author: Michael Wang
 * Date: 2015-06-27
 * Description: Provide CRUD for advertisement table.
 */
package controllers.advertisement;

import java.util.List;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.advertisement.Banner;
import models.application.Application;
import models.plaza.Article;
import models.shop.Shop;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;
import controllers.AppController;
import controllers.ErrDefinition;
import controllers.common.CodeGenerator;
import controllers.inceptors.AccessLevel;

public class BannerController extends AppController {
	
	private enum BANNER_TYPE {
		LARGE_BANNER, //0
		SMALL_BANNER, //1
		VIDEO_BANNER
	}
	
	@AccessLevel(level=2)
	public Result create() {
		Form<Banner> bannerForm = 
				Form.form(Banner.class).bindFromRequest();
		
		if (bannerForm.hasErrors()) {
			return status (ErrDefinition.E_BANNER_FORM_HASERROR,
					Messages.get("banner.failure"));
		}
		
		try {
			Banner banner = bannerForm.get();
			banner.id = CodeGenerator.GenerateUUId();
			banner.app = new Application();
			banner.app.id = session("appId");
			
			Ebean.save(banner);
			
			ObjectNode node = Json.newObject();
			node.put("id", banner.id);
			return ok(node);
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_BANNER_CREATE_FIALED,
					Messages.get("banner.failure"));
		}		
	}
	
	public Result read(String id) {

		try {
			Banner banner = Banner.find.byId(id);
			
			if (banner == null) {
				return failure(ErrDefinition.E_BANNER_NOT_FOUND);
						//Messages.get("banner.failure"));
			}
			
			return ok(Json.toJson(banner));
		}
		catch (Throwable e) {
			return status (ErrDefinition.E_BANNER_READ_FAILED,
					Messages.get("banner.failure"));
		}
		
	}
	
	
	@AccessLevel(level=2)
	public Result update() {
		Form<Banner> bannerForm = 
				Form.form(Banner.class).bindFromRequest();
		
		if (bannerForm.hasErrors()) {
			return status (ErrDefinition.E_BANNER_FORM_HASERROR,
					Messages.get("banner.failure"));
		}
		
		try {
			Banner banner = bannerForm.get();			
			//Banner bannerToUpdate = Banner.find.byId(banner.id);
			
			//if (bannerToUpdate == null) {
			//	return status(ErrDefinition.E_BANNER_UPDATE_FAILED, 
			//			Messages.get("banner.failure"));
			//}
			
			Ebean.update(banner);
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_BANNER_CREATE_FIALED,
					Messages.get("banner.failure"));
		}
		
		return ok();		
	}
	
	@AccessLevel(level=2)
	public Result delete(String id) {
		try {
			Ebean.delete(Banner.class, id);
		}
		catch (Exception e) {
			return status (ErrDefinition.E_BANNER_DELETE_FAILED,
					Messages.get("banner.failure"));
		}
		
		return ok();
	}
	
public Result readAll(Integer pageNumber, Integer sizePerPage) {
		
		try {
			List<Banner> bannerList = 
					Banner.find.where()
					.eq("app_id",session("appId"))
					.setFirstRow(pageNumber*sizePerPage)
					.setMaxRows(sizePerPage)
					.orderBy("type")
					.findList();
			
			double total = Banner.find.where()
					.eq("app.id",session("appId"))
                    .findRowCount();
			
			ObjectNode node = Json.newObject();
			double page=Math.ceil(total/sizePerPage);
			
			node.put("totalPages", page);
			node.put("rows", Json.toJson(bannerList));
			
			return ok(node);
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_BANNER_READ_FAILED,
					Messages.get("shop.failure"));
		}
	}
	
	public Result getSmallBanners() {
		return getBanners(BANNER_TYPE.SMALL_BANNER);
	}
	
	public Result getLargeBanners() {
		return getBanners(BANNER_TYPE.LARGE_BANNER);		
	}
	
	public Result getVideoBanners() {
		return getBanners(BANNER_TYPE.VIDEO_BANNER);
	}
	
	private Result getBanners(BANNER_TYPE type) {
		int bannerType = type.ordinal();
		String appId = session("appId");
		
		try {
			List<Banner> bannerList = Banner.find.where()
										.eq("app_id", appId)
										.eq("type", bannerType).findList();
			
			return ok(Json.toJson(bannerList));
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_BANNER_READ_FAILED,
					Messages.get("banner.failure"));
		}
	}
}
