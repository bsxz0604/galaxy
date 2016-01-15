package controllers.users;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlUpdate;
import com.avaje.ebean.annotation.Transactional;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

import controllers.AppController;
import controllers.ErrDefinition;
import controllers.chatroom.ChatRoomController;
import controllers.common.CodeGenerator;
import controllers.stock.StockManager;
import models.application.Application;
import models.chatroom.ChatRoomTime;
import models.common.Account;
import models.gift.Gift;
import models.hotpeople.HotPeopleMp3;
import models.users.AccountAppId;
import models.users.AppProfile;
import models.users.AppProfileGift;
import models.users.CharmValue;
import models.users.Money;
import models.users.MoneyRecord;
import play.Logger;
import play.data.Form;
import play.db.DB;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;

class GiftTable {
	public String gift_name;
	public String gift_image;
	public Integer gift_number;
}

public class AppProfileGiftController extends AppController {
	public Result readGift(String account_id) {
		try {
			List<AppProfileGift> userProfileGift = AppProfileGift.find.where()
					.eq("account_id", account_id)
					.eq("app_id", session("appId")).findList();

			if (userProfileGift != null) {
				List<GiftTable> gift_list = new ArrayList<GiftTable>();
				for (AppProfileGift itera : userProfileGift) {
					GiftTable newGift = new GiftTable();
					newGift.gift_name = itera.gift.gift_name;
					newGift.gift_image = itera.gift.gift_image;
					newGift.gift_number = itera.gift_number;
					gift_list.add(newGift);
				}
				return ok(Json.toJson(gift_list));
			} else {
				return ok(Json.toJson("[]"));
			}
		} catch (Throwable e) {
			return status(ErrDefinition.E_GIFT_READ_FAILED,
					Messages.get("readgift.failure"));
		}
	}

	class TodayGift {
		public boolean isSendGiftToday;
		public Integer numberForToday;
	}

	public Result isSendGiftToday() {

		TodayGift todayGiftInfo = new TodayGift();
		todayGiftInfo.isSendGiftToday = false;
		todayGiftInfo.numberForToday = 0;

		Date nowDate = new Date();
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(nowDate);
		startDate.set(Calendar.HOUR_OF_DAY, 0);
		startDate.set(Calendar.MINUTE, 0);
		startDate.set(Calendar.SECOND, 0);
		startDate.set(Calendar.MILLISECOND, 0);

		List<CharmValue> fan_charm = CharmValue.find.where()
				.eq("sender_id", session("userId"))
				.setOrderBy("create_time desc").setFirstRow(0).setMaxRows(3)
				.findList();

		if (fan_charm == null) {
			todayGiftInfo.isSendGiftToday = false;
			todayGiftInfo.numberForToday = 0;
		} else {
			for (CharmValue itera : fan_charm) {
				if (startDate.getTime().getTime() <= itera.create_time
						.getTime()) {
					todayGiftInfo.numberForToday += itera.charm_value;
				}
			}
			if (todayGiftInfo.numberForToday > 0) {
				todayGiftInfo.isSendGiftToday = true;
			}
		}
		return ok(Json.toJson(todayGiftInfo));
	}

	public static boolean isFreeGiftOver(String userId) {
		int todayGiftNumber = 0;

		Date nowDate = new Date();
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(nowDate);
		startDate.set(Calendar.HOUR_OF_DAY, 0);
		startDate.set(Calendar.MINUTE, 0);
		startDate.set(Calendar.SECOND, 0);
		startDate.set(Calendar.MILLISECOND, 0);

		List<CharmValue> fan_charm = CharmValue.find.where()
				.eq("sender_id", userId).setOrderBy("create_time desc")
				.setFirstRow(0).setMaxRows(3).findList();

		if (fan_charm == null) {
			return false;
		} else {
			for (CharmValue itera : fan_charm) {
				if (startDate.getTime().getTime() <= itera.create_time
						.getTime()) {
					todayGiftNumber += 1;
				}
			}
			if (todayGiftNumber >= 3) {
				return true;
			}
		}
		return false;
	}

	public Result getGiftTable(int pageNumber, int sizePerPage) {
		try {
			List<Gift> giftTable = Gift.find.where()
					.setFirstRow(pageNumber * sizePerPage)
					.setMaxRows(sizePerPage).findList();

			if (null != giftTable) {
				return ok(Json.toJson(giftTable));
			} else {
				return status(ErrDefinition.E_GIFT_TABLE_ERROR,
						Messages.get("gifttable.failure"));
			}
		} catch (Throwable e) {
			return status(ErrDefinition.E_GIFT_TABLE_ERROR,
					Messages.get("gifttable.failure"));
		}
	}

