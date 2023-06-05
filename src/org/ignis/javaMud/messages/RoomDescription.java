package org.ignis.javaMud.messages;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ignis.javaMud.Mud.utils.Defaults;

public class RoomDescription {
	private String[] seeing;
	private String[] smelling;
	private String[] listening;
	private String[] magic;	
	private List<String> exits;
	private List<String> items;
	private List<String> livings;
	
	public RoomDescription() {
		
	}
	// terkep
	public void setSeeing(String seeing) {
		String str = StringUtils.strip(seeing);
		if(StringUtils.isBlank(str)) return;
		this.seeing = str.split("\\n",-1);
	}
	public void setSmelling(String smelling) {
		String str = StringUtils.strip(smelling);
		if(StringUtils.isBlank(str)) return;
		this.smelling = str.split("\\n",-1);
	}
	public void setListening(String listening) {
		String str = StringUtils.strip(listening);
		if(StringUtils.isBlank(str)) return;
		this.listening = str.split("\\n",-1);
	}
	public void setMagic(String magic) {
		String str = StringUtils.strip(magic);
		if(StringUtils.isBlank(str)) return;
		this.magic = str.split("\\n",-1);
	}
	public List<String> getExits() {
		return exits;
	}
	public void setExits(List<String> exits) {
		this.exits = exits;
	}
	public List<String> getItems() {
		return items;
	}
	public void setItems(List<String> items) {
		this.items = items;
	}
	public List<String> getLivings() {
		return livings;
	}
	public void setLivings(List<String> livings) {
		this.livings = livings;
	}
	public String[] getSeeing() {
		return seeing;
	}
	public String[] getSmelling() {
		return smelling;
	}
	public String[] getListening() {
		return listening;
	}
	public String[] getMagic() {
		return magic;
	}
	public static RoomDescription createOne(org.ignis.javaMud.Mud.dataholder.RoomDescription room) {
		RoomDescription ret = new RoomDescription();
		ret.setSeeing(room.getSenseDescription(Defaults.Sense_Latas));
		ret.setSmelling(room.getSenseDescription(Defaults.Sense_Szaglas));
		ret.setListening(room.getSenseDescription(Defaults.Sense_Hallas));
		ret.setMagic(room.getSenseDescription(Defaults.Sense_Magia));
		ret.setExits(room.getExits());
		ret.setItems(room.getObjects());
		ret.setLivings(room.getLivings());
		return ret;
	}
}
