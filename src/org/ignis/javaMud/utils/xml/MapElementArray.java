package org.ignis.javaMud.utils.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class MapElementArray {
	@XmlAttribute
    public String key;
    @XmlElement
    public String[] value;

    private MapElementArray() {
    } //Required by JAXB

    public MapElementArray(String key, String[] value) {
        this.key = key;
        this.value = value;
    }
}
