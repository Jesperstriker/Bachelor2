package automaton.components;

import java.util.*;

import data.access.Log;

public class Node {
	
	private HashMap<String,Connection> successors;
	
	private String name;
	
	public Node (String name)
	{
		this.name = name;
		successors = new HashMap<String,Connection>();		
	}
	
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public HashMap<String,Connection> getSuccessors() {return successors;}
	
	public void addSuccessor(String n, Connection c) 
	{
		if (successors.containsKey(n))
		{
			successors.get(n).addPolicies(c.getPolicies());
		}
		else{successors.put(n, c);}
	}	
	//WARNING this method will remove all existing successors
	public void setSuccessors (HashMap<String, Connection> s) {successors = s;}

	public String toString()
	{
		String s = "Node: '" + name + "' has following connections \n";
		
		for (Connection c : successors.values()) 
		{
			 s += c.toString() +"\n";
		}
		return s;
	}
	
	public boolean equals(Object o)
	{
		return (o instanceof Node) && (((Node) o).getName()).equals(this.getName());
	}
	
	public int hashCode() {
	    return name.hashCode();
	}
	
	public boolean canMerge(Node n)
	{
		return (successors.containsKey(n.getName()) && successors.get(n.getName()).hasEmptyPolicy());
	}

	public void removeSuccessor(String name) {
		successors.remove(name);
		
	}
	public boolean containsNode(Log log)
	{
		if (name.contains(log.getFrom()))
		{
			for (Connection c : successors.values())
			{
				if (c.getNode().getName().contains(log.getTo()))
				{
					for (Policy p : c.getPolicies())
					{
						if (p.getName().equals(log.getPolicy()))
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	public Node getSuccessorByLog(Log log)
	{
		if (name.contains(log.getFrom()))
		{
			for (Connection c : successors.values())
			{
				if (c.getNode().getName().contains(log.getTo()))
				{
					for (Policy p : c.getPolicies())
					{
						if (p.getName().equals(log.getPolicy()))
						{
							return c.getNode();
						}
					}
				}
			}
		}

		return null;
	}
}
