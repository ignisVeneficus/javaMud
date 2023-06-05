package org.ignis.javaMud.Mud.handlers;

import org.ignis.javaMud.Mud.Engine;

public interface Handler {
	public void init(Engine e);
	public void dest();
}
