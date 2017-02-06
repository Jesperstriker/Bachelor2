package data.access;

public class Log {
	
	String actorID;
	String from;
	String to;
	int counter;
	String policy;
	
	public Log(String actorID, String from, String to, String policy, int counter)
	{
		this.actorID = actorID;
		this.from = from;
		this.to = to;
		this.counter = counter;
		this.policy = policy;
	}
	public String getPolicy() {
		return policy;
	}
	public String getActorID()
	{
		return actorID;
	}
	public String getFrom()
	{
		return from;
	}
	public String getTo()
	{
		return to;
	}
	public int getCounter()
	{
		return counter;
	}
	public String toString()
	{
		return actorID + " " + from + " " + to + " " + policy + " " + counter;
	}

}