package org.ignis.javaMud.Mud;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PreDestroy;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.ServletContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.Core.Entity;
import org.ignis.javaMud.Mud.Core.Monster;
import org.ignis.javaMud.Mud.Core.MonsterLoader;
import org.ignis.javaMud.Mud.Core.Object;
import org.ignis.javaMud.Mud.Core.Singleton;
import org.ignis.javaMud.Mud.Core.Skill;
import org.ignis.javaMud.Mud.database.Database;
import org.ignis.javaMud.Mud.database.DatabaseItem;
import org.ignis.javaMud.Mud.database.RaceDatabase;
import org.ignis.javaMud.Mud.database.SkillDatabase;
import org.ignis.javaMud.Mud.database.SkillItem;
import org.ignis.javaMud.Mud.deamon.Astronomy;
import org.ignis.javaMud.Mud.deamon.CommandQueue;
import org.ignis.javaMud.Mud.deamon.HeartBeat;
import org.ignis.javaMud.Mud.derby.DBHandler;
import org.ignis.javaMud.Mud.handlers.ActionHandler;
import org.ignis.javaMud.Mud.handlers.Handler;
import org.ignis.javaMud.Mud.handlers.Outlands;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.context.WebApplicationContext;

public class Engine implements ApplicationContextAware, ApplicationListener<ApplicationContextEvent> {
	static private Logger LOG = LogManager.getLogger(Engine.class);
	/**
	 * betoltes / keresesnel: van a szobak, jatekosok (find or load) van az egyeb:
	 * csak load
	 * 
	 * betoltes: path kell neki, ha nincs ott xml, akkor elkezdi csonkolni, es
	 * atadni a path maradek reszet (outlands stb)
	 */

	private static JAXBContext jaxbContext = null;
	private static Unmarshaller unmarshaller = null;

	private static JAXBContext jaxbMapContext = null;
	private static Unmarshaller mapUnmarshaller = null;

	private static JAXBContext jaxbDBContext = null;
	private static Unmarshaller dbUnmarshaller = null;

	/**
	 * Javascript engine manager, kell a javascript engine-hez
	 */
	private static ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
	/**
	 * javascript engine
	 */
	private static ScriptEngine scriptEngine = null;
	/**
	 * utolso kioszott Id
	 */
	private static int lastID = 0;
	/**
	 * path referencia regex pattern
	 */
	private static Pattern refPattern = Pattern.compile("(@(.*?)/)");

	/**
	 * Webapplication context, kell a servletcontex-hez
	 */
	private WebApplicationContext wac;
	/**
	 * servletcontext, kell a filebol valo felolvasashoz
	 */
	private ServletContext sc;

	/**
	 * mud properties, eleresi ut, parameter
	 */
	private String propFile;
	/**
	 * path reference properties
	 */
	private Properties refs;
	/**
	 * mud properties, tenyleges osztaly
	 */
	private Properties mudProps = null;
	/**
	 * mindefele tarhely, pl outlands, stb..
	 */
	private Map<String, Handler> objects = null;
	/**
	 * Logo
	 */
	private String[] logo;

	private int status;

	/**
	 * object cache, olyan mud cuccoknak amibol 1 lehet: pl room, player
	 */
	private MemoryCache cache;

	/**
	 * Terkep cache, a betoltendo XML terkepket tarolja
	 */
	private LRUMap<String, org.ignis.javaMud.Mud.dataholder.cartography.Map> mapCache;

	/**
	 * Cache a living/monster loader cache-hez
	 */
	private Map<String, MonsterLoader> loaderCache;

	/**
	 * "adatbazisok"
	 */
	private SkillDatabase skillDb;
	private RaceDatabase raceDb;
	
	
	/**
	 * static cuccok letrehozasa pl, scriptengine
	 */
	static {
		try {
			// scriptEngine = scriptEngineManager.getEngineByExtension(".js");
			scriptEngine = scriptEngineManager.getEngineByName("nashorn");
			LOG.info("Scriptengine: " + scriptEngine);
		} catch (Exception e) {
			LOG.catching(Level.FATAL, e);
		}

		try {
			jaxbContext = JAXBContext.newInstance(Object.class);
			unmarshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			LOG.catching(Level.FATAL, e);
			e.printStackTrace();
		}
		try {
			jaxbMapContext = JAXBContext.newInstance(org.ignis.javaMud.Mud.dataholder.cartography.Map.class);
			mapUnmarshaller = jaxbMapContext.createUnmarshaller();
		} catch (JAXBException e) {
			LOG.catching(Level.FATAL, e);
		}
		try {
			jaxbDBContext = JAXBContext.newInstance(Database.class);
			dbUnmarshaller = jaxbDBContext.createUnmarshaller();
		} catch (JAXBException e) {
			LOG.catching(Level.FATAL, e);
		}
	}

