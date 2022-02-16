package fr.leflodu62.textflowserver.data;

import org.json.JSONObject;

public class UserData {
	
	private final String name;
	
	public UserData(String name) {
		this.name = name;
	}
	
	
	public String getName() {
		return name;
	}
	
	public JSONObject toJson() {
		return new JSONObject(this);
	}

}
