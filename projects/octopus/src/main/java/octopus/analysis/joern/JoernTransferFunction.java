package octopus.analysis.joern;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.Direction;
import octopus.analysis.Lattice;
import octopus.analysis.TransferFunction;
import octopus.analysis.Evaluator;

import java.util.stream.StreamSupport;
import java.util.Spliterators;
import java.util.Spliterator;
import java.util.Optional;

public abstract class JoernTransferFunction extends TransferFunction {
	// base class
	public JoernTransferFunction(Vertex v) {
		super(v);
	}

	public static Vertex getChildWithProperty(Vertex v, String key, String value) {
		Optional<Vertex> child =
			StreamSupport.stream(
				Spliterators.spliteratorUnknownSize(
					v.vertices(Direction.OUT,"IS_AST_PARENT"),
					Spliterator.ORDERED),
				false)
			.filter(x -> x.value(key) == value).findFirst();
		
		if (child.isPresent()) {
			return child.get();
		} else {
			return null;
		}
	}

	public static Vertex getChildWithNumber(Vertex v, String childNum) {
		return getChildWithProperty(v,"childNum",childNum);
	}

	public abstract Lattice eval(Evaluator e,Lattice analysis);
}

