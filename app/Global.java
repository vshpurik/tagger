import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import models.account.AppUser;
import models.account.SecurityPermission;
import models.account.SecurityPermission.SecurityPermissionType;
import models.account.SecurityRole;
import models.account.SecurityRole.SecurityRoleType;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.db.DB;

//====================================== FROM PlayAuthenticate ===============================
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.PlayAuthenticate.Resolver;
import com.feth.play.module.pa.exceptions.AccessDeniedException;
import com.feth.play.module.pa.exceptions.AuthException;

import play.mvc.Call;

//============================================================================================

public class Global extends GlobalSettings {
	
	private boolean forceReload = false;
	
	@Override
	public void onStart(Application application)
	{
		if (forceReload || !dbAlreadyLoaded()) {
			Logger.info("populating database with test data ...");
			
			Logger.info("Step 1: truncating all tables ...");
			truncateAllTables();

			Logger.info("Step 2: populating tables ...");
			createSecurityRoles();
			createSecurityPermissions();
		}

		onStartPlayAuthenticate(application);
	}
	
	@Override
	public void onStop(Application application) {
		Logger.info("received application shutdown request");
	}
	
	// TODO nice to have
	/*
	@Override
	public Result onError(RequestHeader requestHeader, Throwable t) {
		Logger.error("onError handler; uri=" + requestHeader.uri() + ", exception=" + t.getStackTrace());
		return Results.internalServerError(views.html.message.render(Messages.get("error.generic"), Messages.get("error.genericSystemError")));
	}

	@Override
	public Result onHandlerNotFound(play.mvc.Http.RequestHeader requestHeader) {
		Logger.error("received request for unknown url/site: " + requestHeader.uri());
		return Results.internalServerError(views.html.message.render(Messages.get("error.generic"), Messages.get("error.genericPageNotFoundError")));
	}
	
	@Override
	public Result onBadRequest(RequestHeader requestHeader, String error) {
		Logger.error("onError handler; uri=" + requestHeader.uri() + ", error=" + error);
		return Results.internalServerError(views.html.message.render(Messages.get("error.generic"), Messages.get("error.genericSystemError")));
	}
	*/
	
	/***********************************************************************************************
	 *
	 * Private methods
	 * 
	 ***********************************************************************************************/
	
	private boolean dbAlreadyLoaded() {
		
		if (AppUser.getRowCount() == 0) return false;
		if (SecurityRole.find.findRowCount() == 0) return false;
		if (SecurityPermission.find.findRowCount() == 0) return false;

		return true;
	}
	
	private boolean truncateAllTables() {
		boolean result = true;
		
		truncateTable("app_user_security_role");
		truncateTable("security_role");
		truncateTable("app_user_security_permission");
		truncateTable("security_permission");
		truncateTable("app_user");
		
		return result;
	}

	private boolean createSecurityRoles() {
		boolean result = true;
		
		if (SecurityRole.find.findRowCount() == 0) {
			Logger.info("Creating security roles ...");

			for (SecurityRoleType securityRoleType : SecurityRoleType.values()) {
				SecurityRole securityRole = new SecurityRole();
				securityRole.roleName = securityRoleType.name();
				securityRole.save();
			}
		} else {
			Logger.info("Skipping creation of security roles ...");
		}
		
		return result;
	}
	
	private boolean createSecurityPermissions() {
		boolean result = true;
		
		Logger.info("Creating security permissions ...");
			
		for (SecurityPermissionType securityPermissionType : SecurityPermissionType.values()) {
			SecurityPermission securityPermission = new SecurityPermission();
			securityPermission.value = securityPermissionType.name();
			securityPermission.save();
		}
		
		return result;
	}
	
	private static void truncateTable(String tableName) {
		Logger.info("truncating table " + tableName);
		
		String sql = "truncate " + tableName + " cascade";
		if (!executeSqlUpdate(sql))
			Logger.info("Can not truncate table(" + tableName + ").");
	}
	
	private static boolean executeSqlUpdate(String sql) {
		Connection connection = DB.getConnection();
		try {
			PreparedStatement ps;
			ps = connection.prepareStatement(sql);
			ps.executeUpdate();
		} catch (SQLException e) {
			String msg = "can not execute sql: " + e.toString();
			Logger.info(msg);
			return false;
		}
		finally {
			try {
				connection.close();
			} catch (Exception e) {
				Logger.info("can not close connection");
			}
		}

		return true;
	}

	private static boolean executeSql(String sql) {
		Connection connection = DB.getConnection();
		try {
			PreparedStatement ps;
			ps = connection.prepareStatement(sql);
			ps.execute();
		} catch (SQLException e) {
			String msg = "can not execute sql: " + e.toString();
			Logger.info(msg);
			return false;
		}
		finally {
			try {
				connection.close();
			} catch (Exception e) {
				Logger.info("can not close connection");
			}
		}

		return true;
	}

	/***********************************************************************************************
	 *
	 * PlayAuthenticate stuff
	 * 
	 ***********************************************************************************************/

	private void onStartPlayAuthenticate(Application app) {
		PlayAuthenticate.setResolver(new Resolver() {

			@Override
			public Call login() {
				// Your login page
				return controllers.account.routes.AccountMgmt.login();
			}

			@Override
			public Call afterAuth() {
				// The user will be redirected to this page after authentication
				// if no original URL was saved
				return controllers.routes.Application.index();
			}

			@Override
			public Call afterLogout() {
				return controllers.routes.Application.index();
			}

			@Override
			public Call auth(final String provider) {
				// You can provide your own authentication implementation,
				// however the default should be sufficient for most cases
				return com.feth.play.module.pa.controllers.routes.Authenticate.authenticate(provider);
			}

			@Override
			public Call askMerge() {
				return controllers.account.routes.Account.askMerge();
			}

			@Override
			public Call askLink() {
				return controllers.account.routes.Account.askLink();
			}

			@Override
			public Call onException(final AuthException e) {
				if (e instanceof AccessDeniedException) {
					return controllers.account.routes.Signup.oAuthDenied(((AccessDeniedException) e).getProviderKey());
				}

				// more custom problem handling here...
				return super.onException(e);
			}
		});

	}
	
}
