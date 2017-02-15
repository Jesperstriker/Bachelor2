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
//Made by Jesper Riis Douglas
public class LogdataGenerator {
	private static HashMap<Integer, HashSet<Actor>> availableActors = new HashMap<Integer, HashSet<Actor>>();

	// Data should be created from the non simplified automata (e-NFA)
	public static void generateLogdata(BuildingModel bm, Path logdataFilePath, Path errorPath, int numberOfLogs,
			int tailgatingViolations, int cardViolations) {
		int numberOfViolations = tailgatingViolations + cardViolations;

		if (numberOfLogs < 1) {
			System.out.println("Error no logs will be generated");
			return;
		}
		if (numberOfViolations < 0) {
			System.out.println("Error the amount of violations in the logdata can not be negative");
			return;
		}
		// Initialize components
		Automaton automaton = bm.getENFA();
		HashSet<Actor> allActors = new HashSet<Actor>();
		for (Actor actor : bm.getActors().values()) {
			allActors.add(actor);
			actor.setAutomaton(automaton);
			actor.setTimestamp(0);
		}

		int timestamp = 0;
		availableActors.put(timestamp, allActors);
		int counter = 0;
		// List of logs
		List<String> logdata = new ArrayList<String>();
		List<String> logdataError = new ArrayList<String>();
		HashSet<Integer> errorLines = new HashSet<Integer>();

		// Data generating loop
		while (true) {

			HashSet<Actor> actors = availableActors.get(timestamp);
			// For all actors at available at time t
			for (Actor currentActor : safe(actors)) {
				Node currentNode = currentActor.getPosition();
				String nextNode;
				boolean cardFraud = false;
				
				if (currentNode.getSuccessors().isEmpty()) {
					continue;
				} else {
					nextNode = selectRandomConnection(currentNode.getSuccessors(), currentActor, bm.getRoleHierarchy());
					if (nextNode == null) {
						continue;
					}
				}
				Connection nextCon = currentNode.getSuccessors().get(nextNode);

				if (move()) {
					if (nextCon.getFirstPolicy().isLogged() && nextCon.isPersonSpecific()) {
						// Adds violations in case other actors are at the location of currentactor
						//while also being available
						HashSet<Actor> possibleViolaters = checkLocation(allActors, currentNode, currentActor);
						for (Actor actor : possibleViolaters) {
							if (tailgatingViolations > 0) {
								actor.setPosition(nextCon.getNode());
								addAvailableActor(timestamp, actor, nextCon.getTime());
								tailgatingViolations--;
							} else if (cardViolations > 0) {
								actor.setPosition(nextCon.getNode());
								addAvailableActor(timestamp, actor, nextCon.getTime());
								cardViolations--;
								cardFraud = true;
							} else break;
						}
						//Creates a log
						String log = currentActor.getName() + " " + currentNode.getName() + " " + nextNode + " "
								+ nextCon.getFirstPolicy().getName() + " " + (timestamp + nextCon.getTime()); // Log
						logdata.add(log);
						counter++;
						
						//Adds actor to the list when he/she is available again
						if (cardFraud){
							addAvailableActor(timestamp, currentActor, 1);	
						} else {
							addAvailableActor(timestamp, currentActor, nextCon.getTime());
						}
					} else if (nextCon.getFirstPolicy().isLogged()) {
						//Creates a violation, only tailgating allowed in unknown log
						HashSet<Actor> possibleViolaters = checkLocation(allActors, currentNode, currentActor);
						for (Actor actor : possibleViolaters) {
							if (tailgatingViolations > 0) {
								actor.setPosition(nextCon.getNode());
								tailgatingViolations--;
							} else break;
						}
						//Creates the unknown log. (In case you type in a password multiple
						//actors know.
						String log = "Unknown " + currentNode.getName() + " " + nextNode + " "
								+ nextCon.getFirstPolicy().getName() + " " + (timestamp + nextCon.getTime()); // Log																						// string
						logdata.add(log);
						counter++;
						addAvailableActor(timestamp, currentActor, nextCon.getTime());
								
					}
				} else {
					//If the actor stays put. 
					addAvailableActor(timestamp, currentActor, 1);
					continue;
				}
				//In case of card fraud,this is handling the current actors' position.
				if (!cardFraud){
				currentActor.setPosition(nextCon.getNode());
				addAvailableActor(timestamp, currentActor, nextCon.getTime());
				} else {
					addAvailableActor(timestamp, currentActor, 1);	
				}
				
				}

			// Break statement is used instead of logdata.size so we don't
			// constantly have to evaluate the length of the list.
			if (counter >= numberOfLogs) {
				break;
			}

			timestamp++;
		}

		// Exceptions should probably be handled differently
		try {
			Tools.WriteAndCreateFile(logdataFilePath, logdata);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void addAvailableActor(int timestamp, Actor currentActor, int edgeTime) {
		if (availableActors.get(timestamp + edgeTime) == null) {
			HashSet<Actor> newset = new HashSet<Actor>();
			newset.add(currentActor);
			availableActors.put(timestamp + edgeTime, newset);
		} else {
			availableActors.get(timestamp + edgeTime).add(currentActor);
		}
	}

	// Checks the position of the current actor, returns a list of other actors
	// at the current actors' position
	private static HashSet<Actor> checkLocation(HashSet<Actor> actors, Node currentNode, Actor currentActor) {
		HashSet<Actor> possibleViolaters = new HashSet<Actor>();
		for (Actor actor : actors) {
			if (actor.getPosition().equals(currentNode) && !(actor.equals(currentActor))) {
				possibleViolaters.add(actor);
			}
		}
		return possibleViolaters;
	}

	// Used as a safety net incase actors is null. (noone available at time t).
	private static HashSet<Actor> safe(HashSet<Actor> actors) {
		return actors == null ? new HashSet<Actor>() : actors;
	}

	private static boolean createViolation(int numberOfLogs, int counter, int cardViolations) {
		if (cardViolations == 0) {
			return false;
		}
		double probability = (double) cardViolations / (double) (numberOfLogs - counter);
		if (probability > 0.98) {
			return true;
		} else {
			return Math.random() >= 1.0 - probability;
		}
	}
//Not made by Jesper ------->
	private static String selectRandomConnection(HashMap<String, Connection> successors, Actor actor,
			HashMap<String, Role> rh) {
		Random r = new Random();
		Role role = actor.getRole();
		int numberOfSuccessors = successors.keySet().size();
		List<String> successorKeys = new ArrayList<String>(successors.keySet());
		int i = 0;
		while (i < 25) // To make sure it will not execute forever....
		{
			int successor = r.nextInt(numberOfSuccessors);
			if (successors.get(successorKeys.get(successor)).hasRole(role, rh)) {
				return successorKeys.get(successor);
			}
			i++;
		}
		return null;

	}
//Not made by Jesper ends <------

	private static boolean move() {
		Random rand = new Random();
		int n = rand.nextInt(100) + 1;
		return n > 70;
	}

}