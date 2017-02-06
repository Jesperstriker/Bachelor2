package data.generation.logdata;
import data.generation.Tools;
import model.*;
import automaton.*;
import automaton.components.Connection;
import automaton.components.Node;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LogDataGeneratorOld {
	//Data should be created from the non simplified automata (e-NFA)
		public static void generateLogdataOld(BuildingModel bm, Path logdataFilePath, Path errorPath, int numberOfLogs, int tailgatingViolations, int cardViolations)
		{
			int numberOfViolations = tailgatingViolations + cardViolations;
			
			if(numberOfLogs < 1)
			{
				System.out.println("Error no logs will be generated");
				return;
			}
			if(numberOfViolations < 0) {
				System.out.println("Error the amount of violations in the logdata can not be negative");
				return;
			}
			//Initialize components
			Automaton automaton = bm.getENFA();
			HashMap<Actor, Integer> actorTimestamps = new HashMap<Actor,Integer>();
			List<Actor> actors = new ArrayList<Actor>();
			for(Actor actor : bm.getActors().values())
			{
				actors.add(actor);
				actor.setAutomaton(automaton);
				actor.setTimestamp(0);
			}
			
			int timestamp = 0;
			
			int numberOfActors = actors.size();
			//Used to check the number of logs created
			int counter = 0;
			//List of logs
			List<String> logdata = new ArrayList<String>();
			List<String> logdataError = new ArrayList<String>();
			HashSet<Integer> errorLines = new HashSet<Integer>();
			
			//Data generating loop
			while(true)
			{
				Actor currentActor = selectRandomActor(numberOfActors, actors);
				Node currentNode = currentActor.getPosition();
					
				//Select next Node from successors to current Node.
				String nextNode;
				if(currentNode.getSuccessors().isEmpty())
				{
					continue;
				}
				else
				{
					nextNode = selectRandomConnection(currentNode.getSuccessors(),currentActor,bm.getRoleHierarchy());
					if (nextNode == null)
					{
						continue;
					}
				}
				Connection nextCon = currentNode.getSuccessors().get(nextNode);
				
				int temp = timestamp;
				
				timestamp = timestamp+nextCon.getTime();
				
				if (temp < currentActor.getTimestamp()){
					timestamp = temp;
					for (Actor actor : actors){
						if (actor.getTimestamp() <= temp){
						actor.setTimestamp(temp);
						}
					}
					continue;
				}
				
				if(numberOfLogs + numberOfViolations - counter <= cardViolations - logdataError.size() && 
						((nextCon.getFirstPolicy().isLogged() && !nextCon.isPersonSpecific()) || !actorTimestamps.containsKey(currentActor)))
				{
					continue;
//					we want to get the last card errors so we know how many errors to expect precisely!
				}
				
				if(nextCon.getFirstPolicy().isLogged() && nextCon.isPersonSpecific()) 
				{
					String log;
					boolean violate = createViolation((numberOfLogs + tailgatingViolations),counter,cardViolations);
					if (actorTimestamps.containsKey(currentActor) && violate)
					{
						String time = actorTimestamps.get(currentActor).toString();
						log = currentActor.getName() + " " + currentNode.getName() + " " + nextNode + " " + nextCon.getFirstPolicy().getName() + " " + timestamp; //Log string
						cardViolations--;
						
						for (int i = logdata.size()-1; i >=0; i--)
						{
							String s = logdata.get(i);
							if (s.endsWith(time))
							{
								errorLines.add(new Integer(Integer.valueOf(i)));
								break;
							}
						}
						
						logdataError.add(log);
					}
					else
					{
						log = currentActor.getName() + " " + currentNode.getName() + " " + nextNode + " " + nextCon.getFirstPolicy().getName() + " " + timestamp; //Log string
						logdata.add(log);
						actorTimestamps.put(currentActor, new Integer(Integer.valueOf(timestamp)));
					}
					
					counter++;
				}
				else if (nextCon.getFirstPolicy().isLogged())
				{
					String log = "Unknown " + currentNode.getName() + " " + nextNode + " " + nextCon.getFirstPolicy().getName() + " " + timestamp; //Log string
					logdata.add(log);
					counter++;
				}
				currentActor.setPosition(nextCon.getNode());
				currentActor.setTimestamp(timestamp);
				
				for (Actor actor : actors){
					if (actor.getTimestamp() <= temp){
					actor.setTimestamp(temp);
					}
				}
				//Break statement is used instead of logdata.size so we don't constantly have to evaluate the length of the list.
				if(counter - tailgatingViolations >= numberOfLogs) {
					break;
				}
				timestamp = temp+1;
				
			}
			
			//Exceptions should probably be handled differently
			try {
				if(numberOfViolations > 0) {
					createViolations(tailgatingViolations, logdata, logdataError, errorLines,errorPath,logdataFilePath);
				} else {
					Tools.WriteAndCreateFile(logdataFilePath,logdata);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private static boolean createViolation(int numberOfLogs, int counter, int cardViolations) 
		{
			if (cardViolations == 0)
			{
				return false;
			}
			double probability = (double) cardViolations / (double) (numberOfLogs - counter);
			if (probability > 0.98)
			{
				return true;
			}
			else
			{
				return Math.random() >= 1.0 - probability;
			}		
		}
		private static String selectRandomConnection(HashMap<String,Connection> successors, Actor actor, HashMap<String, Role> rh) {
			Random r = new Random();
			Role role = actor.getRole();
			int numberOfSuccessors = successors.keySet().size();
			List<String> successorKeys = new ArrayList<String>(successors.keySet());
			int i = 0;
			while(i < 25)		// To make sure it will not execute forever....
			{
				int successor = r.nextInt(numberOfSuccessors);
				if (successors.get(successorKeys.get(successor)).hasRole(role,rh))
				{
					return successorKeys.get(successor);
				}
				i++;
			}
			return null;
			
		}
		private static Actor selectRandomActor(int numberOfActors, List<Actor> actors) {
			Random r = new Random();
			int actorNumber = r.nextInt(numberOfActors);

			return actors.get(actorNumber);
		}
		private static void createViolations(int numberOfViolations, List<String> logdata, List<String> logdataError, 
													HashSet<Integer> errorLines,Path errorPath, Path path) {
			Random r = new Random();
			int i = 0;
			List<String> errors = new ArrayList<String>();
			while (i < numberOfViolations)
			{
				int removelog = r.nextInt(logdata.size());
				
				if (!errorLines.contains(removelog))
				{
					errors.add(logdata.get(removelog));
					logdata.set(removelog, "");
					errorLines.add(removelog);
					i++;
				}
			}
			
			for(int j = logdata.size()-1; j >= 0; j--)
			{
				if (logdata.get(j).equals(""))
				{
					logdata.remove(j);
				}
			}
			logdata.addAll(logdataError);

			logdata.sort((s1,s2) -> Integer.valueOf(s1.split(" ")[4]).compareTo(Integer.valueOf(s2.split(" ")[4])));
			
			errors.sort((s1,s2) -> Integer.valueOf(s1.split(" ")[4]).compareTo(Integer.valueOf(s2.split(" ")[4])));
			
			try {
				Tools.WriteAndCreateFile(errorPath,errors);
				Tools.WriteAndCreateFile(path, logdata);
			} catch (IOException e) {}
		}
		
		private static int changeTimestamp (int currentTimestamp, int timeConnection, int currentActorTimestamp){
			//System.out.println(currentTimestamp+ ","+timeConnection+ ","+currentActorTimestamp );
			if (currentTimestamp + timeConnection < currentActorTimestamp){
			return currentActorTimestamp+timeConnection;
		}
		else 
			return currentTimestamp+timeConnection;
		}
		
		public static void printMap(Map mp) {
		    Iterator it = mp.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		        System.out.println(pair.getKey() + " = " + pair.getValue());
		        it.remove(); // avoids a ConcurrentModificationException
		    }
		}

}
