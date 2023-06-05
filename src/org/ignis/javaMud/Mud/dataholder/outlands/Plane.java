package org.ignis.javaMud.Mud.dataholder.outlands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.ignis.javaMud.Mud.Engine;
import org.ignis.javaMud.Mud.StringUtil;
import org.ignis.javaMud.Mud.AreaEffect.Water;
import org.ignis.javaMud.Mud.Core.OutlandsRoom;
import org.ignis.javaMud.Mud.data.Coordinate;
import org.ignis.javaMud.Mud.dataholder.Exit;
import org.ignis.javaMud.Mud.dataholder.Stimulus;
import org.ignis.javaMud.Mud.dataholder.test.SenseTest;
import org.ignis.javaMud.Mud.handlers.Outlands;
import org.ignis.javaMud.Mud.utils.Defaults;

// terkephez: https://gist.github.com/Utyff/fb4b47b37d7e975e1f18df6c9dea847f Bresenham's line algorithm.

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name ="Plane")
public class Plane {
	static private Logger LOG = LogManager.getLogger(Plane.class);
	
	@XmlAttribute
	private String planeId;
	@XmlElement(name="map")
	private ArrayList<Map> layers;

	@XmlElement(name="mapAddon")
	private ArrayList<MapAddon> addonList;
	
	@XmlElement(name="city")
	private ArrayList<City> cityList;
	
	
	/* *********************************************
	 * localis vatozok
	 * 
	 * *********************************************/
	
	// bekotesek-ek kellenenek:
	// varosok
	
	@XmlTransient
	private Map terrain= null;
	@XmlTransient
	private Map biomes = null;
	@XmlTransient
	private ArrayList<Map> overlays;
	
	@XmlTransient
	private HashMap<Coordinate,MapAddon> addons;
	@XmlTransient
	private HashMap<Coordinate,City> cities;
	
	@XmlTransient
	private int width;
	@XmlTransient
	private int height;
	
	@XmlTransient
	private LinkedHashSet<String> lineKeys;
	@XmlTransient
	private HashMap<String,HashSet<String>> lineRooms;
	@XmlTransient
	private HashSet<String> cityLines;
	
	@XmlTransient
	private int maxSlope;
	
	public Plane() {
		overlays = new ArrayList<>();
		addons = new HashMap<>();
		lineRooms = new HashMap<>();
		cities = new HashMap<>();
	}
	
