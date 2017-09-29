package octopus.analysis;

import octopus.analysis.Lattice;

public enum SignLattice implements Lattice<SignLattice> {
	BOT, NEG, ZER, POS, ZoN, ZoP, NoP, TOP;
	static SignLattice join_table[][] = {
		{ BOT, NEG, ZER, POS, ZoN, ZoP, NoP, TOP },
		{ NEG, NEG, ZoN, NoP, ZoN, TOP, NoP, TOP },
		{ ZER, ZoN, ZER, ZoP, ZoN, ZoP, TOP, TOP },
		{ POS, NoP, ZoP, POS, TOP, ZoP, NoP, TOP },
		{ ZoN, ZoN, ZoN, TOP, ZoN, TOP, TOP, TOP },
		{ ZoP, TOP, ZoP, ZoP, TOP, ZoP, TOP, TOP },
		{ NoP, NoP, TOP, NoP, TOP, TOP, NoP, TOP },
		{ TOP, TOP, TOP, TOP, TOP, TOP, TOP, TOP }
	};

	public SignLattice join(SignLattice other) {
		return join_table[this.ordinal()][other.ordinal()];
	}

	public boolean leq(SignLattice other) {
		return this.join(other) == other;
	}

	public SignLattice bottom() {
		return BOT;
	}
}
