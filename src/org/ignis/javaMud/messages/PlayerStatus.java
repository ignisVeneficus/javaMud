package org.ignis.javaMud.messages;

public class PlayerStatus {
	private int maxHP;
	private int actHP;
	private int maxSP;
	private int actSP;
	private int maxMP;
	private int actMP;
	private int wimpy;
	
	public int getMaxHP() {
		return maxHP;
	}
	public void setMaxHP(int maxHP) {
		this.maxHP = maxHP;
	}
	public int getActHP() {
		return actHP;
	}
	public void setActHP(int actHP) {
		this.actHP = actHP;
	}
	public int getMaxSP() {
		return maxSP;
	}
	public void setMaxSP(int maxSP) {
		this.maxSP = maxSP;
	}
	public int getActSP() {
		return actSP;
	}
	public void setActSP(int actSP) {
		this.actSP = actSP;
	}
	public int getMaxMP() {
		return maxMP;
	}
	public void setMaxMP(int maxMP) {
		this.maxMP = maxMP;
	}
	public int getActMP() {
		return actMP;
	}
	public void setActMP(int actMP) {
		this.actMP = actMP;
	}
	public int getWimpy() {
		return wimpy;
	}
	public void setWimpy(int wimpy) {
		this.wimpy = wimpy;
	}
}
