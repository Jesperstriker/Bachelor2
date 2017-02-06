package application;

import data.access.Log;
import model.Actor;

public class UnknownLog {

	private Actor actor;
	private Log log;
	
	public UnknownLog(Log log)
	{
		this.log = log;
		this.actor = null;
	}
	
	public Actor getActor() {
		return actor;
	}

	public void setActor(Actor actor) {
		this.actor = actor;
	}

	public Log getLog() {
		return log;
	}

	public void setLog(Log log) {
		this.log = log;
	}
	
	public String toString(){
		return actor + " in log: " + log;
	}
}
