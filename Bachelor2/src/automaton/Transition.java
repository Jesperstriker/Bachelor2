package automaton;

import java.util.HashSet;
import java.util.Set;

import automaton.components.*;

public class Transition {
	private Policy p;
	private Connection c;
	private Set<Node> s;
	private String name;
	
	public Transition(Policy p, String name)
	{
		s = new HashSet<Node>();
		this.p = p;
		this.name = name;
	}

	public Policy getPolicy() {return p;}
	public Connection getConnection() {return c;}
	public Set<Node> getSet() {return s;}
	public void add (Node n) {s.add(n);}
	public void addAll (Set<Node> n) {s.addAll(n);}
	public String getName() {return name;}

}
