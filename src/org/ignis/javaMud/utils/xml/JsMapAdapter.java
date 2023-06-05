package org.ignis.javaMud.utils.xml;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.ignis.javaMud.Mud.StringUtil;

public class JsMapAdapter extends XmlAdapter<JsMapElement[], Map<String, String>> {
	
    public JsMapAdapter() {
    }

    public JsMapElement[] marshal(Map<String, String> arg0) throws Exception {
         JsMapElement[] mapElements = new JsMapElement[arg0.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : arg0.entrySet())
            mapElements[i++] = new JsMapElement(entry.getKey(), entry.getValue());

        return mapElements;
    }

    public Map<String, String> unmarshal(JsMapElement[] arg0) throws Exception {
        Map<String, String> r = new HashMap<String, String>();
        for (JsMapElement mapelement : arg0)
            r.put(StringUtil.exEkezet(mapelement.name), mapelement.function);
        return r;
    }

}
