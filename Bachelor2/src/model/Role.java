package model;

public class Role {
	private String name;
	private Role parent;
	
	public Role(String name, Role parent)
	{
		this.name = name;
		this.parent = parent;
	}
	
	public Role(String name)
	{
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Role getParent() {
		return parent;
	}
	public void setParent(Role parent) {
		this.parent = parent;
	}
	public String toString(){
		return name;
	}
	public boolean contains(Role r)
	{
		if(parent == null)
		{
			return r.getName().equals(name);
		}
		return r.getName().equals(name) || parent.contains(r);
		
	}
}
