package org.ignis.javaMud.Mud.action;

public class ItemPointer {
	private String name;
	private int number;
	public String getName() {
		return name;
	}
	public int getNumber() {
		return number;
	}
	public ItemPointer(String name, int number) {
		super();
		this.name = name;
		this.number = number;
	}
	
}
