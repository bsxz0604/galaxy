package controllers.weixin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import models.application.Application;
import models.common.Account;
import models.users.AppProfile;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import controllers.BaseController;
import controllers.common.CodeGenerator;
import controllers.inceptors.HeaderAccessAction;
import controllers.users.AppProfileController;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;
import play.mvc.Http.Request;

@With(HeaderAccessAction.class)
public class WxController extends Controller {
    private final static String WX_APP_ID = "wx3080a3c5b418e14a";
    private final static String WX_APP_SECRET = "a4283a97447d48614313e01835349bf0";    
    
    private final static String STR_WEBOAUTH2_FORMAT = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
    private final static String STR_WEB_TOKEN_FORMAT = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
    private final static String STR_USER_INFO_FORMAT = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";
    private final static String STR_ACCESSTOKEN_FORMAT = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
    private final static String STR_JSTICKET_FORMAT = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=%s&type=jsapi";

    private static String ACCESS_TOKEN = "";
    private static Date   ACCESS_TOKEN_EXPIRE_DATE  = null;
    
    private static String JS_TICKET = "";
    private static Date   JS_TICKET_EXPIRE_DATE = null;
    
    private static String[] strs = new String[]
    {
        "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",
        "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"
    };
    
   
    
    public Result verify(String url) {
        
        //1. get app id
    	session("appId","9a93c96b-4db2-93a2-c5dc-b95cef0694fb"); 
        if (session("appId") == null) {
            return status(404);
        }
        Logger.info("verify---------"+url);
        Application app = Application.find.byId(session("appId"));
        
        if (app == null) {
            //Logger.info("application not found!");
            return status(404);
        }
        
        url = url.replace('&', '@');
        String requestUrl = String.format(STR_WEBOAUTH2_FORMAT, WX_APP_ID, Messages.get("site.name") + "/callback?url=" + url);
        //Logger.info("requestUrl: " + requestUrl);
        return redirect(requestUrl);
        
        /*
        ObjectNode parameters = Json.newObject();
                
        int andIndex = url.indexOf('&');
        while (andIndex != -1) {
            int equalIndex = url.indexOf('=', andIndex);
            
            if (equalIndex != -1) {
                String paramName = url.substring(andIndex+1, equalIndex);
                int endIndex = url.indexOf('&', andIndex+1);
                endIndex = endIndex == -1 ? url.length() : endIndex;
       
                String paramValue = url.substring(equalIndex+1, endIndex);
                parameters.put(paramName, paramValue);
                
                Logger.info("Name:" + paramName);
                Logger.info("Value:" + paramValue);
            }
            
            andIndex = url.indexOf('&', andIndex+1);
        }
        
        
        JsonNode node = parameters.get("applicationId");
        if (null == node) {
            return status(404);
        }
        else {
            String appId = node.toString().substring(1, node.toString().length()-1);
            Logger.info("applicationId:" + appId);
            //Application app = Ebean.find(Application.class, appId);
            Application app = Application.find.byId(appId);
            
            if (app == null) {
                Logger.info("application not found!");
                return status(404);
            }
            //session("appId", appId);
            Logger.info("application found!");
        }
        
        //session().put("params", Json.toJson(parameters).toString());
        url = url.replace('&', '@');
        String requestUrl = String.format(STR_WEBOAUTH2_FORMAT, WX_APP_ID, "http://galaxy.24-7.com.cn/callback?url=" + url);
        Logger.info("requestUrl: " + requestUrl);
        return redirect(requestUrl);
        */
    }
    

