/** Author: Michael Wang
 * Date: 2015-06-26
 * Description: This controller derives from base controller and will check the application id for all actions.
 * Controllers related to application should derives from this controller.
 */
package controllers;

import controllers.inceptors.ExistApp;

@ExistApp
public class AppController extends BaseController {

}
