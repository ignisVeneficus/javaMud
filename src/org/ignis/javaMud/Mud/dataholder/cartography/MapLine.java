package org.ignis.javaMud.Mud.dataholder.cartography;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class MapLine {
	@XmlAttribute
	private int x1;
	@XmlAttribute
	private int x2;
	@XmlAttribute
	private int y1;
	@XmlAttribute
	private int y2;
	public int getX1() {
		return x1;
	}
	public int getX2() {
		return x2;
	}
	public int getY1() {
		return y1;
	}
	public int getY2() {
		return y2;
	}
	public MapLine() {
		
	}
}
