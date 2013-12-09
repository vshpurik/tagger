package security;

import be.objectify.deadbolt.java.AbstractDeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;
import be.objectify.deadbolt.core.models.Subject;
import models.account.AppUser;
import play.mvc.Http;
import play.mvc.Result;
// import util.ConfigParameter;

/**
* @author Steve Chaloner (steve@objectify.be)
*/
public class MyAlternativeDeadboltHandler extends AbstractDeadboltHandler
{
    public Result beforeAuthCheck(Http.Context context)
    {
        // returning null means that everything is OK. Return a real result if you want a redirect to a login page or
        // somewhere else
        return null;
    }

    public Subject getSubject(Http.Context context)
    {
		final AppUser appUser = controllers.account.AccountMgmt.getLocalUser(context.session());
		return appUser;
    }

    public DynamicResourceHandler getDynamicResourceHandler(Http.Context context)
    {
        return new MyAlternativeDynamicResourceHandler();
    }

    @Override
    public Result onAuthFailure(Http.Context context, String content)
    {
    	return redirect("/");
    }
}
