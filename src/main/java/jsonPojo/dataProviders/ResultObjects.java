package jsonPojo.dataProviders;

import java.util.ArrayList;

public class ResultObjects {
	private static ArrayList<String> name = new ArrayList<String>();

	public static ArrayList<String> getName() {
		return name;
	}

	public static void setName(ArrayList<String> resObjname) {
		name = resObjname;
	}
	public static void setName(String resObj, int index) {
		name.add(index,  resObj);
	}

}
