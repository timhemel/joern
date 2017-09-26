package tests.analysis;

import org.junit.Test;
import static org.junit.Assert.*;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import java.util.HashSet;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.Spliterators;
import java.util.Spliterator;
import java.util.Iterator;

public class AnalysisTest {

	@Test
	public void testBuildVertexWithoutProperties() {
		Graph g = TinkerGraph.open();
		AVertex vertex = new AVertex().inGraph(g);
		assertEquals(0L,vertex.get().id());
	}

	@Test
	public void testBuildVertexWithProperties() {
		Graph g = TinkerGraph.open();
		AVertex vertex = new AVertex()
			.with("code","a = 1;")
			.with("type","AssignmentExpression")
			.inGraph(g);
		assertEquals("AssignmentExpression",vertex.get().value("type"));
		assertEquals("a = 1;",vertex.get().value("code"));
	}

	@Test
	public void testBuildVertexWithPropertyLists() {
		Graph g = TinkerGraph.open();
		AVertex vertex = new AVertex()
			.with("code",new ArrayList<String>(Arrays.asList("a = 1;","b=2;")))
			.inGraph(g);
		assertEquals(Arrays.asList("a = 1;","b=2;"),vertex.get().value("code"));
	}

	public static <T> ArrayList<T> toArrayList(final Iterator<T> iterator) {
		return StreamSupport.stream(
				Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
			false)
			.collect(Collectors.toCollection(ArrayList::new));
	}

	@Test
	public void testBuildAST() {
		Graph g = TinkerGraph.open();
		Vertex v0 = new AVertex().with("code","a = 1;").with("type","AssignmentExpression").inGraph(g).get();
		Vertex v1 = new AVertex().with("code","a").with("type","Identifier").with("childNum","0").inGraph(g).get();
		Vertex v2 = new AVertex().with("code","1").with("type","PrimaryExpression").with("childNum","1").inGraph(g).get();
		Edge e1 = new AnEdge().withLabel("IS_AST_PARENT").from(v0).to(v1).inGraph(g).get();
		Edge e2 = new AnEdge().withLabel("IS_AST_PARENT").from(v0).to(v2).inGraph(g).get();

		assertEquals(3,toArrayList(g.vertices()).size());
		assertEquals("a",g.vertices(v1.id()).next().value("code"));
		assertEquals(2,toArrayList(g.edges()).size());
		assertEquals(2,toArrayList(g.vertices(v0.id()).next().edges(Direction.OUT)).size());
		assertEquals(v1.id(),g.vertices(v0.id()).next().edges(Direction.OUT).next().inVertex().id());
	}


}
