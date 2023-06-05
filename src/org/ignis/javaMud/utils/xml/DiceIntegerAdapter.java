package org.ignis.javaMud.utils.xml;

import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//https://stackoverflow.com/questions/35020687/how-to-parse-dice-notation-with-a-java-regular-expression

public class DiceIntegerAdapter extends XmlAdapter<String, Integer>{
	static final Pattern dicePattern = Pattern.compile("([1-9]\\d*)?[dk]([1-9]\\d*)([/x][1-9]\\d*)?([+-][1-9]\\d*)?");
	static private Logger LOG = LogManager.getLogger(DiceIntegerAdapter.class);
 
    @Override
    public Integer unmarshal(String string) throws Exception {
        int amount, die, mult = 1, add = 0;
        boolean isMultiple = true;
        Matcher m = dicePattern.matcher(string);
        if (m.matches()) {
        	try {
	            amount = (m.group(1) != null) ? Integer.parseInt(m.group(1)) : 1;
	            die = Integer.parseInt(m.group(2));
	            if (m.group(3) != null) {
	            	isMultiple = m.group(3).startsWith("x");
	                mult = Integer.parseInt(m.group(3).substring(1));
	            }
	            if (m.group(4) != null) {
	                boolean positive = m.group(4).startsWith("+");
	                int val = Integer.parseInt(m.group(4).substring(1));
	                add = positive ? val : -val;
	            }
	            int res = 0;
	            for(int i=0;i<amount;i++) {
	            	res+= ThreadLocalRandom.current().nextInt(1, die+1);
	            }
	            if(isMultiple) {
	            	res = res*mult;
	            }
	            else {
	            	res =  res / mult + ((res % mult == 0) ? 0 : 1); 
	            }
	            res+=add;
	            
	            LOG.trace("parsed: |" + string + "| => " + res);
	            return res;
	            
        	}
        	catch(Exception e) {
        		LOG.error("Cant parse |" + string +"|");
        		return 0;
        	}
       }
        else
        	try {
        		LOG.trace("parsed: |" + string + "| " );
        		return Integer.valueOf(string);
        	}
        	catch(Exception e) {
        		LOG.error("Cant parse |" + string +"|");
        		return 0;
        	}
    }

    @Override
    public String marshal(Integer integer) throws Exception {
        return String.valueOf(integer);
    }

}
