package demo;

import java.util.HashMap;
import java.util.HashSet;

import TREsPASS.apis.JavaAPI;
import TREsPASS.model.Model;

public class JavaAPI_demo {

    public static void main(String[] args) {
        
    	if (args.length != 1) {
    		System.err.println("invoke with filename as argument!");
    		System.exit(-1);
    	}
    	
    	Model model = JavaAPI.readFile(args[0]);
    	
    	HashSet<String> nodes = JavaAPI.getNodes(model);
    	for (String nodelabel : nodes) {
    		System.out.println("----------------------------------------");
    		System.out.println("Node: "+nodelabel);
    		HashSet<String> successors = JavaAPI.getSuccessors(model, nodelabel);
    		HashSet<String> predecessors = JavaAPI.getSuccessors(model, nodelabel);
    		HashSet<String> policies = JavaAPI.getPolicies(model, nodelabel);
    		
    		for (String predlabel: predecessors)
    			System.out.println("Edge: "+predlabel+" -> "+nodelabel);
    		
    		for (String succlabel: successors)
    			System.out.println("Edge: "+nodelabel+" -> "+succlabel);
    		
    		for (String succlabel: successors) { 
    			HashMap<String, String> map = JavaAPI.getMetrics(model, nodelabel, succlabel);
    			
    			for (String key : map.keySet()) {
    				System.out.println("Metrics: "+key+" = "+map.get(key));
    			}
    		}
    		
    		for (String policy: policies) {
    			
    			HashSet<String> credentials = JavaAPI.getCredentials(model, policy);
    			HashSet<String> actions = JavaAPI.getActions(model, policy);
    			
    			System.out.print("Policy: {");
    			for (String credential: credentials) {
    				System.out.print(credential+",");
    			}
    			System.out.print("} : {");
    			for (String action: actions) {
    				System.out.print(action+",");
    			}
    			System.out.println("}");
    		}
    		
    	}
    	
    	HashSet<String> actors = JavaAPI.getActors(model);
    	for (String actorlabel : actors) {
    		System.out.println("----------------------------------------");
    		System.out.println("Actor: "+actorlabel);
    		HashSet<String> assets = JavaAPI.getAssets(model, actorlabel);
			System.out.print("Assets: {");
			for (String asset: assets) {
				System.out.print(asset+",");
			}
			System.out.println("}");
    	}
    	
    	
    	HashSet<String> predicates = JavaAPI.getPredicates(model);
    	java.util.HashSet<java.util.ArrayList<String>> predicateValues;
    	
    	for (String predicateName : predicates) {
    		System.out.println("----------------------------------------");
    		System.out.println("Predicate: "+predicateName);
    		predicateValues = JavaAPI.getPredicateValues(model, predicateName);

    		System.out.print("Values: {");
			for (java.util.ArrayList<String> tuple: predicateValues) {
				System.out.print("[");
				for (String name : tuple) {
					System.out.print(name+",");
				}
				System.out.print("],");
			}
			System.out.println("}");
    	}
    	
    }

}
