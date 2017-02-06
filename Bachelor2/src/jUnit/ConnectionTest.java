package jUnit;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import automaton.components.*;
import model.Role;

public class ConnectionTest {
	
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
	public void hasEmptyPolicy() {
		assertFalse(c1.hasEmptyPolicy());
		assertFalse(c2.hasEmptyPolicy());
		assertFalse(c3.hasEmptyPolicy());
		Policy p = new Policy();
		p.setName("epsillon");
		Connection c = new Connection(p, n1);
		assertTrue(c.hasEmptyPolicy());
	}

	@Test
	public void equals() {
		Node n = new Node("n");
		Policy p = new Policy();
		Connection c = new Connection(p,n);
		assertFalse(c.equals(c1));
		
		n.setName(n2.getName());
		assertFalse(c.equals(c1));
		
		c = new Connection(p1,n);
		assertTrue(c.equals(c1));
	}
	
	@Test
	public void containsPolicy() {
		assertFalse(c1.containsPolicy(p2));
		assertFalse(c1.containsPolicy(p3));
		assertTrue(c1.containsPolicy(p1));
	}
	
	@Test
	public void getFirstPolicy() {
		assertTrue(c1.getFirstPolicy().equals(p1));
		HashSet<Policy> p = new HashSet<Policy>();
		p.add(p2);
		p.add(p3);
		c1.addPolicies(p);
		assertTrue(c1.getFirstPolicy().equals(p1) || 
				c1.getFirstPolicy().equals(p2) ||
				c1.getFirstPolicy().equals(p3));
		assertTrue(c1.getFirstPolicy().equals(p3));
		assertFalse(c1.getFirstPolicy().equals(p2));
		assertFalse(c1.getFirstPolicy().equals(p1));
	}
	
	@Test
	public void hasRole() {
		Role admin = new Role("admin",null);
		Role employee = new Role("employee",admin);
		
		HashMap<String,Role> m = new HashMap<String,Role>();
		m.put("employee", employee);
		m.put("admin",admin);
		
		assertTrue(c2.hasRole(admin, m));
		assertTrue(c2.hasRole(employee, m));
		
		
		assertFalse(c3.hasRole(employee, m));
		assertTrue(c3.hasRole(admin, m));	
	}
	
	@Test
	public void isPersonSpecific() {
		assertTrue(c1.isPersonSpecific());
		assertFalse(c2.isPersonSpecific());
		assertFalse(c3.isPersonSpecific());
	}
}
