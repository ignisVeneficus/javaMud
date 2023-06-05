package org.ignis.javaMud.utils.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.ignis.javaMud.Mud.dataholder.outlands.StringColor;

public class StringStringColorAdapter extends XmlAdapter<String, StringColor>{

	@Override
	public StringColor unmarshal(String v) throws Exception {
		return StringColor.createFromString(v);
	}

	@Override
	public String marshal(StringColor v) throws Exception {
		return v.toString();
	}


}
