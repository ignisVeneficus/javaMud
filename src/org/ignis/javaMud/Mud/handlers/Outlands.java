package org.ignis.javaMud.Mud.handlers;

import java.util.HashMap;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.Engine;
import org.ignis.javaMud.Mud.Core.OutlandsRoom;
import org.ignis.javaMud.Mud.dataholder.outlands.Plane;

public class Outlands implements Handler{
	static private Logger LOG = LogManager.getLogger(Outlands.class);
	static public String REG_NAME = "outlands";
	
	private static JAXBContext jaxbContext=null; 
	private static Unmarshaller unmarshaller = null;
	static {
		try {
			jaxbContext= JAXBContext.newInstance(Plane.class);
			unmarshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			e.printStackTrace();
			LOG.catching(Level.FATAL, e);
		}
	}
	
	private Engine engine;
	private HashMap<String, Plane> planes;
	
	@Override
	public void init(Engine e) {
		LOG.info("INIT: Start");
		engine = e;
		String outlandsRoot = e.getProperty("Outlands.path");
		outlandsRoot = e.compileRef(outlandsRoot);
		planes = readAllPlane(e, outlandsRoot);
		LOG.info("INIT: End");
	}
	
	public void fillRoom(String fullName, OutlandsRoom room) throws IllegalArgumentException{
		if(LOG.isTraceEnabled()){
			LOG.trace("Fill room: "+fullName);
		}
		String coord = StringUtils.substringBefore(fullName.substring(fullName.lastIndexOf("/")+1),"$");
		String[] coords = explodeName(coord);
		if(coords.length!=3) {
			LOG.error("Wrong coords: " + coord);
			throw new IllegalArgumentException("Wrong coordinate");
		}
		Plane plane = planes.get(coords[0]);
		if(plane==null) {
			LOG.error("No plane for: " + coords[0]);
			return;
		}
		int x = Integer.parseInt(coords[1]);
		int y = Integer.parseInt(coords[2]);
		try {
			plane.fillRoom(x,y,room);
		}
		catch(IllegalArgumentException e) {
			LOG.catching(Level.FATAL, e);
		}
		
	}
	private HashMap<String, Plane> readAllPlane(Engine e, String path){
		if(LOG.isDebugEnabled()) {
			LOG.debug("Crawle path: " + path);
		}
		HashMap<String, Plane> ret = new HashMap<>();
		Set<String> content = e.getResourceList(path);
		for(String s:content) {
			if(StringUtils.endsWith(s, "/")) {
				ret.putAll(readAllPlane(e, s));
			}
			else {
				if(StringUtils.endsWith(s, ".xml")) {
					try {
						if(LOG.isDebugEnabled()) {
							LOG.debug("Load XML: " + s);
						}
						Plane p = (Plane)unmarshaller.unmarshal(e.getFromResource(s));
						if(p!=null) {
							p.init(e, path);
							ret.put(p.getPlaneId(), p);
						}
						else {
							LOG.fatal("Error loading, plane is null: "+s);
						}
					} catch (JAXBException e1) {
						LOG.fatal("Error loading: " + s);
						LOG.catching(Level.FATAL,e1);
					}
					
				}
			}
		}
		return ret;
	}
	public static final String[] explodeName(String name) {
		return name.split("_");
	}
	public static final String composeName(String planeId, int x, int y) {
		return planeId + "_" + x + "_" + y;
	}

	@Override
	public void dest() {
	}
	
	public Plane getPlane(String planeID) {
		return planes.get(planeID);
	}
}
