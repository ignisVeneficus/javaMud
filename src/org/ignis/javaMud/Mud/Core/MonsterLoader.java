package org.ignis.javaMud.Mud.Core;

/**
 * 
 * @author Csaba Toth
 * monster (living, ami nem jatekos) betolto.
 * Ezen keresztul tortenik a peldanyositas, szamontartja az instance-okat.
 * Ha meg van adva a max mennyiseg 
 *
 */
public class MonsterLoader {
	private String file;
	private int qty;
	private int max;
	public MonsterLoader(String file, int maxQty) {
		super();
		this.file = file;
		this.max = maxQty;
		this.qty = 0;
	}
	public String getFile() {
		return file;
	}
	public int getQty() {
		return qty;
	}
	
	public boolean canCreateNewOne(){
		synchronized (this) {
			if((max>0) && (qty==max)) return false;
		}
		return true;
	}
	public void addNew() {
		synchronized (this) {
			qty++;
		}
	}
	public void substractOld() {
		synchronized (this) {
			qty--;
		}
	}
	
}
