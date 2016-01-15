package controllers.users;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import models.application.Application;
import models.common.Account;
import models.users.AppProfile;
import models.users.Vote;
import models.users.VoteRecord;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.With;
import controllers.AppController;
import controllers.ErrDefinition;
import controllers.common.CodeGenerator;
import controllers.inceptors.HeaderAccessAction;

@With(HeaderAccessAction.class)
public class VoteController extends AppController {
	
	public ObjectNode selfstatus = Json.newObject();
	
	private final static String WX_APP_ID = "wx98833137c362518b";
    private final static String WX_APP_SECRET = "274d7f58d56fe395a887ada1f73d4cef";  
    
    private final static String STR_WEBOAUTH2_FORMAT = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
    private final static String STR_WEB_TOKEN_FORMAT = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
    private final static String STR_USER_INFO_FORMAT = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";
    private final static String STR_ACCESSTOKEN_FORMAT = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

    private static String ACCESS_TOKEN = "";
    private static Date   ACCESS_TOKEN_EXPIRE_DATE  = null;
    
    private static String JS_TICKET = "";
    private static Date   JS_TICKET_EXPIRE_DATE = null;
    
    private static String[] strs = new String[]
    {
        "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",
        "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"
    };
    
    
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
   }
    
    public Result verify() {
    	Form<models.users.follow> followForm = 
				Form.form(models.users.follow.class).bindFromRequest();
    	if (followForm.hasErrors()) {
            return status(ErrDefinition.E_VOTE_FORM_ERROR);
        }
		String url =followForm.get().url;
		
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
        String requestUrl = String.format(STR_WEBOAUTH2_FORMAT, WX_APP_ID, Messages.get("site.name") + "/fmcallback?url=" + url);
        return ok(Json.toJson(requestUrl));
//        return redirect(requestUrl);
        
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
    
    public Result wxCallback() {
        try {
            DynamicForm form = Form.form().bindFromRequest();
            String url = form.get("url");
//            Logger.info("form: " + form);
          
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
            
            session().put("openid", openId.toString());
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
            if (url.indexOf("?")<0) {
            	url=url+"?status=1&id="+session("openid").toString().replace("\"", "");
			}else {
				url=url+"&status=1&id="+session("openid").toString().replace("\"", "");
			}
            
//            Logger.info("wxCallback------"+url+" "+session("userId")+" "+session("appId")+session("openid"));
//            Logger.info("Callback: " + url);
            return redirect(url);           
            
        }
        catch (Throwable e) {
            Logger.info("Caollback: Exception:" + e.getMessage());
            return status(404);
        }
    }
    
	public Result follow(String id) {
        
		try {
			getAccessToken();
			String requestUrl = String.format(STR_USER_INFO_FORMAT, ACCESS_TOKEN, id);
			
			String tokenResponse = sendGetRequest(requestUrl);
			
	        JsonNode node = Json.parse(tokenResponse);
	        if (node.get("subscribe").toString()=="1") {
	        	selfstatus.put("status", 1);
				return ok(Json.toJson(selfstatus));
			} else {
				selfstatus.put("status", 0);
				return ok(Json.toJson(selfstatus));
			}
		} catch (Throwable e) {
			return status(ErrDefinition.E_FOLLOW_ERROR, 
					Messages.get("followerror.failure"));
		}
		
	}
	
	public Result readVote(int pageNumber, int sizePerPage) {
		try {
			List<Vote> votes = Vote.find.where()
					.eq("app_id", session("appId"))
					.orderBy("vote_total desc")
					.setFirstRow(pageNumber*sizePerPage)
					.setMaxRows(sizePerPage)
					.findList();
			double total = Vote.find.where()
                    .findRowCount();
			
			ObjectNode node = Json.newObject();
			
			double page=Math.ceil(total/sizePerPage);
			
			node.put("total", page);
			node.put("raws",Json.toJson(votes));
			return ok(Json.toJson(node));
		} catch (Throwable e) {
			return status(ErrDefinition.E_VOTEREAD_ERROR, 
					Messages.get("readerror.failure"));
		}
	}
	
	public Result readBySearch(String keyword, Integer pageNumber, Integer sizePerPage) {
		String appId = session("appId");
		
		List<Vote> searchList=Vote.find.where()
				.or(com.avaje.ebean.Expr.like("search_id", "%"+keyword+"%"), 
                        com.avaje.ebean.Expr.ilike("vote_name", "%"+keyword+"%"))
                .eq("app_id", appId)
                .orderBy("search_id")
                .setFirstRow(pageNumber*sizePerPage)
                .setMaxRows(sizePerPage)
                .findList();
		
		double total = Vote.find.where()
                .findRowCount();
		
		ObjectNode node = Json.newObject();
		
		double page=Math.ceil(total/sizePerPage);
		
		node.put("total", page);
		node.put("raws",Json.toJson(searchList));
		return ok(Json.toJson(node));
	}
	
	public Result readMe(String id) {
		try {
			if (id==null) {
				id=session("userId");
			}			
			Vote vote=Vote.find.where()
					.eq("account_id", id)
					.findUnique();
			
			String sql = String.format("SELECT account_id,count(*) from (SELECT * FROM vote ORDER BY vote_total DESC) "
					+ "as a where vote_total > (SELECT vote_total from vote where account_id = '%s');",id);
			
			RawSql rawSql=
					RawSqlBuilder.parse(sql)
					.columnMapping("account_id", "account_id")
					.columnMapping("count(*)", "value")
					.create();
			Query<Vote> query = Ebean.find(Vote.class);
			query.setRawSql(rawSql);
			Vote voteme = query.findUnique();
			if (voteme==null) {
				selfstatus.put("idx", 1);
			}else {
				selfstatus.put("idx", voteme.value+1);
			}
			if (vote==null) {
				selfstatus.put("status", 5);
				return ok(Json.toJson(selfstatus));
			}
			else {
				selfstatus.put("status", 2);
				selfstatus.put("vote", Json.toJson(vote));
				
				return ok(Json.toJson(selfstatus));
			}
			
		} catch (Throwable e) {
			return status(ErrDefinition.E_VOTEREAD_ERROR, 
					Messages.get("readerror.failure"));
		}
	}
	public synchronized Result vote(String voteId) {
		
		try {
//			follow();
			Calendar now = Calendar.getInstance();  
			Date nowTime = new Date();
			int today=now.get(Calendar.DAY_OF_MONTH);
			
			Vote vote=Vote.find.where()
					.eq("account_id", voteId)
					.eq("app_id", session("appId"))
					.findUnique();
			
			VoteRecord voteRecord = VoteRecord.find.where()
					.eq("user_id", session("userId"))
					.eq("app_id", session("appId"))
					.findUnique();
			
			if (voteRecord==null) {
				VoteRecord newRecord = new VoteRecord();
				newRecord.app_id=session("appId");
				newRecord.user_id=session("userId");
				newRecord.vote_num=2;
				newRecord.vote_time=nowTime;
				Ebean.save(newRecord);
				vote.vote_total++;
				Ebean.update(vote);
				selfstatus.put("status", 4);
				return ok(Json.toJson(selfstatus));
			} 
			else 
			{
				Calendar cal=Calendar.getInstance();
				cal.setTime(voteRecord.vote_time);
				int voteday=cal.get(Calendar.DAY_OF_MONTH);
				if (voteday<today) {
					voteRecord.vote_num=2;
					vote.vote_total++;
					voteRecord.vote_time=nowTime;
					Ebean.update(vote);
					Ebean.update(voteRecord);
					selfstatus.put("status", 4);
					return ok(Json.toJson(selfstatus));
				} 
				else {
					if (voteRecord.vote_num==0) {
						selfstatus.put("status",3);
						return ok(Json.toJson(selfstatus));
					} else {
						voteRecord.vote_num--;
						vote.vote_total++;
						voteRecord.vote_time=nowTime;
						Ebean.update(vote);
						Ebean.update(voteRecord);
						selfstatus.put("status", 4);
						return ok(Json.toJson(selfstatus));
					}
				}
				
			}
			
		} catch (Throwable e) {
			return status(ErrDefinition.E_VOTE_ERROR, 
					Messages.get("voteerror.failure"));
		}
	}
	
	public Result voteJoin() {
		Form<Vote> voteForm = 
				Form.form(Vote.class).bindFromRequest();
		if (voteForm.hasErrors()) {
            return status(ErrDefinition.E_VOTE_FORM_ERROR);
        }
		try {
			Vote vote=Vote.find.where()
					.eq("account_id", session("userId"))
					.findUnique();
			if (vote==null) {
				Vote newvote = new Vote() ;
				newvote.account_id=session("userId");
				newvote.app_id=session("appId");
				newvote.vote_name=voteForm.get().vote_name;
				newvote.vote_img=voteForm.get().vote_img;
				newvote.vote_total=0;
				newvote.vote_phone=voteForm.get().vote_phone;
				Ebean.save(newvote);
				selfstatus.put("vote", Json.toJson(newvote));
				return ok(Json.toJson(selfstatus));
			}
			else {
				selfstatus.put("status",2);
				return ok(Json.toJson(selfstatus));
			}
			
		} catch (Throwable e) {
			return status(ErrDefinition.E_VOTE_JOIN_ERROR, 
					Messages.get("votejoinerror.failure"));
		}
	}
}
