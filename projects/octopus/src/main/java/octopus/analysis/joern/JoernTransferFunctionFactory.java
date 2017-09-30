package octopus.analysis.joern;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import java.util.Map;
import java.util.AbstractMap.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.stream.Stream;

import octopus.analysis.joern.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class JoernTransferFunctionFactory {

	public static JoernTransferFunction create(Vertex v) {
		try {
			Class<? extends JoernTransferFunction> klass = (Class<? extends JoernTransferFunction>) Class.forName("octopus.analysis.joern.JoernTransferFunction"+v.value("type"));
			Constructor<? extends JoernTransferFunction> ctor = klass.getConstructor(Vertex.class);
			JoernTransferFunction xfer = ctor.newInstance(v);
			return xfer;
		} catch (ClassNotFoundException e) {
			System.out.println(e);
			e.printStackTrace(System.out);
			return null;
		} catch (NoSuchMethodException e) {
			System.out.println(e);
			e.printStackTrace(System.out);
			return null;
		} catch (InstantiationException e) {
			System.out.println(e);
			e.printStackTrace(System.out);
			return null;
		} catch (IllegalAccessException e) {
			System.out.println(e);
			e.printStackTrace(System.out);
			return null;
		} catch (InvocationTargetException e) {
			System.out.println(e);
			e.printStackTrace(System.out);
			return null;
		}
	}

}


