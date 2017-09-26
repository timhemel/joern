package tests.analysis;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.apache.tinkerpop.gremlin.structure.Vertex;
// import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.structure.util.detached.DetachedVertex;
import org.apache.tinkerpop.gremlin.structure.util.detached.DetachedVertexProperty;

public class AVertex {
	String label;
	Object id;
	Map<String,Object> properties;

	AVertex() {
		id = null;
		label = "";
		properties = new HashMap<String,Object>();
	}

	AVertex withId(Object id) {
		this.id = id;
		return this;
	}
	AVertex with(String key, Object value) {
		if (value.getClass().isArray()) {
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			int i = 0;
			for(Object v: (List<?>) value) {
				Map<String,Object> propertyValueMap = new HashMap<String,Object>();
				propertyValueMap.put(String.valueOf(i),v);
				
				list.add(propertyValueMap);
				i++;
			}
			properties.put(key,list);
		} else {
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			Map<String,Object> propertyValueMap = new HashMap<String,Object>();
			propertyValueMap.put("1",properties.get(key));
			list.add(propertyValueMap);
			properties.put(key,list);
		}
		return this;
	}

	Vertex get() {
		Vertex vertex = new DetachedVertex(id, label, properties);
		return vertex;
	}
}

