package org.ignis.javaMud.messages;

import java.util.ArrayList;

public class Map {
	private String type;
	private String youAreHere;
	private ArrayList<MapItem> items;
	private ArrayList<MapLine> connections;

	public ArrayList<MapItem> getItems() {
		return items;
	}

	public Map(){
		items = new ArrayList<>();
		connections = new ArrayList<>();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	public void addMap(org.ignis.javaMud.Mud.dataholder.cartography.Map map) {
		ArrayList<org.ignis.javaMud.Mud.dataholder.cartography.MapItem> mil = map.getItems();
		for(org.ignis.javaMud.Mud.dataholder.cartography.MapItem itm:mil) {
			MapItem mi = new MapItem(itm.getId(),itm.getX(), itm.getY(),itm.getTypes());
			mi.setGrp(itm.getGroup());
			mi.getLines().putAll(itm.getLines());
			mi.getOverlays().addAll(itm.getOverlays());
			items.add(mi);
		}
		for(org.ignis.javaMud.Mud.dataholder.cartography.MapLine line:map.getLines()) {
			MapLine ml = new MapLine(line.getX1(), line.getX2(), line.getY1(), line.getY2());
			connections.add(ml);
		}
	}

	public String getYouAreHere() {
		return youAreHere;
	}

	public ArrayList<MapLine> getConnections() {
		return connections;
	}

	public void setYouAreHere(String youAreHere) {
		this.youAreHere = youAreHere;
	}
	
}
