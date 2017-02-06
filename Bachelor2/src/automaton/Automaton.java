package automaton;

import java.util.*;

import automaton.components.*;

public class Automaton {
	private HashMap<String,Node> allNodes = new HashMap<String,Node>();
	private Node startNode;
	
	public void setStart(Node n) {startNode = n;}
	public Node getStart() {return startNode;}
	
	public HashMap<String,Node> getAllNodes() {return allNodes;}
	public void setAllNodes(HashMap<String,Node> allNodes) {this.allNodes = allNodes;}
	public void addNode(Node n)
	{
		allNodes.put(n.getName(),n);
	}
	public void removeNode(Node n)
	{
		allNodes.remove(n.getName());
	}
	public String toString()
	{
		String s = "Automata: \n";
		for (Node n : allNodes.values())
		{
			s += n.toString();
		}
		return s;
	}

	public boolean canTransition(Node position, Node newPos) 
	{
		return allNodes.containsKey(position.getName()) && allNodes.containsKey(newPos.getName());
	}
	
	public boolean hasTailgated(Node position, Node newPos)
	{
		Node pos1 = allNodes.get(position.getName());
		Node pos2 = allNodes.get(newPos.getName());

		if (pos1 == null || pos2 == null) { return true; }
		
		for (Connection c : pos1.getSuccessors().values())
		{
			if (c.getNode().equals(pos2))
			{
				return false;
			}
		}
		return true;
	}
}