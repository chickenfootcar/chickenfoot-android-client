package org.as.chickenfoot.client;

import org.json.JSONException;
import org.json.JSONObject;

public class MapElement {

	JSONObject object = new JSONObject();
	JSONObject parameters = new JSONObject();
	
	public MapElement() {
		try {
			object.put("p", null);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setModule(String module) {
		try {
			object.put("m", module);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void setAction(String action) {
		try {
			object.put("a", action);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void setParameter(String name, String value) {
		try {
			parameters.put(name, value);
			object.put("p", parameters);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void setParameter(String name, int value) {
		try {
			parameters.put(name, value);
			object.put("p", parameters);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public String toString() {
        return object.toString() + "";

    }

}
