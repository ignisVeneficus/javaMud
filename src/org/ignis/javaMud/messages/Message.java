package org.ignis.javaMud.messages;

public class Message {
	private Event event;
	private RoomDescription room;
	private PlayerStatus status;
	private Item[] inventory;
	private Map map;
	
	public Message() {
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}
	public static final Message createTextMessage(String[] text) {
		Event e = new Event();
		e.setText(text);
		Message ret = new Message();
		ret.setEvent(e);
		return ret;
	}

	public RoomDescription getRoom() {
		return room;
	}

	public void setRoom(RoomDescription room) {
		this.room = room;
	}

	public PlayerStatus getStatus() {
		return status;
	}

	public void setStatus(PlayerStatus status) {
		this.status = status;
	}

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}
	public static final Message createTextMessage(String msg) {
		String[] lines = msg.split("\\\n",-1);
		return createTextMessage(lines);
	}

	public Item[] getInventory() {
		return inventory;
	}

	public void setInventory(Item[] inventory) {
		this.inventory = inventory;
	}
}
