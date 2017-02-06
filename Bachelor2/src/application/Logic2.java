package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import automaton.Automaton;
import automaton.components.Connection;
import automaton.components.Node;
import data.access.Log;
import data.access.LogReader;
import model.*;

public class Logic2 {
	static Set<Movement> possibleMoves = new HashSet<Movement>();

	public static void checkLog2(BuildingModel bm, String filePath) {
		LogReader reader = new LogReader(filePath);
		ArrayList<Log> currentLogs = new ArrayList<Log>();
		int maxObserved = 0;
		while (reader.hasNext()) {
			Log log = reader.getNextLog();
			int timeLog = log.getCounter();
			if (timeLog > maxObserved) {
				maxObserved = timeLog;
			}
			currentLogs.add(log);

		}
		findActors(bm, maxObserved, currentLogs);
	}

	public static void findActors(BuildingModel bm, int maxObserved, ArrayList<Log> currentLogs) {
		HashMap<String, Node> nodes = bm.getENFA().getAllNodes();
		Node startNode = nodes.get("s");
		for (Actor actor : bm.getActors().values()){
			actor.addLocation(startNode);
		}
		System.out.println(bm.actorToString());

		// Timespan of the logs, from 0 to max observed
		for (int t = 0; t <= maxObserved; t++) {
			if (compareLogs(bm, currentLogs, t, nodes)) {
				continue;
			}

			for (Actor actor : bm.getActors().values()) {
				for (Node location : actor.getLocations()) {
					// Foreach edge that each of theese locations has.
					for (Map.Entry<String, Connection> entry : location.getSuccessors().entrySet()) {
						Connection edge = entry.getValue();
						edge.getNode().getName();
						edge.getTime();
						possibleMoves.add(new Movement(edge.getNode(), edge.getTime(), actor, t));
					}
				}
			}
			// Finds possible locations the actor can move to.
			for (Iterator<Movement> i = possibleMoves.iterator(); i.hasNext();) {
				Movement movement = i.next();
				if (movement.edgeTime + movement.getTime() == t) {
					movement.getActor().addLocation(movement.getlocTarget());
					i.remove();
				}
			}
			System.out.println(bm.actorLocationsToString(t));
		}

	}
//Finds actors at time t in the logs.
	private static boolean compareLogs(BuildingModel bm, ArrayList<Log> currentLogs, int t,
			HashMap<String, Node> nodes) {
		//Flag to see if any has been found.
		boolean flag = false;
		for (Log log : currentLogs) {
			//Checks if the time written in the log is equal to t, whilst
			//also not having the actor as unknown.
			if (log.getCounter() == t && !log.getActorID().equals("Unknown")) {
				Actor actor = bm.getActors().get(log.getActorID());
				//Removes all previous locations and adds the one from the log.
				actor.resetLocation(nodes.get(log.getTo()));
				//Removes all occurences of the actor from possible moves
				for (Iterator<Movement> i = possibleMoves.iterator(); i.hasNext();) {
					Movement movement = i.next();
					if (movement.getActor() == actor) {
						i.remove();
					}
				}
				//Adds possible moves for the actor from current position and time.
				for (Map.Entry<String, Connection> entry : nodes.get(log.getTo()).getSuccessors().entrySet()) {
					Connection edge = entry.getValue();
					edge.getNode().getName();
					edge.getTime();
					possibleMoves.add(new Movement(edge.getNode(), edge.getTime(), actor, t));
				}
				flag = true;
			}
		}
		//If any actor is found at time t in the logs, it should be printed.
		if (flag){
			System.out.println(bm.actorLocationsToString(t));
		}
		return flag;
	}

}
