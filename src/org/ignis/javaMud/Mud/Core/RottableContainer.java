package org.ignis.javaMud.Mud.Core;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.dataholder.Timeline;
import org.ignis.javaMud.Mud.dataholder.timeline.EventBag;
import org.ignis.javaMud.utils.CoreTools;
/**
 * Elrohado kontener.
 * Adott ido es tobb lepesen keresztul megszunik letezni, es a vegen minden targy kikerul a szobaba
 * @author Ignis
 *
 */
@XmlRootElement(name ="RottableContainer")
public class RottableContainer extends Container implements Rottable {
	static private Logger LOG = LogManager.getLogger(RottableContainer.class);
	/**
	 * Esemenyek, amiken atmegy
	 */
	@XmlElement(name="timeline")
	private Timeline timeline;
	/**
	 * volt-e inicializalva
	 */
	@XmlTransient
	private boolean hasInit;
	
	@XmlTransient
	private int nrOfTick=0;
	
	@Override
	public void tick() {
		LOG.trace("Tick: "+ nrOfTick);
		super.tick();
		if(hasInit) {
			nrOfTick++;
			EventBag eb = timeline.getLast();
			if(eb.getTS()==nrOfTick) {
				eb.handleEvent(this);
				boolean n = timeline.next();
				if(!n) {
					engine.destrObject(this);
				}
			}
		}
		
	}
	public void initBeforeTick() {
		initTimeline();
	}
	@Override
	protected void init() {
		super.init();
		if(timeline!=null) {
			boolean ni = timeline.isNeedInit();
			if(!ni) {
				initTimeline();
			}
		}
		CoreTools.registerHeartBeat(engine, this);
	}
	private void initTimeline() {
		hasInit = true;
		timeline.init(this);
		if(timeline.next()) {
			LOG.trace("INIT");
			EventBag eb = timeline.getLast();
			if(eb!=null) {
				int Ts = eb.getTS();
				LOG.trace(Ts);
				if(Ts ==0) {
					eb.handleEvent(this);
				}
			}
			boolean n = timeline.next();
			if(!n) {
				engine.destrObject(this);
			}
		}
	}

}
