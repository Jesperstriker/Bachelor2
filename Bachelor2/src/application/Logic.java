package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import automaton.components.Node;
import data.access.Log;
import data.access.LogReader;
import model.*;
import model.BuildingModel;

public class Logic {
	//Used to evaluate log data
	public static void checkLog(BuildingModel bm, String filePath)
	{
		int errorNumber = 0, cardError = 0, tailgatingError = 0;
    	LogReader reader = new LogReader(filePath);
    	HashMap<String,Actor> actors = bm.getActors();
    	
    	int oldestTime = -1;
    	Actor oldestActor = null;
    	
    	List<UnknownLog> unknownLogs = new ArrayList<UnknownLog>();
    	
    	while (reader.hasNext())
    	{
    		
    		Node startPos, endPos;
    		Actor actor;
    		Log log = reader.getNextLog();		
    		Boolean isCardFraud = false;
    		
    		if (!actors.containsKey(log.getActorID()))
    		{
    			unknownLogs.add(new UnknownLog(log));
    			continue;
    		}
    		
    		if(oldestActor == null)
    		{
    			oldestActor = actors.get(log.getActorID());
    		}    
    		    		
    		actor = actors.get(log.getActorID());
    		
    		//Detect card fraud
    		if(actor.getTimestamp() == log.getCounter())
    		{
    			errorNumber++;
    			cardError++;
    			isCardFraud = true;
    			System.out.println(errorNumber + ": Card fraud detected: " + actor.getName());
    		}
    		
    		startPos = actor.getPosition();
    		endPos = actor.getPosition().getSuccessorByLog(log);
    		
    		//Detect tailgating
    		if (endPos == null && !isCardFraud)
    		{
        		List<UnknownLog> possibleLogs = new ArrayList<UnknownLog>();
        		boolean hasUnknownLog = false;
        		
    			for (UnknownLog ul : unknownLogs)
    			{
    				Node ulEndPos = startPos.getSuccessorByLog(ul.getLog());
    				if (ulEndPos != null && actor.getTimestamp() < ul.getLog().getCounter() && ulEndPos.getName().contains(log.getFrom()))
    				{
    					if(ul.getActor() == null) 
    					{
        					ul.setActor(actor);
        					hasUnknownLog = true;
        					break;
    					}
    					else 
    					{
    						possibleLogs.add(ul);
    					}
    				}
    				
    			}
    			if (!hasUnknownLog)
    			{
    				errorNumber++;
    				tailgatingError++;
    				if (!possibleLogs.isEmpty())
    				{
        				System.out.println(errorNumber + ": Tailgating problem for either:\n" + actor.getName());
        				for (UnknownLog ul : possibleLogs)
        				{
        					//System.out.println("or " + ul.toString());
        				}
    				}
    				else
    				{
        				System.out.println(errorNumber + ": Tailgating detected: "  + actor.getName());

    				}
    			}

    		}
			if (endPos == null)
			{
				for(Node n : actor.getAutomaton().getAllNodes().values())
				{
					if (n.containsNode(log))
					{
						endPos = n.getSuccessorByLog(log);
						break;
					}
				}
			}
			
    		actor.setPosition(endPos); 					//update position from last log
    		actor.setTimestamp(log.getCounter());		//update time from last log
    		
    		if(oldestActor.getName().equals(log.getActorID()))
    		{
    			oldestTime = log.getCounter();
    			for(Actor a : actors.values())
    			{
    				if(a.getTimestamp() < oldestTime)
    				{
    					oldestTime = a.getTimestamp();
    					oldestActor = a;
    				}
    			}
    			//Removes unknownlogs that are no longer needed
    			List<UnknownLog> oldLogs = new ArrayList<UnknownLog>();
    			for(UnknownLog ul : unknownLogs)
    			{
    				if(ul.getActor() != null && ul.getLog().getCounter() < oldestTime)
    				{
    					oldLogs.add(ul);
//    					unknownLogs.remove(ul);
    				}
    			}
    			unknownLogs.removeAll(oldLogs);
    		}
    	}
		int numberOfULLeft = 0;   	
    	for (UnknownLog l : unknownLogs)
    	{
    		if(l.getActor() == null)
    		{
    			numberOfULLeft++;
    		}
    	}
    	System.out.println("Tailgating errors detected: " + tailgatingError + 
    			"\nCard errors detected: " + cardError +
    			"\nTotal errors detected: " + errorNumber +
    			"\n\nNumber of unknown logs left: " + numberOfULLeft);
	}
}
