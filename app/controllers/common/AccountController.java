/** Author: Michael Wang
 * Date: 2015-06-26
 * Description: this controller includes all operations related to account information.
 */
package controllers.common;

import java.util.Date;

import javax.persistence.PersistenceException;

import org.h2.command.dml.Delete;

import models.common.Account;
import models.gift.Gift;
import models.users.AppProfile;

import com.avaje.ebean.Ebean;
import play.Logger;

import controllers.ErrDefinition;
import controllers.application.ApplicationController;
import controllers.inceptors.*;
import controllers.users.AppProfileController;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.With;

import java.util.List;

//Login controller should not derive from BaseController as it will not do secured check.
@With(HeaderAccessAction.class)
public class AccountController extends Controller {

	// this parameter defines the max try times
	private static final int MAX_TRY_TIMES = 5;

	/*
	 * Function: Register Parameters: None Description: Register an account. It
	 * will generate a uuid for every account.
	 */
	public Result register() {
		session().clear();
		// session().remove("userRole");

		Form<Account> accountForm = Form.form(Account.class).bindFromRequest();

		if (accountForm.hasErrors()) {
			return status(ErrDefinition.E_ACCOUNT_INCORRECTPARAM,
					Messages.get("account.registerfailed"));
		}

		Account account = accountForm.get();
		return register(account);
	}

	private Result register(Account account) {
		try {
			// If type is 0, this is not a 3rd party account. Need to check the
			// password
			if (account.type == 0) {
				if (account.password == null || account.password.isEmpty()) {
					return ok(Json.newObject().put("code",
							ErrDefinition.E_ACCOUNT_NO_PASSWORD));
					// return status(ErrDefinition.E_ACCOUNT_NO_PASSWORD,
					// "account.nopassword");
				}

				// encoding the password
				account.password = CodeGenerator.GenerateMD5(account.password);
			} else {
				account.password = null;
			}

			/*
			if (!account.username.startsWith("ol-")) {
				return status(ErrDefinition.E_ACCOUNT_UNKNOWN_ERR,
						Messages.get("account.createfailed"));
			}
			*/
			
			// set the uuid for the account
			account.id = CodeGenerator.GenerateUUId();
			account.role = 0;
			account.create_time = new Date();
			Ebean.save(account);

			// successfully login, save the userId in the session.
			session("userId", account.id);
			session("userRole", "0");
		} catch (PersistenceException pe) {
			return status(ErrDefinition.E_ACCOUNT_ALREADY_EXIST,
					Messages.get("account.createfailed"));
		} catch (Throwable e) {
			return status(ErrDefinition.E_ACCOUNT_UNKNOWN_ERR,
					Messages.get("account.createfailed"));
		}

		return ok();
	}

	public Result loginWithApp(String appId) {
		Ebean.beginTransaction();
		try {
			session().clear();
			// session().remove("userId");
			// session().remove("userRole");

			Form<Account> accountForm = Form.form(Account.class)
					.bindFromRequest();

			if (accountForm.hasErrors()) {
				return status(ErrDefinition.E_ACCOUNT_INCORRECTPARAM,
						Messages.get("account.registerfailed"));
			}

			try {
				Account account = accountForm.get();
				Account userAccount = Account.find.where()
						.eq("username", account.username).findUnique();

				if (userAccount == null || account.type != userAccount.type) {
					if (account.type == 0) {
						return ok(Json.newObject().put("code",
								ErrDefinition.E_ACCOUNT_NOT_FOUND));
					}
					Status result = (Status) register(account);
					if (result.getWrappedSimpleResult().header().status() != 200) {
						return result;
					}
				} else {
					// need to check the password if the type is 0.
					if (userAccount.type == 0) {
						String password = CodeGenerator
								.GenerateMD5(account.password);

						if (password.compareTo(userAccount.password) != 0) {
							return ok(Json.newObject().put("code",
									ErrDefinition.E_ACCOUNT_NOT_FOUND));
							// status(ErrDefinition.E_ACCOUNT_NOT_FOUND,
							// Messages.get("account.notfound"));
						}
					}

					session("userId", userAccount.id);
					session("userRole", userAccount.role.toString());
				}

				if (null != appId) {
					ApplicationController app = new ApplicationController();
					Status result = (Status) app.select(appId);
					
					session("appId",appId);
					if (result.getWrappedSimpleResult().header().status() != 200) {
						return result;
					}
					AppProfile profile = AppProfile.find.where()
							.eq("account_id", session("userId"))
							.eq("app_id", appId).findUnique();
					if (userAccount == null || profile == null) {
						// create a new profile for him
						AppProfileController profileController = new AppProfileController();
						result = (Status) profileController
								.internalCreate(new AppProfile());
						if (result.getWrappedSimpleResult().header().status() != 200) {
							return result;
						}
					}
				}

			} catch (Throwable e) {
				return status(ErrDefinition.E_ACCOUNT_UNKNOWN_ERR,
						e.getMessage());
			}
			Ebean.commitTransaction();
		} finally {
			Ebean.endTransaction();
		}

		return ok(Json.newObject().put("id", session("userId")));
	}

	/*
	 * Function: login Parameters: None Description: login account. It compares
	 * the username and password. If they are the same, it will login
	 * successfully.
	 */
	public Result login() {
		return loginWithApp(null);
	}

	/*
	 * Function: logout Parameters: None Description: logout an account.
	 */
	public Result logout() {
		session().clear();
		return ok();
	}

	public Result delete(String accountId) {

		AppProfile appProfile = AppProfile.find.where()
				.eq("id.accountId", accountId).findUnique();
		appProfile.delete();
		return ok();
	}

	/*
	 * Function: isReachMaxTryTimes Parameters: None Description: Calculate
	 * whether the account reaches the max login times
	 */
	private boolean isReachMaxTryTimes() {
		String tryTimes = session("trytimes");

		if (tryTimes == null) {
			return false;
		}

		int nTryTimes = Integer.parseInt(tryTimes);

		if (nTryTimes <= MAX_TRY_TIMES) {
			return false;
		}

		return true;
	}
}