	public synchronized static Result giveGift(String gift_id, String star_id,
			Integer gift_number) {

		int status = 0;
		AppProfileGift userGift = null;
		GiftTable gift_item = new GiftTable();

		// cannot send gift to oneself
		if (star_id == session("userId"))
			return status(ErrDefinition.E_GIFT_GIVE_FAILED,
					Messages.get("givegift.failure"));

		Money userMoney = Money.find.where()
				.eq("account_id", session("userId"))
				.eq("app_id", session("appId")).findUnique();

		Gift gift = Gift.find.where().eq("id", gift_id)
				.eq("app_id", session("appId")).findUnique();

		if (null == gift) {
			status = -1;
			Logger.debug("givegift.failure");
		}
		// Only 1 gift is allowed each time now
		if (gift_number != 1)
			gift_number = 1;
		gift_item.gift_name = gift.gift_image;
		gift_item.gift_image = gift.gift_image;
		gift_item.gift_number = gift_number;

		Account newaccount = Account.find.where().eq("id", session("userId"))
				.findUnique();

		Application newapplication = Application.find.where()
				.eq("id", session("appId")).findUnique();

		// Free gift is over, now judge time
		if (isFreeGiftOver(session("userId"))) {
			Date nowDate = new Date();
			long maxNum = 0;
			long durationTime = 0;

			ChatRoomTime lastJoinTime = ChatRoomTime.find.where()
					.eq("account_id", session("userId")).eq("action", "join")
					.findUnique();

			if (lastJoinTime == null)
				return status(ErrDefinition.E_GIFT_GIVE_FAILED,
						Messages.get("givegift.failure"));

			long nowDateTime = nowDate.getTime();
			durationTime = nowDateTime - lastJoinTime.action_time.getTime();

			if (gift.gift_free_time > 0) {
				maxNum = durationTime / (1000 * gift.gift_free_time);
			}
			if ((maxNum) > 9) {
				maxNum = 9;
			}

			if ((maxNum <= 0))
				return status(ErrDefinition.E_GIFT_GIVE_FAILED,
						Messages.get("givegift.failure"));

			if (maxNum == 9)
				lastJoinTime.action_time = new Date(nowDateTime - 8 * 1000
						* gift.gift_free_time);
			else
				lastJoinTime.action_time = new Date(
						lastJoinTime.action_time.getTime() + 1000
								* gift.gift_free_time);
			// lastJoinTime.send_gift_number = (int)
			// (lastJoinTime.send_gift_number+ deltaNum); // possible
			// maximum number
			Ebean.update(lastJoinTime);

		}
		// Ebean.beginTransaction();

		// try {

		if (null == userMoney) {
			Money newUser = new Money();

			newUser.id = new AccountAppId(session("userId"), session("appId"));
			newUser.operation_time = new Date();
			newUser.account = newaccount;
			newUser.application = newapplication;
			newUser.money = 0;

			Ebean.save(newUser);
			userMoney = newUser;
		}
		if (userMoney.money < (gift_number * gift.price)) {
			status = ErrDefinition.E_GIFT_LACK_MONEY;
		} else {
			userMoney.money = userMoney.money - gift.price * gift_number;
			userMoney.operation_time = new Date();
			Ebean.update(userMoney);

			MoneyRecord moneyRecord = new MoneyRecord();
			moneyRecord.create_time = new Date();
			moneyRecord.account = userMoney.account;
			moneyRecord.application = userMoney.application;
			moneyRecord.money = -(gift.price * gift_number);
			moneyRecord.action = "gift_cost";
			Ebean.save(moneyRecord);

			CharmValue latest_fan_charm = CharmValue.find.where()
					.eq("receiver_id", star_id).setOrderBy("miliSecond desc")
					.setFirstRow(0).setMaxRows(1).findUnique();

			Integer total = 0;
			Date date = new Date();
			SimpleDateFormat newDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH-mm-ss");
			long miliSecond = System.currentTimeMillis();
			String newId = CodeGenerator.GenerateUUId();
			if (latest_fan_charm == null) {
				total = gift_number;
			} else {
				total = latest_fan_charm.total_charm_value + gift_number;
			}
			String sqlString = String
					.format("INSERT INTO `galaxy`.`charm_value` (`id`, `create_time`,"
							+ "`sender_id`, `receiver_id`, `app_id`, `charm_value`, `total_charm_value`, `milisecond`)"
							+ " VALUES ('%s', '%s', '%s', '%s', "
							+ "'34536418-d6b2-451f-a400-4f0e284c9497', '1', '%s','%s');",
							newId, newDateFormat.format(date),
							session("userId"), star_id, total, miliSecond);
			SqlUpdate update = Ebean.createSqlUpdate(sqlString);
			Ebean.execute(update);

			userGift = AppProfileGift.find.where()
					.eq("account_id          ", star_id).eq("gift_id", gift_id)
					.eq("app_id", session("appId")).findUnique();

			if (null == userGift) {
				userGift = new AppProfileGift();
				Account newstaraccount = Account.find.byId(star_id);

				userGift.id = CodeGenerator.GenerateUUId();
				userGift.account = newstaraccount;
				userGift.application = newapplication;
				userGift.gift = gift;
				userGift.create_time = new Date();
				userGift.gift_number = gift_number;
				Ebean.save(userGift);
			} else {
				userGift.gift_number += gift_number;
				userGift.create_time = new Date();
				Ebean.update(userGift);
			}
			new ChatRoomController().notifySendingPresent(star_id, "",
					AppProfile.find.byId(userMoney.id).name, "",
					gift.gift_name, gift_number);
		}
		// Ebean.commitTransaction();
		// AppProfileGiftController.getInstance().notifyAll();

		if (status == 0) {
			return ok(Json.toJson(gift_item));
		} else {
			return ok(Json.toJson(status));
		}
		// }finally {
		// // rollback if we didn't commit
		// // i.e. an exception occurred before commitTransaction().
		// Ebean.endTransaction();
		// }

	}

