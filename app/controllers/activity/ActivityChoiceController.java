package controllers.activity;

import java.util.Date;

import com.avaje.ebean.Ebean;

import models.activity.ActivityChoice;
import models.activity.ActivityContent;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;
import controllers.AppController;
import controllers.ErrDefinition;
import controllers.common.CodeGenerator;
import controllers.inceptors.AccessLevel;
import controllers.users.AppProfileGiftController;

public class ActivityChoiceController extends AppController {

	@AccessLevel(level = 2)
	public Result create() {
		Form<ActivityChoice> choiceForm = Form.form(ActivityChoice.class)
				.bindFromRequest();

		if (choiceForm.hasErrors()) {
			return status(ErrDefinition.E_ACTIVITY_CHOICE_FORM_HASERROR,
					Messages.get("activitychoice.failure"));
		}

		try {
			ActivityChoice choice = choiceForm.get();

			choice.id = CodeGenerator.GenerateUUId();

			choice.content = new ActivityContent();
			choice.content.id = choice.id;

			Ebean.save(choice);

			return ok(Json.toJson(choice));
		} catch (Throwable e) {
			return status(ErrDefinition.E_ACTIVITY_CHOICE_CREATE_FAILED,
					Messages.get("activitychoice.failure"));
		}
	}

	public Result readById(String id) {

		try {
			ActivityChoice choice = ActivityChoice.find.byId(id);
			if (choice == null) {
				return status(ErrDefinition.E_ACTIVITY_CHOICE_READ_FAILED,
						Messages.get("activitychoice.failure"));
			}
			return ok(Json.toJson(choice));
		} catch (Throwable e) {
			return status(ErrDefinition.E_ACTIVITY_CHOICE_READ_FAILED,
					Messages.get("activitychoice.failure"));
		}
	}

	public static void ConvertDescription(ActivityChoice choice) {
		if (choice.type == 1) {
			int limit = choice.content.max_selection;
			Date startTime = choice.content.activity.startTime;
			Date endTime = choice.content.activity.endTime;

			choice.description = AppProfileGiftController.getGiftStatus(
					choice.choice, startTime, endTime, limit,
					Integer.parseInt(choice.description)).toString();
		}
	}

	@AccessLevel(level = 2)
	public Result update() {
		Form<ActivityChoice> choiceForm = Form.form(ActivityChoice.class)
				.bindFromRequest();

		if (choiceForm.hasErrors()) {
			return status(ErrDefinition.E_ACTIVITY_CHOICE_FORM_HASERROR,
					Messages.get("activitychoice.failure"));
		}

		try {
			ActivityChoice choice = choiceForm.get();

			choice.content = new ActivityContent();
			choice.content.id = choice.id;

			Ebean.update(choice);

			return ok(Json.toJson(choice));
		} catch (Throwable e) {
			return status(ErrDefinition.E_ACTIVITY_CHOICE_UPDATE_FAILED,
					Messages.get("activitychoice.failure"));
		}
	}

	@AccessLevel(level = 2)
	public Result delete(String id) {
		try {
			Ebean.delete(ActivityChoice.class, id);
		} catch (Throwable e) {
			return status(ErrDefinition.E_ACTIVITY_CHOICE_DELETE_FAILED,
					Messages.get("activitychoice.failure"));
		}

		return ok();
	}
}
