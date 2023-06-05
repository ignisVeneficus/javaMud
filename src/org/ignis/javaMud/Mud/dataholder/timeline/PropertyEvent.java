package org.ignis.javaMud.Mud.dataholder.timeline;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import org.ignis.javaMud.Mud.Core.Object;
import org.ignis.javaMud.Mud.utils.StringFunctions;
/**
 * Olyan tortenes, mely hatasara az adott objektum valamelyik tulajdonsaga megvaltozik
 * @author Ignis
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class PropertyEvent{
	@XmlAttribute(name="field")
	private String field;
	
	@XmlValue
	private String value;
	
	public void init(Object obj) {
		value = StringFunctions.process(obj, value);
	}
	
	public void process(Object obj) {
		obj.set(field, value);
	}

}
