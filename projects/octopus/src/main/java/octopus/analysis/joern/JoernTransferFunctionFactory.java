package octopus.analysis.joern;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import java.util.Map;
import java.util.AbstractMap.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.stream.Stream;

import octopus.analysis.joern.*;

public class JoernTransferFunctionFactory {

	private static final Map<String,Class<?>> xferMap = Stream.of(
		new SimpleEntry<String,Class<?>>("Condition",JoernTransferFunctionCondition.class),
		new SimpleEntry<String,Class<?>>("CFGEntryNode",JoernTransferFunctionCFGEntryNode.class),
		new SimpleEntry<String,Class<?>>("CFGExitNode",JoernTransferFunctionCFGExitNode.class)
	).collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));

	public static JoernTransferFunction create(Vertex v) {
		return new JoernTransferFunction(v);
	}

}


