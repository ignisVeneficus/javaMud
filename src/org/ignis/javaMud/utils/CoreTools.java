package org.ignis.javaMud.utils;

import org.ignis.javaMud.Mud.Engine;
import org.ignis.javaMud.Mud.Core.HeartBeatListener;
import org.ignis.javaMud.Mud.deamon.HeartBeat;
import org.ignis.javaMud.Mud.handlers.Handler;

public class CoreTools {
	public static final void registerHeartBeat(Engine engine, HeartBeatListener obj) {
		Handler h = engine.getHandler(HeartBeat.REG_NAME);
		if((h!=null)&&(h instanceof HeartBeat)){
			HeartBeat hb = (HeartBeat)h;
			hb.registerObject(obj);
		}
	}
	public static final void unRegisterHeartBeat(Engine engine, HeartBeatListener obj) {
		Handler h = engine.getHandler(HeartBeat.REG_NAME);
		if((h!=null)&&(h instanceof HeartBeat)){
			HeartBeat hb = (HeartBeat)h;
			hb.unRegisterObject(obj);
		}
	}
}