	// private static AppProfileGiftController m_instance = null;
	// public static AppProfileGiftController getInstance() {
	// if (null == m_instance) {
	// m_instance = new AppProfileGiftController();
	// }
	//
	// return m_instance;
	// }

	public Result giveGiftBack(String gift_id, String star_id,
			Integer gift_number) {

		// cannot send gift to oneself
		if (star_id == session("userId"))
			return status(ErrDefinition.E_GIFT_GIVE_FAILED,
					Messages.get("givegift.failure"));
		Account newaccount = Account.find.where().eq("id", session("userId"))
				.findUnique();

		Application newapplication = Application.find.where()
				.eq("id", session("appId")).findUnique();
		Gift gift = Gift.find.where().eq("id", gift_id)
				.eq("app_id", session("appId")).findUnique();
		CharmValue latest_fan_charm = CharmValue.find.where()
				.eq("receiver_id", star_id).setOrderBy("create_time desc")
				.setFirstRow(0).setMaxRows(1).findUnique();
		Form<CharmValue> newCharm = Form.form(CharmValue.class)
				.bindFromRequest();
		CharmValue charmBack = newCharm.get();
		charmBack.application = newapplication;
		charmBack.id = CodeGenerator.GenerateUUId();
		charmBack.create_time = new Date();
		charmBack.sender = newaccount;
		charmBack.receiver = Account.find.byId(star_id);
		charmBack.charm_value = gift_number * gift.effect;
		if (latest_fan_charm != null)
			charmBack.total_charm_value = latest_fan_charm.total_charm_value
					+ gift_number * gift.effect;
		else
			charmBack.total_charm_value = gift_number * gift.effect;
		Ebean.save(charmBack);

		AppProfileGift userGift = AppProfileGift.find.where()
				.eq("account_id          ", star_id).eq("gift_id", gift_id)
				.eq("app_id", session("appId")).findUnique();

		if (null == userGift) {
			userGift = new AppProfileGift();
			Account newstaraccount = Account.find.byId(star_id);

			userGift.id = CodeGenerator.GenerateUUId();
			userGift.account = newstaraccount;
			userGift.application = newapplication;
			userGift.gift = gift;
			userGift.create_time = new Date();
			userGift.gift_number = gift_number;
			Ebean.save(userGift);
		} else {
			userGift.gift_number += gift_number;
			userGift.create_time = new Date();
			Ebean.update(userGift);
		}
		return ok();
	}

	public static ObjectNode getGiftStatus(String hostId, Date startDate,
			Date endDate, Integer limit, Integer maxCardNumber) {
		ObjectNode node = Json.newObject();

		AppProfile profile = AppProfile.find.byId(new AccountAppId(hostId,
				session("appId")));
		if (null == profile) {
			return node;
		}

		node.put("id", hostId);
		node.put("name", profile.name);
		node.put("head_picture", profile.head_image);

		Connection conn = DB.getConnection();

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Statement state = conn.createStatement();
			String sql = String
					.format("select count(*) from (select create_time, sender_id, count(sender_id) number from charm_value where charm_value = 1 and receiver_id = '%s' and create_time >= '%s' and create_time <' %s' group by sender_id order by number desc) t0 where t0.number >= %d;",
							hostId, sdf.format(startDate), sdf.format(endDate),
							limit);
			ResultSet results = state.executeQuery(sql);

			if (results.next()) {
				int giftGiven = results.getInt(1);
				node.put("card", maxCardNumber >= giftGiven ? maxCardNumber
						- giftGiven : 0);
			} else {
				node.put("card", maxCardNumber);
			}
			sql = String
					.format("select count(sender_id) number from charm_value where charm_value = 1 and receiver_id = '%s' and sender_id = '%s' and create_time >= '%s' and create_time < '%s' group by sender_id;",
							hostId, session("userId"), sdf.format(startDate),
							sdf.format(endDate));

			results = state.executeQuery(sql);

			if (results.next()) {
				node.put("number", results.getInt(1));
			} else {
				node.put("number", 0);
			}

			conn.close();
		} catch (Throwable e) {
			Logger.info(e.getMessage());
		}

		return node;
	}
}
