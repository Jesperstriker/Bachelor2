package automaton;
import java.util.ArrayList

;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import TREsPASS.apis.JavaAPI;
import TREsPASS.model.*;
import automaton.components.*;
import model.Actor;
import model.Role;

public class Tools {	
	public static HashMap<String,Actor> getActors(Model m, HashMap<String,Role> rolehierarchy) {
		//Returns all actors from a given TREsPASS model
		HashMap<String,Actor> actors = new HashMap<String,Actor>();
		
		for(ArrayList<String> l : JavaAPI.getPredicateValues(m, "role"))
		{
			actors.put(l.get(1),new Actor(l.get(1), rolehierarchy.get(l.get(0))));
		}
		return actors;
	}
	public static HashMap<String,Role> createRoleHierarchy(Model m)
	//Construct a role hierarchy from a given TREsPASS model
	{
		HashMap<String,Role> roles = new HashMap<String,Role>();
		HashSet<ArrayList<String>> r = JavaAPI.getPredicateValues(m, "role_hierarchy");
		
		if (!r.isEmpty())
		{
			for(ArrayList<String> l : r)
			{
				Role parent;
				Role role;
				String s0 = new String(l.get(0));
				String s1 = new String(l.get(1));
					
				if(roles.containsKey(s0)) { parent = roles.get(s0); }
				else
				{
					parent = new Role(s0);
					roles.put(s0, parent);
				}						

				if(roles.containsKey(s1)) 
				{ 
					role = roles.get(s1); 
				}
				else
				{
					role = new Role(s1,parent);
					roles.put(s1, role);
				}
		
			}
		}
		else 
		{
			return null;
		}
		
		return roles;
	}
	public static Automaton createAutomaton(Model m, Role role, boolean simplify, HashMap<String, Role> roleHierarchy) 
	//Construct the e-NFA
	{
		Automaton a = new Automaton();
		HashSet<String> nodes = JavaAPI.getNodes(m);
		HashMap<String,Node> addedNodes = new HashMap<String,Node>();
    	
		int id = 1; 		//Used to make logged policies unique

		for (String nodelabel : nodes)
    	{
			HashSet<String> pp = JavaAPI.getPolicies(m, nodelabel);

			boolean isNode = true;
			
			if (!pp.isEmpty())
			{
				for (String pol : pp)
				{
	    			HashSet<String> actions = JavaAPI.getActions(m, pol);
	    			for (String action : actions)
	    			{
	    				if (action.toLowerCase().contains("move")) { isNode = false; }
	    			}
				}
			}
			if (!isNode)
			{
				continue;
			}
			Node n;

			if (!addedNodes.containsKey(nodelabel))
			{
				n = new Node(nodelabel);
				addedNodes.put(nodelabel, n);
			}
			else {n = addedNodes.get(nodelabel);}
			
			for (String successor : JavaAPI.getSuccessors(m, nodelabel))
			{				
				HashSet<String> policies = JavaAPI.getPolicies(m, successor);
				
				Policy p = new Policy();
				boolean door = false;		// initiate flags.
				boolean logged = false;
				boolean hasRole = true;
				int time = 0;
    			
				HashMap<String, String> map = JavaAPI.getMetrics(m, nodelabel, successor);
    			//Finds the time from the node to the successor
				for (String key : map.keySet()) {
    				if (key.equals("time")){
    					time = Integer.parseInt(map.get(key));
    				}
    			}
				
    			
				if (!policies.isEmpty())
				{
					for (String policy : policies)
					{
						p.setName(policy);
		    			HashSet<String> credentials = JavaAPI.getCredentials(m, policy);
		    			HashSet<String> actions = JavaAPI.getActions(m, policy);
		    			
		    			
		    			for (String credential : credentials)
		    			{
		    				if (role != null && roleHierarchy.containsKey(credential)) 
		    				{ 
		    					hasRole = roleHierarchy.get(credential).contains(role);
		    				}
		    				p.addCredential(credential);
		    			}
		    			for (String action : actions)
		    			{
		    				if (action.toLowerCase().contains("move")) { door = true; }
		    				if (action.toLowerCase().contains("log")) 
		    				{ 
		    					logged = true; 
		    					p.setName(p.getName() + "_" + id);		// policies can be reused
		    					id++;
		    				}
		    				p.addAction(action);
		    			}
						if (!logged && hasRole && role != null) //The user has access, and it will not be logged
		    			{
		    				p = new Policy();
		    				p.setName("epsillon");
		    			}

					}
				}
				else 
				{
					p.setName("epsillon");
				}

				if (hasRole) 
				{ 
					Node succ;	
					if (!addedNodes.containsKey(successor) && !door) //why not !door? (p.isEmpty())
					{
						succ = new Node(successor);
						addedNodes.put(successor, succ);
						
					}
					
					else if (door)		//New: Make door into edge. Node for why this ugly implementation works can be found in .txt.
					{
						succ = null;
						for (String successor2 : JavaAPI.getSuccessors(m, successor))
						{
							
							if (!addedNodes.containsKey(successor2))
							{
								succ = new Node(successor2);
								addedNodes.put(successor2, succ);
							}
							else 
							{
								
								succ = addedNodes.get(successor2); 
							}
							//If a door, add the time from the door to the next successor
							map = JavaAPI.getMetrics(m, successor, successor2);
			    			for (String key : map.keySet()) {
			    				if (key.equals("time")){
			    					time += Integer.parseInt(map.get(key));
			    				}
			    			}
							
							successor = successor2;
						}

					}
					else { succ = addedNodes.get(successor); }
					
					
					
					Connection c = new Connection(p, succ);
					c.setTime(time);
					

					n.addSuccessor(succ.getName(),c);; 
				}		
			}

			if (nodelabel.equals("s")) {a.setStart(n);}  //Only for testing

			
			a.addNode(n);
    	}
		if(simplify)
		{
			return simplifyAutomaton(a);
		}
		return a;
	}	

