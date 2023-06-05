package org.ignis.javaMud.Mud.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * MUD datum/ido
 * jelenleg 1 sec valos ido = 1 perc mudido
 * @author Csaba Toth (csaba.toth@sptech.ch)
 *
 */
public class MudDate {
	static private Logger LOG = LogManager.getLogger(MudDate.class);
	static private final int minuteInHour = 60;
	/**
	 * napok szama
	 */
	private long day;
	/**
	 * percek
	 */
	private int minute;
	/**
	 * orak egy nap
	 */
	private int hoursInDay;
	public MudDate(int hoursInDay) {
		day = 0;
		minute = 0;
		this.hoursInDay  = hoursInDay;
	}
	
	public MudDate clone() {
		return new MudDate(day,minute,hoursInDay);
	}
	
	private MudDate(long day, int minute, int hoursInDay) {
		super();
		this.day = day;
		this.minute = minute;
		this.hoursInDay = hoursInDay;
	}

	public long getDay() {
		return day;
	}

	public int getMinute() {
		return minute;
	}
	/**
	 * Adott perc hozzaadasa az aktualis idohoz, szamolja a napokat is
	 * @param minute a hozzaadando perc
	 */
	public void addMinute(long minute) {
		synchronized(this) {
			long tm = this.minute + minute;
			LOG.trace("tm: " + tm); 
			
			this.minute = (int)tm%(hoursInDay*minuteInHour);
			LOG.trace("tm: " + this.minute); 
			if(tm>=(hoursInDay*minuteInHour)) {
				tm = tm-this.minute;
				tm = Math.floorDiv(tm, hoursInDay*minuteInHour);
				this.day+= tm;
			}
		}
	}
	/**
	 * Nap hosszat adja vissza percben
	 * @return nap hossza
	 */
	public int getDayLong() {
		return hoursInDay*minuteInHour;
	}
	/**
	 * idot adja vissza ora formatumu string-kent
	 * @return aktualis mud ido
	 */
	public String getTime() {
		int hour = Math.floorDiv(minute, minuteInHour);
		int m = minute%(60);
		return String.format( "%02d:%02d", hour,m ); 
	}
	
}
