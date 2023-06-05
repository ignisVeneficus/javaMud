package org.ignis.javaMud.utils.xml;

import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class MapAdapterArray extends XmlAdapter<MapElementArray[], Map<String, String[]>> {
    public MapAdapterArray() {
    }

    public MapElementArray[] marshal(Map<String, String[]> arg0) throws Exception {
         MapElementArray[] mapElements = new MapElementArray[arg0.size()];
        int i = 0;
        for (Map.Entry<String, String[]> entry : arg0.entrySet())
            mapElements[i++] = new MapElementArray(entry.getKey(), entry.getValue());

        return mapElements;
    }

    public Map<String, String[]> unmarshal(MapElementArray[] arg0) throws Exception {
        Map<String, String[]> r = new TreeMap<>();
        for (MapElementArray mapelement : arg0)
            r.put(mapelement.key, mapelement.value);
        return r;
    }
}
