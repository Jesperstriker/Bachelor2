package application;

import automaton.components.Node;
import model.Actor;

public class Movement {
	Node locTarget;
	Actor actor;
	int time;
	int edgeTime;
	
	public Movement( Node locTarget ,int edgeTime, Actor actor, int time){
	this.locTarget = locTarget;
	this.actor = actor;
	this.time = time;
	this.edgeTime = edgeTime;
	}
	
	public Node getlocTarget(){
		return locTarget;
	}
	public void setlocTarget(Node locTarget){
		this.locTarget = locTarget;
	}
	
	public Actor getActor(){
		return actor;
	}
	
	public void setActor(Actor actor){
		this.actor = actor;
	}
	
	public int getTime(){
		return time;
	}
	
	public void setTime(int time){
		this.time = time;
	}
	
}
