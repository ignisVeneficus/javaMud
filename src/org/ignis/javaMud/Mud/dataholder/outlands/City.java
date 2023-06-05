package org.ignis.javaMud.Mud.dataholder.outlands;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.ignis.javaMud.Mud.data.Coordinate;
/**
 * Varos bekotese
 * Az egtajiranyok, hogy az arre levo teruletrol melyik szobaba lehet belepni.
 * Ahol nincs megadva onnet sehova.
 * @author Csaba Toth
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class City {
	@XmlAttribute
	private int x;
	@XmlAttribute
	private int y;
	@XmlAttribute
	private String eszak;
	@XmlAttribute
	private String eszakkelet;
	@XmlAttribute
	private String kelet;
	@XmlAttribute
	private String delkelet;
	@XmlAttribute
	private String del;
	@XmlAttribute
	private String delnyugat;
	@XmlAttribute
	private String nyugat;
	@XmlAttribute
	private String eszaknyugat;
	public String getEszak() {
		return eszak;
	}
	public String getEszakkelet() {
		return eszakkelet;
	}
	public String getKelet() {
		return kelet;
	}
	public String getDelkelet() {
		return delkelet;
	}
	public String getDel() {
		return del;
	}
	public String getDelnyugat() {
		return delnyugat;
	}
	public String getNyugat() {
		return nyugat;
	}
	public String getEszaknyugat() {
		return eszaknyugat;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public String getDirection(int direction) {
		switch(direction){
		case Coordinate.DIR_E:
			return eszak;
		case Coordinate.DIR_EK:
			return eszakkelet;
		case Coordinate.DIR_K:
			return kelet;
		case Coordinate.DIR_DK:
			return delkelet;
		case Coordinate.DIR_D:
			return del;
		case Coordinate.DIR_DNY:
			return delnyugat;
		case Coordinate.DIR_NY:
			return nyugat;
		case Coordinate.DIR_ENY:
			return eszaknyugat;
		}
		return "";
		
	}
}
