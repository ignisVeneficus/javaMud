package org.ignis.javaMud.Mud.dataholder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.script.Invocable;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.StringUtils;
import org.ignis.javaMud.Mud.Core.Room;
import org.ignis.javaMud.Mud.dataholder.test.SenseTest;
import org.ignis.javaMud.Mud.utils.Defaults;
import org.ignis.javaMud.utils.xml.MapAdapterArray;
/**
 * Szoba kijarata
 * 
 * @author Ignis
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Exit {
	/**
	 * Irany, ami megjelenik, pl "eszak", "ki", "res" stb
	 */
	@XmlAttribute
	private String direction;
	/**
	 * szoba ahova kerul
	 */
	@XmlAttribute
	private String destination;
	/**
	 * true esetben nem jatekosok nem mehetnek arrafele (rohangalo szornyek)
	 */
	@XmlAttribute
	private boolean onlyPlayer;
	/**
	 * mennyire veheto eszre
	 */
	@XmlElement(name="test")
	private List<SenseTest> notice;

	/**
	 * mennyi SP-t von le (aranyszam)
	 */
	@XmlAttribute
	private double spLost;
	
	//ajtot bele

	// szurot bele
	
	/**
	 * szoveg amikor valaki elhagyja a szobat
	 */
	private String leaveTxtEnvSource;
	/**
	 * szoveg az illetonek amikor elhagyja a szobat
	 */
	private String leaveTxtSource;
	/**
	 * javascript fuggveny amikor elhagyja a szobat
	 */
	private String leaveFunc;
	/**
	 * Bolokkolo objektumok listaja
	 */
	@XmlElement(name="blokker")
	private ArrayList<String> blokkers;
	
	@XmlJavaTypeAdapter(MapAdapterArray.class)
	private Map<String, String[]> properties;
	/*
	 *  kepessegprobat bele
	 *  lista:
	 *  ANY vagy ALL
	 *  -kepesseg + proba szintje
	 *  pl maszas az outlands-en. hogy leesik-e a sziklarol
	 *  
	 *  
	 *  condition-t bele
	 *  Ezt megolja a leave fg;
	 */
	
	/* *********************************************
	 * localis vatozok
	 * 
	 * *********************************************/
	
	public Exit() {
		// not defined
		spLost = 1;
		properties = new HashMap<>();
		notice = new ArrayList<>();
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public boolean isOnlyPlayer() {
		return onlyPlayer;
	}

	public void setOnlyPlayer(boolean onlyPlayer) {
		this.onlyPlayer = onlyPlayer;
	}


	public String getLeaveTxtEnvSource() {
		return leaveTxtEnvSource;
	}

	public void setLeaveTxtEnvSource(String leaveTxtEnvSource) {
		this.leaveTxtEnvSource = leaveTxtEnvSource;
	}

	public String getLeaveTxtSource() {
		return leaveTxtSource;
	}

	public void setLeaveTxtSource(String leaveTxtSource) {
		this.leaveTxtSource = leaveTxtSource;
	}

	public String getLeaveFunc() {
		return leaveFunc;
	}

	public void setLeaveFunc(String leaveFunc) {
		this.leaveFunc = leaveFunc;
	}

	public double getSpLost() {
		return spLost;
	}

	public void setSpLost(double spLost) {
		this.spLost = spLost;
	}

	public ArrayList<String> getBlokkers() {
		return blokkers;
	}

	public void setBlokkers(ArrayList<String> blokkers) {
		this.blokkers = blokkers;
	}
	
	public Map<String, String[]> getProperties() {
		return properties;
	}

	public void setProperties(ConcurrentHashMap<String, String[]> properties) {
		this.properties.putAll(properties);
	}

	public void setProperty(String key, String value) {
		properties.put(key, new String[] { value });
	}

	public String[] getProperties(String key) {
		return properties.get(key);
	}

	public String getProperty(String key) {
		String[] values = properties.get(key);
		if (values != null) {
			return StringUtils.join(values, "|");
		}
		return null;
	}
	void afterUnmarshal(Unmarshaller unmarshaller, java.lang.Object parent) {
		if(notice==null) notice=new ArrayList<>();
		if(notice.isEmpty()) {
			notice.add(new SenseTest(Defaults.Sense_Latas, 100));
		}
	}

	public List<SenseTest> getNotice() {
		return notice;
	}

}
