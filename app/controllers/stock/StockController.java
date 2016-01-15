package controllers.stock;

//    stock_info       for test  stock
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import models.plaza.Theme;
import models.stock.Stock;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.htmlparser.Parser;
import org.htmlparser.visitors.TextExtractingVisitor;

import models.application.Application;
import akka.util.Collections;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;

import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import controllers.AppController;
import controllers.ErrDefinition;
import controllers.common.CodeGenerator;
import play.mvc.Result;
import views.html.index;

import com.avaje.ebean.Ebean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Date;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class StockController extends AppController {

	public Result sqlStock() {

		List<Stock> newList = new ArrayList<Stock>();
		try {

			Document doc = Jsoup.connect(
					"http://quote.eastmoney.com/stocklist.html").get();
			Elements elements = doc.getElementsByTag("ul");
			Stock stock = new Stock();

			Element element = elements.get(6);
			Elements liList = element.getElementsByTag("li");
			for (Element li : liList) {
				String pairStr = li.getElementsByTag("a").get(0).text();
				Pattern pattern1 = Pattern.compile("(.*)\\((.*)\\)");
				Matcher matcher1 = pattern1.matcher(pairStr);
				if (matcher1.find()) {
					String name = matcher1.group(1);
					String code = matcher1.group(2);
					if (code.charAt(0) == '6' && code.charAt(1) == '0') {
						stock.id = "sh" + code;
						stock.stockName = name;
						newList.add(stock);
						// Stock existStock = Stock.find.byId(stock.id);
						// if(existStock == null)
						// {
						// Ebean.save(stock);
						// }
						// else
						// if(existStock.stockName.equalsIgnoreCase(stock.stockName)
						// == false){
						// existStock.stockName = stock.stockName;
						// Ebean.update(existStock);
						// }
					}
				}
				stock = new Stock();
			}

			element = elements.get(7);
			liList = element.getElementsByTag("li");
			for (Element li : liList) {
				String pairStr = li.getElementsByTag("a").get(0).text();
				Pattern pattern1 = Pattern.compile("(.*)\\((.*)\\)");
				Matcher matcher1 = pattern1.matcher(pairStr);
				if (matcher1.find()) {
					String name = matcher1.group(1);
					String code = matcher1.group(2);
					if (code.charAt(0) == '0' && code.charAt(1) == '0') {
						stock.id = "sz" + code;
						stock.stockName = name;
						newList.add(stock);
						// Stock existStock = Stock.find.byId(stock.id);
						// if(existStock == null)
						// {
						// Ebean.save(stock);
						// }
						// else
						// if(existStock.stockName.equalsIgnoreCase(stock.stockName)
						// == false){
						// existStock.stockName = stock.stockName;
						// Ebean.update(existStock);
						// }
					} else if (code.charAt(0) == '3' && code.charAt(1) == '0') {
						stock.id = "sz" + code;
						stock.stockName = name;
						newList.add(stock);
						// Stock existStock = Stock.find.byId(stock.id);
						// if(existStock == null)
						// {
						// Ebean.save(stock);
						// }
						// else
						// if(existStock.stockName.equalsIgnoreCase(stock.stockName)
						// == false){
						// existStock.stockName = stock.stockName;
						// Ebean.update(existStock);
						// }
					}
				}
				stock = new Stock();
			}

			List<Stock> existList = Stock.find.all();
			for (Stock indexNew : newList) {
				boolean isExist = false;
				for (Stock indexExist : existList) {
					if (indexNew.id.equals(indexExist.id)
							&& indexNew.stockName.equals(indexExist.stockName)) {
						isExist = true;
						break;
					} else if (indexNew.id.equals(indexExist.id)
							&& indexNew.stockName
									.equalsIgnoreCase(indexExist.stockName) == false) {
						indexExist.stockName = indexNew.stockName;
						isExist = true;
						Ebean.update(indexExist);
					}
				}
				if (isExist == false) {
					Ebean.save(indexNew);
				}
			}

			List<Theme> existThemeList = Theme.find.all();
			for (Stock indexNew : newList) {
				boolean isExist = false;
				for (Theme indexExist : existThemeList) {
					if (indexNew.id.equals(indexExist.id)
							&& indexNew.stockName.equals(indexExist.theme_name)) {
						isExist = true;
						break;
					} else if (indexNew.id.equals(indexExist.id)
							&& indexNew.stockName
									.equalsIgnoreCase(indexExist.theme_name) == false) {
						indexExist.theme_name = indexNew.stockName;
						isExist = true;
						Ebean.update(indexExist);
					}
				}
				if (isExist == false) {
					Theme theme = new Theme();
					theme.id = indexNew.id;
					theme.theme_name = indexNew.stockName;
					theme.theme_class = 2;
					Application application = new Application();
					application.id = "7248d7fc-1fab-45a6-87fe-5b57e03ac425";
					theme.application = application;
					Ebean.save(theme);
				}
			}

			StockChineseToPinyin stockChineseToPinyin  = new StockChineseToPinyin();
			stockChineseToPinyin.newChinese();
		}

		catch (Throwable e) {
			e.printStackTrace();
		}
		return ok();
	}

	public static boolean isInteger(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public Result likeSearch(String keyWords, Integer pageNumber,
			Integer sizePerPage) {
		
		List<Stock> List = Stock.find.all();
		List<Stock> likeList = new ArrayList<Stock>();
		//Stock likeList[] = new Stock[1000];
		int size = List.size();

		//id
		for (int i = 0; i < size; i++) {
			Stock stockTemp = List.get(i);
			if(stockTemp.id.contains(keyWords)){
				stockTemp.num = stockTemp.id.indexOf(keyWords);
				likeList.add(stockTemp);
			}
		}
		
		//  name
		for (int i = 0; i < size; i++) {
			Stock stockTemp = List.get(i);
			if(stockTemp.stockName.contains(keyWords)){
				stockTemp.num = stockTemp.stockName.indexOf(keyWords);
				likeList.add(stockTemp);
			}
		}
		
		//pinyin
		for (int i = 0; i < size; i++) {
			Stock stockTemp = List.get(i);
			if(stockTemp.pinyin.contains(keyWords)){
				stockTemp.num = stockTemp.pinyin.indexOf(keyWords);
				likeList.add(stockTemp);
			}
		}
		
		int likeSize = likeList.size();
		Stock likeListArray[] = new Stock[likeSize];
		for(int i=0; i<likeSize;i++){
			likeListArray[i] = likeList.get(i);
		}
       //   position  order
		for(int k=0; k<likeSize; k++){
			for(int l=0; l<likeSize; l++){
				if(likeListArray[k].num<likeListArray[l].num){
					Stock temp = likeListArray[k];
					likeListArray[k] = likeListArray[l];
					likeListArray[l] = temp;
				}
			
			}
		}
		
		List<Stock> wantedList = new ArrayList<Stock>();
		for(int m=pageNumber*sizePerPage; m<(pageNumber*sizePerPage+sizePerPage); m++){
			if(m>=likeSize){
				break;
			}
			wantedList.add(likeListArray[m]);
		}
		
		return ok(Json.toJson(wantedList));

	}

}

// Parser parser = new Parser( (HttpURLConnection) (new
// URL("http://bbs.10jqka.com.cn/codelist.html")).openConnection() );
//
// TextExtractingVisitor visitor = new TextExtractingVisitor();
// parser.visitAllNodesWith(visitor);
// String textInPage = visitor.getExtractedText();
// int len = textInPage.length();
//
// // division of sh sz fund length
// String shStock = " ";
// String szStock = " ";
// String fund = " ";
//
// for (int i=3000; i<21530; i++) {shStock+=textInPage.charAt(i);} int shLen =
// shStock.length();
// for (int i=21583; i<44440; i++) {szStock+=textInPage.charAt(i);} int szLen =
// szStock.length();
// for (int i=44496; i<52111; i++) {fund+=textInPage.charAt(i);} int fundLen =
// fund.length();
// //
// String pareSh[] = shStock.split("\\s+");
// int i = 1; boolean judge =false;
// Stock stock = new Stock();
// StringBuilder test = new StringBuilder();
// while (i<pareSh.length) {
// judge= isInteger(pareSh[i]);
// if(pareSh[i].length() == 6 && judge ) {
// stock.id = "sz"+pareSh[i];
// i+=1;
// Ebean.save(stock);
// test = new StringBuilder();
// stock = new Stock();
// continue;}
// else {
// test.append(pareSh[i]);
// stock.stockName = test.toString();
// i+=1;}
// }

// String pareSz[] = szStock.split("\\s+");
// int i = 1; boolean judge =false;
// Stock stock = new Stock();
// StringBuilder test = new StringBuilder();
// while (i<pareSz.length) {
// judge= isInteger(pareSz[i]);
// if(pareSz[i].length() == 6 && judge ) {
// stock.id = "sz"+pareSz[i];
// i+=1;
// Ebean.save(stock);
// test = new StringBuilder();
// stock = new Stock();
// continue;}
// else {
// test.append(pareSz[i]);
// stock.stockName = test.toString();
// i+=1;}
// }

// String pareFund[] = fund.split("\\s+");
// int i = 1; boolean judge =false;
// Stock stock = new Stock();
// StringBuilder test = new StringBuilder();
// while (i<pareFund.length) {
// judge= isInteger(pareFund[i]);
// if(pareFund[i].length() == 6 && judge ) {
// stock.id = pareFund[i];
// i+=1;
// Ebean.save(stock);
// test = new StringBuilder();
// stock = new Stock();
// continue;}
// else {
// test.append(pareFund[i]);
// stock.stockName = test.toString();
// i+=1;}
// }

// public Result sqlStock() {
//
// try{
//
// Document doc = Jsoup.connect("http://bbs.10jqka.com.cn/codelist.html").get();
// Elements elements = doc.getElementsByClass("bbsilst_wei3");
// Stock stock = new Stock();
//
// Element element = elements.get(0);
// Elements liList = element.getElementsByTag("li");
// for(Element li : liList){
// String pairStr = li.getElementsByTag("a").get(0).text();
// String[] pairArray = pairStr.split("\\s+");
// if (pairArray.length > 0) {
// String code = "sh"+pairArray[pairArray.length-1];
// String name = pairArray[0];
// for(int i = 1 ; i < pairArray.length-1; i++){
// name = name.concat(pairArray[i]);
// }
// stock.stockName = name;
// stock.id = code;
// Ebean.save(stock);
// stock = new Stock();
// }
// }
//
//
// element = elements.get(1);
// liList = element.getElementsByTag("li");
// for(Element li : liList){
// String pairStr = li.getElementsByTag("a").get(0).text();
// String[] pairArray = pairStr.split("\\s+");
// if (pairArray.length > 0) {
// String code = "sz" + pairArray[pairArray.length-1];
// String name = pairArray[0];
// for(int i = 1 ; i < pairArray.length-1; i++){
// name = name.concat(pairArray[i]);
// }
// stock.stockName = name;
// stock.id = code;
// Ebean.save(stock);
// stock = new Stock();
// }
// }
//
// }
//
// catch( Throwable e ) { e.printStackTrace();
// } return ok();
// }
//
// public static boolean isInteger(String value) {
// try {
// Integer.parseInt(value);
// return true;
// } catch (NumberFormatException e) {
// return false;
// }
// }