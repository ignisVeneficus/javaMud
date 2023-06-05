package org.ignis.javaMud.Mud.dataholder;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.derby.impl.sql.execute.rts.RealLastIndexKeyScanStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.Core.Object;
import org.ignis.javaMud.Mud.dataholder.timeline.EventBag;
/**
 * Olyan tortenesek gyujtese ami egymas utan, adott idoben jon letre.
 * Leginkabb valami esemeny automatikus utohatasait kezeli le, pl a rothado kontener szetrohadasat 
 * @author Ignis
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Timeline {
	static private Logger LOG = LogManager.getLogger(Timeline.class);
	/**
	 * Kell-e inicializalni
	 */
	@XmlAttribute(name="needInit")
	private boolean needInit;
	/**
	 * tortenesek (idovel kiegeszitett) listaja
	 */
	@XmlElement(name = "time")
	private List<EventBag> times;
	
	@XmlTransient
	private int lastItem = -1;
	
	public Timeline() {
		times=new ArrayList<>();
	}

	public boolean isNeedInit() {
		return needInit;
	}

	public void init(Object obj) {
		for(EventBag b:times) {
			b.init(obj);
		}
	}
	void afterUnmarshal(Unmarshaller unmarshaller, java.lang.Object parent) {
		LOG.trace("after afterUnmarshal");
		times.sort(null);
	}
	public EventBag getLast() {
		if(lastItem>=0) {
			return times.get(lastItem);
		}
		return null;
	}
	public boolean next() {
		times.sort(null);
		lastItem++;
		if(lastItem>= times.size()) return false;
		return true;
	}
}