    public Result wxCallback() {
        try {
            DynamicForm form = Form.form().bindFromRequest();
            String url = form.get("url");

          
            String code = form.get("code");
            if (null == url || null == code) {
                return status(404);
            }
          
            url = url.replace('@', '&');
            url = url.replaceFirst("&", "?");
            
            String strGetToken = String.format(STR_WEB_TOKEN_FORMAT, WX_APP_ID, WX_APP_SECRET, code); 

            String tokenResponse = sendGetRequest(strGetToken);
            JsonNode node = Json.parse(tokenResponse);

            JsonNode token = node.get("access_token");
            JsonNode openId = node.get("openid");
            Logger.info("wxopenId: " + openId);
            Account account = Account.find.where()
                    .eq("username", openId.asText()).findUnique();
            
            if (account == null) {
                account = new Account();
                account.id = CodeGenerator.GenerateUUId();
                account.role = 0;
                account.username = openId.asText();
                account.create_time = new Date();
                account.type=1;
                Ebean.save(account);
            }
            
            
            session("userId", account.id);

            if (session("profile") != null) {
                AppProfile profile = AppProfile.find.where()
                	.eq("app_id", session("appId"))
                	.eq("account_id", session("userId"))
                	.findUnique();
                
                if (null == profile) 
                {
                	new AppProfileController().internalCreate(new AppProfile());
                }
                session().remove("profile");
            }                
            
            Logger.info("wxCallback------"+url+" "+session("userId")+" "+session("appId"));
            Logger.info("Callback: " + url);
            return redirect(url);           
            
        }
        catch (Throwable e) {
            Logger.info("Caollback: Exception:" + e.getMessage());
            return status(404);
        }
//        try {
//                        
//            String header = request().getHeader("Origin");
//            header = (header == null ? "*" : header);
//            response().setHeader("Access-Control-Allow-Origin", header);
//            response().setHeader("Access-Control-Allow-Credentials", "true");
//            response().setHeader("Access-Control-Allow-Methods", "POST,GET,PUT,DELETE,OPTIONS");
//            
//            DynamicForm form = Form.form().bindFromRequest();
//            String url = form.get("url");
//            String code = form.get("code");
//                        
//            Logger.info("code: " + code);
//            //String sessionCode = session().get("code");
//            //Logger.info("session code:" + sessionCode);
//            
//            if (null == url || null == code) {
//                return status(404);
//            }
//             
//            String params = session("params");
//            //if (null == params) {
//            //    return status(404);
//            //}
//                        
//            boolean bAppend = false;
//            int markIndex = url.indexOf('@');
//            if (markIndex != -1) {
//                url = url.replace('@', '&');
//                url = url.replaceFirst("&", "?");
//                bAppend = true;
//            }
//            /*
//            if (params != null) {
//                url += "?";
//                Logger.info("params:" + params);
//                
//                JsonNode node = Json.parse(params);
//                Iterator<String> iterName = node.fieldNames();
//                
//                String name = "";
//                String value = "";
//                while (iterName.hasNext()) {
//                    name = iterName.next();
//                    
//                    if (name.compareToIgnoreCase("applicationId") == 0) {
//                        //session("appId", node.get(name).toString());
//                        continue;
//                    }
//                    
//                    value = node.get(name).toString();
//                    value = value.substring(1, value.length()-1);
//                    
//                    url += name + "=" + value + "&";
//                    bAppend = true;
//                }
//                
//                url = url.substring(0, url.length() - 1);
//            }
//            */
//            //Logger.info("url1: " + url);
//
//            session().remove("params");
//            /*
//            String userId = session("userId");
//            
//            if (userId != null) {
//                if (!bAppend) {
//                    url += "?userId=" + userId;                    
//                }
//                else {
//                    url += "&userId=" + userId;
//                }
//                Logger.info("url1:" + url);
//                Logger.info("userId:" + userId);
//                return redirect(url);
//            }
//            */
//            
//            //session().clear();
//                         
//            String strGetToken = String.format(STR_WEB_TOKEN_FORMAT, WX_APP_ID, WX_APP_SECRET, code); 
//            //Logger.info("get token:" + strGetToken);
//            
//            String tokenResponse = sendGetRequest(strGetToken);
//            Logger.info("token response:" + tokenResponse);
//            JsonNode node = Json.parse(tokenResponse);
//            
//            //session().put("code", code);
//            JsonNode token = node.get("access_token");
//            JsonNode openId = node.get("openid");
//
//            Account account = Account.find.where()
//                    .eq("username", openId.asText()).findUnique();
//            
//            if (account == null) {
//                account = new Account();
//                account.id = CodeGenerator.GenerateUUId();
//                account.role = 0;
//                account.username = openId.asText();
//                account.create_time = new Date();
//                Ebean.save(account);                
//            }
//            
//            Logger.info("userId:" + account.username);
//            //session("userId", account.id);
//            
//            if (!bAppend) {
//                url += "?userId=" + account.id;                    
//            }
//            else {
//                url += "&userId=" + account.id;
//            }
//            Logger.info("url:" + url);
//            return redirect(url);           
//        }
//        catch (Throwable e) {
//            //Logger.info(e.getMessage());
//            return status(404);
//        }
    }
    
