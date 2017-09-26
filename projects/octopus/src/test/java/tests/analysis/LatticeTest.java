package tests.analysis;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Map;
import java.util.HashMap;

import octopus.analysis.Lattice;
import octopus.analysis.SignLattice;
import octopus.analysis.MapLattice;
import static octopus.analysis.SignLattice.*;

public class LatticeTest {

	@Test
	public void testJoinOnSignLattice() {
		assertEquals(ZoP,ZoP.join(BOT));
		assertEquals(ZoP,BOT.join(ZoP));
	}

	@Test
	public void testLeqOnSignLattice() {
		assertTrue(BOT.leq(ZoP));
		assertTrue(ZoP.leq(ZoP));
		assertFalse(ZoN.leq(ZoP));
	}

	@Test
	public void testJoinOnMapLatticeDifferentVars() {
		MapLattice<Lattice<SignLattice>> m1 = new MapLattice<Lattice<SignLattice>>();
		MapLattice<Lattice<SignLattice>> m2 = new MapLattice<Lattice<SignLattice>>();
		m1.put("x", NEG);
		m2.put("y", NEG);
		MapLattice<Lattice<SignLattice>> m_expected = new MapLattice<Lattice<SignLattice>>();
		m_expected.put("x", NEG);
		m_expected.put("y", NEG);
		assertEquals(m_expected, m1.join(m2));
	}

	@Test
	public void testJoinOnMapLatticeSharedVars() {
		MapLattice<Lattice<SignLattice>> m1 = new MapLattice<Lattice<SignLattice>>();
		MapLattice<Lattice<SignLattice>> m2 = new MapLattice<Lattice<SignLattice>>();
		m1.put("x", NEG);
		m2.put("x", NoP);
		MapLattice<Lattice<SignLattice>> m_expected = new MapLattice<Lattice<SignLattice>>();
		m_expected.put("x", NoP);
		assertEquals(m_expected, m1.join(m2));
	}
}

