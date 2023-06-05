package org.ignis.javaMud.messages;

public class Item {
	private String weight;
	private String name;
	private boolean wielded;
	private boolean holded;
	private String type;
	private Item(String name, String weight, boolean wielded, boolean holded, String type) {
		super();
		this.weight = weight;
		this.name = name;
		this.wielded = wielded;
		this.holded = holded;
		this.type = type;
	}
	public String getWeight() {
		return weight;
	}
	public String getName() {
		return name;
	}
	public boolean isWielded() {
		return wielded;
	}
	public boolean isHolded() {
		return holded;
	}
	public String getType() {
		return type;
	}
	
	
}