	private static Automaton simplifyAutomaton(Automaton a) {
		//Construct DFA for a given e-NFA
		HashMap<String,Node> allNodes = a.getAllNodes();

		HashMap<String,Set<Node>> eclose = new HashMap<String,Set<Node>>(); //hashmap to later search for eclose for specific states
		Set<Node> states;
		
		for (Node n : allNodes.values())					// Calculate ECLOSE(Node)
		{
			states = eclose(n, new HashSet<Node>());
			eclose.put(n.getName(), states);
		}
		
		HashMap<String,List<Transition>> table = transitionTable(a,eclose);	// Make transition table
		
		String s = generateKey(eclose.get(a.getStart().getName())); // Define start node
		return constructDFA(table, s);		// Construct DFA
	}
	
	private static Automaton constructDFA(HashMap<String, List<Transition>> table, String start) {
		//Creates the DFA
		Automaton a = new Automaton();
		HashMap<String,Node> nodes = new HashMap<String,Node>();
		
		for (String s : table.keySet())
		{
			nodes.put(s,new Node(s));
		}
		for (Node n : nodes.values())
		{
			for (Transition t : table.get(n.getName()))
			{
				Connection c = new Connection(t.getPolicy(),nodes.get(t.getName()));
				n.addSuccessor(c.getNode().getName(), c);
			}
			a.addNode(n);
			if (n.getName().equals(start)) {a.setStart(n);}
		}
		return a;
	}

	private static HashMap<String, List<Transition>> transitionTable(Automaton a, HashMap<String, Set<Node>> eclose) {
		//Construct the transition table
		HashMap<String, List<Transition>> table = new HashMap<String, List<Transition>>();	// Will keep the data
		List<Transition> lst = new ArrayList<Transition>();									// Will be used for values to the map 
		Set<Policy> policies = extractPolicies(a.getAllNodes().values()); 			// Used to determine new edges
		
		for (Policy p : policies)			
		{
			Transition t = null;
			for (Node n : eclose.get(a.getStart().getName()))
			{
				for (Connection c : n.getSuccessors().values())
				{
					if (c.containsPolicy(p))
					{
						Set<Node> nodes = eclose.get(c.getNode().getName());
						if (t == null) {t = new Transition(p,generateKey(nodes));}
						t.addAll(nodes);
					}
				}
			}
			if (t != null) {lst.add(t);}
		}
		String s = generateKey(eclose.get(a.getStart().getName()));
		table.put(s, lst); 		// Add to map
		
		// This was for the start state..
		// Same process for the rest of the nodes.
		
		Queue<Transition> queue = new LinkedList<Transition>();
		queue.addAll(lst);		// Initialize queue

		while (!queue.isEmpty())
		{
			Set<Node> next = queue.poll().getSet();
			String key = generateKey(next);

			if (table.containsKey(key)) {continue;} 	// The state has already been handled
			
			List<Transition> transitions = new ArrayList<Transition>();

			for (Policy p : policies)
			{
				Transition t = null;
				for (Node n : next)
				{
					for (Connection c : n.getSuccessors().values())
					{
						if (c.containsPolicy(p))
						{
							Set<Node> nodes = eclose.get(c.getNode().getName());
							if (t == null) {t = new Transition(p,generateKey(nodes));}
							t.addAll(nodes);
						}
					}
				}
				if (t != null) 
				{
					transitions.add(t);
					queue.add(t);
				}
			}
			table.put(key,transitions);
		}
		return table;
	}

	private static String generateKey(Set<Node> set) {
		String s = "";
		for (Node n : set)
		{
			s += n.getName() + " ";
		}
		return s.trim();
	}

	private static Set<Policy> extractPolicies(Collection<Node> collection) {
		Set<Policy> set = new HashSet<Policy>();
		for (Node n : collection)
		{
			for (Connection c : n.getSuccessors().values())
			{
				for (Policy p : c.getPolicies())
				{
					if (!p.isEmpty()) //No need for epsilon edges here
					{
						set.add(p);
					}
				}
			}
		}
		return set;
	}

	private static Set<Node> eclose(Node n, HashSet<Node> states) {
		//Calculate eclose
		if (!states.contains(n))
		{
			states.add(n);
		}
		for (Connection c : n.getSuccessors().values())
		{
			if (!states.contains(c.getNode()) && c.hasEmptyPolicy())
			{
				states.addAll(eclose(c.getNode(),states));
			}
		}

		return states;
	}
}
