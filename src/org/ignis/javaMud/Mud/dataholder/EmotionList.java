package org.ignis.javaMud.Mud.dataholder;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
/**
 * Lista az emotionokhoz,
 * Adott idozokonkent, adott esellyel valaszt egyet a rendszer es kiirja
 * @author Ignis
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class EmotionList {
	/**
	 * Emotion lista
	 */
	@XmlElement(name="emotion")
	private List<Emotion> emotionList;
	/**
	 * Esely 0-probality kozott general veletlenszamot, 0 eseten lesz emotion -> minel nagyobb annal ritkabban
	 */
	@XmlAttribute
	private int probality;
	/**
	 * Hany tickkenkent probalkozzon
	 */
	@XmlAttribute
	private int intervall;
	
	@XmlTransient
	private int tickNr;
	
	public EmotionList() {
		emotionList = new ArrayList<>();
		intervall = 0;
		tickNr=0;
	}
	
	public List<Emotion> getEmotionList() {
		return emotionList;
	}
	public void setEmotionList(List<Emotion> emotionList) {
		this.emotionList = emotionList;
	}
	public int getProbality() {
		return probality;
	}
	public void setProbality(int probality) {
		this.probality = probality;
	}

	public int getIntervall() {
		return intervall;
	}

	public void setIntervall(int intervall) {
		this.intervall = intervall;
	}
	
	public boolean tick() {
		if(intervall==0) return false;
		tickNr++;
		if(tickNr%intervall==0) {
			tickNr = 0;
			return true;
		}
		return false;
	}
}
