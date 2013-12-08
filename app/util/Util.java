package util;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.typesafe.plugin.MailerAPI;
import com.typesafe.plugin.MailerPlugin;

import play.Logger;
import providers.MyUsernamePasswordAuthProvider;

public class Util {

	private static final String XForwardedProtoHeaderName = "X-FORWARDED-PROTO";
	private static final String XForwardedProtoHeaderValueSecure = "https";
	
	/*
	 * This method removes all parameters from url. The rest format is assumed for the url.
	 */
	public static String removeHttpParam(String url, int paramCount) {
		for (int i=0; i<paramCount; i++) {
			url = url.substring(0,  url.lastIndexOf("/"));
		}
		return url;
	}
	
	public static void displayHeaders() {
		Map<String, String[]> stuff = play.mvc.Controller.request().headers();
		Iterator<Entry<String, String[]>> i = stuff.entrySet().iterator();
		while(i.hasNext()) {
			Entry<String, String[]> e = i.next();
			Logger.info("i=" + e.getKey());
			for(String s: e.getValue()) {
				Logger.info("  val=" + s);
			}
		}
		
		Logger.info("request().host()=" + play.mvc.Controller.request().host());
	}

	/**
	 * Play application is running behind proxy that handles SSL. In our case we will be using Apache that
	 * will set header X-FORWARDED-PROTO with http or https based on original request. Here we check what we
	 * have in this header and return true or false.
	 */
	public static boolean isConnectionSecure() {
		if (util.ConfigParameter.appMode.equals(ConfigParameter.AppModeType.prod)) {
			String proto = play.mvc.Controller.request().getHeader(XForwardedProtoHeaderName);
			if (proto == null) {
				Logger.error("Missing " + XForwardedProtoHeaderName + " http header. Make sure http proxy (Apachece?) is configured to add it.");
				return false;
			} else {
				return proto.equals(XForwardedProtoHeaderValueSecure);
			}
		} else {
			return true;
		}
	}
	
	public static void sendTextEmail(String emailFrom, String emailTo, String subject, String body) {
		MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
		mail.addFrom(emailFrom);
		mail.addRecipient(emailTo);
		mail.setSubject(subject);
		mail.send(body);
	}

	public static void sendHtmlEmail(String emailFrom, String emailTo, String subject, String body) {
		MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
		mail.addFrom(emailFrom);
		mail.addRecipient(emailTo);
		mail.setSubject(subject);
		mail.sendHtml(body);
	}
	
	public static void sendTextAndHtmlEmail(String emailFrom, String emailTo, String subject, String bodyText, String bodyHtml) {
		MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
		mail.addFrom(emailFrom);
		mail.addRecipient(emailTo);
		mail.setSubject(subject);
		mail.send(bodyText, bodyHtml);	
	}

}