	public Engine() {
		cache = new MemoryCache(1000);
		status = 0;
		logo = new String[0];
		mapCache = new LRUMap<>();
		loaderCache = Collections.synchronizedMap(new HashMap<String, MonsterLoader>());
	}

	/**
	 * javascript forditas, altalaban a felolvasott XML-bol jon
	 * 
	 * @param script a leforditando js stringben
	 * @return leforditott script
	 */
	// atirni throws-ra
	public CompiledScript compileScript(String script) {
		try {
			return ((Compilable) scriptEngine).compile(script);
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Szoba + user betolto cucc
	 * 
	 * @param name amit be kell toltenie
	 * @return betoltott mud objektum
	 */
	public Singleton getOrLoad(String name) {
		LOG.debug("Ask for " + name);
		Singleton cachedSingleton = null;
		String searchName = compileRef(name);
		String uniqueId = null;
		Object obj = null;
		synchronized (this) {
			cachedSingleton = cache.get(searchName);
			if (cachedSingleton != null) {
				LOG.trace("Found in cache: " + name);
				if (cachedSingleton.getStatus() != Object.STATUS_DESTROYED) {
					return cachedSingleton;
				}
				LOG.trace("It's destroyed: " + name);
			}
			if (!StringUtils.startsWith(searchName, "/")) {
				// Player
				return null;
			}

			uniqueId = "" + (++lastID);
			obj = loadObject(searchName, uniqueId);
			if (obj == null) {
				LOG.error(searchName + " cant load");
				return null;
			}
			if (obj instanceof Singleton) {
				cache.put(searchName, (Singleton) obj);
			} else {
				LOG.error(searchName + " loading error: its object but not singleton!");
				return null;
			}

		}
		obj.initObject(searchName, uniqueId, this);

		return (Singleton) obj;
	}

	/**
	 * leforditja a path-t
	 * 
	 * @param ref path referenciakkal
	 * @return kifejtett path
	 */
	public String compileRef(String ref) {
		LOG.trace("Input: " + ref);
		Matcher m = refPattern.matcher(ref);
		while (m.find()) {
			String r = m.group(1);
			String s = m.group(2);
			String t = refs.getProperty(s);
			if (StringUtils.isNotBlank(t)) {
				ref = ref.replace(r, t);
				m = refPattern.matcher(ref);
			}
		}
		return ref;
	}

	private Object loadObject(String origName, String uniqueID) {
		LOG.debug("Loading " + origName);
		String name = origName;
		Object ret = null;
		InputStream in = getFromResource(name + ".xml");
		try {
			while ((in == null) && (!("/".equalsIgnoreCase(name)))) {
				name = name.substring(0, name.lastIndexOf("/"));
				in = getFromResource(name + ".xml");
			}
		} catch (StringIndexOutOfBoundsException e) {
			LOG.error(name);
		}
		if (in == null) {
			LOG.error("Cant find " + origName);
			return null;
		}

		try {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Load XML: " + origName);
			}
			ret = (Object) unmarshaller.unmarshal(in);
			if (ret != null) {
				if (LOG.isDebugEnabled())
					LOG.debug("Loading " + origName + ": success. Its a " + ret.getClass().getName());
			} else {
				LOG.fatal("Error loading, Object is null: " + origName);
			}
		} catch (JAXBException e1) {
			LOG.fatal("Error loading: " + origName);
			LOG.catching(Level.FATAL, e1);
		}
		return ret;
	}

	public Entity load(String name) {
		LOG.debug("Try to load: " + name);
		String searchName = compileRef(name);
		if (!StringUtils.startsWith(searchName, "/")) {
			// Player
			return null;
		}
		String uniqueId = null;
		Object ret;
		MonsterLoader loader = loaderCache.get(name);
		synchronized (this) {
			boolean canCreate = true;
			if (loader != null) {
				canCreate = loader.canCreateNewOne();
			}
			if (!canCreate)
				return null;
			uniqueId = "" + (++lastID);
			ret = loadObject(searchName, uniqueId);
			if (ret == null)
				return null;
			if (!(ret instanceof Entity)) {
				LOG.error(searchName + " loading error: its object but entity!");
				return null;
			}
			if ((ret instanceof Monster) && (((Monster) ret).getMaxQty() > 0)) {
				if (loader == null) {
					loader = new MonsterLoader(name, ((Monster) ret).getMaxQty());
					loaderCache.put(name, loader);
				}
				loader.addNew();
				((Monster) ret).setLoader(loader);
			}
		}
		ret.initObject(searchName, uniqueId, this);
		return (Entity) ret;
	}

	public void destrObject(Object obj) {
		if (obj._destrObject()) {

		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (LOG.isInfoEnabled()) {
			LOG.info("CONFIG: set applicationContext " + applicationContext);
		}
		wac = (WebApplicationContext) applicationContext;
		sc = wac.getServletContext();

	}

	public Properties getMudProps() {
		return mudProps;
	}

	public void setMudProps(Properties mudProps) {
		this.mudProps = mudProps;
	}

	/**
	 * init resz, ami feldolgoz mindent.
	 */
	private void init() {
		if (mudProps != null)
			return;
		LOG.info("INIT: Start");
		mudProps = new Properties();
		try {
			LOG.info("INIT: load MudProperties from: " + propFile);
			InputStream in = getFromResource(propFile);
			if (in == null) {
				LOG.fatal("Cant load properties!");
				return;
			}
			mudProps.loadFromXML(in);

			LOG.info("INIT: create Object cache");
			String maxSizestr = mudProps.getProperty("Cache.MaxSize", "1000");
			int maxSize = Integer.parseInt(maxSizestr);
			cache = new MemoryCache(maxSize);
			objects = Collections.synchronizedMap(new HashMap<String, Handler>());

			String refFile = mudProps.getProperty("Mud.Ref", "/MUD/ref.xml");
			LOG.info("INIT: load RefProperties from: " + refFile);

			refs = new Properties();
			in = getFromResource(refFile);
			if (in != null)
				refs.loadFromXML(in);

			String logoFile = mudProps.getProperty("Mud.Logo", "/MUD/logo.txt");
			LOG.info("INIT: load logo from: " + refFile);
			BufferedReader inreader = new BufferedReader(new InputStreamReader(getFromResource(logoFile), "UTF8"));
			ArrayList<String> logostr = new ArrayList<>();
			String str;
			while ((str = inreader.readLine()) != null) {
				logostr.add(str);
			}
			logo = logostr.toArray(new String[0]);

			Outlands out = new Outlands();
			out.init(this);
			objects.put(Outlands.REG_NAME, out);
			LOG.info("INIT: added: " + Outlands.REG_NAME);

			CommandQueue queue = new CommandQueue();
			queue.init(this);
			objects.put(CommandQueue.REG_NAME, queue);
			LOG.info("INIT: added: " + CommandQueue.REG_NAME);

			Astronomy astronomy = new Astronomy();
			astronomy.init(this);
			objects.put(Astronomy.REG_NAME, astronomy);
			LOG.info("INIT: added: " + Astronomy.REG_NAME);

			HeartBeat heartbeat = new HeartBeat();
			heartbeat.init(this);
			objects.put(HeartBeat.REG_NAME, heartbeat);
			LOG.info("INIT: added: " + HeartBeat.REG_NAME);

			DBHandler dbHandler = new DBHandler();
			dbHandler.init(this);
			objects.put(DBHandler.REG_NAME, dbHandler);
			LOG.info("INIT: added: " + DBHandler.REG_NAME);

			ActionHandler actionHandler = new ActionHandler();
			actionHandler.init(this);
			objects.put(ActionHandler.REG_NAME, actionHandler);
			LOG.info("INIT: added: " + ActionHandler.REG_NAME);

			
			LOG.info("INIT: load databases");
			skillDb = (SkillDatabase)loadDatabase(mudProps.getProperty("Mud.database.skill"));
			
		} catch (IOException e) {
			LOG.catching(Level.FATAL, e);
		}
		LOG.info("INIT: End");
		status = 1;

	}

	public String getPropFile() {
		return propFile;
	}

	public void setPropFile(String propFile) {
		if (LOG.isInfoEnabled()) {
			LOG.info("CONFIG: set MUD properties to " + propFile);
		}
		this.propFile = propFile;
		// init();
	}

	public InputStream getFromResource(String file) {
		return sc.getResourceAsStream(file);
	}

	public String getContent(String ref) {
		LOG.trace("read from " + ref);
		String filename = compileRef(ref);
		InputStream in = getFromResource(filename);
		try {
			int bufferSize = 1024;
			char[] buffer = new char[bufferSize];
			StringBuilder out = new StringBuilder();
			Reader ir = new InputStreamReader(in, StandardCharsets.UTF_8);
			int charsRead;
			while ((charsRead = ir.read(buffer, 0, buffer.length)) > 0) {
				out.append(buffer, 0, charsRead);
			}
			return out.toString();
		} catch (Exception e) {
			LOG.catching(Level.FATAL, e);
			LOG.fatal("cant load: " + ref);
		}
		return null;
	}

	public Set<String> getResourceList(String path) {
		return sc.getResourcePaths(path);
	}

	public String getRealPath(String relativePath) {
		return sc.getRealPath(relativePath);
	}

	public Handler getHandler(String what) {
		return objects.get(what);
	}

	public String[] getLogo() {
		return logo;
	}

	public String getProperty(String key) {
		return mudProps.getProperty(key);
	}

	@PreDestroy
	public void onDestroy() throws Exception {
		for (Handler h : objects.values()) {
			h.dest();
		}
	}

	@Override
	public void onApplicationEvent(ApplicationContextEvent evn) {
		if (evn instanceof ContextRefreshedEvent) {
			init();
		}
		if (evn instanceof ContextClosedEvent) {
			try {
				onDestroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public org.ignis.javaMud.Mud.dataholder.cartography.Map getMap(String mapFile) {
		String fullname = compileRef(mapFile);
		org.ignis.javaMud.Mud.dataholder.cartography.Map ret = mapCache.get(fullname);
		if (ret != null)
			return ret;
		ret = loadMap(fullname);
		if (ret != null) {
			mapCache.put(fullname, ret);
		}
		return ret;
	}

	private org.ignis.javaMud.Mud.dataholder.cartography.Map loadMap(String path) {
		LOG.debug("Loading " + path);
		org.ignis.javaMud.Mud.dataholder.cartography.Map ret = null;
		InputStream in = getFromResource(path + ".xml");
		if (in == null) {
			LOG.error("Cant find " + path);
			return null;
		}

		try {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Load XML: " + path);
			}
			ret = (org.ignis.javaMud.Mud.dataholder.cartography.Map) mapUnmarshaller.unmarshal(in);
			if (ret != null) {
				if (LOG.isDebugEnabled())
					LOG.debug("Loading " + path + ": success.");
			} else {
				LOG.fatal("Error loading, Object is null: " + path);
			}
		} catch (JAXBException e1) {
			LOG.fatal("Error loading: " + path);
			LOG.catching(Level.FATAL, e1);
		}
		return ret;
	}
	
	private java.lang.Object loadDatabase(String path){
		java.lang.Object ret = null;
		String filename = compileRef(path);
		LOG.debug("Loading " + filename);
		InputStream in = getFromResource(filename);
		if (in == null) {
			LOG.error("Cant find " + filename);
			return null;
		}

		try {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Load XML: " + filename);
			}
			ret = dbUnmarshaller.unmarshal(in);
			if (ret != null) {
				if (LOG.isDebugEnabled())
					LOG.debug("Loading " + filename + ": success.");
			} else {
				LOG.fatal("Error loading, Database is null: " + filename);
			}
		} catch (JAXBException e1) {
			LOG.fatal("Error loading: " + filename);
			LOG.catching(Level.FATAL, e1);
		}
		return ret;
	}

	
	public Skill getSkill(String name) {
		if(skillDb == null) return null;
		return skillDb.createOne(name);
	}
}
