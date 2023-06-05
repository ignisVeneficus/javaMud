package org.ignis.javaMud.server;

import org.springframework.web.socket.WebSocketSession;

public interface UserFactory {
	public User getUser(WebSocketSession session);
}
