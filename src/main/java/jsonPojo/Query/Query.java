package jsonPojo.Query;

import static BOMain.Program.Alias_Qualification;
import static BOMain.Program.dPNameandID;
import static BOMain.Program.dataFiltersAliasMap;
import static BOMain.Program.keyAliasList;
import static BOMain.Program.keyDPAndMergeDimension;
import static BOMain.Program.keyNameandDataType;
import static BOMain.Program.logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import BOMain.Program;
import jsonPojo.dataProviders.ResultObjects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Query {
	private String id;

	private String name;
	
	private String dataSourceCuid;

	private String query;

	public String promptFilters = "";

	public static String Distinct_count_alias = "";

	private LinkedHashSet<String> duplicateAlias = new LinkedHashSet<String>();

	public static HashSet<String> aliasNames = new HashSet<String>();

	public static HashMap<String, String> aliasMap = new HashMap<String, String>();

	public static HashSet<String> modified_aliasNames = new HashSet<String>();

	public static HashSet<String> keys_Set = new HashSet<String>();

	public static HashSet<String> alias_data_provider = new HashSet<String>();

	public static Map<String, String> alias_map = new HashMap<>();

	public static Set<String> alias_table_set = new HashSet<>();

	private Map<String, String> alias_map_new = new HashMap<>();

	private HashMap<String, String> counterRoundOffDetails = new HashMap<>();

	public static Map<String, Map<String, String>> alias_mapWithDP = new HashMap<String, Map<String, String>>();

	public static Map<String, Map<String, String>> counter_alias_mapWithDP = new HashMap<String, Map<String, String>>();

	public static Map<String, String> counter_alias_map = new HashMap<>();

	public static ArrayList<String> modifiedQueries = new ArrayList<>();

	private Map<String, String> counter_alias_map_new = new HashMap<>();

	private static ArrayList<String> multipleQueryResultObjectsUsed = new ArrayList<String>();

	private Map<String, String> multipleFlowAliasMap = new HashMap<>();

	public static HashMap<String, String> keyNames = new HashMap<String, String>();

	public static Set<String> checkForDuplicateInKeys = new HashSet<String>();

	public static Set<String> checkForDuplicateInResultObject = new HashSet<String>();

	public static Map<String, String> tablesList = new HashMap<String, String>();

	public static Map<String, Set<String>> conditionList = new HashMap<String, Set<String>>();

	private static ArrayList<String> keyValue = new ArrayList<String>();

	public static ArrayList<String> Union = new ArrayList<String>();

	public static Map<String, HashMap<String, String>> keysMap = new HashMap<String, HashMap<String, String>>();

	public static Map<String, HashMap<String, String>> originalString = new HashMap<String, HashMap<String, String>>();

	public static ArrayList<String> dataProviderNames = new ArrayList<String>();

	public static HashMap<String, String> duplicateResultObjectForSingleKey = new HashMap<String, String>();

	public static Map<String, String> DuplicateAliasesForKey = new HashMap<>();

	public static Map<String, String> tablesTypeCastingMap = new HashMap<>();

	public static Map<String, String> duplicateResultObjects = new HashMap<>();

	private ArrayList<String> resObjNames = new ArrayList<String>();

	public static Map<String, String> prompt_map = new HashMap<>();

	public static Map<String, String> prompt_map_CustomQuery = new HashMap<>();

	public static String NE_NAME_details = "";

	private static int[] index;

	private static String previousDP = "";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQuery() {
		return query;
	}

	public void setFilter(String prompt) {
		this.promptFilters = prompt;
	}

	public void setQuery(String query) throws IOException {
		
		resObjNames = ResultObjects.getName();

		String alias_query = query;
		//System.out.println("dp name :" + name);

		if (!query.equals("")) {
			logger.info("\nModifying the query for the dataProvider: " + name + "\nquery: " + query);

			getCounterDetails();

			String queryWithoutPrompt = removePromptFilter(query);

			updateResultObjects(query);

			alias_query = resultObjectsAsAlias(queryWithoutPrompt);

			//queryWithoutPrompt = addCastFunction(alias_query);

			//alias_query = modifyQuery(alias_query);

			//alias_query = addCastForPromptsTable(alias_query);

			getKeysFromTheQuery(alias_query);
			alias_query = alias_query.replace(" as* varchar(10)* ", " as varchar(10) ");

			logger.info("\nChanges are done to the query of the dp: " + name);
		}
		this.query = alias_query;
	}

	private void getCounterDetails() {
		counterRoundOffDetails = Program.getRoundOffCounters();
	}

	private void updateResultObjects(String query) {
		String union[], temp[];
		LinkedHashMap<Integer, Integer> splitQuery = new LinkedHashMap<Integer, Integer>();
		ArrayList<String> resultObjects = new ArrayList<String>();

		int size = 0, k = 0;
		size = resObjNames.size();
		index = new int[size];
		size = 0;
		duplicateAlias.clear();
		for (int j = 0; j < resObjNames.size(); j++) {
			resultObjects.add(resObjNames.get(j).trim());
			duplicateAlias.add(resObjNames.get(j).trim());
		}

		union = query.split("UNION");
		if (union.length > 1) {
			for (int i = 0; i < union.length; i++) {
				query = modifyQuery(union[i], ",", ";");
				query = modifyFROMKeyword(query);
				temp = query.split("#FROM#");
				temp = temp[0].split(",");
				size += temp.length;
				splitQuery.put(i, temp.length);
			}
			int count = 0;
			if (size > resObjNames.size()) {
				k = 0;
				Object[] object = duplicateAlias.toArray();
				for (int i : splitQuery.keySet()) {
					for (int j = 0; j < splitQuery.get(i); j++) {
						if (!object[j].equals(resultObjects.get(k).trim())) {
							resObjNames.add(k + count, (String) object[j]);
							count++;
						} else
							k++;
					}
				}
			}
		}

		modifyResultObjMergeDimension();

	}

	private String addCastFunction(String query1) {
		String union[] = query1.split(" UNION ");
		int i = 0;

		ArrayList<String> queryParts = new ArrayList<>();
		ArrayList<String> unionList = new ArrayList<>();
		Pattern pattern;
		Matcher m;
		String matchedPattern;

		for (String query : union) {
			queryParts = new ArrayList<>();
			query = modifyQuery(query, ",", ";");
			query = modifyFROMKeyword(query);
			String qArray[] = query.split("#FROM#");
			String fromPart[] = qArray[0].split(",");

			for (String string : fromPart) {
				matchedPattern = "";
				String regex = "as" + "(\\s){1,}" + "(.*)";
				pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
				m = pattern.matcher(string);
				if (m.find()) {
					matchedPattern = m.group();
					string = string.replace(matchedPattern, "");
				}

				boolean key_flag = false;
				String aliasFromResObj = resObjNames.get(i++);
				String table_name = getTableName(string);

				if (Alias_Qualification.containsKey(aliasFromResObj)
						&& Alias_Qualification.get(aliasFromResObj).equals("Dimension")) {
					key_flag = true;

				}

				if (key_flag) {

					String updatedResObject = modifyResultObject(aliasFromResObj);

					string = addCast(updatedResObject, string);

				}

				if (!matchedPattern.isEmpty())
					string = string + " " + matchedPattern;

				queryParts.add(string);
			}
			String modifyColumnsList = String.join(",", queryParts);
			String result = modifyColumnsList + " FROM " + qArray[1];
			result = modifyQuery(result, ";", ",");
			unionList.add(result);
		}
		String resultQuery = "";
		for (int j = 0; j < unionList.size(); j++) {
			if (j != unionList.size() - 1)
				resultQuery += unionList.get(j) + " UNION ";
			else
				resultQuery += unionList.get(j);

		}
		logger.info("Type casting is done");

		return resultQuery;

	}

	public String addCastForPromptsTable(String alias_query) {
		String regex = "([a-zA-Z0-9_\\.])*" + "(\\s)*" + "IN" + "(\\s)*" + "\\(" + "<NODENAME> :" + "(\\s)*"
				+ "(\\[[^\\[]*\\]|\\.)*" + "(\\s)*" + "\\)";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		java.util.regex.Matcher m = pattern.matcher(alias_query);
		while (m.find()) {

			String matched[] = m.group().split("\\sIN\\s");
			String table = matched[0].trim();
			if (tablesTypeCastingMap.containsKey(table)) {
				String addedCast = m.group().replace(table, tablesTypeCastingMap.get(table));
				alias_query = alias_query.replace(m.group(), addedCast);
			}
		}
		return alias_query;
	}

	private String modifyQuery(String query2) {
		String queries[] = query2.split("UNION");
		ArrayList<String> unionList = new ArrayList<>();
		String regex = " UNION ", tempString;
		int k = 0, start = 0, size = 0, end = 0;
		
		

		ArrayList<String> updatedResultObjects = new ArrayList<>();

		for (String resObject : resObjNames) {
			updatedResultObjects.add(modifyResultObject(resObject));
		}
		//System.out.println("updated Result object  " + updatedResultObjects.toString());
		//System.out.println("aliasList  " + keyAliasList.toString());
		
		

		for (int j = 0; j < queries.length; j++) {

			
			
			String query = queries[j];
			// String groupBy[] = query.split("GROUP BY");
			String str[] = query.split(",");
			ArrayList<String> queryParts = new ArrayList<>();
			for (String string : str) {
				queryParts.add(string);
			}

			ArrayList<String> groupByKeys = new ArrayList<>();

			query = modifyQuery(query, ",", ";");
			query = modifyFROMKeyword(query);
			String qArray[] = query.split("#FROM#");
			String beforeFrom[] = qArray[0].split(",");
			size = beforeFrom.length;
			ArrayList<String> beforeFromParts = new ArrayList<>();
			for (String string : beforeFrom) {
				beforeFromParts.add(string);
			}

			int i = 1;

			start = k;
			end = start + size;

			if (j == 0)
				end = size;

			ArrayList<String> tempList = new ArrayList<>();

			for (int i1 = start; i1 < end && i1 < updatedResultObjects.size(); i1++) {
				tempString = updatedResultObjects.get(i1);

				tempList.add(tempString.toUpperCase());
			}
			//System.out.println("before from  " + beforeFromParts.toString());
			//System.out.println("tempList  " + tempList.toString());

			for (String keyAlias : keyAliasList) {
				regex = "as" + "(\\s)*" + keyAlias + "(\\s{1,}|,)";
				if (!tempList.contains(keyAlias.toUpperCase())) {
                    //System.out.println("keyALias  " + keyAlias);
					String addKey = "";

					if (j == 0)
						addKey = "'@QWERTY'" + " as " + keyAlias;
					else
						addKey = "'@QWERTY'";

					String temp = "";
					if (keyAlias.toUpperCase().contains("DATE_ID")) {

						temp = addKeyFromWhereCondition(query, "DATE_ID", "WHERE");

						if (temp.isEmpty()) {
							temp = addKeyFromWhereCondition(query, "DATE_ID", "INNER JOIN");

						}
					} else if (keyAlias.toUpperCase().contains("OSS_ID")) {

						temp = addKeyFromWhereCondition(query, "OSS_ID", "WHERE");

						if (temp.isEmpty()) {
							temp = addKeyFromWhereCondition(query, "OSS_ID", "INNER JOIN");

						}
					}

					if (!temp.isEmpty()) {
						addKey = temp + " as " + keyAlias;
						addKey = addCast(keyAlias, temp) + " as " + keyAlias;
						if (j == 0)
							addKey = addCast(keyAlias, temp) + " as " + keyAlias;
						else
							addKey = addCast(keyAlias, temp);
						groupByKeys.add(temp);

					}

					//System.out.println("addKey  " + addKey);
					beforeFromParts.add(i, addKey);
					Alias_Qualification.put(keyAlias, "Dimension");
					// if (j == 0) {
					ResultObjects.setName(keyAlias, i);
					// }
					i++;
				} else {

				}
			}

			if (!groupByKeys.isEmpty() && !query.contains("GROUP BY"))
				query = String.join(",", queryParts) + " GROUP BY " + String.join(",", groupByKeys);
			else if (!groupByKeys.isEmpty()) {
				query = String.join(",", queryParts) + "," + String.join(",", groupByKeys);
			} else
				query = String.join(",", queryParts);
			String toBeReplaced = String.join(",", beforeFromParts);
			qArray[0] = modifyQuery(qArray[0], ";", ",");
			query = query.replace(qArray[0], toBeReplaced);
			query = query.replace("#FROM#", " FROM ");
			query = modifyQuery(query, ";", ",");
			unionList.add(query);
			k = end;
		}
		String result = "";
		for (int p = 0; p < unionList.size() - 1; p++) {
			result += unionList.get(p) + " UNION ";
		}
		result += unionList.get(unionList.size() - 1);

		modifiedQueries.add(result);

		return result;
	}

	private String addCast(String updatedResObject, String string) {
		String table_name = getTableName(string);
		if (keyNameandDataType.containsKey(updatedResObject)
				&& (keyNameandDataType.get(updatedResObject).equals("Numeric")
						|| keyNameandDataType.get(updatedResObject).equals("DateTime")
						|| keyNameandDataType.get(updatedResObject).equals("Date"))) {

			if (table_name != null) {
				string = string.replace(table_name, "CAST" + "(" + table_name + " as* varchar(10)* " + ")");
				tablesTypeCastingMap.put(table_name, "CAST" + "(" + table_name + " as* varchar(10)* " + ")");
			} else {
				string = "CAST" + "(" + string + " as* varchar(10)* " + ")";
				tablesTypeCastingMap.put(string, "CAST" + "(" + string + " as* varchar(10)* " + ")");
			}

		} else if (keyNameandDataType.containsKey(updatedResObject.toUpperCase())
				&& (keyNameandDataType.get(updatedResObject.toUpperCase()).equals("Numeric")
						|| keyNameandDataType.get(updatedResObject.toUpperCase()).equals("DateTime")
						|| keyNameandDataType.get(updatedResObject.toUpperCase()).equals("Date"))) {

			if (table_name != null) {
				string = string.replace(table_name, "CAST" + "(" + table_name + " as* varchar(10)* " + ")");
				tablesTypeCastingMap.put(table_name, "CAST" + "(" + table_name + " as* varchar(10)* " + ")");
			} else {
				string = "CAST" + "(" + string + " as* varchar(10)* " + ")";
				tablesTypeCastingMap.put(string, "CAST" + "(" + string + " as* varchar(10)* " + ")");
			}

		}

		return string;
	}

	private String addKeyFromWhereCondition(String query, String key, String condition) {

		if (!query.contains(" " + condition + " "))
			return "";
		String where_condition = query.substring(query.indexOf(condition));

		String addKey = "";

		String regex = "\\(" + "(\\s*)" + "[a-zA-Z0-9_\\.]*" + key + "(\\s*)" + "=";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher(where_condition);
		if (m.find()) {
			if (m.group().contains("DC.DIM"))
				addKey = m.group().replace("=", "").replaceAll("\\(", "").replaceAll("\\)", "").trim();
			else {
				regex = "=" + "[a-zA-Z0-9_\\.]*" + key + "(\\s*)" + "\\)";
				pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
				m = pattern.matcher(where_condition);
				if (m.find()) {
					addKey = m.group().replace("=", "").replaceAll("\\(", "").replaceAll("\\)", "").trim();
				}

			}
		}

		return addKey;
	}

	public String removePromptFilter(String query) {
		prompt_map.clear();
		Set<String> promptNames = Program.getPromptNames();
		String promptName = new String("");
		String reg = "@" + "(\\s)*" + "Prompt" + "\\(";
		Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
		java.util.regex.Matcher m = pattern.matcher(query);
		while (m.find()) {
			int open = m.end() - 1;
			int close = findcloseParen(query.toCharArray(), open);
			String prompt = query.substring(m.start(), close + 1);

			String prompt_check = prompt.toUpperCase();
			if (prompt_check.contains("RAW DATA")) {
				prompt_map.put(prompt, " {Rawdata} ");
			} else if (prompt_check.contains("FIRST DATE")) {
				prompt_map.put(prompt, " {STARTINGDATE} ");
			} else if (prompt_check.contains("LAST DATE")) {
				prompt_map.put(prompt, " {ENDINGDATE} ");
			} else if (prompt_check.contains("DATE")) {
				prompt_map.put(prompt, " {STARTINGDATE} ");
			}

			else {
				for (String string : promptNames) {
					if (prompt.contains(string)) {
						if (string.trim().endsWith(":"))
							string = string.replace(":", "");
						promptName = "<" + string.replaceAll("\\s+", "") + ">";
						prompt_map.put(prompt, promptName);
					}
				}
			}

		}

		reg = "@" + "(\\s)*" + "dpvalue" + "\\(";
		pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
		m = pattern.matcher(query);
		while (m.find()) {
			String dpName = "";
			int open = m.end() - 1;
			int close = findcloseParen(query.toCharArray(), open);
			String temp = query.substring(m.start() + 1, close);
			String str[] = temp.split(",");
			String column_details = str[str.length - 1].trim();
			Pattern p = Pattern.compile("(.*)\\..*");
			Matcher match = p.matcher(column_details);
			if (match.find()) {
				for (Map.Entry<String, String> entry : dPNameandID.entrySet()) {
					if (entry.getKey().contains(match.group(1))) {
						p = Pattern.compile("(.*)\\..*");
						Matcher matched = p.matcher(entry.getValue());
						if (matched.find()) {
							dpName = matched.group(1);
							break;
						}
					}
				}
			}

			if (dPNameandID.containsKey(column_details)) {
				String column_id = column_details;

				column_details = dPNameandID.get(column_details);
				p = Pattern.compile("(.*)\\..*");
				Matcher matchedPattern = p.matcher(column_details);
				if (!matchedPattern.find()) {

					String tempColumnDetais = Program.modifiedKey(column_details, column_id);

					column_details = "[" + modifyResultObject(tempColumnDetais) + "]";
					column_details = dpName + "." + column_details;

				} else {

					String regex = "(\\[[^\\[]*\\]\\.){0,}" + "(\\[([^\\[]*)\\])";
					p = Pattern.compile(regex);
					matchedPattern = p.matcher(column_details);
					if (matchedPattern.find()) {

						String value = Program.modifiedKey(column_details, column_id);

						column_details = column_details.replace(matchedPattern.group(2),
								"[" + modifyResultObject(value) + "]");

					}

				}
			}

			String prompt = query.substring(m.start(), close + 1);
			prompt_map.put(prompt, " (<NODENAME> : " + column_details + ")");
		}
		for (String prompts : prompt_map.keySet()) {
			query = query.replace(prompts, prompt_map.get(prompts));
		}
		prompt_map_CustomQuery.putAll(prompt_map);
		return query;
	}

	public static String modifyQuery(String query, String charToReplace, String charToBeReplaced) {
		String regex = Pattern.quote("case") + "(\\s)*" + Pattern.quote("when") + "(.*?)" + Pattern.quote("end");
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher(query);
		while (m.find()) {
			String matchedPattern = m.group();
			String replaced = matchedPattern.replace(charToReplace, charToBeReplaced);
			query = query.replace(matchedPattern, replaced);
		}
		regex = "substring" + "(\\s)*" + "\\(";
		pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		m = pattern.matcher(query);
		while (m.find()) {
			int start = m.start();
			int open = m.end() - 1;
			int close_index = findcloseParen(query.toCharArray(), open);
			String function = query.substring(start, close_index + 1);
			String replaced = function.replace(charToReplace, charToBeReplaced);
			query = query.replace(function, replaced);
		}
		pattern = Pattern.compile("Round\\(\\s*", Pattern.CASE_INSENSITIVE);
		m = pattern.matcher(query);
		while (m.find()) {
			int start = m.start();
			int open = m.end() - 1;
			int close_index = findcloseParen(query.toCharArray(), open);
			String matchedPattern = query.substring(start, close_index + 1);
			String replaced = matchedPattern.replace(charToReplace, charToBeReplaced);
			query = query.replace(matchedPattern, replaced);
		}
		return query;
	}

	public static String modifyFROMKeyword(String query) {
		Pattern pattern = Pattern.compile("[^a-zA-Z0-9_]" + "FROM" + "[^a-zA-Z0-9_]", Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher(query);
		if (m.find()) {
			query = query.replaceFirst(m.group(), "#FROM#");
		}
		return query;
	}

	private String resultObjectsAsAlias(String query) {
		alias_map_new.clear();
		duplicateAlias.clear();
		counter_alias_map_new.clear();
		multipleFlowAliasMap.clear();
		multipleQueryResultObjectsUsed.clear();
		String regex = "", temporaryAlias, originalAlias, matchedPattern = "";
		Pattern pattern = null;
		Matcher m = null;
		query = modifyQuery(query, ",", ";");
		query = modifyFROMKeyword(query);
		ArrayList<String> resultObjectsUsed = new ArrayList<String>();

		int size = resObjNames.size();
		index = new int[size];
		String alias = "", aliasFromResObj = "", aliasFromQuery = "";
		String temp_alias = "";
		boolean found = false;
		String qArray[] = query.split("#FROM#");
		regex = "SELECT" + "\\s+" + "(DISTINCT|)" + "\\s*";
		pattern = Pattern.compile(regex);
		m = pattern.matcher(qArray[0]);
		while (m.find()) {
			matchedPattern = m.group();
			qArray[0] = qArray[0].replace(matchedPattern, "");
		}
		String str[] = qArray[0].split(",");

		ArrayList<String> queryParts = new ArrayList<String>();
		int i = 0, k = 0;
		for (int j = 0; j < resObjNames.size(); j++) {
			alias = resObjNames.get(j);

			temp_alias = alias.toLowerCase();
			if (!duplicateAlias.add(temp_alias)) {
				k = 0;
				index[j] = 1;
				for (String string : resObjNames) {
					if (string.equalsIgnoreCase(alias)) {
						index[k] = 1;
						break;
					}
					k++;
				}
			}
		}

		for (String string : str) {

			aliasFromResObj = "";
			boolean fixed_key_flag = false, key_flag = false;

			alias = "";
			if (i < resObjNames.size())
				aliasFromResObj = resObjNames.get(i);

			String parts[] = aliasFromResObj.split("(\\s+|_)");
			if (Alias_Qualification.containsKey(aliasFromResObj)
					&& Alias_Qualification.get(aliasFromResObj).equals("Dimension")) {
				key_flag = true;

			}

			/*
			 * else if (aliasFromResObj.equalsIgnoreCase("Date") ||
			 * aliasFromResObj.equalsIgnoreCase("OSS_ID")) key_flag = true; else if
			 * (parts.length == 2 && parts[1].toUpperCase().equals("NAME")) key_flag = true;
			 */

			/*
			 * regex = "OSS" + "(\\s)*" + "(_)*" + "Id"; pattern = Pattern.compile(regex,
			 * Pattern.CASE_INSENSITIVE); m = pattern.matcher(aliasFromResObj); if(m.find())
			 * { fixed_key_flag = true; } regex = "Date" + "(\\s)*" + "(_)*" + "Id"; pattern
			 * = Pattern.compile(regex, Pattern.CASE_INSENSITIVE); m =
			 * pattern.matcher(aliasFromResObj); if(m.find()) { fixed_key_flag = true; }
			 * regex = "NE" + "(\\s)*" + "(_)*" + "Version"; pattern =
			 * Pattern.compile(regex, Pattern.CASE_INSENSITIVE); m =
			 * pattern.matcher(aliasFromResObj); if(m.find()) { fixed_key_flag = true; }
			 * String parts[] = aliasFromResObj.split("(\\s+|_)"); if(parts.length == 2 &&
			 * parts[1].toUpperCase().equals("NAME")) fixed_key_flag = true;
			 */

			String table_name = "";

			table_name = getTableName(string);

			String counter_alias = "";
			pattern = Pattern.compile(".*\\..*\\..*");
			m = pattern.matcher(string);
			String s = string;
			if (m.find()) {
				s = string.replaceFirst(Pattern.quote("."), "");
			}
			pattern = Pattern.compile(Pattern.quote(".") + "(.*[^\\)])");

			m = pattern.matcher(s);
			if (m.find()) {

				alias = m.group(1).replaceAll("\\)", "");
				aliasFromQuery = alias;
				counter_alias = alias;
				if (alias.contains("_")) {
					alias = alias.replaceAll("_", " ");
				}

				found = true;
			}

			if (!Program.getMultipleQuery()) {
				try {

					string = checkIfRoundRequired(string, aliasFromResObj);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}

			/*
			 * if (key_flag) {
			 * 
			 * String updatedResObject = modifyResultObject(aliasFromResObj);
			 * 
			 * if (keyNameandDataType.containsKey(updatedResObject) &&
			 * keyNameandDataType.get(updatedResObject).equals("Numeric")) { if (table_name
			 * != null) { string = string.replace(table_name, "CAST" + "(" + table_name +
			 * " as* varchar(10)* " + ")"); tablesTypeCastingMap.put(table_name, "CAST" +
			 * "(" + table_name + " as* varchar(10)* " + ")"); } else { string = "CAST" +
			 * "(" + string + " as* varchar(10)* " + ")"; tablesTypeCastingMap.put(string,
			 * "CAST" + "(" + string + " as* varchar(10)* " + ")"); }
			 * 
			 * } else if (keyNameandDataType.containsKey(updatedResObject.toUpperCase()) &&
			 * keyNameandDataType.get(updatedResObject.toUpperCase()).equals("Numeric")) {
			 * if (table_name != null) { string = string.replace(table_name, "CAST" + "(" +
			 * table_name + " as* varchar(10)* " + ")");
			 * tablesTypeCastingMap.put(table_name, "CAST" + "(" + table_name +
			 * " as* varchar(10)* " + ")"); } else { string = "CAST" + "(" + string +
			 * " as* varchar(10)* " + ")"; tablesTypeCastingMap.put(string, "CAST" + "(" +
			 * string + " as* varchar(10)* " + ")"); }
			 * 
			 * } }
			 */

			String aliasDataFilters = "";

			if (Program.siName.contains("MSC, Management Overview (Busy Hour)")
					|| (index != null && i < index.length && (index[i] == 0 || key_flag))) {

				if (i < resObjNames.size())
					aliasFromResObj = resObjNames.get(i);

				aliasDataFilters = resObjNames.get(i);

				if (Program.getMultipleQuery()) {

					boolean flag = false;
					HashMap<String, String> multipleQueryAlias = new HashMap<String, String>();
					int j = i;
					String tempAlias = "", originalResObject = "";
					String string1, string2 = "";
					String[] strTemp;
					multipleQueryAlias = Program.getMultipleQueryAlias();
					for (int listIndex = 0; listIndex < resObjNames.size(); listIndex++) {
						originalResObject = resObjNames.get(listIndex);
						tempAlias = string1 = originalResObject.trim();
						if (!multipleQueryResultObjectsUsed.contains(tempAlias)) {
							flag = false;
							aliasFromQuery = aliasFromQuery.trim();
							Pattern p = Pattern.compile("\\(avg|min|max|sum\\)");
							Matcher m1 = p.matcher(string1);
							if (m1.find()) {
								if (m1.group().contains("avg") || m1.group().contains("min")
										|| m1.group().contains("max") || m1.group().contains("sum")) {
									string1 = string1.substring(0, m1.start() - 1).trim();
								}
							}
							if (multipleQueryAlias.containsKey(string1)
									&& aliasFromQuery.equalsIgnoreCase(multipleQueryAlias.get(string1))) {
								aliasFromResObj = originalResObject;
								multipleQueryResultObjectsUsed.add(aliasFromResObj);
								flag = true;
								break;
							}
							string2 = string1.replaceAll("\\s+", "").replaceAll("\\.", "_");
							string1 = string1.replaceAll("\\s+", "_").replaceAll("\\.", "_");
							if (string1.equalsIgnoreCase(aliasFromQuery) || string2.equalsIgnoreCase(aliasFromQuery)
									|| aliasFromQuery.contains(string2.toUpperCase())
									|| aliasFromQuery.contains(string2)
									|| aliasFromQuery.contains(string1.toUpperCase())
									|| aliasFromQuery.contains(string1)) {
								flag = true;

								aliasFromResObj = originalResObject;

								multipleQueryResultObjectsUsed.add(aliasFromResObj);
								break;
							}
							// if(flag)
							// break;
						}
					}

					if (!flag) {
						while (true) {
							tempAlias = aliasFromResObj;
							strTemp = tempAlias.split("\\s+");
							for (int k2 = 0; k2 < strTemp.length; k2++) {
								if (aliasFromQuery.contains(strTemp[k2].toUpperCase())
										|| aliasFromQuery.contains(strTemp[k2])) {
									flag = true;
									aliasFromResObj = tempAlias;

									break;
								}
							}
							if (flag)
								break;
							if (j == resObjNames.size())
								break;
							j++;
							if (j < resObjNames.size()) {
								aliasFromResObj = resObjNames.get(j);

							}
						}
					}
					multipleFlowAliasMap.put(aliasFromQuery, aliasFromResObj);
					try {
						string = checkIfRoundRequired(string, aliasFromResObj);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}

				if (Alias_Qualification.containsKey(aliasFromResObj)
						&& Alias_Qualification.get(aliasFromResObj).equals("Dimension")) {
					key_flag = true;
				}

				if (aliasFromResObj.equalsIgnoreCase("OSS Identifier"))
					aliasFromResObj = "OSS ID";
				resultObjectsUsed.add(aliasFromResObj);

				originalAlias = aliasFromResObj.replaceAll("\\s+", "");
				temporaryAlias = modifyResultObject(aliasFromResObj);

				if (duplicateResultObjectForSingleKey.containsKey(table_name)) {
					String final_alias = duplicateResultObjectForSingleKey.get(table_name);
					if (!temporaryAlias.equals(final_alias)) {
						duplicateResultObjects.put(temporaryAlias, modifyResultObject(final_alias));
						DuplicateAliasesForKey.put("[" + aliasFromResObj + "]", "[" + final_alias + "]");
					}
					if (!(name.equalsIgnoreCase(previousDP) || previousDP.equals(""))) {
						aliasFromResObj = duplicateResultObjectForSingleKey.get(table_name);

					}
				}

				aliasNames.add(aliasFromResObj);

				if (!alias.equals(""))
					aliasMap.put(alias, aliasFromResObj);

				parts = aliasFromResObj.split("(\\s+|_)");
				if (parts.length == 2 && parts[1].toUpperCase().equals("NAME"))
					NE_NAME_details = alias + "," + aliasFromResObj;

				if (aliasFromResObj.equals("Date")) {
					string = string + " as " + aliasFromResObj + "_Id";
					i++;
					queryParts.add(string);
					continue;
				}
				/*
				 * char c = aliasFromResObj.charAt(0); if (Character.isDigit(c)) {
				 * aliasFromResObj = "_" + aliasFromResObj; }
				 */

				aliasFromResObj = modifyResultObject(aliasFromResObj);

				modified_aliasNames.add(aliasFromResObj);

/* */				if (alias_data_provider.contains(aliasFromResObj.toUpperCase()) && !key_flag) {

					String key = "[" + name + "]" + "." + "[" + originalAlias + "]";
					String table_key = "[" + table_name + "]" + "." + "[" + originalAlias + "]";

					if (!alias_map.containsKey(key) && (!alias_table_set.contains(table_key) || !key_flag)) {

						String modified_dp = name.replaceAll(" ", "").replaceAll("-", "_").replaceAll("\\(", "_")
								.replaceAll("\\)", "");
						modified_dp = modified_dp.trim();
						for (char ch : modified_dp.toCharArray()) {
							if (!Character.isAlphabetic(ch) && !Character.isDigit(ch)) {
								modified_dp = modified_dp.replace(String.valueOf(ch), "_");
							}
						}
						if (modified_dp.endsWith("_"))
							modified_dp = modified_dp.substring(0, modified_dp.length() - 1);

						alias_map.put(key, "[" + aliasFromResObj + "_" + modified_dp + "]");
						alias_table_set.add(table_key);
						alias_map_new.put(key, "[" + aliasFromResObj + "_" + modified_dp + "]");
						string = string + " as " + aliasFromResObj + "_" + modified_dp;
						dataFiltersAliasMap.put("[" + name + "]" + "." + "[" + aliasDataFilters + "]",
								"[" + aliasFromResObj + "_" + modified_dp + "]");
					} else {
						string = string + " as " + aliasFromResObj;
						alias_table_set.add(table_key);
						counter_alias_map.put(counter_alias, aliasFromResObj);
						counter_alias_map_new.put(counter_alias, aliasFromResObj);
						dataFiltersAliasMap.put("[" + name + "]" + "." + "[" + aliasDataFilters + "]",
								"[" + aliasFromResObj + "]");
					}
				} else {
					string = string + " as " + aliasFromResObj;
					counter_alias_map.put(counter_alias, aliasFromResObj);
					counter_alias_map_new.put(counter_alias, aliasFromResObj);
					alias_table_set.add("[" + table_name + "]" + "." + "[" + originalAlias + "]");
					dataFiltersAliasMap.put("[" + name + "]" + "." + "[" + aliasDataFilters + "]",
							"[" + aliasFromResObj + "]");
				}
/* */			alias_data_provider.add(aliasFromResObj.toUpperCase());
				alias_table_set.add("[" + table_name + "]" + "." + "[" + originalAlias + "]");
				queryParts.add(string);

			} else {

				if (i >= resObjNames.size()) {
					resObjNames.add(i, aliasFromQuery);
				}
				aliasDataFilters = resObjNames.get(i);
				resultObjectsUsed.add(aliasFromQuery);
				if (found) {

/* */					if (alias_data_provider.contains(aliasFromQuery.toUpperCase()) && !key_flag) {
						String key = "[" + name + "]" + "." + "[" + aliasFromQuery + "]";
						String table_key = "[" + table_name + "]" + "." + "[" + aliasFromQuery + "]";

						if (!alias_map.containsKey(key) && (!alias_table_set.contains(table_key) || !key_flag)) {
							String modified_dp = name.replaceAll(" ", "").replaceAll("-", "_").replaceAll("\\(", "_")
									.replaceAll("\\)", "");
							modified_dp = modified_dp.trim();
							for (char ch : modified_dp.toCharArray()) {
								if (!Character.isAlphabetic(ch) && !Character.isDigit(ch)) {
									modified_dp = modified_dp.replace(String.valueOf(ch), "_");
								}
							}
							if (modified_dp.endsWith("_"))
								modified_dp = modified_dp.substring(0, modified_dp.length() - 1);

							alias_map.put(key, "[" + aliasFromQuery + "_" + modified_dp + "]");
							alias_table_set.add(table_key);
							alias_map_new.put(key, "[" + aliasFromQuery + "_" + modified_dp + "]");
							string = string + " as " + aliasFromQuery + "_" + modified_dp;
							dataFiltersAliasMap.put("[" + name + "]" + "." + "[" + aliasDataFilters + "]",
									"[" + aliasFromQuery + "_" + modified_dp + "]");
						} else {
							string = string + " as " + aliasFromQuery;
							alias_table_set.add(table_key);
							counter_alias_map.put(counter_alias, aliasFromQuery);
							counter_alias_map_new.put(counter_alias, aliasFromQuery);
							dataFiltersAliasMap.put("[" + name + "]" + "." + "[" + aliasDataFilters + "]",
									"[" + aliasFromQuery + "]");
						}

					} else {
						string = string + " as " + aliasFromQuery;
						dataFiltersAliasMap.put("[" + name + "]" + "." + "[" + aliasDataFilters + "]",
								"[" + aliasFromQuery + "]");
						alias_table_set.add("[" + table_name + "]" + "." + "[" + aliasFromQuery + "]");
						counter_alias_map.put(counter_alias, aliasFromQuery);
						counter_alias_map_new.put(counter_alias, aliasFromQuery);

					}
					alias_table_set.add("[" + table_name + "]" + "." + "[" + aliasFromQuery + "]");
/* */					alias_data_provider.add(aliasFromQuery.toUpperCase());
					aliasNames.add(aliasFromQuery);
					modified_aliasNames.add(aliasFromQuery);
					queryParts.add(string);
					aliasMap.put(alias, aliasFromQuery);
				}
			}
			i++;

		}
		if (Program.getMultipleQuery()) {
			resObjNames = resultObjectsUsed;
		}

		String result = matchedPattern;
//-->		
		String joinQueryParts = String.join(",", queryParts);
		if (joinQueryParts.startsWith("(")) {
			joinQueryParts = joinQueryParts.substring(1);
		}
		result = result +joinQueryParts;
//-->
		
		//result = result + String.join(",", queryParts);
		query = result;
		previousDP = name;
		for (int j = 1; j < qArray.length; j++) {
			query = query + " FROM ";
			query += qArray[j];
		}
		// result = query;
		query = modifyQuery(query, ";", ",");
		regex = "as" + "(\\s)*" + "OSS" + "(\\s)*" + "(_)*" + "Id";
		pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		m = pattern.matcher(query);
		while (m.find()) {
			String matched = m.group();
			query = query.replace(matched, "as OSS_ID");
		}
		if (alias_map_new.size() > 0)
			alias_mapWithDP.put(name, alias_map_new);
		if (counter_alias_map_new.size() > 0)
			counter_alias_mapWithDP.put(name, counter_alias_map_new);
		return query;
	}

	private String checkIfRoundRequired(String string, String aliasFromResObj) throws FileNotFoundException {

		if (counterRoundOffDetails.containsKey(aliasFromResObj)) {
			String regex = Pattern.quote("case") + "(\\s)*" + Pattern.quote("when") + "(.*?)" + Pattern.quote("end");
			Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			Matcher m = pattern.matcher(string);
			if (m.find()) {
				string = "Round(" + string + ",12)";

			} else {

				string = "Round(" + string + "," + counterRoundOffDetails.get(aliasFromResObj) + ")";
			}
		}
		return string;
	}

	private String getTableName(String string) {
		String regex = "(DC|DIM)" + "(.*)";
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(string);
		if (m.find()) {
			return m.group().replaceAll("\\(", "").replaceAll("\\)", "").trim();
		}
		return null;

	}

	public static String modifyResultObject(String resultObject) {
		resultObject = resultObject.trim();
		if (resultObject.equals("Date")) {
			return "Date_ID";
		} else if (resultObject.equalsIgnoreCase("OSS Identifier")) {
			return resultObject = "OSS_ID";
		}

		resultObject = resultObject.replaceAll("\\s+", "_");
		resultObject = resultObject.replaceAll("\\(", "_");
		resultObject = resultObject.replaceAll("-", "_");
		resultObject = resultObject.replaceAll("\\)", "");
		resultObject = resultObject.replaceAll("\\.", "__");
		resultObject = resultObject.replaceAll("%", "");

		if (resultObject.startsWith("_"))
			resultObject = resultObject.substring(1, resultObject.length());

		char c = resultObject.charAt(0);
		if (Character.isDigit(c)) {
			resultObject = "_" + resultObject;
		}
		return resultObject;
	}

	private void getKeysFromTheQuery(String queryWithoutPrompt) {

		keyValue.clear();
		HashMap<String, String> keyAliasNames = new HashMap<String, String>();
		ArrayList<String> keysList = new ArrayList<String>();

		String string1 = "", alias = "", resultObject = "", s = "", dpName = "", modifiedAlias = "", table_name = "";
		String[] temp1, temp2, temp3;
		String conditions = "";
		int resObjectIndex = 0, k = 0;

		resObjNames = ResultObjects.getName();

		modifyResultObjMergeDimension();

		Pattern pattern = null;
		Matcher m = null;

		for (String string : resObjNames) {

			if (Alias_Qualification.containsKey(string) && Alias_Qualification.get(string).equals("Dimension"))
				keyValue.add(string);
			/*
			 * else if (string.equalsIgnoreCase("Date") ||
			 * string.equalsIgnoreCase("OSS_ID")) keyValue.add(string); else if
			 * (parts.length == 2 && parts[1].toUpperCase().equals("NAME"))
			 * keyValue.add(string);
			 */
		}
		String query = queryWithoutPrompt;
		String[] queryParts = query.split("UNION");
		for (int i = 0; i < queryParts.length; i++) {
			keyAliasNames.clear();
			keysList.clear();

			query = queryParts[i].toUpperCase().replaceFirst("SELECT", "select");

			query = modifyQuery(query, ",", ";");

			query = modifyFROMKeyword(query);
			String[] splitQuery = query.split("select");

			temp1 = splitQuery[1].split("#FROM#");

			temp2 = temp1[0].split(",");

			for (int j = 0; j < temp2.length; j++, resObjectIndex++) {
				resultObject = resObjNames.get(resObjectIndex);

				if (resultObject.equals(keyValue.get(k))) {
					String regex = "as" + "(\\s){1,}" + "(.*)";
					pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
					m = pattern.matcher(temp2[j]);
					if (m.find()) {
						temp2[j] = temp2[j].replace(m.group(), "");
					}
					alias = string1 = temp2[j].replace("DISTINCT", "").trim();
					s = string1;
					table_name = getTableName(string1);
					temp3 = s.split("\\.");
					if (temp3.length == 3) {
						s = string1.replaceFirst(Pattern.quote("."), "");
					}
					pattern = Pattern.compile(Pattern.quote(".") + "(.*[^\\)])");
					m = pattern.matcher(s);
					if (m.find()) {
						alias = m.group(1).replaceAll("\\)", "").trim();
					}
					if (Program.getMultipleQuery()) {
						if (!resultObject.equals(multipleFlowAliasMap.get(alias))) {
							break;
						}
					}
					string1 = modifyQuery(string1, ";", ",");
					if (duplicateResultObjectForSingleKey.containsKey(table_name) && !name.equalsIgnoreCase(previousDP))
						modifiedAlias = duplicateResultObjectForSingleKey.get(table_name);
					else {
						modifiedAlias = modifyResultObject(resultObject);
					}
					keyAliasNames.put(modifiedAlias, string1);

					if (checkForDuplicateInResultObject.add(modifiedAlias.toLowerCase())) {
						Union.add(modifiedAlias);
					}
					k++;
					if (k > keyValue.size() - 1) {
						break;
					}
				}

			}
			if (resObjectIndex == resObjNames.size())
				resObjectIndex = 0;

			if (k == keyValue.size())
				k = 0;
			int index = temp1[1].lastIndexOf("GROUP BY");
			if (index != -1) {
				conditions = temp1[1].substring(0, index);
			} else
				conditions = temp1[1];
			dpName = name + "_" + i;
			tablesList.put(dpName, modifyQuery(conditions, ";", ","));
			keysMap.put(dpName, keyAliasNames);
			dataProviderNames.add(dpName);
		}
	}

	public void modifyResultObjMergeDimension() {

		ArrayList<String> resultObjects = new ArrayList<>();
		for (int j = 0; j < resObjNames.size(); j++) {
			String alias = resObjNames.get(j);

			if (keyDPAndMergeDimension.containsKey(alias + "(" + name + ")")) {

				alias = keyDPAndMergeDimension.get(alias + "(" + name + ")");
			} else if (keyDPAndMergeDimension.containsKey(alias)) {

				alias = keyDPAndMergeDimension.get(alias);

			}

			resultObjects.add(alias);
		}
		resObjNames = resultObjects;

	}

	public static HashSet<String> getAliasNames() {
		return aliasNames;
	}

	public static HashMap<String, String> getAliasMap() {
		return aliasMap;
	}
	
	public String getDataSourceCuid() {
		return dataSourceCuid;
	}

	public void setDataSourceCuid(String dataSourceCuid) {
		this.dataSourceCuid = dataSourceCuid;
	}

	public int findOpenParen(char[] text, int closePos) {
		int openPos = closePos;
		int counter = 1;
		while (counter > 0 && openPos > 0) {
			char c = text[--openPos];
			if (c == '(') {
				counter--;
			} else if (c == ')') {
				counter++;
			}
		}
		return openPos;
	}

	public static int findcloseParen(char[] text, int openPos) {
		int closePos = openPos;
		int counter = 1;
		while (counter > 0 && closePos < text.length - 1) {
			char c = text[++closePos];
			if (c == '(') {
				counter++;
			} else if (c == ')') {
				counter--;
			}
		}
		return closePos;
	}

	public String getFormattedString() {
//-->	
		if (Program.siName.contains("AIR, Overview (Busy Hour)_AIR; Overview; Busy Hour")) {
			if (query.endsWith(")")) {
				query = query.substring(0, query.length() - 1);
			}
		}
//-->
		return id + "," + name + "," + "\"" + query + "\"" + "," + "\"" + String.join(",", keyAliasList) + "\"" + "," + "\""+ dataSourceCuid +"\"";
	}

	@Override
	public String toString() {
		return "Query{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", query='" + query + '\'' + '}';
	}

	public static HashSet<String> getModifiedAliasNames() {
		return modified_aliasNames;
	}

}
