package util;

import java.util.Calendar;

import play.Logger;

public class ConfigParameter {
	public static enum AppModeType {
		dev,
		prod,
		unknown
	}

	public static final String FLASH_MESSAGE_KEY = "message";
	public static final String FLASH_ERROR_KEY = "error";

	public static final float MAX_TEXT_SIZE = 100;
	
	public static final Calendar calendar = Calendar.getInstance();
	public static final int CopyrightYear = calendar.get(Calendar.YEAR);
	
	public static final String EmailSalutation = "Dear";
	public static final String CompanyName = "NitroCloud";
	
	public static final String WebsiteUrl = "http://safe-dawn-5454.herokuapp.com";
	
	// Data storage type
	public static final AppModeType appMode = 
		AppModeType.valueOf(getStringParameter("myapp.appMode", AppModeType.dev.name()));
	
	
	/********************************************************************************************************
	 * 
	 * Helper methods to extract values from application.conf file
	 * 
	 ********************************************************************************************************/
	
	/**
	 *
	 * Helper method to get String parameter value from application.conf file
	 * 
	 * @param parameterName
	 * @param defaultValue
	 * @return String value from application.conf gile or defaultValue if parameter is not found. 
	 */
	private static String getStringParameter(String parameterName, String defaultValue) {
		String parameterValue = play.Play.application().configuration().getString(parameterName);
		if (parameterValue == null) {
			Logger.info("configuration file is missing the following parameter: " + parameterName + "; default value of " + defaultValue + " will be used instead.");
			return defaultValue;
		} else {
			return parameterValue;
		}
	}

	/**
	 * 
	 * Helper method to get long parameter value from application.conf file
	 * 
	 * @param parameterName
	 * @param defaultValue
	 * @return String value from application.conf gile or defaultValue if parameter is not found.
	 */
	private static long getLongParameter(String parameterName, long defaultValue) {
		String parameterValueStr = play.Play.application().configuration().getString(parameterName);
		if (parameterValueStr == null) {
			Logger.info("configuration file is missing the following parameter: " + parameterName + "; default value of " + defaultValue + " will be used instead.");
			return defaultValue;
		} else {
			long result;
			try {
				result = Long.parseLong(parameterValueStr);
				return result;
			} catch(NumberFormatException e) {
				Logger.error("Configuration file has an invalid value for parameter " + parameterName + ": " + parameterValueStr + "; must be a long value.");
				return defaultValue;
			} catch (Exception e) {
				Logger.error("Can not parse long value in configuration file for parameter " + parameterName + ": " + parameterValueStr + "; must be a long value.");
				return defaultValue;
			}
		}
	}

	/**
	 * 
	 * Helper method to get long parameter value from application.conf file
	 * 
	 * @param parameterName
	 * @param defaultValue
	 * @return String value from application.conf gile or defaultValue if parameter is not found.
	 */
	private static int getIntParameter(String parameterName, int defaultValue) {
		String parameterValueStr = play.Play.application().configuration().getString(parameterName);
		if (parameterValueStr == null) {
			Logger.info("configuration file is missing the following parameter: " + parameterName + "; default value of " + defaultValue + " will be used instead.");
			return defaultValue;
		} else {
			int result;
			try {
				result = Integer.parseInt(parameterValueStr);
				return result;
			} catch(NumberFormatException e) {
				Logger.error("Configuration file has an invalid value for parameter " + parameterName + ": " + parameterValueStr + "; must be an int value.");
				return defaultValue;
			} catch (Exception e) {
				Logger.error("Can not parse int value in configuration file for parameter " + parameterName + ": " + parameterValueStr + "; must be an int value.");
				return defaultValue;
			}
		}
	}

	/**
	 *
	 * Helper method to get Boolean parameter value from application.conf file
	 * 
	 * @param parameterName
	 * @param defaultValue
	 * @return boolean value from application.conf gile or defaultValue if parameter is not found. 
	 */
	private static boolean getBooleanParameter(String parameterName, boolean defaultValue) {
		Boolean parameterValue = play.Play.application().configuration().getBoolean(parameterName);
		if (parameterValue == null) {
			Logger.info("configuration file is missing the following parameter: " + parameterName + "; default value of " + defaultValue + " will be used instead.");
			return defaultValue;
		} else {
			return parameterValue;
		}
	}



}
