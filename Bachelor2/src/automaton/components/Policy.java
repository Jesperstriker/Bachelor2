package automaton.components;

import java.util.HashSet;

public class Policy {
	
	private HashSet<String> actions;
	private HashSet<String> credentials;
	private String name ="";

	public Policy() 
	{
		actions = new HashSet<String>();
		credentials = new HashSet<String>();
	}
	
	public boolean isLogged() 
	{
		for(String c : actions)
		{
			if(c.toLowerCase().contains("log")) {
				return true;
			}
		}
		return false;
	}
	
	public String toString(){
		String s = "          Policy: " + name + "\n";

		if (isEmpty())
		{
			return s; // + "          Has no credentials or actions.\n";
		}
		if (actions.isEmpty())
		{
			s += "               Has no actions.\n";
		}
		else 
		{
			s += "               Has actions:\n";
			for (String a : actions)
			{
				s += "               " + a + "\n";
			}
		}
		if (credentials.isEmpty())
		{
			s += "               Has no credentials.\n";
		}
		else
		{
			s += "               Has credentials:\n";
			for (String c : credentials)
			{
				s += "               " + c + "\n";
			}
		}

		return s;
	}

	public boolean isEmpty() {
		return name.equals("epsillon");
	}
	public boolean equals(Policy p){
		return name.equals(p.getName());
	}

	public String getName() {return name;}

	public void addCredential(String credential) {
		credentials.add(credential);
	}
	public void addAction(String action) {
		actions.add(action);
	}
	public HashSet<String> getCredentials() {
		return credentials;
	}
	public HashSet<String> getActions() {
		return actions;
	}

	public void setName(String name) {this.name = name;}
}
