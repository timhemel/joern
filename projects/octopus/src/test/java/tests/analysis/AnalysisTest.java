package tests.analysis;

import org.junit.Test;
import static org.junit.Assert.*;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.util.detached.DetachedEdge;
import java.util.HashSet;
import java.util.Arrays;
import java.util.ArrayList;

public class AnalysisTest {

	@Test
	public void testBuildEmptyCFG() {
		ACFG cfg = new ACFG();
		assertEquals(new HashSet<DetachedEdge>(), cfg.edges());
	}

	@Test
	public void testBuildVertexWithoutProperties() {
		AVertex vertex = new AVertex().withId(12);
		assertEquals(12,vertex.get().id());
	}

	@Test
	public void testBuildVertexWithProperties() {
		AVertex vertex = new AVertex().withId(12)
			.with("code","a = 1;")
			.with("type","AssignmentExpression");
		assertEquals("AssignmentExpression",vertex.get().value("type"));
		assertEquals("a = 1;",vertex.get().value("code"));
	}

	@Test
	public void testBuildVertexWithPropertyLists() {
		AVertex vertex = new AVertex().withId(12)
			.with("code",new ArrayList<String>(Arrays.asList("a = 1;","b=2;")));
		assertEquals(Arrays.asList("a = 1;","b=2;"),vertex.get().value("code"));
	}

	@Test
	public void testBuildAST() {
		AGraph graph = new AGraph()
			.withNode(new AVertex().withId(1).with("code","a = 1;").with("type","AssignmentExpression").get())
			.withNode(new AVertex().withId(2).with("code","a").with("type","Identifier").with("childNum","0").get())
			.withNode(new AVertex().withId(3).with("code","1").with("type","PrimaryExpression").with("childNum","1").get())
			.withEdge("IS_AST_PARENT",1,2)
			.withEdge("IS_AST_PARENT",1,3);

		assertEquals(3,graph.nodes().size());
		assertEquals("a",graph.nodes().get(2).value("code"));
		assertEquals(2,Arrays.asList(graph.nodes().get(1).edges(Direction.OUT)).size());
		assertEquals(2,graph.nodes().get(1).edges(Direction.OUT).next().inVertex().id());
	}


}
