package octopus.analysis;

import java.util.HashMap;
import java.util.HashSet;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import octopus.analysis.Lattice;

public abstract class Analyzer {
	protected HashMap<Vertex,Lattice> results;
	protected HashSet<Vertex> vertices;

	protected Analyzer() {
		results = new HashMap<Vertex,Lattice>();
		vertices = new HashSet<Vertex>();
	}

	public void addVertex(Vertex v) {
		vertices.add(v);
	}

	public void init() {
		for(Vertex vertex: vertices) {
			results.put(vertex, bottom());
		}
	}

	public Lattice get(Vertex v) {
		return results.get(v);
	}

	protected abstract Lattice bottom();
}

