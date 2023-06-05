package org.ignis.javaMud.Mud.dataholder;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.ignis.javaMud.Mud.dataholder.test.SenseTest;
/**
 * Emotion, olyan leiras/esemeny, amit a szornyek/njk-k generalnak, de nincs a jatekbeli hatasuk, inkabb hangulatleirasok:
 * nyul fuvet majszol, szarvas veszelyt kemlel, stb..
 * @author Ignis
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Emotion {
	/**
	 * tipusa: latas/hallas/szaglas stb
	 */
	@XmlElement(name="sense")
	private List<SenseTest> senseTests;
	/**
	 * Leirasa
	 */
	private String descr;
	/**
	 * Feltetel, adott listaban meglete eseten jelenik meg. ilyen listat allit elo pl az astronomy, este, nappal, ejjel, ejfel stb
	 */
	@XmlAttribute
	private String condition;
	
	public Emotion(List<SenseTest> type, String descr,String condition) {
		super();
		this.senseTests = type;
		this.descr = descr;
		this.condition = condition;
	}
	public Emotion() {
		senseTests = new ArrayList<>();
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public Emotion clone() {
		return new Emotion(senseTests, descr, condition);
	}
	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer();
		boolean f = false;
		for(SenseTest t:senseTests) {
			if(f) {
				buff.append(",");
			}
			buff.append(t.toString());
			f = true;
		}
		return "{type: " +  buff.toString() +  " descr: " + descr + " scondition:" + condition + "}";
	}
	public List<SenseTest> getTests() {
		return senseTests;
	}
	public void setSenseTests(List<SenseTest> senseTests) {
		this.senseTests = senseTests;
	}
	

}
