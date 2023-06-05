package org.ignis.javaMud.messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MapItem {
	private String id;
	private int x;
	private int y;
	private int height;
	private HashSet<String> types;
	private HashMap<String,String> lines;
	private ArrayList<String> overlays;
	private String descr;
	private String grp;
	public MapItem(String id,int x, int y, HashSet<String> types) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.types = types;
		lines= new HashMap<>();
		overlays = new ArrayList<>();
	}
	public void addLine(String name, String line) {
		lines.put(name,line);
	}
	public void addOverlay(String overlay) {
		overlays.add(overlay);
	}
	public int getHeight() {
		return height;
	}
	public HashSet<String> getTypes() {
		return types;
	}
	public HashMap<String, String> getLines() {
		return lines;
	}
	public ArrayList<String> getOverlays() {
		return overlays;
	}
	public String getDescr() {
		return descr;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public String getId() {
		return id;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public String getGrp() {
		return grp;
	}
	public void setGrp(String grp) {
		this.grp = grp;
	}

}
