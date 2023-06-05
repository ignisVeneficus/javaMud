package org.ignis.javaMud.Mud.deamon;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.Engine;
import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.Core.Room;
import org.ignis.javaMud.Mud.data.MudDate;
import org.ignis.javaMud.Mud.dataholder.AstronomyDailyEvent;
import org.ignis.javaMud.Mud.handlers.Handler;

/**
 * 
 * Idokezelo:
 * 1 real sec = 1 perc a jatekban => 1 perc = 1 ora
 * a nap 27 orabol all
 * az outdoor szobak fenye innet jon
 *
 *
 * hold(ak): keringenek, fenyuk hasonlo a nappalihoz + fazis szorzo: telihold -> max, ujhold 0
 * ezeket 15 secenkent hivja;
 * van egy listaja, ahova be kell regisztralni a szobakat (szoba csinalja)
 * ha lement a nap, felkelt a hold, stb -> uzenetet kuld
 * naptarat belerakni.
 * karbantart egy listat az esemenyekrol (nappal, ejszaka, stb) hogy a szobak tudjak mit kell megjeleniteni
 * (naptarbol unnepek, naptari napok, stb az esemenyek koze)
 * vagy karbantartja es atadja a szobaknak, vagy a szoba kerdezi le egy is("") fg-el..
 * 
 * kezeli  a "nez" akarmit command-ot..
 * ha a szoba kerdezi, akkor szamolja az aktualis ertekeket
 * 
 */
public class Astronomy implements Handler, Runnable {
	static private Logger LOG = LogManager.getLogger(Astronomy.class);
	public static final String REG_NAME = "astronomy";
	private Set<Room> outdoors;
	private MudDate date;
	private LocalDateTime lastAccess;
	private AstronomyDailyEvent actualEvent;
	private ArrayList<AstronomyDailyEvent> dailyEvents;
	
	/**
	 * kozepertek
	 */
	private int mean;
	/**
	 * szoras
	 */
	private int variance;
	private ScheduledExecutorService scheduler;

	@Override
	public void run() {
		checkTime();
	}
	
	private void checkTime() {
		long diff = 0;
		MudDate newDate = null;
		synchronized (this) {
			LOG.warn("IDO KIKAPCSOLVA");
			/*
			LocalDateTime now = LocalDateTime.now();
			Duration dur = Duration.between(lastAccess, now);
		    diff = dur.getSeconds();
		    LOG.trace("duration " + diff);
		    if(diff>0) {
		    	date.addMinute(diff);
		    }
		    lastAccess =now;
		    */
		    newDate = date.clone();
		}
		LOG.trace("Tick " + newDate.getMinute());
		AstronomyDailyEvent newEvent = null;
		for(AstronomyDailyEvent e:dailyEvents) {
			if(e.getTime()>newDate.getMinute()) break;
			newEvent = e;
		}
		if((newEvent!=actualEvent)||(diff>newDate.getDayLong())) {
			broadcast(newEvent.getDescription());
		}
		actualEvent = newEvent;
		
	}
	public MudDate getTime() {
		checkTime();
		return date.clone();
	}

	@Override
	public void init(Engine e) {
		// esemenyek kezzel belerakva:
		// XML-bol betolteni
		dailyEvents.add(new AstronomyDailyEvent(0,"este,ejfel",""));
		dailyEvents.add(new AstronomyDailyEvent(10,"este,ejjel",""));
		dailyEvents.add(new AstronomyDailyEvent(320,"nappal,hajnal","Elkezdett kivilágosodni"));
		dailyEvents.add(new AstronomyDailyEvent(390,"nappal,delelott","Felkelt a Nap"));
		dailyEvents.add(new AstronomyDailyEvent(790,"nappal,del","Delel a Nap"));
		dailyEvents.add(new AstronomyDailyEvent(810,"nappal,delutan",""));
		dailyEvents.add(new AstronomyDailyEvent(1230,"este,szurkulet","Lement a Nap"));
		dailyEvents.add(new AstronomyDailyEvent(1300,"este","Besötétedett"));
		dailyEvents.add(new AstronomyDailyEvent(1610,"este,ejfel","Éjfél van"));
		
		Collections.sort(dailyEvents);
		// nappal
		date.addMinute(800);
		checkTime();
		
		// XML-bol vagy akarhonnet betolteni
		mean = 810;
		variance = 600;
		lastAccess = LocalDateTime.now();
		
		scheduler = Executors.newSingleThreadScheduledExecutor();
		// scheduler.scheduleAtFixedRate(this, 0, 15, TimeUnit.SECONDS);
	}
	public Astronomy() {
		outdoors = Collections.synchronizedSet(new HashSet<Room>());
		dailyEvents = new ArrayList<>();
		date = new MudDate(27);
	}

	public void registerRoom(Room room) {
		synchronized (outdoors) {
			LOG.trace(room.getFullObjectName());
			outdoors.add(room);
		}
	}
	public void unRegisterRoom(Room room) {
		synchronized (outdoors) {
			LOG.trace(room.getFullObjectName());
			outdoors.remove(room);
		}
	}
	private void broadcast(String eventText) {
		if(StringUtils.isNotBlank(eventText)) {
			Room[] rooms;
			synchronized (outdoors) {
				rooms = new Room[outdoors.size()];
				rooms = outdoors.toArray(rooms);
			}
			for(Room r:rooms) {
				r.tellRoom(eventText);
			}
		}
	}
	
	public int getLight() {
		MudDate md = getTime();
		int time = md.getMinute();
//		int light =(int)Math.round(Math.exp(-Math.pow((double)(time-mean),2.0)/Math.pow((double)variance, 2.0))*50);
		int multiple = 800;
		int light = Math.max(0,(int)Math.round( Math.pow(1-Math.pow(Math.abs((double)(time-mean)/multiple), 5),5))*50);
		return light;
	}
	public void dest() {
		scheduler.shutdown();
	}
	public String getDescription(String what) {
		MudDate d = getTime();
		return d.getTime() + " " + getLight();
	}
	public Set<String> getAllStates(){
		getTime();
		String daily = StringUtil.exEkezet(actualEvent.getName());
		HashSet<String> ret = new HashSet<>();
		String[] strArray = daily.split(",");
		for(int i=0;i<strArray.length;i++) {
			String str = StringUtils.strip(strArray[i]);
			if(StringUtils.isNoneBlank(str))
				ret.add(str);
		}
		return ret;
	}
	public Set<String> getLook(){
		Set<String> ret = new HashSet<>();
		ret.add("eg");
		ret.add("egbolt");
		ret.add("nap");
		ret.add("csillagok");
		ret.add("felhok");
		return ret;
		
	}
}
