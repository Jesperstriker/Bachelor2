package jUnit;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import automaton.Automaton;
import automaton.components.*;

public class AutomatonTest {
	
	Automaton a = new Automaton();
	Node n1 = new Node("n1");
	Node n2 = new Node("n2");
	Node n3 = new Node("n3");
	Node n4 = new Node("n4");
	HashMap<String,Node> allNodes = new HashMap<String,Node>();
	
	
	Policy p1 = new Policy();
	Policy p2 = new Policy();
	Policy p3 = new Policy();
	
	Connection c1;
	Connection c2;
	Connection c3;
	
	@Before
	public void setUp() {
		allNodes.put(n1.getName(),n1);
		allNodes.put(n2.getName(),n2);
		allNodes.put(n3.getName(),n3);
		a.setAllNodes(allNodes);
		
		p1.addAction("Move_Log");
		p1.addCredential("Card");
		p1.addCredential("Pin");
		p1.addCredential("Id");
		p1.setName("pol001");
		
		p2.addAction("Move_Log");
		p2.addCredential("Card");
		p2.addCredential("admin");
		p2.setName("pol002");
		
		p3.addAction("Move");
		p3.addCredential("Pin");
		p3.addCredential("employee");
		p3.setName("pol003");
		
		c1 = new Connection(p1,n2);
		c2 = new Connection(p3,n3);
		c3 = new Connection(p2,n1);
		
		n1.addSuccessor(n2.getName(), c1);
		n2.addSuccessor(n3.getName(), c2);
		n2.addSuccessor(n1.getName(), c3);
	}

	@Test
	public void canTransition() {
		//This only depends on the fact that the nodes appear in the automaton
		assertTrue(a.canTransition(n1, n2));
		assertTrue(a.canTransition(n2, n3));
		assertTrue(a.canTransition(n1, n3));
		
		assertFalse(a.canTransition(n1, n4));
		assertFalse(a.canTransition(n4, n1));
	}
	
	@Test
	public void hasTailgated() {
		//If either node do not exist in the automaton it should return true.
		//This check is mostly used since it checks in the role specific DFA
		assertTrue(a.hasTailgated(n1, n4));
		
		//No direct connection between n1 and n3
		assertTrue(a.hasTailgated(n1, n3));
		//Direct connection between n1 and n2
		assertFalse(a.hasTailgated(n1, n2));
	}
}