package automaton.components;

import java.util.HashMap;
import java.util.HashSet;

import model.Role;

public class Connection 
{
	private HashSet<Policy> p = new HashSet<Policy>();
	private Node n;
	private int time;
	public Connection(Policy p, Node n)
	{
		this.p.add(p);
		this.n = n;
	}
	
	public HashSet<Policy> getPolicies() {return p;}
	public void setPolicy(HashSet<Policy> p) {this.p = p;}
	public void addPolicies(HashSet<Policy> p) {this.p.addAll(p);}

	public Node getNode() {return n;}
	public void setNode(Node n) {this.n = n;}
	
	public boolean hasEmptyPolicy() 
	{
		for (Policy pol : p)
		{
			if (pol.isEmpty())
			{
				return true;
			}
		}
		return false;
	}
	
	public String toString() {
		String s = "    Connection: '" + n.getName() + "':\n" + p.toString() + " time: "+time;
		return  s;
	}
	
	public void setTime(int time){
		this.time = time;
	}
	public int getTime(){
		return this.time;
	}
	
	public boolean equals(Connection c){
		return n.getName().equals(c.getNode().getName()) && p.equals(c.getPolicies());
	}

	public boolean containsPolicy(Policy p2) {
		for (Policy pol : p)
		{
			if (pol.getName().equals(p2.getName()))
			{
				return true;
			}
		}
		return false;
	}
	
	public Policy getFirstPolicy()
	{
		for (Policy pol : p)
		{
			return pol;
		}
		return null;
	}

	public boolean hasRole(Role r, HashMap<String, Role> rh)
	{
		boolean b = true;
		for (Policy pol : p)
		{
			for (String s : pol.getCredentials())
			{
				if (rh.containsKey(s))
				{
					b = rh.get(s).contains(r);
				}
			}
		}
		return b;
	}

	public boolean isPersonSpecific() {
		for (Policy pol : p)
		{
			for (String s : pol.getCredentials())
			{
				if (s.toLowerCase().contains("id"))
				{
					return true;
				}
			}	
		}
		return false;
	}

}
