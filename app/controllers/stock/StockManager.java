package controllers.stock;

import java.io.IOException;
import java.lang.Thread.State;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import models.stock.IncomeSortResult;
import models.stock.StockRecommend;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import play.Logger;
import play.libs.Json;
import play.mvc.Result;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlUpdate;
import com.fasterxml.jackson.databind.JsonNode;

import controllers.AppController;

public class StockManager extends AppController {
	private static StockManager m_instance = null;

	private HashMap<String, HashMap<String, StockRecommend>> m_stockMap = null;
	private Thread m_thread = null;
	private boolean m_bIsStop = true;

	public static StockManager getInstance() {
		if (null == m_instance) {
			m_instance = new StockManager();
		}

		return m_instance;
	}

	private StockManager() {
		m_stockMap = new HashMap<String, HashMap<String, StockRecommend>>();
	}

	public synchronized void AddRecommendStock(StockRecommend recommend) {
		if (!m_stockMap.containsKey(recommend.stock.id)) {
			m_stockMap.put(recommend.stock.id,
					new HashMap<String, StockRecommend>()); // first-level
		}

		m_stockMap.get(recommend.stock.id).put(recommend.id, recommend); // second-level
	}

	public synchronized void clear() {
		m_stockMap.clear();
	}

	public synchronized void start() {
		Logger.info("inside start");

		try {
			if (null != m_thread) {
				// stop the thread first
				m_bIsStop = true;
				while (m_thread.getState() != State.TERMINATED) {
					Thread.sleep(1);
					Logger.info("waiting");
				}
				Logger.info("inside start waiting");
			}
		} catch (Throwable e) {
			Logger.info("start -----------" + e.toString());
		}
		Logger.info("inside start waiting overrrrrrrrrr");
		m_thread = new Thread(new Runnable() {

			@Override
			public void run() {

				while (!m_bIsStop) {
					Date infoStartDate = new Date();
					// Logger.info("getStockInfoStart" + infoStartDate);
					// Logger.info("mapSize" + m_stockMap.size());
					// Logger.info(m_thread.getName()+" runnning");
					synchronized (m_stockMap) {
						for (Entry<String, HashMap<String, StockRecommend>> entry : m_stockMap
								.entrySet()) { // first-level traversal
							try {
								if (m_bIsStop) {
									break;
								}

								// TODO: update the open and close price float

								double open = 0;
								double close = 0;
								double income = 0;
								double current = 0;

								int num = 0;
								String stockId = entry.getKey();
								double arrayResult[] = DataStock(stockId);
								if (arrayResult[5] == 1) {
									continue;
								}
								open = arrayResult[0];
								close = arrayResult[1];
								income = arrayResult[2];
								current = arrayResult[3];

								boolean isClosed = false;
								boolean isClosedTime = isClosedTime(); // true :
																		// 9:30---15:30

								if (open == 0 && isClosedTime == false) { // 0---9:15
																			// newArray[4]=1;
									continue;
								}
								if (open == 0 && isClosedTime == true) {
									isClosed = true;
								}
								// save the recommend

								for (Entry<String, StockRecommend> recommend : entry
										.getValue().entrySet()) {
									if (m_bIsStop) {
										break;
									}
									// the first time to fetch data
									// isClosed-----firstFetch
									if (recommend.getValue().open == 0
											&& isClosed == true
											&& recommend.getValue().isClosed == false) {
										// Logger.info("existRecord----first time to fetch "
										// + recommend.getValue().current);
										recommend.getValue().open = open;
										recommend.getValue().close = close;
										recommend.getValue().income = income;
										recommend.getValue().current = current;
										recommend.getValue().isClosed = true;
										num = recommend.getValue().num;
										recommend.getValue().averageIncome = (recommend
												.getValue().averageIncome * num + income)
												/ (num + 1);
										recommend.getValue().num += 1;
										recommend.getValue().total += recommend
												.getValue().income;
										if (income >= 0) {
											recommend.getValue().up += 1;
										} else {
											recommend.getValue().down += 1;
										}
										// update others

										Ebean.update(recommend.getValue());
										continue;
									} // isOpen---firstFetch
									else if (recommend.getValue().open == 0
											&& isClosed == false) {
										recommend.getValue().open = open;
										recommend.getValue().close = close;
										recommend.getValue().income = income;
										recommend.getValue().current = current;
										num = recommend.getValue().num;
										recommend.getValue().averageIncome = (recommend
												.getValue().averageIncome * num + income)
												/ (num + 1);
										recommend.getValue().num += 1;
										recommend.getValue().total += recommend
												.getValue().income;
										if (income >= 0) {
											recommend.getValue().up += 1;
										} else {
											recommend.getValue().down += 1;
										}
										Ebean.update(recommend.getValue());
									} else { // not the first time
										// Logger.info("existRecord----not the first time to fetch "
										// + recommend.getValue().current);

										StockRecommend stock = recommend
												.getValue();
										int numAlready = stock.num;
										double totalAlready = stock.total;
										double incomeAlready = stock.income;

										double totalIndex = totalAlready
												- incomeAlready + income;
										recommend.getValue().total = totalIndex;
										recommend.getValue().averageIncome = totalIndex
												/ numAlready;
										if (incomeAlready < 0 && income >= 0) {
											stock.up += 1;
											stock.down += -1;
										}
										if (incomeAlready >= 0 && income < 0) {
											stock.up += -1;
											stock.down += 1;
										}
										recommend.getValue().up = stock.up;
										recommend.getValue().down = stock.down;
										recommend.getValue().open = open;
										recommend.getValue().close = close;
										recommend.getValue().current = current;
										recommend.getValue().income = income;

										String sqlString = "UPDATE stock_recommend SET current=:current, income=:income,"
												+ "open=:open,close=:close,up=:up,down=:down,total=:total,"
												+ "average_income=:average  where id =:id";
										SqlUpdate update = Ebean
												.createSqlUpdate(sqlString);
										update.setParameter("id",
												recommend.getKey());
										update.setParameter("current",
												recommend.getValue().current);
										update.setParameter("open",
												recommend.getValue().open);
										update.setParameter("close",
												recommend.getValue().close);
										update.setParameter("income",
												recommend.getValue().income);
										update.setParameter("total",
												recommend.getValue().total);
										update.setParameter(
												"average",
												recommend.getValue().averageIncome);
										update.setParameter("up",
												recommend.getValue().up);
										update.setParameter("down",
												recommend.getValue().down);
									//	Logger.info(String.valueOf(recommend
									//			.getValue().down));
										Ebean.execute(update);

										// Logger.info("updated  current    ======current new "
										// + recommend.getValue().current);
									}
								}

								Thread.sleep(300);
							} catch (Throwable e) {
								// Logger.info("update  error " + e);
								e.printStackTrace();
								Logger.info("for in -----------" + e.toString());
							}
						}
						Date infoEndDate = new Date();
						// Logger.info("getStockInfoEnd" + infoEndDate);
						try {
							Thread.sleep(30000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Logger.info("for -----------" + e.toString());
						}

						// Logger.info("notify--------before");
						m_stockMap.notify();
						// Logger.info("notify---------after");
					}
				}

			}
		});

		m_bIsStop = false;
		m_thread.start();
	}

	public synchronized void stop() {
		m_bIsStop = true;
		// Logger.info("stop---------------InSide");
		synchronized (m_stockMap) {
			try {
				m_stockMap.wait(5000);
				Logger.info("stop successed");
			} catch (Throwable e) {
				Logger.info("stop -----------" + e.toString());
			}
		}
	}

	class RemindTask extends TimerTask {
		public void run() {
			boolean listNull = false;
			stop();
			// Logger.info("stop=======excute");
			if (m_stockMap != null) {
				clear();
			}
			// Logger.info("clear=========excute");

			Date nowDate = new Date();
			Calendar startDate = Calendar.getInstance();
			startDate.setTime(nowDate);

			startDate.add(Calendar.DAY_OF_YEAR, -1);
			startDate.set(Calendar.HOUR_OF_DAY, 15);
			startDate.set(Calendar.MINUTE, 30);
			startDate.set(Calendar.SECOND, 0);
			Date yesterdayClose = startDate.getTime();
			startDate.setTime(nowDate);
			// change open time
			startDate.set(Calendar.HOUR_OF_DAY, 9);
			startDate.set(Calendar.MINUTE, 15);
			startDate.set(Calendar.SECOND, 0);
			Date todayOpen = startDate.getTime();

			// Logger.info("addStockStart " + nowDate);

			List<StockRecommend> todayList = StockRecommend.find.where()
					.eq("app_id", "7248d7fc-1fab-45a6-87fe-5b57e03ac425")
					.between("create_time", yesterdayClose, todayOpen)
					.findList();
			if (todayList.size() == 0) {
				 listNull = true;
			}
			if(!listNull){
			for (StockRecommend recommend : todayList) {
				// if festival ---- don't add
               
				double arrayResult[] = DataStock(recommend.stock.id);
				if (arrayResult[4] == 1) {
					continue;
				}
				StockManager.getInstance().AddRecommendStock(recommend);
			}
			Logger.info("mapSize -----------" + m_stockMap.size());
			start();
		}
		}
	}

	public double[] DataStock(String stockId) {

		double newArray[] = new double[10];
		// add judgement ------ no duplicate stock code
		DefaultHttpClient httpclient = new DefaultHttpClient();
		String apikey = "2e9bb5adc7653ee5bc737605a24a2d82";
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
						return entity != null ? EntityUtils.toString(entity)
								: null;
					} else {
						throw new ClientProtocolException(
								"Unexpected response status: " + status);
					}
				}

			};
			String responseBody = httpclient.execute(httpget, responseHandler);
			if (responseBody == null) { // response is null
				newArray[5] = 1;
				return newArray;
			}
			String abc = decodeUnicode(responseBody);
			JsonNode node = Json.parse(abc);

			double open = node.get("retData").get("stockinfo")
					.get("OpenningPrice").floatValue();
			double close = node.get("retData").get("stockinfo")
					.get("closingPrice").floatValue();
			double current = node.get("retData").get("stockinfo")
					.get("currentPrice").floatValue();
			double income = 0; // stock is closed to avoid open = 0 NaN
			if (open == 0) {
				income = 0;
			} else {
				income = (current - open) / open * 100; // income = current -
			}
			// open

			Calendar cal = Calendar.getInstance();
			Calendar nowCal = Calendar.getInstance();
			String Str = node.get("retData").get("stockinfo").get("date")
					.asText();
			if (Str.equals("null") == false) { // not in the market
				String dateStr = decodeDate(Str);
				SimpleDateFormat m_sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date IgnoreDate = m_sdf.parse(dateStr);
				Date nowDate = new Date();
				cal.setTime(IgnoreDate);
				nowCal.setTime(nowDate);
				// judge the weekend or festival // open == 0 stop---stock
				if (cal.get(Calendar.DAY_OF_YEAR) != nowCal
						.get(Calendar.DAY_OF_YEAR)) {
					newArray[0] = 0;
					newArray[1] = 0;
					newArray[2] = 0;
					newArray[3] = 0;
					newArray[4] = 1; // is festival recommend
					return newArray;
				}
			}
			// not weekend and festival
			newArray[0] = open;
			newArray[1] = close;
			newArray[2] = income;
			newArray[3] = current;

		} catch (Throwable e) {
			e.printStackTrace();
			Logger.info("api-------" + e.toString());
		}
		return newArray;
	}

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

	private static String decodeDate(String theString) {

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
			if (aChar == '/') {
				aChar = '-';
			}

			outBuffer.append(aChar);
		}
		return outBuffer.toString();
	}

	private static final long PERIOD_DAY = 24 * 24 * 60 * 1000;

	// int i=10;
	public void addBegin() {
		// start();
		Logger.info("first start");
		new RemindTask().run();
		// Logger.info("first end");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 9);
		cal.set(Calendar.MINUTE, 15);
		cal.set(Calendar.SECOND, 0);
		Date date = cal.getTime();
		if (date.before(new Date())) {
			cal.add(Calendar.DAY_OF_YEAR, 1);
			date = cal.getTime();
		}
		Timer timer = new Timer();
		// Logger.info("timer  ====period Start");
		timer.schedule(new RemindTask(), date, PERIOD_DAY);
	}

	private boolean isClosedTime() {
		boolean isClosedTime = false;

		Date nowDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(nowDate);

		cal.set(Calendar.HOUR_OF_DAY, 9);
		cal.set(Calendar.MINUTE, 30);
		cal.set(Calendar.SECOND, 0);
		Date startDate = cal.getTime();

		cal.set(Calendar.HOUR_OF_DAY, 15);
		cal.set(Calendar.MINUTE, 30);
		cal.set(Calendar.SECOND, 0);
		Date endDate = cal.getTime();

		if (nowDate.before(endDate) && nowDate.after(startDate)) {
			isClosedTime = true;
		}
		return isClosedTime;
	}

	public String getStatus() {
		return m_thread.getState().toString();
	}

	public static Result threadStatus() {
		return ok(StockManager.getInstance().getStatus()); // one object
															// timeWaiting
	}

	public static Result threadStop() {
		StockManager.getInstance().stop();
		return ok("stop successed"); // one object timeWaiting
	}
}
