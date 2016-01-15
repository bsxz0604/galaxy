package controllers.common;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
 
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.Logger;

import controllers.BaseController;
import controllers.ErrDefinition;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;

public class GeoController extends BaseController {
	//39.983424;116.322987
	public Result getAddress(Float latitude, Float longitude) {
		String url = "http://api.map.baidu.com/geocoder/v2/?";
		Map paramsMap = new LinkedHashMap<String, String>();
		paramsMap.put("ak", "5RwnRvG1VSIUhu3PGw1NNS1x");
		paramsMap.put("callback", "rednerReverse");
		paramsMap.put("location", String.format("%f,%f", latitude, longitude));
		paramsMap.put("output", "json");
		
		try {
			url += toQueryString(paramsMap);
			url += "&sn=" + CalcSn(paramsMap);
			
	        HttpClient client = new DefaultHttpClient();
	        HttpGet httpget = new HttpGet(url);
	        HttpResponse response = client.execute(httpget);
	        InputStream is = response.getEntity().getContent();
	        String result = inStream2String(is);
	        	        
	        JsonNode node = Json.parse(result.substring(result.indexOf('(')+1, result.length()-1));
	        
	        //ObjectMapper mapper = JsonProcessUtil.getMapperInstance(false);  
	        
	        JsonNode retStatus = node.get("status");
	        if (Integer.parseInt(retStatus.toString()) != 0) {
	        	return failure(ErrDefinition.E_GEO_READ_FAILED);
	        }
	        
	        JsonNode addressComponent = node.get("result").get("addressComponent");
	        
	        String country = addressComponent.get("country").toString();
	        String province = addressComponent.get("province").toString();
	        String city = addressComponent.get("city").toString();
	        
	        if (country.isEmpty()) {
	        	return ok(Json.newObject().put("country", Messages.get("geo.unknown")));
	        }
	        
	        ObjectNode retNode = Json.newObject()
	        		.put("country", country)
	        		.put("province", province);
	        if (province.compareTo(city) != 0) {
	        	retNode.put("city", city);
	        }
	        
	        return ok(retNode);
		}
		catch (Throwable e) {
			
		}
		
    	return failure(ErrDefinition.E_GEO_READ_FAILED);
	}
	
	//http://api.map.baidu.com/geocoder/v2/?ak=E4805d16520de693a3fe707cdc962045&callback=renderReverse&location=39.983424,116.322987&output=json&pois=1
	
	private String CalcSn(Map paramsMap) throws UnsupportedEncodingException {

		String paramsStr = toQueryString(paramsMap);
		
		String wholeStr = new String("/geocoder/v2/?" + paramsStr + "j0ntU2BXndRDk2AHUqNCqWx3lpeM8lfM");
		
		String tempStr = URLEncoder.encode(wholeStr, "UTF-8");
		
		return MD5(tempStr);
	}
	
    private static String toQueryString(Map<?, ?> data) throws UnsupportedEncodingException {
	    StringBuffer queryString = new StringBuffer();
	    for (Entry<?, ?> pair : data.entrySet()) {
	            queryString.append(pair.getKey() + "=");
	            queryString.append(URLEncoder.encode((String) pair.getValue(),
	                            "UTF-8") + "&");
	    }
	    if (queryString.length() > 0) {
	            queryString.deleteCharAt(queryString.length() - 1);
	    }
	    
	    return queryString.toString();
    }
    
    private static String MD5(String md5) {
        try {
                java.security.MessageDigest md = java.security.MessageDigest
                                .getInstance("MD5");
                byte[] array = md.digest(md5.getBytes());
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < array.length; ++i) {
                        sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
                                        .substring(1, 3));
                }
                return sb.toString();
        } 
        catch (java.security.NoSuchAlgorithmException e) {
        
        }
        return null;
    }
    
    private static String inStream2String(InputStream is) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = -1;
        while ((len = is.read(buf)) != -1) {
            baos.write(buf, 0, len);
        }
        return new String(baos.toByteArray(), "UTF-8");
    }    

}
