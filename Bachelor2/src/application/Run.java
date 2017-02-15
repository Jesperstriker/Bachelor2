package application;

import java.nio.file.Path
;
import java.nio.file.Paths;
import java.util.HashMap;

import TREsPASS.apis.JavaAPI;
import TREsPASS.model.Model;

import automaton.Automaton;
import automaton.Tools;
import data.generation.logdata.LogdataGenerator;
import data.generation.logdata.LogDataGeneratorOld;

import model.Actor;
import model.BuildingModel;
import model.Role;

public class Run {
	//Config attributes
//	static String user = "bach2";
	static String user = "jespe";
	//Enter xml model path
	static String modelLocation = "C:/Users/" + user + "/Documents/7.Semester/Bachelor/project/Bachelor/src/LogBased/XMLModels/SimpleOffice.xml";
	//Enter log data output path
	static String logdataLocation = "C:/Users/" + user + "/Documents/7.Semester/Bachelor/project/Bachelor/Code/LogBased/Logdata/logdata.txt";
	//Enter path for removed log entries (eg. tailgating violations)
	static String errorLogLocation = "C:/Users/" + user + "/Documents/7.Semester/Bachelor/project/Bachelor/Code/LogBased/Logdata/logdataerror.txt"; 
	
	//Declaration of model components
	static BuildingModel bm;
	
	//Logdata attributes
	static int numberOfLogs = 10;
	static int cardViolations = 0;
	static int tailgatingViolations = 1;
	
	//TREsPASS model definition
	static Model model = JavaAPI.readFile(modelLocation);
	
	static Automaton basic;
	
	public static void main(String args[])
	{
		initBuilding();

		generateLogdata();
		initActors();
		//Logic.checkLog(bm, logdataLocation);
		Logic2.checkLog2(bm, logdataLocation);
	}
	
	private static void initActors() {
    	for (Role r: bm.getRoleHierarchy().values()) 
    	{
    		Automaton a = Tools.createAutomaton(model, r, true, bm.getRoleHierarchy());
    		bm.getAutomata().put(r, a);
    		for (Actor actor : bm.getActors().values())
    		{
    			if(actor.getRole().equals(r))
    			{
    				actor.setAutomaton(a);
    			}
    		}
    	}
    	
    	bm = new BuildingModel(bm.getAutomata(), bm.getActors(), bm.getRoleHierarchy(), bm.getENFA());
	}

	private static void generateLogdata() {
		Path path = Paths.get(logdataLocation);
		Path errorPath = Paths.get(errorLogLocation);
		LogdataGenerator.generateLogdata(bm, path,errorPath, numberOfLogs, tailgatingViolations, cardViolations);
		//LogDataGeneratorOld.generateLogdataOld(bm, path, errorPath, numberOfLogs, tailgatingViolations, cardViolations);
	}

	private static void initBuilding()
	{
		HashMap<String,Role> roleHierarchy = Tools.createRoleHierarchy(model);
		HashMap<String, Actor> actors = Tools.getActors(model, roleHierarchy);
		basic = Tools.createAutomaton(model, null, false, roleHierarchy);		
		HashMap<Role,Automaton> automata = new HashMap<Role,Automaton>();
		bm = new BuildingModel(automata, actors, roleHierarchy, basic);
	}
}
