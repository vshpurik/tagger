package security;

import be.objectify.deadbolt.java.DeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;
import play.mvc.Http;

/**
* @author Steve Chaloner (steve@objectify.be)
*/
public abstract class AbstractDynamicResourceHandler implements DynamicResourceHandler
{
    public boolean checkPermission(String permissionValue, DeadboltHandler deadboltHandler, Http.Context ctx)
    {
        return false;
    }

    public boolean isAllowed(String name, String meta, DeadboltHandler deadboltHandler, Http.Context ctx)
    {
        return false;
    }
}
