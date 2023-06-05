package org.ignis.javaMud.Mud.utils;

import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.Core.Object;

public class StringFunctions {
	private static Pattern funcPattern = Pattern.compile("(\\{(.*?:.*?)\\})");
	private static Pattern callbackPattern = Pattern.compile("(\\{(.*?)\\})");
	static private Logger LOG = LogManager.getLogger(StringFunctions.class);
	static public String process(Object obj,String input) {
		LOG.trace("process: " + input);
		Matcher m = funcPattern.matcher(input);
		while (m.find()) {
			LOG.trace("found");
			StringBuffer buff = new StringBuffer(input);
			String s = m.group(2);
			String rep = handleFunc(obj,s);
			if(rep == null) {
				rep = "";
			}
			buff.replace(m.start(1), m.end(1), rep);
			input = buff.toString();
			LOG.trace("next loop: " + input);
			m = funcPattern.matcher(input);
		}
		return input;
	}
	static private String handleFunc(Object obj,String input) {
		String[] pars = input.split(":");
		if(pars.length>1) {
			String fn = pars[0];
			if("r".equalsIgnoreCase(fn)) {
				return rnd(pars);
			}
			if("map".equalsIgnoreCase(fn)) {
				return mapData(obj,pars);
			}
			if("callback".equalsIgnoreCase(fn)) {
				return "{"+pars[1]+"}";
			}
		}
		return input;
	}
	
	static private String rnd(String[] pars) {
		int pos = ThreadLocalRandom.current().nextInt(1, pars.length);
		return pars[pos];
	}
	static private String mapData(Object obj,String[] pars) {
		if(obj==null) return "";
		String ret = obj.getProperty(pars[1]);
		if(ret==null) ret= "";
		return ret;
	}
	static public boolean checkCallback(String input) {
		Matcher m = callbackPattern.matcher(input);
		if (m.find()) {
			LOG.trace("found");
			return true;
		}
		return false;
	}
	static public String processCallback(Object obj, String input) {
		Matcher m = callbackPattern.matcher(input);
		while (m.find()) {
			LOG.trace("found");
			String s = m.group(2);
			if(obj.hasJsFunction(s)) {
				java.lang.Object ret = obj.callJsFunction(s);
				StringBuffer buff = new StringBuffer(input);
				
				buff.replace(m.start(1), m.end(1),ret!=null?ret.toString():"");
				input = buff.toString();
			}
			m = funcPattern.matcher(input);
		}
		return input;
	}
}
