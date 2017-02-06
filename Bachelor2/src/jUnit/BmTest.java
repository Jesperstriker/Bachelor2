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

public class BmTest {
	
	BuildingModel bm;
	@Before
	public void setUp(){
		String user = "jespe";	
		//Enter xml model path
		String modelLocation = "C:/Users/" + user + "/Documents/7.Semester/Bachelor/project/Bachelor/src/LogBased/XMLModels/SimpleBuildingv1.xml";
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
		Node startnode = automaton.getAllNodes().get("s");
		HashMap<String,Connection> successors = startnode.getSuccessors();
		//Starting from s, only one successor (should be the hall).
		assertEquals(1, successors.size());
		successors.get("R1");
		
	}

	
}
