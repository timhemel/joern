package octopus.analysis;

import java.util.Iterator;
import org.apache.tinkerpop.gremlin.structure.Edge;
import octopus.analysis.Analyzer;

public class MFPAlgorithm {

	Analyzer analyzer;
	
	public MFPAlgorithm(Iterator<Edge> edges, Analyzer analyzer) {
		this.analyzer = analyzer;
		while (edges.hasNext()) {
			Edge edge = edges.next();
			this.analyzer.addVertex(edge.inVertex());
			this.analyzer.addVertex(edge.outVertex());
		}
	}
	public void run() {
		analyzer.init();
	}
}
