package jsonPojo.Query;

public class Prompt {
	
	private String id;

	private String name;
	
	private String promptFilters;

	public Prompt(String id, String name, String promptFilters) {
		this.id = id;
		this.name = name;
		this.promptFilters = promptFilters;
	}

	public String getFormattedString() {	
		return id + "," + name + "," + "\"" + promptFilters + "\"" ;	
	}

}
