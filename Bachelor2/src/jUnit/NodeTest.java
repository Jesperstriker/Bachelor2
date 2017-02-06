package jUnit;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import automaton.components.*;
import data.access.Log;

public class NodeTest {
	
	Node n1 = new Node("n1");
	Node n2 = new Node("n2");
	Node n3 = new Node("n3");
	
	Policy p1 = new Policy();
	Policy p2 = new Policy();
	Policy p3 = new Policy();
	
	Connection c1;
	Connection c2;
	Connection c3;
	
	@Before
	public void setUp() {
		p1.addAction("Move_Log");
		p1.addCredential("Card");
		p1.addCredential("Pin");
		p1.setName("pol001");
		
		p2.addAction("Move_Log");
		p2.addCredential("Card");
		p2.setName("pol002");
		
		p3.addAction("Move");
		p3.addCredential("Pin");
		p3.setName("pol003");
		
		c1 = new Connection(p1,n2);
		c2 = new Connection(p3,n3);
		c3 = new Connection(p2,n1);
		
		n1.addSuccessor(n2.getName(), c1);
		n2.addSuccessor(n3.getName(), c2);
		n2.addSuccessor(n1.getName(), c3);
		
	}
	
	@Test
	public void addRemoveSuccessor() {
		assertTrue(n1.getSuccessors().size() == 1);
		//Verify that adding a successor with the same name do not add another successor
		n1.addSuccessor(n2.getName(), c2);
		assertTrue(n1.getSuccessors().size() == 1);
		
		//Remove successor
		n1.addSuccessor(n3.getName(), c2);
		assertTrue(n1.getSuccessors().size() == 2);
		
		boolean b = true;
		for(String s : n1.getSuccessors().keySet())
		{
			if(s.equals(n2.getName())|| s.equals(n3.getName()))
			{
				continue;
			} else {
				b = false;
			}
		}
		assertTrue(b);		
		
		n1.removeSuccessor(n3.getName());
		assertTrue(n1.getSuccessors().size() == 1);	
	}

	@Test
	public void equals() {
		Node n = new Node("node");
		assertFalse(n1.equals(n));
		n.setName(n1.getName());
		assertTrue(n1.equals(n));
	}
	
	@Test
	public void canMerge() {
		assertFalse(n1.canMerge(n2));
		assertFalse(n1.canMerge(n3));
		
		Policy p4 = new Policy();
		p4.setName("epsillon");
		HashSet<Policy> p = new HashSet<Policy>();
		p.add(p4);
		c1.addPolicies(p);
		
		assertTrue(n1.canMerge(n2));
	}
	
	@Test
	public void containsNode() {
		Log l = new Log("actorId",n1.getName(),n2.getName(),p1.getName(),3);
		assertTrue(n1.containsNode(l));
		assertFalse(n2.containsNode(l));
	}
	
	@Test
	public void getSuccessorByLog() {
		Log l = new Log("actorId",n1.getName(),n2.getName(),p1.getName(),3);
		Node n = n1.getSuccessorByLog(l);
		assertEquals(n2,n);
		assertFalse(n1.equals(n));
	}
}
