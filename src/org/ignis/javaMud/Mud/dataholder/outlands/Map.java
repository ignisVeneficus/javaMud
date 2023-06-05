package org.ignis.javaMud.Mud.dataholder.outlands;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.Engine;
import org.ignis.javaMud.Mud.data.Coordinate;

/**
 * Outland egy terkepet tarolo xml
 * 3 fajta van: terrain - domborzat
 * 	biome - kinezet, novenytakaro
 *  overlay - minden mas
 * 
 * @author Csaba Toth 
  *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Map {
	static private Logger LOG = LogManager.getLogger(Map.class);
	/**
	 * terkep tipusa
	 */
	@XmlAttribute
	private String mapType;
	/**
	 * kepfile
	 */
	@XmlAttribute
	private String file;
	/**
	 * a definialt pixelszinek
	 */
	@XmlElement(name="mapColor")
	private ArrayList<MapColor> mapColors;
	
	/* *********************************************
	 * localis vatozok
	 * 
	 * *********************************************/
	
	/**
	 * kep
	 */
	@XmlTransient
	private BufferedImage map;
	/**
	 * szinhez a leirasa
	 */
	@XmlTransient
	private HashMap<StringColor,ArrayList<MapColor>> data;
	/**
	 * szelesseg
	 */
	@XmlTransient
	private int width=0;
	/**
	 * magassag
	 */
	@XmlTransient
	private int height=0;
	
	public Map() {
		data = new HashMap<>();
		mapColors = new ArrayList<>();
	}

	public String getMapType() {
		return mapType;
	}

	public void setMapType(String type) {
		this.mapType = type;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public ArrayList<MapColor> getMapColors() {
		return mapColors;
	}

	public void setMapColors(ArrayList<MapColor> mapColors) {
		this.mapColors = mapColors;
	}
	public void init(Engine e,String path) throws IllegalArgumentException{
		LOG.info("INIT: Start: " +getFile() + " for " + getMapType());
		if(StringUtils.isBlank(file)) {
			throw new IllegalArgumentException("Map image filename is empty");
		}
		try {
			map = ImageIO.read(e.getFromResource(path+file));
		} catch (IOException e1) {
			throw new IllegalArgumentException("Error load map image", e1);
		}
		width = map.getWidth();
		height=map.getHeight();
		if(LOG.isDebugEnabled()) {
			LOG.debug("Size: "+ width + ";" + height);
			LOG.debug("INIT: register map colors.");
		}
		for(MapColor mp:mapColors) {
			StringColor code=mp.getRgb();
			if(LOG.isTraceEnabled()) {
				LOG.trace("INIT: register color " + code.toString() + " for " + mp.getMapType());
			}
			ArrayList<MapColor> list = data.get(code);
			if(list==null) {
				list= new ArrayList<>();
				data.put(code, list);
			}
			list.add(mp);
		}
		if(LOG.isTraceEnabled()) {
			for(StringColor key:data.keySet()) {
				ArrayList<MapColor> mc = data.get(key);
				LOG.trace("Color:" + key + " -> " +(mc!=null?mc.size():"null"));
			}
		}
		LOG.info("INIT: End: "+getFile());
	}
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	public int getColor(Coordinate coord) throws ArrayIndexOutOfBoundsException {
		if(map!=null) {
			return map.getRGB(coord.getX(),coord.getY());
		}
		return 0;
	}
	public int getColorRed(Coordinate coord) throws ArrayIndexOutOfBoundsException{
		LOG.trace("Coord: " + coord + " hasMap: " + (map!=null));
		if(map!=null) {
			return getRedFromInteger(map.getRGB(coord.getX(),coord.getY()));
		}
		return 0;
	}
	public int getColorGreen(Coordinate coord) throws ArrayIndexOutOfBoundsException{
		if(map!=null) {
			return getGreenFromInteger(map.getRGB(coord.getX(),coord.getY()));
		}
		return 0;
	}
	public int getColorBlue(Coordinate coord) throws ArrayIndexOutOfBoundsException{
		if(map!=null) {
			return getBlueFromInteger(map.getRGB(coord.getX(),coord.getY()));
		}
		return 0;
	}
	private int getAlphaFromInteger(int rgb) {
		return (rgb >> 24) & 0x000000FF;
	}
	
	private int getRedFromInteger(int rgb) {
		return (rgb >> 16) & 0x000000FF;
	}
	private int getGreenFromInteger(int rgb) {
		return (rgb >>8 ) & 0x000000FF;
	}
	private int getBlueFromInteger(int rgb) {
		return (rgb) & 0x000000FF;
	}
	
	public StringColor getColorAsString(Coordinate coord) throws ArrayIndexOutOfBoundsException{
		return allColorToString(getColor(coord));
	}
	private StringColor allColorToString(int color) {
		LOG.trace("map: " + getMapType() + " color: " + color + " ALpha: " + getAlphaFromInteger(color));
		int alpha = getAlphaFromInteger(color);
		if(alpha!=255) return null;
		return new StringColor(color);
	}
	/**
	 * osszeszedi az adott ponthoz tartozo osszes tipust egy string set-be
	 * mind a fo, mind a fo+al-tipus belekerul
	 * @param coord koordinata
	 * @return
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public HashSet<String> getAllType(Coordinate coord) throws ArrayIndexOutOfBoundsException{
		HashSet<String> ret = new HashSet<>();
		ArrayList<StringColor> colorList =new ArrayList<>(); 
		StringColor c=	getColorAsString(coord);
		LOG.trace("color: " + c);
		if(c==null) return ret;
		colorList.add(c);
		colorList.addAll(c.explode());
		LOG.trace("list: "+ colorList.size());
		for(StringColor color:colorList) {
			ArrayList<MapColor> mcl = data.get(color);
			LOG.trace("return list: " + (mcl!=null?mcl.size():"null"));
			if(mcl!=null) {
				for(MapColor mc:mcl) {
					ret.add(mc.getMapType());
					ret.add(mc.getMapFullType());
				}
			}
		}
		return ret;
	}
	/**
	 * osszeszedi az osszes mapColor-t ami ahoz a koordinatahoz tartozik
	 * @param coord mapcolor
	 * @return
	 */
	public Collection<MapColor> getAllMapColor(Coordinate coord) {
		if(LOG.isTraceEnabled()) {
			LOG.trace("Type: " + getMapType());
		}
		ArrayList<MapColor> ret = new ArrayList<>();
		ArrayList<StringColor> colorList =new ArrayList<>(); 
		StringColor c=	getColorAsString(coord);
		if(c==null) return ret;
		LOG.trace("Color: "+c);
		colorList.add(c);
		colorList.addAll(c.explode());
		for(StringColor color:colorList) {
			ArrayList<MapColor> mcl = data.get(color);
			LOG.trace("color: " + color + " listSize: " + (mcl!=null?mcl.size():"null")); 
			if(mcl!=null) {
				ret.addAll(mcl);
			}
		}
		return ret;
	}
}
