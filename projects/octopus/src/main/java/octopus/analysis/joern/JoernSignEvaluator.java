package octopus.analysis.joern;

import octopus.analysis.joern.JoernEvaluator;
import octopus.analysis.Lattice;
import octopus.analysis.MapLattice;
import octopus.analysis.SignLattice;
import static octopus.analysis.SignLattice.*;

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
		return analysis.getOrDefault(varname,BOT);
	}

	public Lattice<SignLattice> evalPrimaryExpression(String value) {
		// Joern does not store type information in the AST
		// assume that we only work with integers
		int v = new Integer(value).intValue();
		if (v < 0) return NEG;
		if (v > 0) return POS;
		return ZER;
	}

	static SignLattice transferTablePlus[][] = {
		{ BOT, BOT, BOT, BOT, BOT, BOT, BOT, BOT },
		{ BOT, NEG, NEG, TOP, NEG, TOP, TOP, TOP },
		{ BOT, NEG, ZER, POS, ZoN, ZoP, NoP, TOP },
		{ BOT, TOP, POS, POS, TOP, POS, TOP, TOP },
		{ BOT, NEG, ZoN, TOP, ZoN, TOP, TOP, TOP },
		{ BOT, TOP, ZoP, POS, TOP, ZoP, TOP, TOP },
		{ BOT, TOP, NoP, TOP, TOP, TOP, NoP, TOP },
		{ BOT, TOP, TOP, TOP, TOP, TOP, TOP, TOP }
	};


	public Lattice<SignLattice> evalAdditiveExpression(Lattice value1, Lattice value2) {
		return transferTablePlus[((SignLattice)value1).ordinal()][((SignLattice)value2).ordinal()];
	}

	static SignLattice transferTableTimes[][] = {
		{ BOT, BOT, ZER, BOT, BOT, BOT, BOT, BOT },
		{ BOT, POS, ZER, NEG, ZoP, ZoN, NoP, TOP },
		{ ZER, ZER, ZER, ZER, ZER, ZER, ZER, ZER },
		{ BOT, NEG, ZER, POS, ZoN, ZoP, NoP, TOP },
		{ BOT, ZoP, ZER, ZoN, ZoP, ZoN, TOP, TOP },
		{ BOT, ZoN, ZER, ZoP, ZoN, ZoP, TOP, TOP },
		{ BOT, NoP, ZER, NoP, TOP, TOP, NoP, TOP },
		{ BOT, TOP, ZER, TOP, TOP, TOP, TOP, TOP }
	};

	public Lattice<SignLattice> evalMultiplicativeExpression(Lattice value1, Lattice value2) {
		return transferTableTimes[((SignLattice)value1).ordinal()][((SignLattice)value2).ordinal()];
	}
}

