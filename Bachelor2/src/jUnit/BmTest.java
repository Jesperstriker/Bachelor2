package jUnit;

import static org.junit.Assert.*;


import java.util.HashSet;
import java.util.HashMap;
import org.hamcrest.collection.IsMapContaining;
import org.junit.Before;
import org.junit.Test;

import TREsPASS.apis.JavaAPI;
import TREsPASS.model.Model;
import automaton.Automaton;
import automaton.Tools;
import automaton.components.*;
import model.Actor;
import model.BuildingModel;
import model.Role;
//Made by Jesper Riis Douglas
public class BmTest {
	
	BuildingModel bm;
	@Before
	public void setUp(){
		String user = "jespe";	
		//Enter xml model path
		String modelLocation = "C:/Users/" + user + "/Documents/7.Semester/Bachelor/project/Bachelor/src/LogBased/XMLModels/SimpleOffice.xml";
		//Declaration of model components
		Model model = JavaAPI.readFile(modelLocation);
		HashMap<String,Role> roleHierarchy = Tools.createRoleHierarchy(model);
		HashMap<String, Actor> actors = Tools.getActors(model, roleHierarchy);
		Automaton basic = Tools.createAutomaton(model, null, false, roleHierarchy);		
		HashMap<Role,Automaton> automata = new HashMap<Role,Automaton>();
		bm = new BuildingModel(automata, actors, roleHierarchy, basic);
	}
	@Test
	public void checkBM() {
		Automaton automaton  = bm.getENFA();
		Node currentNode = automaton.getAllNodes().get("s");
		Node tempNode;
		HashMap<String,Connection> successors = currentNode.getSuccessors();
		HashMap<String,Connection> temps;
		//Starting from s, only one successor (should be the hall).
		assertEquals(2, successors.size());
		//Checking the first edge. It is implicit that the check if R1 is the correct node
		assertEquals(5,successors.get("R1").getTime());
		assertEquals(5,successors.get("R2").getTime());
		tempNode = successors.get("R2").getNode();
		
		successors = successors.get("R1").getNode().getSuccessors();
		assertEquals(8,successors.size());
		assertEquals(3,successors.get("R5").getTime());
		assertEquals(3,successors.get("R6").getTime());
		assertEquals(4,successors.get("R7").getTime());
		assertEquals(5,successors.get("R8").getTime());
		assertEquals(4,successors.get("R9").getTime());
		assertEquals(3,successors.get("R10").getTime());
		assertEquals(3,successors.get("R11").getTime());
		assertEquals(5,successors.get("s").getTime());
		
		temps = successors.get("R5").getNode().getSuccessors();
		assertEquals(1,temps.size());
		assertEquals(3,temps.get("R1").getTime());
		
		temps = successors.get("R6").getNode().getSuccessors();
		assertEquals(1,temps.size());
		assertEquals(3,temps.get("R1").getTime());
		
		temps = successors.get("R7").getNode().getSuccessors();
		assertEquals(1,temps.size());
		assertEquals(4,temps.get("R1").getTime());
		
		temps = successors.get("R8").getNode().getSuccessors();
		assertEquals(1,temps.size());
		assertEquals(5,temps.get("R1").getTime());
		
		temps = successors.get("R9").getNode().getSuccessors();
		assertEquals(1,temps.size());
		assertEquals(4,temps.get("R1").getTime());
		
		temps = successors.get("R10").getNode().getSuccessors();
		assertEquals(1,temps.size());
		assertEquals(3,temps.get("R1").getTime());
		
		temps = successors.get("R11").getNode().getSuccessors();
		assertEquals(1,temps.size());
		assertEquals(3,temps.get("R1").getTime());
		
		successors = tempNode.getSuccessors();
		assertEquals(3,successors.size());
		assertEquals(5,successors.get("s").getTime());
		assertEquals(7,successors.get("R3").getTime());
		assertEquals(2,successors.get("R4").getTime());
		
		temps = successors.get("R4").getNode().getSuccessors();
		assertEquals(1,temps.size());
		assertEquals(2,temps.get("R2").getTime());
		
		successors = successors.get("R3").getNode().getSuccessors();
		assertEquals(2,successors.size());
		assertEquals(2,successors.get("S1").getTime());
		assertEquals(7,successors.get("R2").getTime());
		
		temps = successors.get("S1").getNode().getSuccessors();
		assertEquals(1,temps.size());
		assertEquals(2,temps.get("R3").getTime());
	}

	
}
