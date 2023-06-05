package org.ignis.javaMud.utils.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class JsMapList {
    public List<JsMapElement> values = new ArrayList<JsMapElement>();

    public JsMapList() {
    }  
}
