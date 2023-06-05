package org.ignis.javaMud.Mud.dataholder;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
/**
 * Olyan lista, amibol veletlenszeruen valaszt a rendszer
 * @author Ignis
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class RandomObject {
	@XmlElement(name="objRef")
	private List<ObjectRef> refs;
	
}
