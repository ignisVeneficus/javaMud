package org.ignis.javaMud.Mud.deamon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.Engine;
import org.ignis.javaMud.Mud.Core.HeartBeatListener;
import org.ignis.javaMud.Mud.Core.Object;
import org.ignis.javaMud.Mud.handlers.Handler;

public class HeartBeat implements Handler, Runnable {
	static private Logger LOG = LogManager.getLogger(HeartBeat.class);
	public static final String REG_NAME = "heartBeat";
	
	private List<HeartBeatListener> objects;

	
	private ScheduledExecutorService scheduler;

	
	public HeartBeat() {
		objects = Collections.synchronizedList(new ArrayList<HeartBeatListener>());
	}
	@Override
	public void run() {
		LOG.trace("Qty: " + objects.size());
		HeartBeatListener[] listeners;
		synchronized (this) {
			listeners = new HeartBeatListener[objects.size()];
			listeners = objects.toArray(listeners);
		}
		for(HeartBeatListener listener:listeners) {
			try {
				LOG.trace("try to call: "+ listener.getFullObjectName());
				listener.tick();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		LOG.trace("finished");
		
	}

	@Override
	public void init(Engine e) {
		
		scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(this, 0, 1, TimeUnit.SECONDS);
	}

	@Override
	public void dest() {
		LOG.info("Shuting down");
		scheduler.shutdown();
		
	}
	
	public void registerObject(HeartBeatListener listener) {
		try {
			synchronized (this) {
				LOG.debug(listener.getFullObjectName());
				objects.add(listener);
				LOG.debug("success");
			}
		}
		catch(Exception e) {
			LOG.error(e.getMessage());
		}
	}
	public void unRegisterObject(HeartBeatListener listener) {
		synchronized (this) {
			LOG.debug(listener.getFullObjectName());
			objects.remove(listener);
		}
	}
	
}