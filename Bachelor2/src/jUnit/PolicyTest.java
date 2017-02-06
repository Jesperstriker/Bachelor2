package jUnit;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import automaton.components.Policy;;


public class PolicyTest {

	//Logged policy
	Policy p1 = new Policy();
	//Unknown policy
	Policy p2 = new Policy();
	//Empty policy
	Policy p3 = new Policy();	
	
	@Before
	public void setUp() {
		p1.addAction("Move_Log");
		p1.addCredential("Card");
		p1.addCredential("Pin");
		p1.setName("pol001");
		
		p2.addAction("Move");
		p2.addCredential("Pin");
		p2.setName("pol002");
		
		p3.setName("epsillon");
	}
	
	@Test
	public void isLogged() {
		//Only p1 should evaluate to true, since it the value is depended on if the action contains "Log".
		assertTrue(p1.isLogged());
		assertFalse(p2.isLogged());
		assertFalse(p3.isLogged());
	}
	
	@Test
	public void isEmpty() {
		//Only p3 should evaluate to true since it depends on the name of the policy. It should be epsilon
		assertFalse(p1.isEmpty());
		assertFalse(p2.isEmpty());
		assertTrue(p3.isEmpty());
	}
	
	@Test
	public void Equals() {
		//Equality for policies are name dependent, since it specifies where it is present in the automaton.
		Policy p4 = new Policy();
		p4.setName("pol001");
		
		assertTrue(p4.equals(p1));
		assertFalse(p4.equals(p2));
		assertFalse(p4.equals(p3));
	}
	
	@Test
	public void getName() {
		assertEquals(p1.getName(),"pol001");
		assertFalse(p1.getName().equals("pol002"));
	}
	
	@Test
	public void addCredential() {
		HashSet<String> cred = p1.getCredentials();
		assertTrue(cred.equals(p1.getCredentials()));
		p1.addCredential("Admin");		
		assertTrue(p1.getCredentials().contains("Admin"));
		assertTrue(p1.getCredentials().contains("Card"));
		assertTrue(p1.getCredentials().contains("Pin"));
		assertTrue(p1.getCredentials().size() == 3);
	}
	
	@Test
	public void addAction() {
		HashSet<String> action = p1.getActions();
		assertTrue(action.equals(p1.getActions()));
		p1.addAction("In");		
		assertTrue(p1.getActions().contains("In"));
		assertTrue(p1.getActions().contains("Move_Log"));
		assertTrue(p1.getActions().size() == 2);
	}
	
	@Test
	public void setName() {
		assertTrue(p1.getName().equals("pol001"));
		p1.setName("pol004");
		assertTrue(p1.getName().equals("pol004"));
		assertFalse(p1.getName().equals("pol001"));
	}
}
