package org.ignis.javaMud.Mud.Core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ignis.javaMud.Mud.dataholder.Stimulus;
/**
 * Tarolo/ szoba os, interface. Minden amiben targy/elo lehet.
 * (elvileg elo csak szobaban lehet, de a targy mindenben, igy tele van olyan fg-kel ami a szoba vagy a doboz miatt kell)
 * 
 * @author Ignis
 *
 */
public interface Holder {
	/**
	 * hozzaadj az objektumot a belsohoz, es elvegzi a kapcsolod regisztracios dolgokat
	 * beallitja az environment obj valtozot
	 * Ha esemenyek is kellenek, ott a moveObjInside(Object obj, Event evt)
	 * @param obj
	 * @return true, ha sikerult beleraknia. 
	 * @see #moveObjInside(Object obj, Event evt)
	 */
	public boolean addObjInside(Entity ent);
	/**
	 * Elvegzi mozgassal kapcsolatos esemenyeket kapcsolatos reszeket
	 * Ha nem kellenek az eventek (+ uzeneket kezelese, pl szoba leirasa), akkor a ott az addObjInside
	 * Elsonek az addObjInside kerul meghivasra.
	 * @param obj amit mozgatunk
	 * @param evn esemeny amit meg kell jeleniteni, ha nincs akkor, letrehoz egy altalanosat
	 * @return true, ha sikerult beleraknia. 
	 * @see #addObjInside(Object obj)
	 */
	public boolean moveObjInside(Entity ent, Event evt);
	/**
	 * kiveszi az objektumot a belsobol, elvegzi a kulonozo adminisztralasokat
	 * @return true, ha sikerult kivennie
	 * @param obj
	 */
	public boolean removeObjInside(Entity ent);
	/**
	 * kiveszi az objektumot a belsobol, elvegzi a kulonozo adminisztralasokat
	 * @return true, ha sikerult kivennie
	 * @param obj
	 */
	public boolean moveObjectFromInside(Entity ent, Event evt);
	
	/**
	 * Lekeri a kornyezeti allapotokat. 
	 * @return status halmaz ("este", stb)
	 */
	public Set<String> getEnvironmentStatus();
	/**
	 * A legfelso holdert adja vissza
	 * @return a legfelso holder, ami remelhetoen szoba
	 */
	public Holder getTopHolder();
	/**
	 * A legfelso living-et adja vissza, objektum esemenyek letrehozasahoz, hogy valaki tulajdona-e vagy sem..
	 * @return a legfelso living, vagy null ha ilyen nem letezik
	 */
	public Holder getTopLiving();

	
	/**
	 * logolasi okai vannak, elvileg mindenkinek az mud Object leszarmazottjanak kell lennie, ott pedig definialva van
	 * @return
	 */
	public String getFullObjectName();
	
	/**
	 * megkeresi, hogy jelen van-e
	 * @param name amit keresunk (id)
	 * @return amit talaltunk
	 */
	public Entity isPresent(String name);
	/**
	 * megkeresi, hogy jelen van-e (itt lehet folytatni a kereses)
	 * @param what amit keresunk,
	 * @param obj amelyik objektumtol keressuk
	 * @return amit talaltunk
	 */
	public Entity isPresent(String what, Entity obj);
	/**
	 * kornyezeti ingerek erossege (SOK)
	 * @param senses amikre keresunk
	 * @return map, hogy az adott erzekszervre milyen erosseget talaltunk
	 */
	public Map<String, Integer> getStimulus(Set<String> senses);
	/**
	 * kornyezeti ingerek erossege (SOK)
	 * @param senses amikre keresunk
	 * @param astronomyStatus a kornyezeti feltetelek
	 * @return map, hogy az adott erzekszervre milyen erosseget talaltunk
	 */
	public Map<String, Integer> getStimulus(Set<String> senses,Set<String> astronomyStatus);
	/**
	 * egy adott erzekszervhez az ingerek erossege 
	 * @param sense erzekszerv
	 * @return erosseg
	 */
	public int getStimulus(String sense);
	/**
	 * egy adott erzekszervhez az ingerek erossege 
	 * @param sense erzekszerv
	 * @param astronomyStatus a kornyezeti feltetelek
	 * @return erosseg
	 */
	public int getStimulus(String sense,Set<String> astronomyStatus);

	/**
	 * Osszeszedi az OSSZES stimulust amit ez az objektum tartalmaz (sajat, benne levo obejktumoket, azokban levoket stb)
	 * @param types tipusok
	 * @param astronomyStatus a kornyezeti feltetelek
	 * @return stimulusokat tartalmazo map
	 */
	public java.util.Map<String, ArrayList<Stimulus>> collectAllStimulusInside(Set<String> types, Set<String> astronomyStatus);
	/**
	 * Osszeszedi az OSSZES adott tipusu stimulust amit ez az objektum tartalmaz (sajat, benne levo obejktumoket, azokban levoket stb)
	 * @param type tipusok
	 * @param astronomyStatus a kornyezeti feltetelek
	 * @return stimulusokat tartalmazo lista
	 */
	public ArrayList<Stimulus> collectAllStimulusInside(String type, Set<String> astronomyStatus);
	
	
	/**
	 * Adott nez parancsok kiertekelese, ami nem onallo objektum, pl a szobakban a roomItemek, egbolt, 
	 * @param what
	 * @param source
	 * @return
	 */
	public boolean look(String what, Living source);
	/**
	 * Leiras lekerese, mivel minden remelhatoen Object leszarmazott, igy ez definialva van
	 * @param source aki keri
	 * @param b hosszu, vagy rovid leiras
	 * @return a leiras
	 */
	public String getDescription(Living source, boolean b);
	
	/**
	 * A living altal lathato targyakat adja vissza
	 * @param source living, aki nezi
	 * @return a lathato targyak listaja
	 */
	public List<Entity> getItems(Living source);
	
}
