package org.ignis.javaMud.utils.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class JsMapElement {
	@XmlAttribute(name = "name")
	public String name;
	@XmlAttribute(name = "fn")
	public String function;

	private JsMapElement() {
	} // Required by JAXB

	public JsMapElement(String name, String function) {
		this.name = name;
		this.function = function;
	}

}