	public void init(Engine e,String path) throws IllegalArgumentException {
		LOG.info("INIT: Start:" + this.getPlaneId());
		width=-1;
		height=-1;
		for(Map m:layers) {
			m.init(e, path);
			LOG.trace("INIT: " + m.getMapType());
			if("terrain".equalsIgnoreCase(m.getMapType())){
				terrain = m;
			}
			else
				if("biomes".equalsIgnoreCase(m.getMapType())){
					biomes = m;
				}
				else {
					overlays.add(m);
				}
			if((width==-1)&&(height==-1)) {
				width = m.getWidth();
				height = m.getHeight();
			}
			else if((width!=m.getWidth())||(height!=m.getHeight())){
				LOG.fatal(getPlaneId() + " plane has a map what different size: " + m.getMapType() + " map: " + m.getFile());
				throw new IllegalArgumentException(m.getMapType() +" has different size as the others!");
			}
		}
		LOG.info("INIT: load connections");
		for(MapAddon c:addonList) {
			addons.put(new Coordinate(c.getX(), c.getY()), c);
		}
		LOG.info("INIT: load cities");
		for(City c:cityList) {
			cities.put(new Coordinate(c.getX(), c.getY()), c);
		}
		
		lineRooms =  MapDefaults.getLineRoomTypes();
		cityLines = MapDefaults.getCityLineType();
		lineKeys = MapDefaults.getLineTypes();

		maxSlope = 0;
		boolean first = false;
		for(int x = 0;x<width;x++) {
			for(int y=0;y<height;y++) {
				Coordinate coord = new Coordinate(x, y);
				int h = getTerrainHeight(coord);
			
				for(int i=0;i<9;i++) {
					Coordinate c = coord.move(i);
					if(checkCoord(c)) {
						try {
							int h2 = getTerrainHeight(c);
							int slope = Math.abs(h2-h);
							maxSlope = Math.max(maxSlope, slope);
						}
						catch(Exception ex) {
							if(!first) LOG.debug(c);
						}
					}
				}
			}
		}
		LOG.info("INIT: Max slop: " + maxSlope);
	
		
		LOG.info("INIT: End: " + this.getPlaneId());
		
	}
	public void fillRoom(int x, int y, OutlandsRoom room) throws IllegalArgumentException{
		ThreadContext.push("room: " + room.getFullObjectName());

		LOG.trace("Fill room: " + x + ":" + y);
		if(x<0) throw new IllegalArgumentException("X must be positive!");
		if(y<0) throw new IllegalArgumentException("Y must be positive!");
		if(terrain==null) throw new IllegalArgumentException("terrain not loaded");
		if(height<=y) throw new IllegalArgumentException("Y maximum " + height);
		if(width<=x) throw new IllegalArgumentException("X maximum " + width);
		
		room.setOpen(true);
		
		Coordinate coord = new Coordinate(x, y);
		
		room.setCoord(coord);
		room.setPlaneId(getPlaneId());
		
		// a kornyezo teruletek osszeszedese
		Coordinate[] surrounding = new Coordinate[9];
		int[] sHeight = new int[9];
		@SuppressWarnings("unchecked")
		HashSet<String>[] types = new HashSet[9];
		int minh =255;
		int maxh =0;
		int dir_max = 0;
		int dir_min = 0;
		
		
		HashMap<String,String> lines = new HashMap<>();
		HashMap<String,ArrayList<Integer>> linesheight = new HashMap<>();
		
		for(String key:lineKeys) {
			lines.put(key,"");
			linesheight.put(key,new ArrayList<>());
		}
		
		// osszedjuk a kornyeket (3x3-as negyezt)
		for(int i=0;i<9;i++) {
			Coordinate c = coord.move(i);
			if(checkCoord(c)) {
				try {
					surrounding[i] = c;
					int h = getTerrainHeight(c);
					HashSet<String> t = getAllType(c);
					if(LOG.isTraceEnabled()) {
						LOG.trace(c + " => (" +t.size() + ") " + String.join("|", t));
					}
					minh = Math.min(minh, h);
					maxh = Math.max(maxh, h);
					sHeight[i] = h;
					types[i] = t;
					
					if(h==minh) dir_min = i;
					if(h==maxh) dir_max = i;
					if(i>0) {
						// osszeszedjuk a vonalas overlayeket (ut, folyo)
						for(String key:lineKeys) {
							if(isLine(key, t, c)) {
								lines.put(key, lines.get(key)+i);
								linesheight.get(key).add(h);
							}
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					LOG.fatal("Hiba a koordinata kiertekelese kozben: " + coord.toString() + " -> " + c.toString());
					surrounding[i] = null;
				}
			}
			else {
				surrounding[i] = null;
			}
		}
		if(LOG.isTraceEnabled()) {
			StringBuffer buff = new StringBuffer("[");
			for(int i=0;i<9;i++) {
				if(i!=0) buff.append("|");
				buff.append(sHeight[i]);
			}
			buff.append("]");
			LOG.trace("Heights: " + buff.toString());
			for(String key:lineKeys) {
				LOG.trace(key + " line: " + lines.get(key));
			}
		}
		ArrayList<MapColor> mapColors = getAllMapColor(coord);
		LOG.trace("MapColors: " + mapColors.size());
		Stimulus mainSt = null;
		MapColor mainMc = null; 
		// osszeszedjuk az erzekelest
		for(MapColor mc:mapColors) {
			LOG.trace("MapColor: " + mc.getRgb().toString() + " -> " +mc.getMapFullType() + " -> "+ mc.getMapType());
			for(Stimulus st:mc.getStimuluses()) {
				// masoljuk
				Stimulus newSt = st.clone();
				// egyenlore csak a latast piszkaljuk
				if(StringUtil.equalsSecoundString(Defaults.Sense_Latas,st.getType())){
					// ha szoba, az lesz a fo. elvileg csak 1 lehet
					if("room".equals(mc.getType())) {
						mainMc = mc;
						mainSt = newSt;
					}
				}
				// bedobjuk a szobaba
				if(mainSt!=newSt) {
					room.getStimulus().add(newSt);
				}
			}
			room.getRoomItems().addAll(mc.getItems());
		}
		if(mainSt==null) {
			LOG.fatal("Nincs latas: " + coord.toString());
			mainSt = new Stimulus(Defaults.Sense_Latas, 0, "Koordinata: " + coord.toString(), "Koordinata: " + coord.toString(), "",0);
		}
		// vonalas rajzok kiirasa
		for(MapColor mc:mapColors) {
			String mt = StringUtil.exEkezet(mc.getMapType());
			if(lineKeys.contains(mt)) {
				mainSt.setDescr(mainSt.getDescr(room) + "\n" + MapDefaults.getLineString(mt, lineDirection(lines.get(mt)), mc.getMapSubType(), linesheight.get(mt).toArray(new Integer[0])));
			}
		}
		// osszerakjuk az emelkedest
		boolean isWater = false;
		if((mainMc!=null) && (mainMc.isWater())) {
			isWater = true;
		}
		int h = sHeight[Coordinate.DIR_NONE];
		if(!isWater) {
			if((maxh>h)||(minh<h)){
				LOG.trace("h: " + h + " dir_max: " +dir_max + " dir_min: " + dir_min + " maxh-h: " + (maxh-h) + " h-minh: " + (h-minh));
				String slope = MapDefaults.getSlopeString(dir_max, dir_min, maxh-h, h-minh, (maxh>h), (minh<h));
				mainSt.setDescr(mainSt.getDescr(room) + "\n" + slope + "\n");
			}
		}
		// a teruletek bekotese
		
		room.getStimulus().add(0, mainSt);
		
		MapAddon con = addons.get(coord);
		if(con!=null) {
			con.addToRoom(room);
		}
		
		String name = room.getFullObjectName();
		String path = name.substring(0, name.lastIndexOf("/")+1);
		for(int i=1;i<9;i++) {
			Coordinate c = surrounding[i];
			if(c!=null) {
				int slope = sHeight[i] - h;
				if(hasCity(c)) {
					int opp = Coordinate.getOpposite(i);
					City city = cities.get(c);
					Exit e= new Exit();
					e.setDestination(city.getDirection(opp));
					e.getNotice().add(new SenseTest(Defaults.Sense_Latas,Defaults.getDefaultIntensity(Defaults.Sense_Latas)));
					e.setDirection(MapDefaults.getDirection(i));
					e.setOnlyPlayer(true);
					room.getExits().add(e);
				}
				else {
					Exit e = new Exit();
					if(!isWater) {
						double mult = MapDefaults.getSPSlope(slope);
						int terr = (mainMc!=null)?mainMc.getSPLost():1;
						double spLost = (terr*mult);
						e.setSpLost(spLost);
						e.setProperty("slope",""+slope );
						e.setProperty("maxSlope",""+maxSlope);
					}
					e.setDestination(path + Outlands.composeName(planeId, c.getX(),c.getY()));
					e.getNotice().add(new SenseTest(Defaults.Sense_Latas,Defaults.getDefaultIntensity(Defaults.Sense_Latas)));
					e.setDirection(MapDefaults.getDirection(i));
					// maszas eses az outlandsRoom-ban van feluldefinialva
					
					room.getExits().add(e);
				}
			}
		}
		// egyeb kijaratok
		if(isWater) {
			room.addAreaEffect(Water.createWater());
		}
		ThreadContext.pop();
		
		//org.ignis.javaMud.Mud.cartografia.Outlands.getMap(coord, this);
	}
	
	
	private int[] lineDirection(String dir) {
		int[] ret = new int[dir.length()];
		for(int i=0;i<dir.length();i++) {
			ret[i] = Integer.parseInt(dir.substring(i,i+1));
		}
		return ret;
	}
	public int getTerrainHeight(Coordinate coord) throws ArrayIndexOutOfBoundsException{
		return terrain.getColorRed(coord);
	}
	

	public String getPlaneId() {
		return planeId;
	}

	public void setPlaneId(String planeId) {
		this.planeId = planeId;
	}

	public ArrayList<Map> getOverlays() {
		return overlays;
	}

	public void setOverlays(ArrayList<Map> overlays) {
		this.overlays = overlays;
	}

	public int getWidth() {
		return width;
	}
	public String getOverlay(Coordinate c) {
		MapAddon add = addons.get(c);
		if(add!=null) {
			return add.getType();
		}
		return null;
	}

	public int getHeight() {
		return height;
	}

	public HashSet<String> getAllType(Coordinate coord){
		HashSet<String> ret = new HashSet<>();
		ret.addAll(terrain.getAllType(coord));
		ret.addAll(biomes.getAllType(coord));
		for(Map m:overlays) {
			ret.addAll(m.getAllType(coord));
		}
		return ret;
	}
	public boolean checkCoord(Coordinate c) {
		return ((c.getX()>=0)&&
				(c.getX()<width) &&
				(c.getY()>=0)&&
				(c.getY()<height));
	}
	private ArrayList<MapColor> getAllMapColor(Coordinate coord){
		ArrayList<MapColor> ret = new ArrayList<>();
		ret.addAll(terrain.getAllMapColor(coord));
		ret.addAll(biomes.getAllMapColor(coord));
		for(Map m:overlays) {
			ret.addAll(m.getAllMapColor(coord));
		}
		return ret;
	}
	public boolean hasCity(Coordinate coord) {
		return cities.containsKey(coord);
	}
	
	public boolean isLine(String key, HashSet<String> types, Coordinate c) {
		if(types.contains(key)) {
			LOG.trace("found: " + key);
			return true;
		}
		else {
			// azokat, amik szobatipusakent: pl a folyo folyon bele a tengerbe
			if(!Collections.disjoint(types, lineRooms.get(key))) {
				return true;
			}
			else {
				// varoshoz kapcsolodoan, megnezni, hogy van-e arra kapuja
				if((hasCity(c)) && (cityLines.contains(key))) {
					// varos kiertekelese
					return true;
				}
			}
		}
		return false;
		
	}

	public LinkedHashSet<String> getLineKeys() {
		return lineKeys;
	}
}
