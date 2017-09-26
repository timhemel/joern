package tests.analysis;

import org.junit.Test;
import static org.junit.Assert.*;

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
		assertEquals("a = 1;",vertex.get().value("code"));
	}

	@Test
	public void testBuildAST() {
	}


}