    private String sendGetRequest(String url) {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet getMethod = new HttpGet(url);

        String retStr = "";
        try {
            HttpResponse response = client.execute(getMethod);

            if (response.getStatusLine().getStatusCode() == 200) {
                retStr = EntityUtils.toString(response.getEntity());
                //Logger.info("retStr:" + retStr);
            }            
        }
        catch (Throwable e) {
            Logger.info("Error:" + e.getMessage());
        }
        
        return retStr;
        /*
        StringBuilder retStr = new StringBuilder();
        try {
            URL urlAddr = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)urlAddr.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            
            while ((line = rd.readLine()) != null) {
                retStr.append(line);
            }
            rd.close();
            
        }
        catch (Throwable e) {
            Logger.info("Error:" +e.getMessage());
        }
        return retStr.toString();    
        */  
   }
    
    private String getAccessToken() {
        //judge whether the access token is empty
        
        if (!ACCESS_TOKEN.isEmpty()) {
            if (ACCESS_TOKEN_EXPIRE_DATE.after(new Date())) {
                return ACCESS_TOKEN;
            }
        }
        
        //request new access token
        String requestUrl = String.format(STR_ACCESSTOKEN_FORMAT, WX_APP_ID, WX_APP_SECRET);        
        String result = sendGetRequest(requestUrl);     
        JsonNode node = Json.parse(result);
        
        Object token = node.get("access_token");
        Object expire = node.get("expires_in");
        
        if (null == token || null == expire) {
            ACCESS_TOKEN = "";
            ACCESS_TOKEN_EXPIRE_DATE = null;
            return null;
        }
        
        ACCESS_TOKEN = token.toString().substring(1, token.toString().length()-1);
        Calendar newExpiredDate = Calendar.getInstance();
        newExpiredDate.add(Calendar.SECOND, Integer.parseInt(expire.toString()));
        ACCESS_TOKEN_EXPIRE_DATE = newExpiredDate.getTime();
        
        return ACCESS_TOKEN;
    }
    
    public Result login() {        
        String header = request().getHeader("Origin");
        header = (header == null ? "*" : header);
        response().setHeader("Access-Control-Allow-Origin", header);
        response().setHeader("Access-Control-Allow-Credentials", "true");
        response().setHeader("Access-Control-Allow-Methods", "POST,GET,PUT,DELETE,OPTIONS");
        
        DynamicForm form = Form.form().bindFromRequest();
        
        String userId = form.get("userId");
        String appId = form.get("appId");
        
        session("userId", userId);
        session("appId", appId);
        
        //Logger.info("from login: userId: " + userId + " appId: " + appId);
        
        return ok();
    }
    
    public String getJsTicket() {
        
        if (!JS_TICKET.isEmpty()) {
            if (JS_TICKET_EXPIRE_DATE.after(new Date())) {
                return JS_TICKET;
            }
        }
        
        String access_token = getAccessToken();
        
        //request new access token
        String requestUrl = String.format(STR_JSTICKET_FORMAT, access_token);        
        String result = sendGetRequest(requestUrl);     
        JsonNode node = Json.parse(result);
        
        Object ticket = node.get("ticket");
        Object expire = node.get("expires_in");
        
        if (null == ticket || null == expire) {
            JS_TICKET = "";
            JS_TICKET_EXPIRE_DATE = null;
            return null;
        }
        
        JS_TICKET = ticket.toString().substring(1, ticket.toString().length()-1);
        Calendar newExpiredDate = Calendar.getInstance();
        newExpiredDate.add(Calendar.SECOND, Integer.parseInt(expire.toString()));
        JS_TICKET_EXPIRE_DATE = newExpiredDate.getTime();
        
        return JS_TICKET;        
    }
    
    public static String createNonce(int strLength)
    {
        StringBuilder sb = new StringBuilder();
        int length = strs.length;
        
        for (int i = 0; i < strLength; i++)
        {
            int index = (int)(CodeGenerator.random.nextFloat()*length);
            index = index == length ? --index : index;
            sb.append(strs[index]);
        }
        
        return sb.toString();
    }
    
    public static String createTimestamp() {
        return Long.toString(new Date().getTime()/1000);
    }
    
