package security;

import be.objectify.deadbolt.java.DeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;
import play.mvc.Http;

public class MyAlternativeDynamicResourceHandler implements DynamicResourceHandler
{
    public boolean isAllowed(String name,
                             String meta,
                             DeadboltHandler deadboltHandler,
                             Http.Context context)
    {
        // look something up in an LDAP directory, etc, and the answer isn't good for the user
        return false;
    }

    public boolean checkPermission(String permissionValue,
                                   DeadboltHandler deadboltHandler,
                                   Http.Context ctx)
    {
        // Computer says no
        return false;
    }
}
