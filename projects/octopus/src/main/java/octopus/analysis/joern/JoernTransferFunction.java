package octopus.analysis.joern;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.Direction;
import octopus.analysis.Lattice;
import octopus.analysis.TransferFunction;

import java.util.stream.StreamSupport;
import java.util.Spliterators;
import java.util.Spliterator;

public class JoernTransferFunction extends TransferFunction {
	// base class
	public JoernTransferFunction(Vertex v) {
		super(v);
	}

	public static Vertex getChildWithProperty(Vertex v, String key, String value) {
		Vertex child =
			StreamSupport.stream(
				Spliterators.spliteratorUnknownSize(
					v.vertices(Direction.OUT,"IS_AST_PARENT"),
					Spliterator.ORDERED),
				false)
			.filter(x -> x.value(key) == value).findFirst().get();
		return child;
	}

	public static Vertex getChildWithNumber(Vertex v, String childNum) {
		return getChildWithProperty(v,"childNum",childNum);
	}

	public Lattice eval(Lattice v) {
		return v;
	}
}

