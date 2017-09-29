package octopus.analysis.joern;

import octopus.analysis.joern.JoernEvaluator;
import octopus.analysis.Lattice;
import octopus.analysis.MapLattice;
import octopus.analysis.SignLattice;

public class JoernSignEvaluator extends JoernEvaluator<MapLattice<Lattice<SignLattice>>> {
	public MapLattice<Lattice<SignLattice>> evalCFGEntryNode(MapLattice<Lattice<SignLattice>> analysis) {
		return analysis;
	}
	public MapLattice<Lattice<SignLattice>> evalCFGExitNode(MapLattice<Lattice<SignLattice>> analysis) {
		return analysis;
	}
	public MapLattice<Lattice<SignLattice>> evalCondition(MapLattice<Lattice<SignLattice>> analysis) {
		return analysis;
	}
	public MapLattice<Lattice<SignLattice>> evalAssignmentExpression(MapLattice<Lattice<SignLattice>> analysis, String varname, Lattice value) {
		MapLattice<Lattice<SignLattice>> newAnalysis = new MapLattice<Lattice<SignLattice>>();
		newAnalysis.putAll(analysis);
		newAnalysis.put(varname,value);
		return newAnalysis;
	}

	public Lattice<SignLattice> evalIdentifier(MapLattice<Lattice<SignLattice>> analysis, String varname) {
		return analysis.getOrDefault(varname,SignLattice.BOT);
	}

	public Lattice<SignLattice> evalPrimaryExpression(String value) {
		// Joern does not store type information in the AST
		// assume that we only work with integers
		int v = new Integer(value).intValue();
		if (v < 0) return SignLattice.NEG;
		if (v > 0) return SignLattice.POS;
		return SignLattice.ZER;
	}
}

