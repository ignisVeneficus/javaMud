package org.ignis.javaMud.utils;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import org.tempura.console.util.Ansi;

public class Colorize {
	public static final String C_RED = "%^RED%^";
	public static final String C_GREEN = "%^GREEN%^";
	public static final String C_BLUE = "%^BLUE%^";
	public static final String C_MAGENTA = "%^MAGENTA%^";
	public static final String C_CYAN = "%^CYAN%^";
	public static final String C_WHITE = "%^WHITE%^";
	public static final String C_BLACK = "%^BLACK%^";
	public static final String C_ORANGE = "%^ORANGE%^";
	public static final String C_YELLOW = "%^YELLOW%^";
	
	public static final String C_B_RED = "%^BOLD%^%^RED%^";
	public static final String C_B_GREEN = "%^BOLD%^%^GREEN%^";
	public static final String C_B_BLUE = "%^BOLD%^%^BLUE%^";
	public static final String C_B_MAGENTA = "%^BOLD%^%^MAGENTA%^";
	public static final String C_B_CYAN = "%^BOLD%^%^CYAN%^";
	public static final String C_B_WHITE = "%^BOLD%^%^WHITE%^";
	public static final String C_B_BLACK = "%^BOLD%^%^BLACK%^";
	
	public static final String BOLD = "%^BOLD%^";
	public static final String RESET = "%^RESET%^";

	public static final String BG_RED = "%^B_RED%^";
	public static final String BG_GREEN = "%^B_GREEN%^";
	public static final String BG_BLUE = "%^B_BLUE%^";
	public static final String BG_MAGENTA = "%^B_MAGENTA%^";
	public static final String BG_CYAN = "%^B_CYAN%^";
	public static final String BG_WHITE = "%^B_WHITE%^";
	public static final String BG_BLACK = "%^B_BLACK%^";
	public static final String BG_YELLOW = "%^B_YELLOW%^";
	
	
	private static final Pattern allColorPattern = Pattern.compile("%\\^.*?%\\^");
	private static final LinkedHashMap<String, Ansi> colorMap = new LinkedHashMap<>();
	static {
		colorMap.put("%\\^BOLD%\\^%\\^RED%\\^",Ansi.Bold.and(Ansi.Red));
		colorMap.put("%\\^BOLD%\\^%\\^GREEN%\\^",Ansi.Bold.and(Ansi.Green));
		colorMap.put("%\\^BOLD%\\^%\\^BLUE%\\^",Ansi.Bold.and(Ansi.Blue));
		colorMap.put("%\\^BOLD%\\^%\\^MAGENTA%\\^",Ansi.Bold.and(Ansi.Magenta));
		colorMap.put("%\\^BOLD%\\^%\\^CYAN%\\^",Ansi.Bold.and(Ansi.Cyan));
		colorMap.put("%\\^BOLD%\\^%\\^WHITE%\\^",Ansi.Bold.and(Ansi.White));
		colorMap.put("%\\^BOLD%\\^%\\^BLACK%\\^",Ansi.Bold.and(Ansi.Black));
		
		colorMap.put("%\\^BOLD%\\^",Ansi.Bold);
		
		colorMap.put("%\\^RED%\\^",Ansi.Red);
		colorMap.put("%\\^GREEN%\\^",Ansi.Green);
		colorMap.put("%\\^BLUE%\\^",Ansi.Blue);
		colorMap.put("%\\^MAGENTA%\\^",Ansi.Magenta);
		colorMap.put("%\\^CYAN%\\^",Ansi.Cyan);
		colorMap.put("%\\^WHITE%\\^",Ansi.White);
		colorMap.put("%\\^ORANGE%\\^",Ansi.Yellow);
		colorMap.put("%\\^YELLOW%\\^",Ansi.Bold.and(Ansi.Yellow));
		colorMap.put("%\\^BLACK%\\^",Ansi.Black);
		
		colorMap.put("%\\^B_RED%\\^",Ansi.BgRed);
		colorMap.put("%\\^B_GREEN%\\^",Ansi.BgGreen);
		colorMap.put("%\\^B_BLUE%\\^",Ansi.BgBlue);
		colorMap.put("%\\^B_MAGENTA%\\^",Ansi.BgMagenta);
		colorMap.put("%\\^B_CYAN%\\^",Ansi.BgCyan);
		colorMap.put("%\\^B_WHITE%\\^",Ansi.BgWhite);
		colorMap.put("%\\^B_YELLOW%\\^",Ansi.BgYellow);
		colorMap.put("%\\^B_BLACK%\\^",Ansi.BgBlack);	
		
		colorMap.put("%\\^RESET%\\^",Ansi.Sane);
	}
	public static final String colorize(String input) {
		if(input.indexOf("%^")>=0) {
			Set<Entry<String, Ansi>> items = colorMap.entrySet();
			for(Entry<String, Ansi> item:items) {
				input = input.replaceAll(item.getKey(),item.getValue().toString());
				if(input.indexOf("%^")<0) {
					break;
				}
			}
			
		}
		return input;
	}
	public static final String deColorize(String input) {
		return allColorPattern.matcher(input).replaceAll("");
	}
}
