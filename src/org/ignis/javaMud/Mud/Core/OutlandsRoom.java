package org.ignis.javaMud.Mud.Core;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.data.Coordinate;
import org.ignis.javaMud.Mud.dataholder.Exit;
import org.ignis.javaMud.Mud.dataholder.cartography.Map;
import org.ignis.javaMud.Mud.dataholder.outlands.Plane;
import org.ignis.javaMud.Mud.dataholder.test.SenseTest;
import org.ignis.javaMud.Mud.dataholder.test.SkillTest;
import org.ignis.javaMud.Mud.handlers.Handler;
import org.ignis.javaMud.Mud.handlers.Outlands;
import org.ignis.javaMud.Mud.utils.Defaults;

/**
 * Szoba, ami az outlands, vagyis tombben megadott teruletek szobaja
 * 
 * @author Ignis
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "OutlandsRoom")
public class OutlandsRoom extends Room {
	@XmlTransient
	private Logger LOG = LogManager.getLogger(OutlandsRoom.class);
	@XmlTransient
	private Coordinate coord;
	@XmlTransient
	private String planeId;

	@Override
	protected void init() {
		Handler obj = engine.getHandler(Outlands.REG_NAME);
		if ((obj != null) && (obj instanceof Outlands)) {
			((Outlands) obj).fillRoom(getFullObjectName(), this);
		}
		super.init();
	}

	public Coordinate getCoord() {
		return coord;
	}

	public void setCoord(Coordinate coord) {
		this.coord = coord;
	}

	public String getPlaneId() {
		return planeId;
	}

	public void setPlaneId(String planeId) {
		this.planeId = planeId;
	}

	@Override
	protected Map generateMap() {
		Handler oh = engine.getHandler(Outlands.REG_NAME);
		if (oh == null)
			return null;
		if (!(oh instanceof Outlands))
			return null;
		Outlands o = (Outlands) oh;
		Plane p = o.getPlane(getPlaneId());
		if (p == null)
			return null;
		return org.ignis.javaMud.Mud.cartografia.Outlands.getMap(getCoord(), p);

	}

	@Override
	protected int handleOutFunc(Exit exit, Living source, int spLost) {
		if (!source.hasMaterialForm())
			return 0;

		String sl = exit.getProperty("slope");
		String msl = exit.getProperty("maxSlope");
		if (StringUtils.isEmpty(sl))
			return 0;
		if (StringUtils.isEmpty(msl))
			return 0;
		try {
			int slope = Integer.parseInt(sl);
			int maxSlope = Integer.parseInt(msl);
			if (Math.abs(slope) < 5)
				return 0; // do the normal way
			// TODO: ut kezelese

			int diff = (int) Math.floor(Math.abs(slope) * (maxSlope / slope));
			SkillTest test = new SkillTest("maszas", diff);
			int res = test.doSkillTest(source);
			if (Skill.isSuccess(res)) {
				String hol;
				String mit;
				String tMit;
				if (Skill.isProf(res)) {
					if (Math.abs(slope) < (maxSlope * 0.75)) {
						hol = " a lejtőn";
						mit = slope < 0 ? "lemászik" : "felmászik";
						tMit = slope < 0 ? "Lemászol" : "Felmászol";
					} else {
						hol = " a meredélyen";
						mit = slope < 0 ? "leereszkedik" : "felkapaszkodik";
						tMit = slope < 0 ? "Leereszkedsz" : "Felkapaszkodsz";
					}
				} else {
					if (Math.abs(slope) < (maxSlope * 0.75)) {
						hol = " a lejtőn";
						mit = slope < 0 ? "lemászik" : "felmászik";
						tMit = slope < 0 ? "Lemászol" : "Felmászol";
					} else {
						hol = " a meredélyen";
						mit = slope < 0 ? "levergődik" : "felvergődik";
						tMit = slope < 0 ? "Levergődsz" : "Felvergődsz";
					}
				}
				Room newPlace = getDest(exit);
				if (newPlace == null) {
					source.tell("Valami rossz történt!");
					return -1;
				}
				Event evt = Event.createSimpleSourceEvent(source.getAllPerceptibility(), source, "%S " + mit + hol,
						null, tMit + hol);
				source.moveObject((Room) newPlace, evt, null);
				// sp levonas
				source.addSP(-1 * spLost);
				return 1;
			} else {
				String irany;
				String mit;
				String mas;

				String irany1;
				String hol;
				String mit1;
				irany = (slope < 0) ? "le" : "vissza";
				irany1 = (slope < 0) ? "le" : "fel";
				if (Math.abs(slope) < (maxSlope * 0.75)) {
					mit = "gurulsz";
					mas = "gurul";
					mit1 = (slope < 0) ? "lemászni" : "felmászni";
					hol = "lejtőn";
				} else {
					mit = (slope < 0) ? "zuhansz" : "esel";
					mas = (slope < 0) ? "zuhan" : "esik";
					mit1 = (slope < 0) ? "leereszkedni" : "felkapaszkodni";
					hol = "meredek oldalon";
				}
				Event evt = Event.createSimpleSourceEvent(source.getAllPerceptibility(), source,
						"%S megpróbál " + irany1 + mit1 + " a " + hol + ", de ehelyett " + irany + mas + "!", null,
						"Megprobálsz " + irany1 + mit1 + " a " + hol + ", de ehelyett " + irany + mit + "!");

				if (slope < 0) {
					Room newPlace = getDest(exit);
					if (newPlace == null) {
						source.tell("Valami rossz történt!");
						return -1;
					}
					ArrayList<SenseTest> perceptibility = source.getAllPerceptibility();
					addSenseTest(perceptibility, Defaults.Sense_Hallas, 20);
					Event evt2 = Event.createSimpleSourceEvent(perceptibility, source,
							"%S " + (Math.abs(slope) < (maxSlope * 0.75) ? " a lejtőn legurulva"
									: " a meredélyen zuhanva") + " érkezik.",
							null, (Math.abs(slope) < (maxSlope * 0.75) ? "A lejtőn legurulva" : "A meredélyen zuhanva")
									+ " érkezel!");
					source.moveObject((Room) newPlace, evt, evt2);
					// sp levonas
					source.addSP(-1 * spLost);

					SkillTest test2 = new SkillTest("eses", diff);
					int res2 = test2.doSkillTest(source);
					double hpMultiple = 1;
					if (Skill.isProf(res2)) {
						Event evt3 = Event.createSimpleSourceEvent(perceptibility, source,
								"%S látványosan kigurulja az esését.", null, "Látványosan kigurulod az esésed!");
						if (evt3 != null)
							evt3.fire();
						hpMultiple = 0.25;
					} else {
						if (Skill.isSuccess(res2)) {
							Event evt3 = Event.createSimpleSourceEvent(perceptibility, source,
									"%S kigurulja az esését.", null, "Kigurulod az esésed!");
							if (evt3 != null)
								evt3.fire();
							hpMultiple = 0.5;
						} else {
							Event evt3 = Event.createSimpleSourceEvent(perceptibility, source,
									"%S sikertelenül próbálja tompítani az esését.", null,
									"Sikertelenűl próbálod tompítani az esésed!");
							if (evt3 != null)
								evt3.fire();
						}

					}
					source.sebez(Defaults.Sebzes_uto, (int) Math.floor(Math.abs(slope) * hpMultiple), "leesés");

				}
				return 1;

			}

		}
		catch (Exception e) {
		}
		return 0;
	}

}
