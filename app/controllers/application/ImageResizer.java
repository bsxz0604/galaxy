package controllers.application;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.google.common.io.Files;

//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGEncodeParam;
//import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class ImageResizer {
	  

    public static BufferedImage zoomImage(File srcfile) {  
          
        BufferedImage result = null;  
  
        try {  
            if (!srcfile.exists()) {  
            	return null;
            }  
            BufferedImage im = ImageIO.read(srcfile);  
  
            int width = im.getWidth();  
            int height = im.getHeight();
            
            int largerSize = width > height ? width : height;
            
            if (largerSize <= 1920) {
            	return im;
            }
              
            float resizeTimes = 1920f / largerSize;
              
            int toWidth = (int) (width * resizeTimes);  
            int toHeight = (int) (height * resizeTimes);

            result = new BufferedImage(toWidth, toHeight,  
                    BufferedImage.TYPE_INT_RGB);  
  
            Graphics g = result.createGraphics();
            g.drawImage(im, 0, 0, toWidth, toHeight, null);
            g.dispose();            
            //It takes too long here!!!
            //result.getGraphics().drawImage(  
            //        im.getScaledInstance(toWidth, toHeight,  
            //                java.awt.Image.SCALE_FAST), 0, 0, null);  
              
  
        } catch (Exception e) {  
        	result = null;
        }  
          
        return result;  
  
    }  
      
     public static void writeHighQuality(BufferedImage im, String fileFullPath) throws IOException  {  
         String type = fileFullPath.substring(fileFullPath.lastIndexOf('.')+1,
         		fileFullPath.length()).toLowerCase();
         
         File f = new File(fileFullPath);
         Files.createParentDirs(f);
         ImageIO.write(im, type, f);         
    }    
}  
