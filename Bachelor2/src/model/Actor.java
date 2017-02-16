package model;

import java.util.HashSet;
import java.util.Set;

import automaton.Automaton;
import automaton.components.Node;

public class Actor {
	private String name;
	private Role role;
	private Automaton a;
	private Node position;
	private int timestamp;
	private Set<Node> locations = new HashSet<Node>();
	
	public Actor(String name, Role role)
	{
		this.name = name;
		this.role = role;
	}

	public Actor (String name)
	{
		this.name = name;
	}
	public Actor (String name,Role role, Node location){
		this.name = name;
		this.role = role;
		this.locations.add(location);
	}
	
	public Role getRole() { return role; }
	public void setRole(Role role) { this.role = role; }
	public Automaton getAutomaton() { return a; }
	public void setAutomaton(Automaton a) { this.a = a; position = a.getStart(); }
	public String getName() { return name; }
	public Node getPosition() { return position; }
	public void setPosition(Node position) { this.position = position; }
	public int getTimestamp() {return timestamp;}
	public void setTimestamp(int timestamp) {this.timestamp = timestamp;}
	
	public Set<Node> getLocations(){return this.locations;}
	public void addLocation(Node location){this.locations.add(location);}
	public void resetLocation(Node location)
	{
		Set<Node> loc = new HashSet<Node>();
		loc.add(location);
		this.locations = loc;
	}
	
	public String toString()
	{
		return name + ", " + role.toString();
	}
	
	public boolean canTransition(Node newPos)
	{
		return a.canTransition(position,newPos);
	}
	public boolean hasTailgated(Node newPos)
	{
		return a.hasTailgated(position, newPos);
	}
	//Made by Jesper starts --->
	public String locationsToString(){
			boolean flag = true;
			String s1 = "";
			for (Node location : this.locations) {
				if (flag) {
					s1 = s1+location.getName();
					flag = false;
				} else {
					s1 = s1 + "," + location.getName();
				}
			}
			return s1;
	}

	//Made by Jesper ends <-------	

}

