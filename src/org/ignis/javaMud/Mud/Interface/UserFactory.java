package org.ignis.javaMud.Mud.Interface;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ignis.javaMud.Mud.Engine;
import org.ignis.javaMud.Mud.Core.Player;
import org.ignis.javaMud.Mud.Core.Room;
import org.ignis.javaMud.Mud.Core.Skill;
import org.ignis.javaMud.Mud.dataholder.InflectionWord;
import org.ignis.javaMud.server.User;
import org.springframework.web.socket.WebSocketSession;

public class UserFactory implements org.ignis.javaMud.server.UserFactory {
	static private Logger LOG = LogManager.getLogger(UserFactory.class);
	
	private Engine engine;
	private ConcurrentHashMap<String, org.ignis.javaMud.Mud.Interface.User> users;
	public UserFactory() {
		users = new ConcurrentHashMap<>();
	}
	
	int id = 0;
	@Override
	public User getUser(WebSocketSession session) {
		org.ignis.javaMud.Mud.Interface.User user = users.get(session.getId());
		if(user == null) {
			LOG.trace("create user for: " + session.getId());
			user = new org.ignis.javaMud.Mud.Interface.User(session,engine);
			users.put(session.getId(), user);
			
			
			/*jatszani
			 *
			 */
			Player p = new Player();
			p.setEngine(engine);
			p.setUser(user);
			user.setPlayer(p);
			String name = "";
			switch(id) {
			case 0:
				name="ignis";
				break;
			case 1:
				name = "beta";
				break;
			case 2:
				name = "gamma";
				break;
			default:
				name = "player_" + id;
			}

			p.setLongName(name);
			InflectionWord iw = new InflectionWord();
			iw.setWord(name);
			p.setShortName(iw);
			p.setMaxSP(100);
			p.setActSP(100);
			
			p.setMaxHP(100);
			p.setActHP(90);
			
			p.addSkill("uszas");
			p.addSkill("maszas");
			p.addSkill("eses");
			Skill s = p.getSkill("uszas");
			s.setBaseValue(50);
			
			p.initObject(p.getLongName(), "" + (id++), engine);
			// tunderfalu
			//Room room = (Room)engine.getOrLoad("@Outlands/1_300_250");
			// tunderfalu leugras
			//Room room = (Room)engine.getOrLoad("@Tunderfalu1/falu1_1_57");
			// tunderfalu gyumolcsos
			Room room = (Room)engine.getOrLoad("@Tunderfalu1/falu1_0_2");
			// valahol messze a tengerben
			//Room room = (Room)engine.getOrLoad("@Outlands/1_250_250");
			// folyok
			//Room room = (Room)engine.getOrLoad("@Outlands/1_388_291");
			//fovaros
			//Room room = (Room)engine.getOrLoad("@Outlands/1_251_118");
			
			 LOG.trace("room: "+room);
			p.moveObject(room,null,null);
			
		}
		return user;
	}
	public Engine getEngine() {
		return engine;
	}
	public void setEngine(Engine engine) {
		this.engine = engine;
	}

}
