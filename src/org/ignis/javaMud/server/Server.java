package org.ignis.javaMud.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class Server extends TextWebSocketHandler implements WebSocketHandler{
	private Logger LOG = LogManager.getLogger(Server.class);

	protected UserFactory userFactory;

	
	@Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
		LOG.trace("Incomming msg: " + message.getPayload());
		if(userFactory==null) {
			LOG.fatal("No userfactory!");
			return;
		}
		User user = userFactory.getUser(session);
		user.processText(message.getPayload());
    }
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus){
		if(userFactory==null) {
			LOG.fatal("No userfactory!");
			return;
		}
		User user = userFactory.getUser(session);
		user.closedConnection();
	}
	
	
	public UserFactory getUserFactory() {
		return userFactory;
	}
	public void setUserFactory(UserFactory userFactory) {
		if(LOG.isInfoEnabled()) {
			LOG.info("CONFIG: setUserFactory:" + userFactory);
		}
		this.userFactory = userFactory;
	}
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		super.afterConnectionEstablished(session);
		if(userFactory==null) {
			LOG.fatal("No userfactory!");
			return;
		}
		User user = userFactory.getUser(session);
	}
}
