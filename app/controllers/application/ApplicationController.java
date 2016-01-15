/** Author: Michael Wang
 * Date: 2015-06-27
 * Description: Provide CRUD for application table.
 */
package controllers.application;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.api.libs.Files;
import play.data.DynamicForm;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Http.RequestBody;
import play.mvc.Result;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import controllers.inceptors.*;
import controllers.BaseController;
import controllers.ErrDefinition;
import controllers.common.CodeGenerator;
import models.application.Application;;

public class ApplicationController extends BaseController {
	
	@AccessLevel(level=2)
	public Result create() {
		Form<Application> appForm = 
				Form.form(Application.class).bindFromRequest();
		
		if (appForm.hasErrors()) {
			return status(ErrDefinition.E_APP_FORM_HASERROR, 
					Messages.get("application.actionfailed"));			
		}
		
		try {
			Application app = appForm.get();
			app.id = CodeGenerator.GenerateUUId();
			Ebean.save(app);
			
			ObjectNode node = Json.newObject();
			node.put("id", app.id);
			return ok(node);
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_APP_EXCEPTION_FOUND,
					Messages.get("application.actionfailed"));
		}		
	}
	
	public Result getList() {		
		return ok(Json.toJson(Application.find.all()));
	}
	
	public Result select(String appId) {
		session().remove("appId");
		try {
			Application app = Application.find.byId(appId);
			
			if (null == app) {
				return failure(ErrDefinition.E_APP_NOT_FOUND);
						//Messages.get("application.actionfailed"));					
			}
			
			session("appId", appId);
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_APP_NOT_FOUND,
					Messages.get("application.actionfailed"));			
		}
		
		return ok();
	}
		
	@AccessLevel(level=2)
	public Result update() {
		Form<Application> appForm = 
				Form.form(Application.class).bindFromRequest();
		
		if (appForm.hasErrors()) {
			return status(ErrDefinition.E_APP_FORM_HASERROR, 
					Messages.get("application.actionfailed"));			
		}
		
		try {
			Application app = appForm.get();			
			Ebean.update(app);
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_APP_EXCEPTION_FOUND,
					Messages.get("application.actionfailed"));
		}
		
		return ok();
	}
	
	@AccessLevel(level=2)
	public Result delete(String id) {
		try {
			Ebean.delete(Application.class, id);
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_APP_NOT_FOUND,
					Messages.get("application.actionfailed"));			
		}
		
		return ok();
	}
	
	@AccessLevel(level=1)
	public Result uploadAppPicture() {
		//pictures only related to the application
		String appId = session("appId");
		
		if (appId == null || appId.isEmpty()) {
			return failure(ErrDefinition.E_APP_IMG_INVALID_APP);
					//Messages.get("application.actionfailed"));
		}
		
		String dir = "pictures" + File.separator + appId;
		
		return uploadPic(dir, false);
	}
	
	@AccessLevel(level=1)
	public Result uploadAppPictureOld() {
		//pictures only related to the application
		String appId = session("appId");
		
		if (appId == null || appId.isEmpty()) {
			return failure(ErrDefinition.E_APP_IMG_INVALID_APP);
					//Messages.get("application.actionfailed"));
		}
		
		String dir = "pictures" + File.separator + appId;
		
		return uploadPicOld(dir);
	}	
	
	public Result uploadUserPicture() {
		//pictures both related to application and user
		String appId  = session("appId");
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
		
		return uploadPic(dir, true);
	}
	
	
	public Result uploadUserPictureOld() {
		//pictures both related to application and user
		String appId  = session("appId");
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
		
		return uploadPicOld(dir);
	}

	/**
	 * You must mount the picture folder on the remote server to public/pictures folder.
	 * Otherwise the data will only be stored in local.
	 * e.g. sshfs root@122.144.166.215:/home/pic-source /your local dir/public/pictures -o allow_other -o reconnect
	 */	
	private Result uploadPic(String relativeFileDir, boolean bCompress) {
		
		String domain = Messages.get("site.name");
		String linkPath     = domain + File.separator;
		String physicalPath = "public" + File.separator;
		
		
        ObjectNode json = Json.newObject();
		try {
			
			/*
			MultipartFormData body = requestBody.asMultipartFormData();
			FilePart imageData = body.getFile("imageFile");
			*/
			DynamicForm form = Form.form().bindFromRequest();
			Map<String, String> data = form.data();
			String imageData = data.get("base64Img");

			if(imageData == null) {
				return status(ErrDefinition.E_APP_IMG_NODATA, 
						Messages.get("application.actionfailed"));		
			}
			
			int separator = imageData.indexOf(',');
			String imageType = imageData.substring(0, separator);
			imageData = imageData.substring(separator+1, imageData.length());
			
			BASE64Decoder decoder = new BASE64Decoder();  
			byte[] b = decoder.decodeBuffer(imageData);

    		if (b.length >= 1000000L && !bCompress) {
    			return failure(ErrDefinition.E_APP_IMG_TOOLARGE);
    		}
						
			File imageFile = File.createTempFile(CodeGenerator.GenerateRandomNumber(), ".jpg");
			OutputStream out = new FileOutputStream(imageFile);    
            out.write(b);
            out.flush();
            out.close();			
			
    		//File imageFile = imageData.getFile();
    		//String realName =imageData.getFilename();
    		
    		//String fileName = CodeGenerator.GenerateUUId() + 
    		//		realName.substring(realName.lastIndexOf('.'));
			String fileName = CodeGenerator.GenerateUUId() + ".jpg";
    		
    		
    		physicalPath = physicalPath + relativeFileDir;
            String phsyicalFile = physicalPath + File.separator + fileName;
            
            //check whether the directory exists
            //File path = new File(physicalPath);
            //if (!path.exists()) {
            //	path.mkdirs();
            //}

    		if (bCompress) {
    			BufferedImage img = ImageResizer.zoomImage(imageFile);
                ImageResizer.writeHighQuality(img, phsyicalFile);      			
    		}
    		else {
                File f = new File(phsyicalFile);
                Files.copyFile(imageFile, f, false, true);    			
    		}
    		
    		imageFile.delete();
    		
            String linkUrl = linkPath + relativeFileDir + File.separator + fileName;

            json.put("imgUrl", linkUrl);

		}
		catch (Throwable e) {
			return status(ErrDefinition.E_APP_UPLOAD_FAILED, 
					Messages.get("application.actionfailed"));
		}
			
        return ok(json);		
	}
	
	private Result uploadPicOld(String relativeFileDir) {
		
		String domain = Messages.get("site.name");
		String linkPath     = domain + File.separator;
		String physicalPath = "public" + File.separator;
		
		
        ObjectNode json = Json.newObject();
		try {
			
			RequestBody requestBody = request().body();
			MultipartFormData body = requestBody.asMultipartFormData();
			FilePart imageData = body.getFile("imageFile");

			if(imageData == null) {
				return status(ErrDefinition.E_APP_IMG_NODATA, 
						Messages.get("application.actionfailed"));		
			}
			
    		File imageFile = imageData.getFile();
    		String realName =imageData.getFilename();
    		
    		String fileName = CodeGenerator.GenerateUUId() + 
    				realName.substring(realName.lastIndexOf('.'));
    		
            String phsyicalFile = physicalPath + relativeFileDir + File.separator + fileName;
            
            File f = new File(phsyicalFile);
            Files.copyFile(imageFile, f, false, true);

            String linkUrl = linkPath + relativeFileDir + File.separator + fileName;

            json.put("url", linkUrl);

		}
		catch (Throwable e) {
			return status(ErrDefinition.E_APP_UPLOAD_FAILED, 
					Messages.get("application.actionfailed"));				
		}
			
        return ok(json);		
	}
	
	public static BufferedImage resizeImage(BufferedImage originImg, int newSize) {
		int width  = originImg.getWidth();
		int height = originImg.getHeight();
		
		float minSize = width < height ? width * 1.0f : height * 1.0f;
		
		float scaleFactor = minSize <= newSize ? 1.0f : ((float)newSize) / minSize;
		
		AffineTransform transform = 
				AffineTransform.getScaleInstance(scaleFactor, scaleFactor);
		AffineTransformOp aop = new AffineTransformOp(transform,
			    AffineTransformOp.TYPE_BILINEAR);
		
		BufferedImage transImg = new BufferedImage(
		(int)(width * scaleFactor), (int)(height * scaleFactor), originImg.getType());
		
		aop.filter(originImg, transImg);
		
		return transImg;
	}
	
	public static BufferedImage setGaussianBlur(BufferedImage originImg, int size) {
		int mSize = size*size;
		float[] elements = new float[mSize];
		for (int i = 0; i < mSize; i++) {
			elements[i] = 1.0f/mSize;
		}
		
		Kernel kernel = new Kernel(size, size, elements);
		ConvolveOp cop = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
		
		int width  = originImg.getWidth();
		int height = originImg.getHeight();
		
		float minSize = width < height ? width * 1.0f : height * 1.0f;
		
		float scaleFactor = minSize <= 200 ? 1.0f : 200.0f / minSize;
		
		AffineTransform transform = 
				AffineTransform.getScaleInstance(scaleFactor, scaleFactor);
		AffineTransformOp aop = new AffineTransformOp(transform,
			    AffineTransformOp.TYPE_BILINEAR);
		
		BufferedImage transImg = new BufferedImage(
		(int)(width * scaleFactor), (int)(height * scaleFactor), originImg.getType());
		
		aop.filter(originImg, transImg);
		
		BufferedImage blurImg = new BufferedImage(
				transImg.getWidth(), transImg.getHeight(), transImg.getType());
		
		cop.filter(transImg, blurImg);
		
		return blurImg;
	}
	
	
    public static String getImageBase64(BufferedImage img)  
    {
        InputStream in = null;  
        byte[] data = null;  

        try   
        {  
        	ByteArrayOutputStream bs = new ByteArrayOutputStream();  
        	ImageOutputStream imOut = ImageIO.createImageOutputStream(bs); 
        	ImageIO.write(img, "jpg",imOut);
        	
            in = new ByteArrayInputStream(bs.toByteArray());         
            data = new byte[in.available()];  
            in.read(data);  
            in.close();  
            
            imOut.close();
        }   
        catch (IOException e)   
        {  
            e.printStackTrace();  
        }  

        BASE64Encoder encoder = new BASE64Encoder();  
        return encoder.encode(data);
    } 	
	
	public Result resource(String filePath) {
		try {
			String physicalPath = "public" + File.separator + "pictures" + File.separator;
	        return ok(new File(physicalPath + filePath));			
		}
		catch (Throwable e) {
			return status(ErrDefinition.E_APP_IMG_FAILED, 
					Messages.get("application.actionfailed"));				
		}
	}
}
