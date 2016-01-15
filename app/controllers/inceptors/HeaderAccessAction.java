/** Author: Michael Wang
 * Date: 2015-06-26
 * Description: Customed action of adding access control header.
 */
package controllers.inceptors;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import modelslog.logger.LogsBean;
import play.Logger;
import play.libs.Json;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.SimpleResult;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class HeaderAccessAction extends Action.Simple {

	
	@Override
	public Promise<SimpleResult> call(Context context) throws Throwable {
		// TODO Auto-generated method stub
		context = setRespHeader(context);
		addLogs(context);

		return delegate.call(context);
	}
	
	private Context setRespHeader(Context context){
		String header = context.request().getHeader("Origin");
		header = (header == null ? "*" : header);
		context.response().setHeader("Access-Control-Allow-Origin", header);
		context.response().setHeader("Access-Control-Allow-Credentials", "true");
		context.response().setHeader("Access-Control-Allow-Methods", "POST,GET,PUT,DELETE,OPTIONS");
		return context;
	}
	
	private void addLogs(Context ctx){
		SimpleDateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String header = ctx._requestHeader().toString();
		Map<String,String[]> headersMap = ctx.request().headers();
		String method = (String) ctx.args.get("ROUTE_ACTION_METHOD");
		String response = ctx.response().getHeaders().get("Content-Type");
		
		
		ObjectNode node = Json.newObject();
    	for (Map.Entry<String, String[]> entry : headersMap.entrySet()) {    		
    		
    		StringBuilder values = new StringBuilder();
    		
    		for (String strValue : entry.getValue()) {
    			values.append(strValue);
    			values.append("|||");
    		}
    		values.delete(values.length() - 3, values.length());
    		
    		node.put(entry.getKey(), values.toString());
    	}
		
    	/*
		for(String key:headersMap.keySet()){
			headers +=key+":";
			String[] strs = headersMap.get(key);
			for(String str:strs){
				headers += str+",";
			}
			headers +=";";
		}
		*/
		
		String headers = node.toString();
		
		if (headers.length() > 2000) {
			//limit the size;
			headers = headers.substring(0, 2000);
		}
		
 		LogsBean log = new LogsBean();
		log.userId = ctx.session().get("userId");
		log.method = method;
		log.operationTime = dfm.format(new Date());
		log.request = headers;
		log.response = response;
		log.url = header;
		log.ip = ctx.request().remoteAddress();
		
		if (method != null && method.compareToIgnoreCase("resource") != 0) {
	        Logger.info("userId: " + log.userId + "||method:" + log.method);		    
//            Ebean.getServer("log").save(log);
		}
//   Ebean.save(log);
	}
}
