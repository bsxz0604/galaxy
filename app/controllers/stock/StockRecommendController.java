package controllers.stock;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.avaje.ebeaninternal.server.core.Message;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.Logger;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import controllers.AppController;
import controllers.ErrDefinition;
import controllers.common.CodeGenerator;
import models.stock.StockRecommend;
import play.mvc.Result;
import models.common.Account;
import models.stock.Stock;
import models.application.Application;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class StockRecommendController extends AppController {

	public Result create(String stockId) {

		Date nowDate = new Date();

	
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(nowDate);
		
		int nowHour = startDate.get(Calendar.HOUR_OF_DAY);
		int nowMinute = startDate.get(Calendar.MINUTE);

		StockRecommend existNewestOne = ExistNewest();

		startDate.add(Calendar.DAY_OF_YEAR, -1);
		startDate.set(Calendar.HOUR_OF_DAY, 15);
		startDate.set(Calendar.MINUTE, 30);
		startDate.set(Calendar.SECOND, 0);
		Date yesterdayClose = startDate.getTime();

		startDate.add(Calendar.DAY_OF_YEAR, 1);
		Date todayClose = startDate.getTime();

		startDate.set(Calendar.HOUR_OF_DAY, 9);
		startDate.set(Calendar.MINUTE, 15);
		Date todayOpen = startDate.getTime();

		// time limit no stock data (close open) input
		if (nowHour < 9 || (nowHour == 9 && nowMinute < 15)) {
			StockRecommend exist = StockRecommend.find.where()
					.eq("account_id", session("userId"))
					.eq("app_id", session("appId"))
					.between("create_time", yesterdayClose, nowDate)
					.findUnique();
			if (exist == null) {
				StockRecommend stockRec = new StockRecommend();
				Account newAccount = new Account();
				Application newApplication = new Application();
				Stock newStock = new Stock();
				newAccount.id = session("userId");
				newApplication.id = session("appId");
				newStock.id = stockId;
				stockRec.accountId = newAccount;
				stockRec.application = newApplication;
				stockRec.stock = newStock;
				stockRec.id = CodeGenerator.GenerateUUId();
				stockRec.createTime = nowDate;
				if (existNewestOne == null) {
					stockRec.averageIncome = 0;
					stockRec.total = 0;
					stockRec.up = 0;
					stockRec.down = 0;
					stockRec.num = 0;
				} else {
					stockRec.averageIncome = existNewestOne.averageIncome;
					stockRec.total = existNewestOne.total;
					stockRec.up = existNewestOne.up;
					stockRec.down = existNewestOne.down;
					stockRec.num = existNewestOne.num;
				}

				Ebean.save(stockRec);
				return ok(Json.toJson(stockRec));
			} else {
				Stock newStock = new Stock();
				newStock.id = stockId;
				exist.createTime = nowDate;
				exist.stock = newStock;
				Ebean.update(exist);
				return ok(Json.toJson(exist));
			}
		}
		if (nowHour > 15 || (nowHour == 15 && nowMinute > 30)) {
			StockRecommend exist = StockRecommend.find.where()
					.eq("account_id", session("userId"))
					.eq("app_id", session("appId"))
					.between("create_time", todayClose, nowDate).findUnique();
			if (exist == null) {
				StockRecommend stockRec = new StockRecommend();
				Account newAccount = new Account();
				Application newApplication = new Application();
				Stock newStock = new Stock();
				newAccount.id = session("userId");
				newApplication.id = session("appId");
				newStock.id = stockId;
				stockRec.accountId = newAccount;
				stockRec.application = newApplication;
				stockRec.stock = newStock;
				stockRec.id = CodeGenerator.GenerateUUId();
				stockRec.createTime = nowDate;
				if (existNewestOne == null) {
					stockRec.averageIncome = 0;
					stockRec.total = 0;
					stockRec.up = 0;
					stockRec.down = 0;
					stockRec.num = 0;
				} else {
					stockRec.averageIncome = existNewestOne.averageIncome;
					stockRec.total = existNewestOne.total;
					stockRec.up = existNewestOne.up;
					stockRec.down = existNewestOne.down;
					stockRec.num = existNewestOne.num;
				}

				Ebean.save(stockRec);
				return ok(Json.toJson(stockRec));
			} else {
				Stock newStock = new Stock();
				newStock.id = stockId;
				exist.createTime = nowDate;
				exist.stock = newStock;
				Ebean.update(exist);
				return ok(Json.toJson(exist));
			}
		} else {
			return status(ErrDefinition.E_STOCK_RECOMMEND_FAILED,
					Messages.get("marketIsOpen.failure"));
		}
	}

	public Result dataToday() {
		// when the stock is closed
		Date nowDate = new Date();
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(nowDate);

		startDate.add(Calendar.DAY_OF_YEAR, -1);
		startDate.set(Calendar.HOUR_OF_DAY, 15);
		startDate.set(Calendar.MINUTE, 30);
		startDate.set(Calendar.SECOND, 0);
		Date yesterdayClose = startDate.getTime();
		startDate.setTime(nowDate);
		startDate.set(Calendar.HOUR_OF_DAY, 9);
		startDate.set(Calendar.MINUTE, 15);
		startDate.set(Calendar.SECOND, 0);
		Date todayOpen = startDate.getTime();

		// String yesterdayClose = String.valueOf(nowYear) + "-"+
		// String.valueOf(nowMonth)+"-"+String.valueOf(nowDay-1)+" "+"15:00:00";
		// String todayOpen = String.valueOf(nowYear) + "-"+
		// String.valueOf(nowMonth)+"-"+String.valueOf(nowDay)+" "+"9:30:00";

		// add judgement ------ no duplicate stock code
		List<StockRecommend> todayList = StockRecommend.find.where()
				.eq("app_id", session("appId"))
				.between("create_time", yesterdayClose, todayOpen).findList();
		for (StockRecommend list : todayList) {

			DefaultHttpClient httpclient = new DefaultHttpClient();
			String apikey = "2e9bb5adc7653ee5bc737605a24a2d82";
			String stockId = list.stock.id;
			String targetUrl = "http://apis.baidu.com/apistore/stockservice/stock?stockid="
					+ stockId + "&list=1";
			try {

				HttpGet httpget = new HttpGet(targetUrl);
				httpget.addHeader("apikey", apikey);
				ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

					@Override
					public String handleResponse(final HttpResponse response)
							throws ClientProtocolException, IOException {
						int status = response.getStatusLine().getStatusCode();
						if (status >= 200 && status < 300) {
							HttpEntity entity = response.getEntity();
							return entity != null ? EntityUtils
									.toString(entity) : null;
						} else {
							throw new ClientProtocolException(
									"Unexpected response status: " + status);
						}
					}

				};
				String responseBody = httpclient.execute(httpget,
						responseHandler);
				String abc = decodeUnicode(responseBody);
				JsonNode node = Json.parse(abc);

				double open = node.get("retData").get("stockinfo")
						.get("OpenningPrice").floatValue();
				double close = node.get("retData").get("stockinfo")
						.get("closingPrice").floatValue();
				double current = node.get("retData").get("stockinfo")
						.get("currentPrice").floatValue();
				double income = (close - open) / open * 100;
				list.open = open;
				list.close = close;
				list.current = current;
				list.income = income;// String.valueOf(income) +"%";

				if (list.num == 1) {
					list.averageIncome = list.income;
					// list.total = list.income;
				} else {
					List<StockRecommend> latestRec = StockRecommend.find
							.where().eq("account_id", list.accountId.id)
							.orderBy("create_time desc").setFirstRow(0)
							.setMaxRows(2).findList();
					list.averageIncome = (latestRec.get(1).income
							* (list.num - 1) + list.income)
							/ list.num;
				}
				// list.total = latestRec.get(1).income + list.income;}
				Ebean.update(list);
			} catch (Throwable e) {
				return status(ErrDefinition.E_STOCK_RECOMMEND_FAILED,
						Messages.get("STOCKchoice.failure"));
			}
		}
		return ok(Json.toJson(todayList));
	}

	// read all
	public Result readAll() {
		List<StockRecommend> recommendList = StockRecommend.find.all();
		return ok(Json.toJson(recommendList));
	}

	// recommend record by accountId stack——in-out       (open && close)
	public Result readById(String accountId) {

		try {
			List<StockRecommend> existList = ExistList(accountId);
			
			for (StockRecommend recommend : existList){
				Calendar calendar = Calendar.getInstance();
				Date date = recommend.createTime;
				calendar.setTime(date);
				int hour = calendar.get(Calendar.HOUR_OF_DAY);
				int minute = calendar.get(Calendar.MINUTE);
				if (hour > 15 || (hour == 15 && minute > 30)) {
					calendar.add(Calendar.DAY_OF_YEAR, 1);
					recommend.createTime = calendar.getTime();
				}
			}
			return ok(Json.toJson(existList));
		} catch (Throwable e) {
			return status(ErrDefinition.E_STOCK_RECOMMEND_FAILED,
					Messages.get("STOCKchoice.failure"));
		}
	}

	// read todayRec no data
	public Result readToday(String accountId) {

		Calendar cal = Calendar.getInstance();
		Date nowDate = new Date();
		cal.setTime(nowDate);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);

		if (accountId.equals(session("userId"))) {
			ObjectNode node = Json.newObject();

			if (hour > 15 || (hour == 15 && minute > 30)) {
				cal.set(Calendar.HOUR_OF_DAY, 15);
				cal.set(Calendar.MINUTE, 30);
				Date todayClose = cal.getTime();
				StockRecommend todayRec = StockRecommend.find.where()
						.eq("account_id", accountId)
						.eq("app_id", session("appId"))
						.between("create_time", todayClose, nowDate)
						.setFirstRow(0).setMaxRows(1)
						.orderBy("create_time desc").findUnique();
				if (todayRec != null) {
					node.put("stockRecommend", Json.toJson(todayRec));
					node.put("timeJudge", 1);
					return ok(node);
				} else {
					StockRecommend blank = new StockRecommend();
					node.put("stockRecommend", Json.toJson(blank));
					node.put("timeJudge", 1);
					return ok(node);
				}
			}
			cal.setTime(nowDate);
			if (hour < 9 || (hour == 9 && minute < 15)) {
				cal.add(Calendar.DAY_OF_YEAR, -1);
				cal.set(Calendar.HOUR_OF_DAY, 15);
				cal.set(Calendar.MINUTE, 30);
				Date todayClose = cal.getTime();
				StockRecommend todayRec = StockRecommend.find.where()
						.eq("account_id", accountId)
						.eq("app_id", session("appId"))
						.between("create_time", todayClose, nowDate)
						.setFirstRow(0).setMaxRows(1)
						.orderBy("create_time desc").findUnique();
				if (todayRec != null) {
					node.put("stockRecommend", Json.toJson(todayRec));
					node.put("timeJudge", 1);
					return ok(node);
				} else {
					StockRecommend blank1 = new StockRecommend();
					node.put("stockRecommend", Json.toJson(blank1));
					node.put("timeJudge", 1);
					return ok(node);
				}
			} else {
				cal.add(Calendar.DAY_OF_YEAR, -1);
				cal.set(Calendar.HOUR_OF_DAY, 15);
				cal.set(Calendar.MINUTE, 30);
				Date todayClose = cal.getTime();
				StockRecommend todayRec = StockRecommend.find.where()
						.eq("account_id", accountId)
						.eq("app_id", session("appId"))
						.between("create_time", todayClose, nowDate)
						.setFirstRow(0).setMaxRows(1)
						.orderBy("create_time desc").findUnique();

				if (todayRec != null) {
					node.put("stockRecommend", Json.toJson(todayRec));
					node.put("timeJudge", 0); // read myself  between 9:15---15:30
					return ok(node);
				} else {
					StockRecommend blank1 = new StockRecommend();
					node.put("stockRecommend", Json.toJson(blank1));
					node.put("timeJudge", 0);
					return ok(node);
				}

			}
		} else {
			ObjectNode node = Json.newObject();
			if (hour > 15 || (hour == 15 && minute > 30)) {
				cal.set(Calendar.HOUR_OF_DAY, 15);
				cal.set(Calendar.MINUTE, 30);
				Date todayClose = cal.getTime();
				StockRecommend todayRec = StockRecommend.find.where()
						.eq("account_id", accountId)
						.eq("app_id", session("appId"))
						.between("create_time", todayClose, nowDate)
						.setFirstRow(0).setMaxRows(1)
						.orderBy("create_time desc").findUnique();
				if (todayRec == null) {
					StockRecommend blank3 = new StockRecommend();
					node.put("stockRecommend", Json.toJson(blank3));
					node.put("timeJudge", 1);
					return ok(node);
				}
				node.put("stockRecommend", Json.toJson(todayRec));
				node.put("timeJudge", 1);
				return ok(node);
			} else {
				cal.add(Calendar.DAY_OF_YEAR, -1);
				cal.set(Calendar.HOUR_OF_DAY, 15);
				cal.set(Calendar.MINUTE, 30);
				Date todayClose = cal.getTime();
				StockRecommend todayRec = StockRecommend.find.where()
						.eq("account_id", accountId)
						.eq("app_id", session("appId"))
						.between("create_time", todayClose, nowDate)
						.orderBy("create_time desc").findUnique();
				if (todayRec == null) {
					StockRecommend blank3 = new StockRecommend();
					node.put("stockRecommend", Json.toJson(blank3));
					node.put("timeJudge", 1);
					return ok(node);
				} else {
					node.put("stockRecommend", Json.toJson(todayRec));
					node.put("timeJudge", 1);
					return ok(node);
				}
			}

		}
	}

	// [] {}
	private static String decodeUnicode(String theString) {

		char aChar;

		int len = theString.length();

		StringBuffer outBuffer = new StringBuffer(len);

		for (int x = 0; x < len;) {

			aChar = theString.charAt(x++);

			if (aChar == '[') {
				aChar = ' ';
			}
			if (aChar == ']') {
				aChar = ' ';
			}
			if (aChar == '\\') {

				aChar = theString.charAt(x++);

				if (aChar == 'u') {

					// Read the xxxx

					int value = 0;

					for (int i = 0; i < 4; i++) {

						aChar = theString.charAt(x++);

						switch (aChar) {

						case '0':

						case '1':

						case '2':

						case '3':

						case '4':

						case '5':

						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException(
									"Malformed   \\uxxxx   encoding.");
						}

					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';

					else if (aChar == 'n')

						aChar = '\n';

					else if (aChar == 'f')

						aChar = '\f';

					outBuffer.append(aChar);

				}

			} else

				outBuffer.append(aChar);

		}
		return outBuffer.toString();

	}

	//   open and  close       not the weekend
    private StockRecommend ExistNewest() {

		String str = String
				.format("select id, create_time, "
						+ " stock, open, close,"
						+ " income, current, average_income, "
						+ " num, total, up, down,"
						+ " is_closed, account_id, app_id "
						+ " from stock_recommend where (stock_recommend.open !=0 or (stock_recommend.open=0 and stock_recommend.is_closed = 1))"
						+ "	and stock_recommend.account_id = '%s' and stock_recommend.app_id = '%s'"
						+ " order by stock_recommend.create_time desc ",
						session("userId"), session("appId"));
		RawSql rawSql = RawSqlBuilder.parse(str).columnMapping("id", "id")
				.columnMapping("create_time", "createTime")
				.columnMapping("stock", "stock.id")
				.columnMapping("open", "open").columnMapping("close", "close")
				.columnMapping("income", "income")
				.columnMapping("current", "current")
				.columnMapping("average_income", "averageIncome")
				.columnMapping("num", "num").columnMapping("total", "total")
				.columnMapping("up", "up").columnMapping("down", "down")
				.columnMapping("is_closed", "isClosed")
				.columnMapping("account_id", "accountId.id")
				.columnMapping("app_id", "application.id").create();
		StockRecommend theOne = Ebean.find(StockRecommend.class)
				.setRawSql(rawSql).setFirstRow(0).setMaxRows(1).findUnique();

		return theOne;
	}

    private List<StockRecommend> ExistList(String accountId) {

		String str = String
				.format("select id, create_time, "
						+ " stock, open, close,"
						+ " income, current, average_income, "
						+ " num, total, up, down,"
						+ " is_closed, account_id, app_id "
						+ " from stock_recommend where (stock_recommend.open !=0 or (stock_recommend.open=0 and stock_recommend.is_closed = 1))"
						+ "	and stock_recommend.account_id = '%s' and stock_recommend.app_id = '%s'"
						+ " order by stock_recommend.create_time desc ",
						accountId, session("appId"));
		RawSql rawSql = RawSqlBuilder.parse(str).columnMapping("id", "id")
				.columnMapping("create_time", "createTime")
				.columnMapping("stock", "stock.id")
				.columnMapping("open", "open").columnMapping("close", "close")
				.columnMapping("income", "income")
				.columnMapping("current", "current")
				.columnMapping("average_income", "averageIncome")
				.columnMapping("num", "num").columnMapping("total", "total")
				.columnMapping("up", "up").columnMapping("down", "down")
				.columnMapping("is_closed", "isClosed")
				.columnMapping("account_id", "accountId.id")
				.columnMapping("app_id", "application.id").create();
		List<StockRecommend> theOne = Ebean.find(StockRecommend.class)
				.setRawSql(rawSql).findList();

		return theOne;
	}
    
}
