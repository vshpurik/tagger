package controllers.account;

import java.text.SimpleDateFormat;
import java.util.Date;

import models.GlobalEnum;
import models.account.AppUser;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Http.Session;
import providers.MyUsernamePasswordAuthProvider;
import providers.MyUsernamePasswordAuthProvider.MyLogin;
import providers.MyUsernamePasswordAuthProvider.MySignup;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;

public class AccountMgmt extends Controller {

	public static final Form<AppUser> APP_USER_FORM = Form.form(AppUser.class);
	
	public static AppUser getLocalUser(final Session session) {
		final AppUser localUser = AppUser.findByAuthUserIdentity(PlayAuthenticate.getUser(session));
		return localUser;
	}

	public static Result login() {
		// if request was made using http protocol, redirect to https
		if (util.Util.isConnectionSecure()) {
			return ok(views.html.login.render(MyUsernamePasswordAuthProvider.LOGIN_FORM));			
		} else {
			return redirect(controllers.account.routes.AccountMgmt.login().absoluteURL(request(), true));
		}
	}

	public static Result doLogin() {
		// TODO add check here for SSL and do something if it's not
		
		final Form<MyLogin> filledForm = MyUsernamePasswordAuthProvider.LOGIN_FORM.bindFromRequest();

		/*  Before we do anything here, check if user clicked "Reset password" button.
		 *  If that's the case, do not do any validation, just redirect user to the
		 *  forgotPassword page.
		 */
		if (filledForm.data().containsKey(GlobalEnum.ButtonName.resetPassword.name())) {
			String email = filledForm.data().get("email");
			return redirect(controllers.account.routes.Signup.forgotPassword(email));
		}
		
		if (filledForm.hasErrors()) {
			// User did not fill everything properly
			return badRequest(views.html.login.render(filledForm));
		} else {
			// Everything was filled
			return UsernamePasswordAuthProvider.handleLogin(ctx());
		}
	}

	public static Result signup() {
		// if request was made using http protocol, redirect to https
		if (util.Util.isConnectionSecure()) {
			return ok(views.html.signup.render(MyUsernamePasswordAuthProvider.SIGNUP_FORM));
		} else {
			return redirect(controllers.account.routes.AccountMgmt.signup().absoluteURL(request(), true));
		}
	}

	public static Result doSignup() {
		// TODO add check here for SSL and do something if it's not
		
		final Form<MySignup> filledForm = MyUsernamePasswordAuthProvider.SIGNUP_FORM.bindFromRequest();
		if (filledForm.hasErrors()) {
			// User did not fill everything properly
			return badRequest(views.html.signup.render(filledForm));
		} else {
			// Everything was filled, perform validation
			
			MySignup mySignup = filledForm.get();
			
			// 1. make sure user name does not exist
			Logger.info("checking if user exists: " + mySignup.name);
			if (AppUser.findByName(mySignup.name) != null) {
				ctx().flash().put(util.ConfigParameter.FLASH_ERROR_KEY, "This user name has already been taken. Please choose a different one.");
				return badRequest(views.html.signup.render(filledForm));
			}

			// signup
			Result result = UsernamePasswordAuthProvider.handleSignup(ctx());
			
			// check for paid shopping cart.
			AppUser appUser = AppUser.findByName(mySignup.name);
			if (appUser == null) {
				Logger.error("doSignup: user did not get created by Play Authenticate.");
			}
			
			return result;
		}
	}

	public static String formatTimestamp(final long t) {
		return new SimpleDateFormat("yyyy-dd-MM HH:mm:ss").format(new Date(t));
	}

}
