
package jsonPojo.Variables;

import static BOMain.Program.keyDPAndMergeDimension;
import static BOMain.Program.keyNameandDataType;
import static jsonPojo.Query.Query.Distinct_count_alias;
import static jsonPojo.Query.Query.DuplicateAliasesForKey;
import static jsonPojo.Query.Query.alias_map;
import static jsonPojo.Query.Query.counter_alias_map;
import static jsonPojo.Query.Query.counter_alias_mapWithDP;
import static jsonPojo.Query.Query.modifyResultObject;
import static BOMain.Program.logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import BOMain.Program;
import BOMain.UnivObjToCounterMapping;
import jsonPojo.Query.Query;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Formula {

	@JsonProperty("@dataType")
	private String dataType;

	@JsonProperty("@qualification")
	private String qualification;

	private String id;

	private String name;

	public static boolean flag = false;
	
	private boolean no_Filter_Flag=false;

	private String formulaLanguageId;

	private String definition;

	ArrayList<String> tableNameList = Program.getTableName();

	Map<String, Boolean> formattingMap = new HashMap<>();

	ArrayList<String> dataNameList = Program.getDataName();

	private Map<String, String> functions = new HashMap<>();

	private static Map<String, String> neNameDefinition = new HashMap<>();

	private static Set<String> KPINames = new HashSet<>();

	private static Map<String, String> neNameKPIandmodifyDefinition = new HashMap<>();

	public Formula() {
		functions = new HashMap<>();
		functions.put("Percentage", "Percent");
		functions.put("Power", "Power");
		functions.put("Percentile", "Percentile");
		functions.put("Concatenation", "Concatenate");
		functions.put("Fill", "Repeat");
		functions.put("FormatDate", "ParseDate");
		functions.put("Left", "Left");
		functions.put("Pos", "Find");
		functions.put("Substr", "Substring");
		functions.put("Replace", "Substitute");
		functions.put("DaysBetween", "DateDiff");
		functions.put("MonthsBetween", "DateDiff");
		functions.put("Log", "Log");
		functions.put("Mod", "Mod");
		functions.put("Rank", "Rank");
		functions.put("Round", "Round");
		functions.put("Previous", "Previous");
		functions.put("Range", "Range");
		functions.put("InList", "IN");
		functions.put("FormatNumber", "");
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

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

	public String getFormulaLanguageId() {
		return formulaLanguageId;
	}

	public void setFormulaLanguageId(String formulaLanguageId) {
		this.formulaLanguageId = formulaLanguageId;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		// Program.getDataName();
		logger.info("\n\nModifying the formula: " + definition + " for the kpi: " + name);
		KPINames.add(name);

		checkAggregationForFormatting(name, definition);

		definition = modifiedOverAllFunctKeys(definition);

		String updatedDefinition = updateCounterInCSCF(definition);
		
		String modifyKey = modifyKeyAlias(updatedDefinition);
		
		String removeTableName = removeTableName(modifyKey);
		
		String universeObject = univCounterMap(removeTableName);

		String replacedDef = replaceWithUnderscore(universeObject);

		String added_underscore = replaceSpacesWithUnderscore(replacedDef);
		
		if (name != null)
			added_underscore = added_underscore.replaceAll("\\s+", "");
		
		String modDef = ifStatementtoNetAn(added_underscore);

		String removeIfElseDef = convertToNetAnStatement(modDef);

		String In_function = In_Function_handling(removeIfElseDef);

		String modifyFunct = modifyFunctions(In_function);

		if (modifyFunct.contains("AndNot"))
			modifyFunct = modifyFunct.replace("AndNot", "And Not");
		
		modifyFunct = modifyFunct.replace("InReport", "");
		modifyFunct = handlingLogFunction(modifyFunct);
		modifyFunct = applyRoundOff(modifyFunct);
		modifyFunct = modifyTimeKPI(modifyFunct);
		
		if (name != null && name.equals("NE Name")) {
			neNameDefinition.put(definition, modifyFunct);
			neNameKPIandmodifyDefinition.put(name, modifyFunct);
		}
		//modifyFunct = typeCastKeys(modifyFunct);

		// modifyFunct = addRoundForSumAggregation(modifyFunct);

		this.definition = modifyFunct;

		logger.info("\nFormula is modified for the kpi: " + name);
		
	}

	private String addRoundForSumAggregation(String modifyFunct) {
		// TODO Auto-generated method stub
		if (name == null)
			return modifyFunct;
		modifyFunct = modifyFunct.substring(1, modifyFunct.length());
		
		if (formattingMap.containsKey(name) && formattingMap.get(name) == true && !modifyFunct.startsWith("Round")) {
			modifyFunct = "Round" + "(" + modifyFunct + ",0" + ")";
		
		}
		return modifyFunct;
	}

	private void checkAggregationForFormatting(String name, String definition) {
		// TODO Auto-generated method stub

		String regex = "\\[[^\\[]*\\]";

		boolean roundRequired = true;
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(definition);
		UnivObjToCounterMapping obj1 = Program.getUnivObject();
		// obj1.compareUnivObjAndCounter(definition, name);
		Map<String, String> counterAggregationMap = UnivObjToCounterMapping.getCounterAggregationMap();

		while (m.find()) {
			String counter = m.group().substring(1, m.group().length() - 1);

			if (counterAggregationMap.containsKey(counter)
					&& !counterAggregationMap.get(counter).equalsIgnoreCase("SUM")) {
				roundRequired = false;
			}
		}

		if (dataType != null && !dataType.equals("Numeric"))
			roundRequired = false;
		formattingMap.put(name, roundRequired);

	}

	private String modifiedOverAllFunctKeys(String modifyFunct) {
		ArrayList<String> tableColumns = Program.getTableKeysList();
		boolean kpiPresentInVisu = false;
		String columnsToBeUsed[] = null, cols[] = null;
		for (String row : tableColumns) {

			cols = row.split(",'");
			for (String col : cols) {
				col = col.replace("=", "").replace("'", "").replace("\"", "");

				if (col.equals(name) || col.equals("[" + name + "]")) {

					kpiPresentInVisu = true;
					columnsToBeUsed = cols;
				}
			}
		}
		if (!kpiPresentInVisu)
			return modifyFunct;

		Map<String, String> overAllFunctMap = new HashMap<>();
		String modifiedKeys = "";

		for (String col : columnsToBeUsed) {

			col = col.replace("=", "").replace("'", "").replace("\"", "");

			Pattern pattern = Pattern.compile("FormatDate" + "(" + "[^)]*" + ")");
			java.util.regex.Matcher m = pattern.matcher(col);
			if (m.find()) {
				col = col.replace("FormatDate", "").replace("(", "").replace(")", "").replace(";yyyy-MM-dd", "");
			}

			String keyResultObject = col;
			if (col.contains("."))
				keyResultObject = col.substring(col.lastIndexOf(".") + 1);
			keyResultObject = keyResultObject.replace("[", "").replace("]", "");

			String keywithDPFromMergeDimension = col.replace("[", "").replace("]", "");

			int end = keywithDPFromMergeDimension.indexOf(".") == -1 ? keywithDPFromMergeDimension.length()
					: keywithDPFromMergeDimension.indexOf(".");
			keywithDPFromMergeDimension = keywithDPFromMergeDimension.substring(0, end);
			keywithDPFromMergeDimension = keyResultObject + "(" + keywithDPFromMergeDimension + ")";
			keyResultObject = modifyResultObject(keyResultObject);
			if (keyDPAndMergeDimension.containsKey(keywithDPFromMergeDimension))
				keywithDPFromMergeDimension = keyDPAndMergeDimension.get(keywithDPFromMergeDimension);
			else
				keywithDPFromMergeDimension = keyResultObject;

			if (keyNameandDataType.containsKey(modifyResultObject(keywithDPFromMergeDimension))) {

				modifiedKeys += "[" + modifyResultObject(keywithDPFromMergeDimension) + "]" + ",";
			} else if (keyNameandDataType.containsKey(modifyResultObject(col))) {

				modifiedKeys += "[" + modifyResultObject(col) + "]" + ",";
			}

		}
		String regex = "For\\s*All" + "(\\s)*" + "\\(";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		java.util.regex.Matcher m = pattern.matcher(modifyFunct);

		while (m.find()) {
			int open = m.end() - 1;
			int start = m.start();
			int close_index = findcloseParen(modifyFunct.toCharArray(), open);
			String function = modifyFunct.substring(start, close_index + 1);

			String functTemp = function.replace("ForAll(", "").replace(")", "").replace("]", "").replace("[", "");

			String keys = modifyFunct.substring(start + 1, close_index);

			modifiedKeys = modifiedKeys.replace("[" + modifyResultObject(functTemp) + "]", "");
			if (modifiedKeys.endsWith(","))
				modifiedKeys = modifiedKeys.substring(0, modifiedKeys.length() - 1);

			overAllFunctMap.put(function, "ForAll(" + modifiedKeys + ")");

		}

		for (String overAllFunct : overAllFunctMap.keySet()) {
			modifyFunct = modifyFunct.replace(overAllFunct, overAllFunctMap.get(overAllFunct));
		}
		return modifyFunct;
	}

	private String typeCastKeys(String modifyFunct) {
		HashMap<String, String> mergeDimDataType = Program.getMergeDimDetails();

		String regex = "\\[[^\\[]*\\]";
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(modifyFunct);
		Map<String, String> map = new HashMap<>();

		while (m.find()) {
			String key = m.group().substring(1, m.group().length() - 1);

			if ((keyNameandDataType.containsKey(key) && keyNameandDataType.get(key) != null
					&& keyNameandDataType.get(key).equals("Numeric"))
					|| (mergeDimDataType.containsKey(key) && mergeDimDataType.get(key).equals("Numeric"))) {

				map.put(m.group(), " Integer(" + m.group() + ")");
			}
			if (!modifyFunct.toLowerCase().contains("parsedate")&&(keyNameandDataType.containsKey(key) && keyNameandDataType.get(key) != null
					&& keyNameandDataType.get(key).equals("DateTime"))
					|| (mergeDimDataType.containsKey(key) && mergeDimDataType.get(key).equals("DateTime"))) {

				map.put(m.group(), " DateTime(" + m.group() + ")");
			}
			if ((keyNameandDataType.containsKey(key) && keyNameandDataType.get(key) != null
					&& keyNameandDataType.get(key).equals("Date"))
					|| (mergeDimDataType.containsKey(key) && mergeDimDataType.get(key).equals("Date"))) {

				map.put(m.group(), " Date(" + m.group() + ")");
			}
			/*
			 * if ((keyNameandDataType.containsKey(key) && keyNameandDataType.get(key) !=
			 * null && keyNameandDataType.get(key).equals("String")) ||
			 * (mergeDimDataType.containsKey(key) &&
			 * mergeDimDataType.get(key).equals("String"))) {
			 * 
			 * map.put(m.group(), " Upper(" + m.group() + ")"); }
			 */
		}
		for (String key : map.keySet()) {
			modifyFunct = modifyFunct.replace(key, map.get(key));
		}

		modifyFunct = removeFunctionsFromOverPart("Integer", modifyFunct);

		// modifyFunct = removeFunctionsFromOverPart("Upper", modifyFunct);

		modifyFunct = removeFunctionsFromOverPart("DateTime", modifyFunct);

		modifyFunct = removeFunctionsFromOverPart("Date", modifyFunct);

		return modifyFunct;

	}

	private String removeFunctionsFromOverPart(String funct, String modifyFunct) {
		Map<String, Map<String, String>> mainMap = new HashMap<>();

		Map<String, String> subMap = new HashMap<>();
		String regex = "OVER\\s*(ALL|)" + "(\\s)*" + "\\(";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		java.util.regex.Matcher m = pattern.matcher(modifyFunct);
		while (m.find()) {
			int open = m.end() - 1;
			int start = m.start();
			int close_index = findcloseParen(modifyFunct.toCharArray(), open);
			String function = modifyFunct.substring(start, close_index + 1);
			Pattern subPattern = Pattern.compile(funct + "\\(" + "[^)]*" + "\\)");
			java.util.regex.Matcher m2 = subPattern.matcher(function);
			subMap = new HashMap<>();
			while (m2.find()) {

				String modifyfunction = m2.group().replace(funct, "").replace("(", "").replace(")", "");
				subMap.put(m2.group(), modifyfunction);
			}
			mainMap.put(function, subMap);

		}
		Map<String, String> overMap = new HashMap<>();

		for (String overFunct : mainMap.keySet()) {
			String modifyOver = overFunct;
			for (String key : mainMap.get(overFunct).keySet()) {
				modifyOver = modifyOver.replace(key, mainMap.get(overFunct).get(key));
				// modifyFunct = modifyFunct.replace(overFunct, modifyOver);
			}
			overMap.put(overFunct, modifyOver);
		}
		for (String overFunct : overMap.keySet()) {
			modifyFunct = modifyFunct.replace(overFunct, overMap.get(overFunct));
		}
		return modifyFunct;
	}

	public static Map<String, String> getMap() {
		return neNameDefinition;
	}

	public static Map<String, String> getNeNameMap() {
		return neNameKPIandmodifyDefinition;
	}

	public static Set<String> getKPINames() {
		return KPINames;
	}

	private String modifyTimeKPI(String modifyFunct) {
		String matchedPattern = "";
		String reg = "\\[((Hour|Min)_?\\([^\\[]*\\))\\]";
		Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher(modifyFunct);
		while (m.find()) {
			matchedPattern = m.group(1);
			if (matchedPattern.contains("Hour"))
				modifyFunct = modifyFunct.replace(matchedPattern, "Hour");
			else if (matchedPattern.contains("Min"))
				modifyFunct = modifyFunct.replace(matchedPattern, "Min");

		}
		return modifyFunct;

	}

	public static String modifyKeyAlias(String definition) {

		String regex = "(\\[[^\\[]*\\]\\.){0,}" + "\\[[^\\[]*\\]";
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(definition);
		String modifiedKeyDP = "";

		while (m.find()) {
			String sub_match = m.group();
			modifiedKeyDP = "";
			String key = sub_match.replaceAll("\\[", "").replaceAll("\\]$", "");
			if (sub_match.contains("].[")) {
				String keyDPArr[] = sub_match.split("\\]\\.\\[");
				String dP = keyDPArr[0].substring(1, keyDPArr[0].length());
				key = keyDPArr[1].substring(0, keyDPArr[1].length() - 1);
				modifiedKeyDP = key + "(" + dP + ")";
			}

			if (keyDPAndMergeDimension.containsKey(key)) {

				definition = definition.replace(sub_match,
						"[" + modifyResultObject(keyDPAndMergeDimension.get(key)) + "]");

			} else if (keyDPAndMergeDimension.containsKey(modifiedKeyDP)) {

				definition = definition.replace(sub_match,
						"[" + modifyResultObject(keyDPAndMergeDimension.get(modifiedKeyDP)) + "]");

			}

			if (keyDPAndMergeDimension.containsValue(key)) {

				definition = definition.replace(sub_match, "[" + modifyResultObject(key) + "]");

			}

			if (DuplicateAliasesForKey.containsKey(sub_match)) {
				definition = definition.replace(sub_match, DuplicateAliasesForKey.get(sub_match));
			}

		}
		return definition;
	}

	private String applyRoundOff(String modifyFunct) {
		if (name != null) {

			HashMap<String, String> formatDetails = new HashMap<String, String>();
			int noOfDecimalPlaces = 0;
			boolean percentPresent = false, formattingRequired = false;
			String temp;
			formatDetails = Program.formatDetails;
	
			if (!qualification.equalsIgnoreCase("dimension")
					&& !(dataType.equalsIgnoreCase("DateTime") || dataType.equalsIgnoreCase("String"))) {
				if (formatDetails.containsKey(name)) {

					formattingRequired = true;
					temp = formatDetails.get(name);
					if (temp.contains(".")) {
						String[] str = temp.split("\\.");
						char[] charArray = str[1].toCharArray();
						for (int i = 0; i < charArray.length; i++) {
							if (charArray[i] == '0' || charArray[i] == '#')
								noOfDecimalPlaces++;
							else if (charArray[i] == ',')
								break;
						}
						
					} else if (temp.equalsIgnoreCase("Standard"))
						noOfDecimalPlaces = 2;
					Pattern p = Pattern.compile("'%'\\[%\\]");
					Matcher match = p.matcher(temp);
					if (match.find()) {
						percentPresent = true;

					}
				}
/*				else if (!Program.kpiNamesFromTable.contains(name)) {
					formattingRequired = true;
					noOfDecimalPlaces = 12;
				}
*/
			}
			if (formattingRequired)
				modifyFunct = addRoundOffToDefinition(noOfDecimalPlaces, percentPresent, modifyFunct);
		}

		return modifyFunct;
	}

	private String addRoundOffToDefinition(int noOfDecimalPlaces, boolean percentPresent, String modifyFunct) {

		if (percentPresent) 
			modifyFunct = "=100*(" + modifyFunct.substring(1, modifyFunct.length()) + ")";
		modifyFunct = "=Round(" + modifyFunct.substring(1, modifyFunct.length()) + "," + noOfDecimalPlaces + ")";
		return modifyFunct;
	}

	private String handlingLogFunction(String modifyFunct) {
		String regex = "Log[0-9]{1,}(\\s)*\\(";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(modifyFunct);
		while (matcher.find()) {
			int index = matcher.end();
			int close = findClosingParenthesis(modifyFunct.toCharArray(), index);
			modifyFunct = modifyFunct.substring(0, index) + "Real(" + modifyFunct.substring(index, close + 1) + ")";
		}
		return modifyFunct;
	}

	private int findClosingParenthesis(char[] charArray, int index) {
		int open = 0, close = 0;
		for (int i = index; i < charArray.length; i++) {
			char c = charArray[i];
			if (c == '(')
				open++;
			else if (c == ')') {
				close++;
				if (close > open) {

					return i;
				}
			}

		}
		return close;

	}

	private String In_Function_handling(String def) {

		def = def.replaceAll("ForEach", "OVER");

		def = def.replaceAll("ForAll", "OVER All");

		def = def.replaceAll("OVER All", "OVER");

		String regex = "[^a-zA-Z0-9_]" + "IN" + "[^a-zA-Z0-9_]";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		java.util.regex.Matcher m = pattern.matcher(def);
		while (m.find()) {
			String matchedPattern = m.group();
			def = def.replace(matchedPattern,
					matchedPattern.charAt(0) + "IN" + matchedPattern.charAt(matchedPattern.length() - 1));

		}
		def = def.replace("IN)IN", ")IN");

		def = def.replace("InList(", "List(");
		regex = "\\[[^\\[]*\\]" + "(\\s)*" + "IN";
		pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		m = pattern.matcher(def);
		Map<String, String> map = new HashMap<>();
		while (m.find()) {
			String matched = m.group();
			def = def.replace(matched, "Sum" + "(" + matched + ")" + "IN");
			def = def.replace("IN)IN", ")IN");

		}

		regex = "[^a-zA-Z0-9_]" + "IN" + "[^a-zA-Z0-9_]";
		pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		m = pattern.matcher(def);
		while (m.find()) {
			String matchedPattern = m.group();
			def = def.replace(matchedPattern,
					matchedPattern.charAt(0) + "OVER" + matchedPattern.charAt(matchedPattern.length() - 1));

		}

		regex = "OVER" + "(\\s)*" + "\\(" + "[^)]*\\)";
		pattern = Pattern.compile(regex);
		m = pattern.matcher(def);
		while (m.find()) {
			String matchedPattern = m.group();
			String modify = matchedPattern.replace(";", ",");
			def = def.replace(matchedPattern, modify);

		}
		
		regex = "NoFilter" + "(\\s)*" + "\\(";
		pattern = Pattern.compile(regex);
		m = pattern.matcher(def);
		map = new HashMap<>();
		while (m.find()) {
			no_Filter_Flag=true;
			String matched = m.group();
			int open = m.end() - 1;
			int start = m.start();
			int close_index = findcloseParen(def.toCharArray(), open);

			String function = def.substring(start, close_index + 1);
			String replaced = function.replace(";All", "").replace(matched, "");
			replaced = replaced.substring(0, replaced.length() - 1);
			map.put(function, replaced);

		}
		for (String key : map.keySet()) {
			def = def.replace(key, map.get(key));
		}

		regex = "\\[[^\\[]*\\]" + "(\\s)*" + "OVER";
		pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		m = pattern.matcher(def);

		while (m.find()) {
			String matched = m.group();
			String remain_part = def.substring(0, m.start());
			String sub_regex = ".*(?<!(Max|Min|Sum|Avg)\\()$";
			Pattern sub_pattern = Pattern.compile(sub_regex, Pattern.CASE_INSENSITIVE);
			java.util.regex.Matcher m1 = sub_pattern.matcher(remain_part);
			if (!m1.find())
				continue;
			def = def.replace(matched, "Sum" + "(" + matched + ")" + "OVER");
			def = def.replace("OVER)OVER", ")OVER");
		}

		regex = "\\)" + "(\\s)*" + "OVER";
		pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		m = pattern.matcher(def);
		map = new HashMap<>();
		while (m.find()) {

			int open_index = findOpenParen(def.toCharArray(), m.start());
			String str = def.substring(open_index, m.end());
			String funct = def.substring(0, def.indexOf(str));
			String regex2 = "(.*)" + "(Max|Min|Sum|Avg|Count)" + "(\\s)*";
			Pattern pattern2 = Pattern.compile(regex2, Pattern.CASE_INSENSITIVE);
			java.util.regex.Matcher m2 = pattern2.matcher(funct);
			if (!m2.find()) {
				map.put(str, "Sum" + str);
			}
		}
		for (String no_aggregate : map.keySet()) {
			def = def.replace(no_aggregate, map.get(no_aggregate));
		}

		def = def.replace("List(", "InList(");

		return def;
	}

	private String replaceSpacesWithUnderscore(String replacedDef) {
		replacedDef = replacedDef.replace("\\[", "ESCAPEOPEN");
		replacedDef = replacedDef.replace("\\]", "ESCAPECLOSE");

		String regex = "\\[[^\\[]*\\]";
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(replacedDef);
		while (m.find()) {
			String matched = m.group();
			matched = matched.substring(1, matched.length() - 1).trim();
			matched = "[" + matched + "]";
//->
			if (Program.siName.contains("AIR, Overview (Busy Hour)_AIR; Overview; Busy Hour")){
				matched= matched.replace("_", " ");
			}
//->	
			matched = matched.replaceAll("\\s+", "_");
			replacedDef = replacedDef.replace(m.group(), matched);
		}

		replacedDef = replacedDef.replace("ESCAPEOPEN", "[");
		replacedDef = replacedDef.replace("ESCAPECLOSE", "]");
		return replacedDef;
	}

	/*
	 * private String replaceWithUnderscore(String removeTableName) { String regex =
	 * "\\[([^\\[]*)\\]"; HashSet<String> aliasNames = new HashSet<String>();
	 * aliasNames = Query.getAliasNames(); Pattern pattern = Pattern.compile(regex);
	 * Matcher m = pattern.matcher(removeTableName); while (m.find()) { String temp
	 * = m.group(1); temp = m.group(1).replaceAll("\\s", ""); if
	 * (aliasNames.contains(temp)) { char c = temp.charAt(0); if
	 * (Character.isDigit(c)) { temp = "_" + temp; } temp = temp.replaceAll("\\(",
	 * "_"); temp = temp.replaceAll("\\)", ""); temp = temp.replaceAll("-", "_");
	 * temp = temp.replaceAll("\\.", "_"); temp = m.group().replace(m.group(1),
	 * temp); removeTableName = removeTableName.replace(m.group(), temp); } } return
	 * removeTableName; }
	 */
	private String replaceWithUnderscore(String removeTableName) {
		String regex = "\\[([^\\[]*)\\]";
		HashSet<String> aliasNames = new HashSet<String>();
		aliasNames = Query.getAliasNames();
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(removeTableName);

		while (m.find()) {
			String temp = m.group(1);
			temp = temp.trim();
			if (aliasNames.contains(temp)) {
				char c = temp.charAt(0);
				if (Character.isDigit(c)) {
					temp = "_" + temp;
				}
				temp = temp.replaceAll("\\(", "_");
				temp = temp.replaceAll("\\)", "");
				temp = temp.replaceAll("-", "_");
				temp = temp.replaceAll("\\.", "__");
				temp = temp.replaceAll("%", "");
				temp = m.group().replace(m.group(1), temp);
				removeTableName = removeTableName.replace(m.group(), temp);
			}
			/*
			 * if(! modified_aliasNames.contains(matched)) {
			 * 
			 * matched = matched.replaceAll(" ", ""); matched = matched.replaceAll("\\(",
			 * "_"); matched = matched.replaceAll("-", "_"); matched =
			 * matched.replaceAll("\\)", ""); matched= matched.replaceAll("\\.", "_");
			 * matched= matched.replaceAll("AVG", "_AVG"); String matched_lower =
			 * matched.replaceAll("AVG", "avg");
			 * 
			 * if(modified_aliasNames.contains(matched) ||
			 * modified_aliasNames.contains(matched_lower)) { removeTableName =
			 * removeTableName.replace(m.group(), "[" + matched + "]");
			 * 
			 * }
			 * 
			 * 
			 * }
			 */
		}
		return removeTableName;
	}

	public static String removeTableName(String removeIfElseDef) {

		if (removeIfElseDef == null)
			return removeIfElseDef;

		String regex = "(\\[[^\\[]*\\]\\.){0,}" + "\\[[^\\[]*\\]";
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(removeIfElseDef);

		while (m.find()) {
			
			String matched_pattern = m.group();
			String match = matched_pattern;
			String sub_regex = "\\[[^\\[]*\\]";
			Pattern sub_pattern = Pattern.compile(sub_regex);
			Matcher m1 = sub_pattern.matcher(match);
			ArrayList<String> patterns = new ArrayList<>();
			int count = 0;
			while (m1.find()) {
				count++;
				String sub_match = m1.group();

				sub_match = sub_match.replaceAll("\\.", "_").replaceAll("\\[", "").replaceAll("\\]", "");

				patterns.add(sub_match);
			}

			String original_counter_name = matched_pattern.replaceAll("\\.", "").replaceAll("\\[", "").replaceAll("\\]",
					"");

			String counter_name = original_counter_name.toLowerCase();

			String string = "";

			if (matched_pattern.equals("[cscf2Originating].[InviteToAsAttempts]")
					|| matched_pattern.equals("[cscf2Terminating].[InviteToAsAttempts]")) {
				boolean flag = false;

				for (Entry<String, Map<String, String>> entry : Query.alias_mapWithDP.entrySet()) {
					if (entry.getKey().toLowerCase().contains("cscf2")) {
						counter_name = counter_name.replace("cscf2", "");
						for (Entry<String, String> entry1 : entry.getValue().entrySet()) {
							string = entry1.getKey().toLowerCase();
							string = string.substring(string.lastIndexOf('[') + 1, string.length() - 1);
							if (string.contains(counter_name)) {
								removeIfElseDef = removeIfElseDef.replace(matched_pattern, entry1.getValue());
								flag = true;
								break;
							}
						}
						if (flag)
							break;
					}
				}
				if (!flag) {
					for (Entry<String, Map<String, String>> entry : Query.counter_alias_mapWithDP.entrySet()) {
						if (entry.getKey().toLowerCase().contains("cscf2")) {
							counter_name = counter_name.replace("cscf2", "");
							for (Entry<String, String> entry1 : entry.getValue().entrySet()) {
								if (entry1.getKey().toLowerCase().contains(counter_name)) {
									removeIfElseDef = removeIfElseDef.replace(matched_pattern,
											"[" + entry1.getValue() + "]");
									flag = true;
									break;
								}
							}
							if (flag)
								break;
						}
					}
				}
				continue;
			}

			String modify_counter_name = "", dataProvider = "";
			if (count >= 3) {
				modify_counter_name = String.join("", patterns.subList(1, patterns.size()));
				dataProvider = patterns.get(0);
			}
       
			String original_pattern = matched_pattern;
			int index = matched_pattern.lastIndexOf('[');
			String alias = matched_pattern.substring(index);
			String modified_alias = alias.replaceAll(" ", "");
			matched_pattern = matched_pattern.replace(alias, modified_alias);
//-->
			if (Program.siName.contains("AIR, Overview (Busy Hour)_AIR; Overview; Busy Hour")) {
				if (matched_pattern.equals("[AIR_VS_INTF].[Request]")
						|| matched_pattern.equals("[AIR_VS_INTF].[Response]")) {
					matched_pattern = matched_pattern.toUpperCase();
				}
			}
//-->
			if (alias_map.containsKey(matched_pattern)) {
				removeIfElseDef = removeIfElseDef.replace(original_pattern, alias_map.get(matched_pattern));
			} else if (counter_alias_map.containsKey(modify_counter_name) && count >= 3) {

				if (counter_alias_mapWithDP.containsKey(dataProvider)
						&& counter_alias_mapWithDP.get(dataProvider).containsKey(modify_counter_name)) {
					String replacedCol = counter_alias_mapWithDP.get(dataProvider).get(modify_counter_name);
					removeIfElseDef = removeIfElseDef.replace(original_pattern, "[" + replacedCol + "]");
				} else {
					removeIfElseDef = removeIfElseDef.replace(original_pattern,
							"[" + counter_alias_map.get(modify_counter_name) + "]");
				}
			} else if (counter_alias_map.containsKey(original_counter_name)) {
				if (!Query.aliasNames.contains(original_counter_name)) {
					removeIfElseDef = removeIfElseDef.replace(original_pattern,
							"[" + counter_alias_map.get(original_counter_name) + "]");
				} else {
					removeIfElseDef = removeIfElseDef.replace(original_pattern, "[" + original_counter_name + "]");
				}

			} else {
				if (flag) {
					removeIfElseDef = removeIfElseDef.replace(original_pattern, original_pattern.substring(index));
				}
			}
		}
		
		return removeIfElseDef;
	}
	
	private String updateCounterInCSCF(String definition) {
		String regex = "(\\[[^\\[]*\\]\\.){0,}" + "\\[[^\\[]*\\]";
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(definition);

		while (m.find()) {
			String matched_pattern = m.group();
			String match = matched_pattern;
			String sub_regex = "\\[[^\\[]*\\]";
			Pattern sub_pattern = Pattern.compile(sub_regex);
			Matcher m1 = sub_pattern.matcher(match);

			int count = 0;
			while (m1.find()) {
				count++;
			}
			if (count == 1 && (matched_pattern.equalsIgnoreCase("[cscfOriginatingInviteToAsAttempts]")
					|| matched_pattern.equalsIgnoreCase("[cscfTerminatingInviteToAsAttempts]")
					|| matched_pattern.equalsIgnoreCase("[cscfOriginatingInviteNoAsAttempts]")
					|| matched_pattern.equalsIgnoreCase("[cscfTerminatingInviteNoAsAttempts]"))) {
				for (Entry<String, String> entry : alias_map.entrySet()) {
					if (entry.getKey().contains(matched_pattern) && !entry.getValue().contains("CSCF2")) {
						definition = definition.replace(matched_pattern, entry.getValue());
						break;
					}
				}
			}
		}
		return definition;
	}

	public String convertToNEVersion(String formula) {
		String reg = "\\[((DA)?_?NE_?VERSION_?\\(*[^\\[]*\\)*)\\]";
		Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher(formula);
		while (m.find()) {
			String matchedPattern = m.group(1);
			if (!m.group(1).equals("NE_Version_Count_Per_Day")) {
				formula = formula.replace(matchedPattern, "NE_VERSION");
			}

		}
		return formula;
	}

	private String univCounterMap(String removeIfElseDef) {
		flag = true;
		UnivObjToCounterMapping obj1 = Program.getUnivObject();
		String univObjectDef = removeIfElseDef;
		univObjectDef = obj1.compareUnivObjAndCounter(removeIfElseDef, name);
		univObjectDef = removeTableName(univObjectDef);
		flag = false;
		return univObjectDef;
	}

	private String convertToNetAnStatement(String updatedDefinition) {
		updatedDefinition = updatedDefinition.replaceAll("Then", ",");
		updatedDefinition = updatedDefinition.replaceAll("Else If", ",If");
		updatedDefinition = updatedDefinition.replaceAll("Else", ",");
		return updatedDefinition;
	}

	private String modifyFunctions(String def) {

		for (String BOFunct : functions.keySet()) {

			if (def.contains(BOFunct)) {
				if (BOFunct.equals("Range"))
					def = replaceOperatorFunct(def, BOFunct, ":");
				else
					def = replaceOperatorFunct(def, BOFunct, ";");
			}
		}

		return otherChangesFunct(def);
	}

	private String otherChangesFunct(String def) {

		// =If (Match(Lower([NE Type]);"*isite*") Or Match(Lower([NE Type]);"*sbg-is*"))
		// Then 1 Else 0

		String reg = "Match" + "(\\s)*" + "\\(" + "([a-zA-Z])*" + "\\(*" + "(\\[[^\\[]*\\])*" + "(\\.)*"
				+ "\\[[^\\[]*\\]" + "\\)*" + ";" + "[^)]*\\)";
		Pattern pattern = Pattern.compile(reg);
		java.util.regex.Matcher m = pattern.matcher(def);
		
		while (m.find()) {
			String matchedPattern = m.group();

			int start = matchedPattern.indexOf('(');
			int end = matchedPattern.lastIndexOf(')');
			String temp = matchedPattern.substring(start + 1, end);
			temp = temp.replace("Match", "");
			String splitRegex = "\\]" + "\\)*" + ";" + "\\s*" + "\"";
			String arr[] = temp.split(splitRegex);

			String part2 = arr[1].substring(0, arr[1].length());

			String regex = "\\*" + ".*" + "\\*";
			Pattern pattern2 = Pattern.compile(regex);
			java.util.regex.Matcher m1 = pattern2.matcher(part2);


			while (m1.find()) {

				String mod = "";
				if (m1.group().contains(".")) {
					// mod = m1.group().substring(m1.group().indexOf('.'), m1.group().indexOf('.') +
					// 1);
					mod = m1.group().replace(".", "\\" + "\\.");
				} else
					mod = m1.group();

				// temp = ".*" + mod + ".*" ;
				temp = mod.replace("*", ".*");
				part2 = part2.replace(m1.group(), temp);

			}

/*		while (m.find()) {
			String matchedPattern = m.group();
			int start = matchedPattern.indexOf('(');
			int end = matchedPattern.lastIndexOf(')');
			String temp = matchedPattern.substring(start + 1, end);
			temp = temp.replace("Match", "");
			String splitRegex = "\\]" + "\\)*" + ";" + "\\s*" + "\"";
			String arr[] = temp.split(splitRegex);
			String part2 = arr[1].substring(0, arr[1].length());
			String regex = "\\*" + "(.*)" + "\\*";
			Pattern pattern2 = Pattern.compile(regex);
			java.util.regex.Matcher m1 = pattern2.matcher(part2);

			while (m1.find()) {

				String mod = "";
				if (m1.group().contains(".")) {
					mod = m1.group().substring(m1.group().indexOf('.'), m1.group().indexOf('.') + 1);
					mod = mod.replace(".", "\\" + "\\.");
				} else
					mod = m1.group(1);

				temp = ".*" + mod + ".*";
				part2 = part2.replace(m1.group(), temp);
			}
*/
			int count = 0;
			String sub_regex = "\\*";
			Pattern sub_pattern = Pattern.compile(sub_regex);
			java.util.regex.Matcher sub_m = sub_pattern.matcher(part2);
			while (sub_m.find()) {
				count++;

			}
			if (count == 1) {

				String inner_regex = "\\*" + "(.*)";
				Pattern inner_pattern = Pattern.compile(inner_regex);
				java.util.regex.Matcher inner_m = inner_pattern.matcher(part2);
				while (inner_m.find()) {
					part2 = part2.replace(inner_m.group(),
							"." + inner_m.group().substring(0, inner_m.group().length() - 1) + "$" + "\"");
				}
			}
			part2 = part2.replaceAll("\\?", "\\.");
			
		
			
			if(! part2.endsWith("$" + "\""))
			      part2 = "^" + part2.substring(0, part2.length()-1) + "$" + "\"";
			else
				  part2 = "^" + part2.substring(0, part2.length())  ;   
			// temp = "(" + "upper" + "(" + arr[0] + "]" + ")" + "~=" + "\"" + part2 + ")";

			if (arr[0].startsWith("[")) {
				temp = "(" + arr[0] + "]" + "~=" + "\"" + part2 + ")";
				
			} else {
				temp = "(" + arr[0] + "]" + ")" + "~=" + "\"" + part2 + ")";
				
			}

           
			def = def.replace(matchedPattern, temp);
		}

		String s = "Count";
		if (def.contains("Count")) {
			String def_from_Count = def.substring(def.indexOf("Count"), def.length());
			pattern = Pattern.compile("(" + "(.*)" + ";" + "(\\s*)" + "DISTINCT" + ")");
			String modify = "";
			String arr[] = def_from_Count.split("Count");
			ArrayList<String> Count_parts = new ArrayList<>();
			for (String part : arr) {
				m = pattern.matcher(part);
				if (m.find()) {
					String matchedPattern = m.group();
					String replaced = matchedPattern.substring(matchedPattern.indexOf(";"), matchedPattern.length());
					matchedPattern = matchedPattern.replace(replaced, "");
					modify = part.replace(matchedPattern, "UniqueCount" + matchedPattern);
				} else if (!part.equals(""))
					modify = "Count" + part;

				Count_parts.add(modify);
			}
			String result = def.substring(0, def.indexOf("Count")) + String.join("", Count_parts);
			def = result;
		}
		pattern = Pattern.compile("([a-zA-Z0-9\\+])+" + "\\+" + "([a-zA-Z\\+])+");
		m = pattern.matcher(def);
		while (m.find()) {
			String matchedPattern = m.group();
			String replaced = matchedPattern.replaceAll("\\+", "\\&");
			def = def.replace(matchedPattern, replaced);
		}
		pattern = Pattern.compile("([a-zA-Z\\+])+" + "\\+" + "([a-zA-Z0-9\\+])+");
		m = pattern.matcher(def);
		while (m.find()) {
			String matchedPattern = m.group();
			String replaced = matchedPattern.replaceAll("\\+", "\\&");
			def = def.replace(matchedPattern, replaced);
		}
		pattern = Pattern.compile("(\"|\\')+" + "(\\s*)" + "\\+");
		m = pattern.matcher(def);
		while (m.find()) {
			String matchedPattern = m.group();
			String replaced = matchedPattern.replaceAll("\\+", "\\&");
			def = def.replace(matchedPattern, replaced);
		}
		pattern = Pattern.compile("\\+" + "(\\s*)" + "(\"|\\')+");
		m = pattern.matcher(def);
		while (m.find()) {
			String matchedPattern = m.group();
			String replaced = matchedPattern.replaceAll("\\+", "\\&");
			def = def.replace(matchedPattern, replaced);
		}

		pattern = Pattern.compile("(Substring|Replace|Concatenation)" + "(" + "[^)]*" + ")");
		m = pattern.matcher(def);
		while (m.find()) {
			int start = m.start();
			int end = m.end();
			if (start - 1 >= 0 && def.charAt(start - 1) == '+')
				def = def.replace(s.charAt(start - 1), '&');
			if (end + 1 < s.length() && s.charAt(end + 1) == '+')
				def = def.replace(s.charAt(end + 1), '&');
		}

		reg = "Is" + "(\\s)*" + "Null" + "(\\s)*" + "\\(" + "(\\s)*" + "(\\[[^\\[]*\\]|\\+|\\.)*" + "(\\s)*" + "\\)";

		pattern = Pattern.compile(reg);
		m = pattern.matcher(def);

		while (m.find()) {
			String matchedPattern = m.group();
			String function = matchedPattern.replace("IsNull", "");
			def = def.replace(matchedPattern, function + " Is Null ");
		}

		Map<String, String> map = new HashMap<>();
		String subFunct = "", replaced = "", modifySubFunct = "";
		reg = "LeftPad" + "(\\s)*" + "\\(";
		pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
		m = pattern.matcher(def);
		while (m.find()) {
			int start = m.start();
			int open = m.end() - 1;
			int close_index = findcloseParen(def.toCharArray(), open);
			String function = def.substring(start, close_index + 1);
			if (function.contains("(") && function.contains(";")) {
				subFunct = function.substring(function.indexOf("(") + 1, function.indexOf(";"));

				if (subFunct.contains(",")) {
					modifySubFunct = subFunct
							.replace(subFunct.substring(subFunct.lastIndexOf(","), subFunct.length() - 1),"" );
				//			.replace(subFunct.substring(subFunct.lastIndexOf(","), subFunct.length() - 1), ",0");
				}
                replaced = function.replace(subFunct," '0' & String"  + modifySubFunct);
			//	replaced = function.replace(subFunct," '0' & String" + "(" + modifySubFunct + ")");
				replaced = replaced.replace(replaced.substring(replaced.lastIndexOf(";"), replaced.length() - 1), "");
			}

			map.put(function, replaced);

		}
		for (String funct : map.keySet()) {

			def = def.replace(funct, map.get(funct));
		}

		reg = "(\\+)*" + "LeftPad" + "(\\s)*" + "\\(" + "([^\\;]*\\;)*" + "[^)]*\\)" + "(\\+)*";
		pattern = Pattern.compile(reg);
		m = pattern.matcher(def);
		while (m.find()) {
			String matchedPattern = m.group();

			String modify = matchedPattern.replace("+", "&");
			modify = modify.replace(";", ",");
			modify = modify.replace("LeftPad", "Right");
			def = def.replace(matchedPattern, modify);

		}

		reg = "Where" + "(\\s)*" + "\\(" + "([^\\;]*\\;)" + "(\\[[^\\[]*\\]|\\.)*" + "\\)";
		Set<String> set = new HashSet<>();

		pattern = Pattern.compile(reg);
		m = pattern.matcher(def);
		while (m.find()) {
			String matchedPattern = m.group();
			String arr[] = matchedPattern.split(";");
			String removed = "";
			if (arr.length > 1) {
				removed = ";" + arr[1];
				set.add(removed.substring(0, removed.length() - 1));
			}

		}
		for (String strr : set) {
			def = def.replace(strr, "");
		}

		reg = "If" + "(\\s)*" + "\\(" + "([^\\;|\\,]*(\\;|\\,))*" + "[^)]*\\)";

		pattern = Pattern.compile(reg);
		m = pattern.matcher(def);
		while (m.find()) {
			String matchedPattern = m.group();
			String modify = matchedPattern.replace(";", ",");
			def = def.replace(matchedPattern, modify);

		}

		String regex = "Power" + "(\\s)*" + "\\(";
		pattern = Pattern.compile(regex);
		m = pattern.matcher(def);
		map = new HashMap<>();
		while (m.find()) {
			int start = m.start();
			int open = m.end() - 1;
			int close_index = findcloseParen(def.toCharArray(), open);
			String function = def.substring(start, close_index + 1);
			String funct_part = def.substring(open + 1, close_index);
			String funct[] = funct_part.split(",");
			funct[0] = "Real" + "(" + funct[0] + ")";
			map.put(function, "Power" + "(" + funct[0] + "," + funct[1] + ")");
		}
		for (String function : map.keySet()) {
			def = def.replace(function, map.get(function));
		}

		regex = "(\\[|\\()" + "(\\s)*" + "OSS" + "(\\s)*" + "(_)*" + "Id" + "(\\]|\\))";
		pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		java.util.regex.Matcher m1 = pattern.matcher(def);
		while (m1.find()) {
			String matched = m1.group();
			def = def.replace(matched, "[" + "OSS_ID" + "]");
		}

		if (!Distinct_count_alias.isEmpty())
			def = def.replaceAll("\\[" + "DistinctCellCount" + "\\]", "\\[" + Distinct_count_alias + "\\]");
		def = def.replaceAll("\\[" + "1440" + "\\]", "1440");
		def = def.replaceAll("Length", "len");
		def = def.replaceAll("NumberOfRows", "Count");
		def = def.replaceAll("RowIndex", "RowId");
		def = def.replaceAll("Ceil", "Ceiling");
		def = def.replaceAll("MonthNumberOfYear", "Month");
		def = def.replaceAll("DayNumberOfYear", "DayOfYear");
		def = def.replaceAll("DayNumberOfWeek", "DayOfWeek");
		def = def.replaceAll("DayNumberOfMonth", "DayOfMonth");
		def = def.replaceAll("CurrentTime", "DateTimeNow");
		def = def.replaceAll("CurrentDate", "Today");
		def = def.replaceAll("ForAll", "OVER All");
		def = def.replaceAll("ToNumber", "Real");

		return def;
	}

	private String replaceOperatorFunct(String def, String BOfunct, String operator) {

		String regex = BOfunct + "(\\s)*" + "\\(";
		Pattern pattern = Pattern.compile(regex);
		java.util.regex.Matcher m = pattern.matcher(def);
		Map<String, String> map = new HashMap<>();
		while (m.find()) {
			int start = m.start();
			int open = m.end() - 1;
			int close_index = findcloseParen(def.toCharArray(), open);
			String function = def.substring(start, close_index + 1);
			String replaced = function.replace(operator, ",");
			String netan_funct = replaced.replace(BOfunct, functions.get(BOfunct));
			map.put(replaced, netan_funct);
			def = def.replace(function, replaced);
		}
		for (String funct : map.keySet()) {
			def = def.replace(funct, map.get(funct));
		}
		return def;
	}

	private int findcloseParen(char[] text, int openPos) {
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

	public static int findOpenParen(char[] text, int closePos) {
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

	public static int findOpenParenSquareBracket(char[] text, int closePos) {
		int openPos = closePos;
		int counter = 1;
		while (counter > 0 && openPos > 0) {
			char c = text[--openPos];
			if (c == '[') {
				counter--;
			} else if (c == ']') {
				counter++;
			}
		}
		return openPos;
	}

	public String tableNameRemoval(String formula) {

		ArrayList<String> list1 = new ArrayList<String>();
		ArrayList<String> list2 = new ArrayList<String>();

		String abc[] = formula.split("\\.");

		for (String jkl : abc) {
			for (String tableName : tableNameList) {
				if (jkl.endsWith("[" + tableName + "]")) {
					list1.add(jkl);
				}
			}
		}
		for (String pqr : abc) {
			for (String keyName : dataNameList) {
				if (pqr.startsWith("[" + keyName + "]")) {
					list2.add(pqr);
				}
			}
		}
		for (String str1 : list1) {
			for (String str2 : list2) {
				String matching = str1 + "." + str2;
				String updatedstr1 = str1.substring(0, str1.lastIndexOf("["));
				String requiredString = updatedstr1 + str2;
				formula = formula.replace(matching, requiredString);
			}
		}
		return formula;
	}

	public String ifStatementtoNetAn(String def) {

		String regex = "\\[[^\\[]*\\]";
		Pattern pattern = Pattern.compile(regex);
		java.util.regex.Matcher m = pattern.matcher(def);
		while (m.find()) {
			String matchedPattern = m.group();
			String replaced = matchedPattern.replace("If", "@");
			def = def.replace(matchedPattern, replaced);
		}

		String else_part = "", outer_if_part = "", temporary = "";
		int open_if = -1, close_if = 0, counter_if = 0, loopCount = 0;
		boolean containsElse = false, ifInElse = false;

		if (!def.contains("ElseIf") && def.contains("Else")) {

			containsElse = true;
			int startIndex = 0, endIndex = 0;
			int open = 0, close = 0;
			startIndex = def.indexOf("(If");
			if (startIndex > -1) {
				for (int i = startIndex; i < def.length(); i++) {
					char c = def.charAt(i);
					if (c == '(') {
						open++;
					}
					if (c == ')') {
						close++;
						if (close == open) {
							endIndex = i;
							break;
						}
					}
				}
				outer_if_part = def.substring(0, startIndex);
				if (endIndex < def.length() - 1)
					else_part = def.substring(endIndex + 1);

				def = def.substring(startIndex, endIndex + 1);
			}

			for (int i = 0; i < outer_if_part.length(); i++) {
				char x = outer_if_part.charAt(i);
				if (x == '(') {
					open_if++;
				}
				if (x == ')') {
					close_if++;
					if (close_if > open_if) {
						outer_if_part = outer_if_part.substring(0, i) + outer_if_part.substring(i + 1);
						counter_if++;
						i--;
					}
				}
			}
		}
		do {
			if (def.contains("If")) {
				int i = 0, counter = 0, flag1 = 0;
				String result = "", temp = "";
				String[] str2 = def.split("If");
				for (int k = 1; k < str2.length; k++) {
					int open = -1, close = 0, flag = 0, count = 0;
					String tempString = "";
					boolean containsMultipleElse = false;
					if (!(str2[k].startsWith("("))) {
						str2[k] = "(" + str2[k];
						flag = 1;
					}
					pattern = Pattern.compile("Else");
					m = pattern.matcher(str2[k]);
					while (m.find()) {
						count++;
					}
					if (count == 2) {
						containsMultipleElse = true;
						tempString = str2[k].substring(str2[k].lastIndexOf("Then"));
						str2[k] = str2[k].substring(0, str2[k].lastIndexOf("Then"));
						count = 0;
					}
					for (i = 0; i < str2[k].length(); i++) {
						char x = str2[k].charAt(i);
						if (x == '(') {
							;
							open++;
						}
						if (x == ')') {
							close++;
							if (close > open) {
								str2[k] = str2[k].substring(0, i) + str2[k].substring(i + 1);
								counter++;
								count++;
								open = 0;
								close = 0;
								i--;
							}
						}
					}
					if (flag1 == 1) {
						int value = 0, len = str2[k].length() - 1, tempValue = 0;
						String str1 = "", str3 = "";
						char character = str2[k].charAt(len);
						if (temp.charAt(temp.length() - 1) == '(') // no. of brackets to be added
							value = 2;
						else
							value = 1;
						tempValue = value;
						if (character == '+') {
							str1 = str2[k].substring(0, len);
							str3 = str2[k].substring(len);
						} else if (character == '(') {
							str1 = str2[k].substring(0, len - 1);
							str3 = str2[k].substring(len - 1);
						} else
							str1 = str2[k];
						while (value > 0) {
							str1 = str1 + ")";
							value--;
						}
						str2[k] = str1 + str3;
						counter -= tempValue;
						flag1 = 0;
						temp = "";
					}
					int indexOfPlus = str2[k].lastIndexOf('+');
					if (indexOfPlus > 0) {
						if (indexOfPlus == str2[k].length() - 2 || indexOfPlus == str2[k].length() - 1) {
							flag1 = 1;
							temp = str2[k];
						}
					}
					if (flag == 1)
						counter++;

					if (containsMultipleElse) {
						counter -= count;
						while (count > 0) {
							str2[k] += ")";
							count--;
						}
						str2[k] += tempString;
					}
					result = result + "If" + str2[k];
				}
				result = str2[0] + result;

				while (counter > 0) {
					result += ")";
					counter--;
				}
				def = result;
				loopCount++;
			}
			regex = "\\[[^\\[]*\\]";
			pattern = Pattern.compile(regex);
			m = pattern.matcher(def);
			while (m.find()) {
				String matchedPattern = m.group();
				String replaced = matchedPattern.replace("@", "If");
				def = def.replace(matchedPattern, replaced);
			}
			ifInElse = false;
			if (else_part.contains("If")) {
				ifInElse = true;
				temporary = def;
				def = else_part;
				else_part = "";
			}
		} while (ifInElse);

		if (loopCount > 1) {
			else_part = def;
			def = temporary;
		}
		if (containsElse) {
			if (!outer_if_part.equals(""))
				def = outer_if_part + " " + def;
			if (!else_part.equals(""))
				def = def + " " + else_part;
		}
		while (counter_if > 0) {
			def += ")";
			counter_if--;
		}
		return def;
	}

	public String getFormattedString() {

		String siName = Program.getSiName();
		definition = definition.trim();
		String def = definition.substring(1, definition.length());
		def = def.replaceAll("\n", "");
		def = def.replaceAll("\"", "'");
		def = def.replaceAll(",,", ",");
		name = name.trim();
		name = name.replaceAll("\\s+", "_");

		if (name.equals("Time_Resolution") && siName.contains("Raw")) {
			def = '\'' + "RawData" + '\'';
		}
		if (no_Filter_Flag == true) {
			def = "NoFilter-" + def;
		}
		return dataType + "," + id + "," + "\"" + name + "\"" + "," + "\"" + def + "\"" + "," + "\"" + qualification
				+ "\"";
	}

	@Override
	public String toString() {
		return "Formula{" + "dataType='" + dataType + '\'' + ", qualification='" + qualification + '\'' + ", id='" + id
				+ '\'' + ", name='" + name + '\'' + ", formulaLanguageId='" + formulaLanguageId + '\''
				+ ", definition='" + definition + '\'' + '}';
	}
}
