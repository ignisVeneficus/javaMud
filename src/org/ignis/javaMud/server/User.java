package org.ignis.javaMud.server;

public interface User {
	public void processText(String message);
	public void closedConnection();
}