    public Result getJsSignature() {
        
        DynamicForm form = Form.form().bindFromRequest();
        String url = form.get("url");
        //Logger.info("url:" + url);
        String nonce = createNonce(15);
        String timestamp = createTimestamp();
        String ticket = getJsTicket();

        ObjectNode node = Json.newObject();
        
        node.put("appId", WX_APP_ID);
        node.put("timestamp", timestamp);
        node.put("nonceStr", nonce);
        
        url = "url=" + url;
        nonce = "noncestr=" + nonce;
        ticket = "jsapi_ticket=" + ticket;
        timestamp = "timestamp=" + timestamp;
                       
        String[] tmpArray = new String[] {url, ticket, timestamp, nonce};
        Arrays.sort(tmpArray);

        StringBuilder strBuilder = new StringBuilder();
        for (String str : tmpArray) {
                strBuilder.append(str);
                strBuilder.append("&");
        }

        strBuilder.deleteCharAt(strBuilder.length() - 1);
        //Logger.info("string concat:" + strBuilder.toString());

        try {

                MessageDigest md=MessageDigest.getInstance("SHA-1");
                md.update(strBuilder.toString().getBytes());
                String signature = bytes2Hex(md.digest());
                node.put("signature", signature);
                node.put("shareBy", session("userId"));
        }
        catch (Exception e) {
            node = Json.newObject();
        }
        
        return ok(node);        
    }
    
    private static String bytes2Hex(byte[] bts) {
        StringBuilder des= new StringBuilder();
        String tmp=null;
        for (int i=0;i<bts.length;i++) {
                   tmp=(Integer.toHexString(bts[i] & 0xFF));
                   if (tmp.length()==1) {
                       tmp = "0" + tmp;
                   }
                   des.append(tmp);
               }
        return des.toString();
    }
    
    public Result newVerify(String url) {
        
        //1. get app id
        if (session("appId") == null) {
            return status(404);
        }
        Logger.info("verify---------"+url);
        Application app = Application.find.byId(session("appId"));
        
        if (app == null) {
            //Logger.info("application not found!");
            return status(404);
        }
        
        url = url.replace('&', '@');
        String requestUrl = String.format(STR_WEBOAUTH2_FORMAT, WX_APP_ID, Messages.get("site.name") + "/newcallback?url=" + url);
        return redirect(requestUrl);
    }
    
    public Result newWxCallback() {
    	Ebean.beginTransaction();
        try {
            DynamicForm form = Form.form().bindFromRequest();
            String url = form.get("url");

          
            String code = form.get("code");
            if (null == url || null == code) {
                return status(404);
            }
          
            url = url.replace('@', '&');
            url = url.replaceFirst("&", "?");
            
            String strGetToken = String.format(STR_WEB_TOKEN_FORMAT, WX_APP_ID, WX_APP_SECRET, code); 

            String tokenResponse = sendGetRequest(strGetToken);
            JsonNode node = Json.parse(tokenResponse);

            JsonNode token = node.get("access_token");
            JsonNode openId = node.get("openid");
            Logger.info("openId: " + openId);
            Account account = Account.find.where()
                    .eq("username", openId.asText()).findUnique();
            
            if (account == null) {
                account = new Account();
                account.id = CodeGenerator.GenerateUUId();
                account.role = 0;
                account.username = openId.asText();
                account.create_time = new Date();
                account.type=1;
                Ebean.save(account);
            }
            
            
            session("userId", account.id);
            Logger.info(session("userId"));
            if (session("profile") != null) {
                AppProfile profile = AppProfile.find.where()
                	.eq("app_id", session("appId"))
                	.eq("account_id", session("userId"))
                	.findUnique();
                
                if (null == profile) 
                {
                	new AppProfileController().internalCreate(new AppProfile());
                }
                session().remove("profile");
            }                
            
            Logger.info("wxCallback------"+url+" "+session("userId")+" "+session("appId"));
            Logger.info("Callback: " + url);
            Ebean.commitTransaction();
//            ObjectNode node1 = Json.newObject();
//            node1.put("openId", openId.toString());
            if(url.contains("?")) {url = url+"&t="+openId.toString();}
            else {
            	url=url+"?t="+openId.toString();
            }
            
            return redirect(url);           
            
        }
        catch (Throwable e) {
            Logger.info("Caollback: Exception:" + e.getMessage());
            return status(404);
        }finally{
        	Ebean.endTransaction();
        }

    }
}
