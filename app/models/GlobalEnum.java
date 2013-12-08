package models;

import com.avaje.ebean.annotation.EnumValue;

public class GlobalEnum {

	public static enum SiteNavigationOption {
		none,
		home,
		category,
		productMgmt,
		locationMgmt,
		contact,
		signup,
		login,
		profile,
		account,
		linkProvider,
		admin,
		fullScreen
	}

	public static enum ButtonName {
		login,
		signup,
		resetPassword
	}

}
