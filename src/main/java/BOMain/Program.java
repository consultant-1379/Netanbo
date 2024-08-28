package BOMain;

import static jsonPojo.Query.Query.Distinct_count_alias;
import static jsonPojo.Query.Query.NE_NAME_details;
import static jsonPojo.Query.Query.alias_data_provider;
import static jsonPojo.Query.Query.alias_map;
import static jsonPojo.Query.Query.alias_table_set;
import static jsonPojo.Query.Query.modifyFROMKeyword;
import static jsonPojo.Query.Query.modifyQuery;
import static jsonPojo.Query.Query.modifyResultObject;
import static jsonPojo.Query.Query.prompt_map_CustomQuery;
import static jsonPojo.URLConstants.CMS_URL;
import static jsonPojo.URLConstants.DOCUMENT_ALERTERS_URL;
import static jsonPojo.URLConstants.DOCUMENT_ALERTER_DETAILS_URL;
import static jsonPojo.URLConstants.DOCUMENT_DATA_PROVIDER_BYID_URL;
import static jsonPojo.URLConstants.DOCUMENT_DATA_PROVIDER_URL;
import static jsonPojo.URLConstants.DOCUMENT_LINK_BYID_URL;
import static jsonPojo.URLConstants.DOCUMENT_LINK_URL;
import static jsonPojo.URLConstants.DOCUMENT_REPORTS_BYID_URL;
import static jsonPojo.URLConstants.DOCUMENT_REPORTS_URL;
import static jsonPojo.URLConstants.DOCUMENT_VARIABLES_URL;
import static jsonPojo.URLConstants.DOCUMENT_VARIABLE_BYID_URL;
import static jsonPojo.URLConstants.FOLDERS_URL;
import static jsonPojo.URLConstants.PROMPT_FILTER_URL;
import static jsonPojo.URLConstants.REFRESH_PARAMETERS_URL;
import static jsonPojo.URLConstants.REPORT_ELEMENTS_URL;
import static jsonPojo.URLConstants.REPORT_ELEMENT_DETAILS_URL;
import static jsonPojo.URLConstants.REPORT_EXPORT_URL;
import static jsonPojo.URLConstants.RESULT_OBJECT_URL;
import static jsonPojo.URLConstants.query_url;
import static jsonPojo.Variables.Formula.flag;
import static jsonPojo.dataProviders.Tables.Report.contentList;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.opencsv.CSVReader;

import jsonPojo.Query.Query;
import jsonPojo.Query.QueryMapper;
import jsonPojo.ReportElements.Action;
import jsonPojo.ReportElements.Alerter;
import jsonPojo.ReportElements.AlerterDetails;
import jsonPojo.ReportElements.AlerterDetailsMapper;
import jsonPojo.ReportElements.AlerterFormatDetails;
import jsonPojo.ReportElements.AlerterFormatDetailsMapper;
import jsonPojo.ReportElements.Alerters;
import jsonPojo.ReportElements.AlertersMapper;
import jsonPojo.ReportElements.AlertersWrapper;
import jsonPojo.ReportElements.Axes;
import jsonPojo.ReportElements.Axis;
import jsonPojo.ReportElements.BackGround;
import jsonPojo.ReportElements.Color;
import jsonPojo.ReportElements.Condition;
import jsonPojo.ReportElements.ConditionalFormula;
import jsonPojo.ReportElements.Conditions;
import jsonPojo.ReportElements.Content;
import jsonPojo.ReportElements.Data;
import jsonPojo.ReportElements.ElementData;
import jsonPojo.ReportElements.ElementFormula;
import jsonPojo.ReportElements.ElementMapper;
import jsonPojo.ReportElements.Expression;
import jsonPojo.ReportElements.Font;
import jsonPojo.ReportElements.Format;
import jsonPojo.ReportElements.FormatRule;
import jsonPojo.ReportElements.ReportElements;
import jsonPojo.ReportElements.ReportElementsMapper;
import jsonPojo.ReportElements.ReportElementsWrapper;
import jsonPojo.ReportElements.Rule;
import jsonPojo.ReportElements.Style;
import jsonPojo.ReportElements.Template;
import jsonPojo.Reports.Reports;
import jsonPojo.Reports.ReportsMapper;
import jsonPojo.Reports.ReportsWrapper;
import jsonPojo.Variables.Formula;
import jsonPojo.Variables.FormulaMapper;
import jsonPojo.Variables.VariableMapper;
import jsonPojo.Variables.VariableWrapper;
import jsonPojo.Variables.Variables;
import jsonPojo.dataProviders.DataProviderWrapperMapper;
import jsonPojo.dataProviders.DataProviders;
import jsonPojo.dataProviders.DataProvidersMapper;
import jsonPojo.dataProviders.ResultObjects;
import jsonPojo.dataProviders.Tables.Report;

public class Program {

	public static Logger logger = LogManager.getLogger(Program.class);

	public static String serverURI = "";
	public static String user_name = "";
	public static String password = "";
	public static String option = "";
	public static String node = "";
	public static int no_of_reports = 0;
	public static int reports_passed = 0;
	public static int reports_failed = 0;
	public static String DB_URL = "";
	public static String USER = "";
	public static String PASS = "";

	static int index = 0;
	int j = 1;
	public static int inc = 0;

	// public static String path = "C:\\csv2";
	public static String path = "";

	public static String OS = System.getProperty("os.name");

	private static String typeIdName;

	static Scanner sc;

	private static String currentDirectory = System.getProperty("user.dir");

	public static String dataFilters = "";

	public static HashMap<String, String> dataFiltersAliasMap = new HashMap<>();

	public static HashSet<String> kpiNames = new HashSet<String>();

	public static HashSet<String> SiNameAndPath = new HashSet<>();

	public static HashSet<String> multiColumnName = new HashSet<String>();

	private static HashMap<String, String> keyCounterDetails = new HashMap<String, String>();

	public static HashMap<String, String> mergeDimensionAndId = new HashMap<String, String>();

	public HashMap<Integer, ArrayList<String>> alerters = new HashMap<Integer, ArrayList<String>>();

	public HashMap<Integer, String> alerterIdandDesc = new HashMap<Integer, String>();

	public HashMap<Integer, ArrayList<String>> alerterIdandConditionalFormula = new HashMap<Integer, ArrayList<String>>();

	public static HashMap<String, ArrayList<Integer>> alerterDetails = new HashMap<String, ArrayList<Integer>>();

	public static HashSet<String> siNamePathAndFolder = new HashSet<>();

	public static ArrayList<String> siNamesList = new ArrayList<>();

	public static ArrayList<String> mergeDimensionKeysId = new ArrayList<>();

	public static HashMap<String, Long> siNameandReportId = new HashMap<>();

	public static HashMap<String, String> siNameandKeyword = new HashMap<>();

	private static LinkedHashMap<Integer, String> parameterIds = new LinkedHashMap<Integer, String>();

	public static String folderPath = "";

	public static String folderName = "";

	public static boolean widFilePresent = true;

	public static ArrayList<String> tableNameList = new ArrayList<>();

	public static String mainParentId = null;

	public static HashSet<String> counterNames = new HashSet<String>();

	public static String chart_details = "";

	public static String table_details = "";

	public static String sections = "";

	public static ArrayList<String> ReportTablesList = new ArrayList<>();

	public static ArrayList<String> ReportTableKeysList = new ArrayList<>();

	public static String chartType = "";

	public static HashMap<String, String> formatDetails = new HashMap<String, String>();

	public static HashSet<String> kpiNamesFromTable = new HashSet<String>();

	public static HashMap<String, String> dPNameandID = new HashMap<>();

	public static HashMap<String, String> keyAndMergeDimension = new HashMap<>();

	public static HashMap<String, String> keyDPAndMergeDimension = new HashMap<>();

	public static HashMap<String, String> keyandDPKey = new HashMap<>();

	private static HashMap<String, String> mergeDimenDataType = new HashMap<>();

	public static HashMap<String, ArrayList<String>> mergeDimenDetails = new HashMap<>();

	public static HashMap<String, String> keyNameandDataType = new HashMap<>();

	public static HashSet<String> keyAliasList = new HashSet<>();

	public static HashMap<String, String> Alias_Qualification = new HashMap<>();

	private static HashMap<String, String> counterRoundOffValues = new HashMap<>();

	public static HashSet<String> promptNames = new HashSet<String>();

	private static HashSet<String> typeIds = new HashSet<String>();

	private static HashMap<String, String> multipleQueryAlias = new HashMap<String, String>();

	public static boolean isMultipleQueryPresent = false;

	public static HashSet<String> node_list = new HashSet<String>();

	private Map<String, String> neNameDefinition = new HashMap<String, String>();

	private Map<String, String> neNameKPIandmodifyDefinition = new HashMap<String, String>();
	
	private HashMap<String, String > tbleName = new HashMap<String, String>();
	
	private String tempId;
	
	public boolean stylePresent =false;
	
	private String bg_color="";
	
	private String font_color="";
	
	public HashMap<Integer, String> aleters1= new HashMap<>();
	public HashMap<Integer,String> alerterList_Map=new HashMap<Integer,String>();

	public static long reportId;

	public static String csvPath = "";

	public static String report = "";

	BO_Connection connection;

	static HttpUtilities httputils;

	String dir, subDir;

	public static String siName;

	public static String node_type;

	public static String pathSeperator;

	public String resultObjects;

	String queryFilePath;
	String reportsFilePath;
	String variablesFilePath;
	String promptFilePath;
	static String response = "";
	static int size = 0;

	BufferedWriter queryWriter, reportsWriter, variableWriter;

	private String keyNamesOfCustomQuery = "";

	static BufferedWriter promptWriter;
	static BufferedWriter ReportDetailsListWriter;
	static BufferedWriter ReportDetailsGenerateWriter;

	String newLine = System.getProperty("line.separator");

	static ArrayList<String> dataNameList = null;
	static String log4jPath;

	static UnivObjToCounterMapping obj1 = null;
	static HashMap<String, String> operators = new HashMap<String, String>();

	static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm");

	static FileAppender fa = new FileAppender();

	static {
		if (OS.contains("Windows")) {
			pathSeperator = "//";
		} else {
			pathSeperator = "/";
		}
		fa.setName("FileLogger");
		// log4jPath = "H:"+pathSeperator+"MyAutomationCode"+pathSeperator; //if log
		// file path is taken as command line argument, assign that variable here
		log4jPath = "C:" + pathSeperator + "csv" + pathSeperator;
		fa.setFile(log4jPath + "connection_" + dateFormat.format(new Date()) + ".log");
		fa.setLayout(new PatternLayout("%p | %d{HH:mm:ss:SSS} | [%t] | %C | %l | %m%n"));
		fa.setThreshold(Level.ALL);
		fa.activateOptions();
		Logger.getRootLogger().addAppender(fa);
		operators.put("Less", "<");
		operators.put("LessOrEqual", "<=");
		operators.put("Greater", ">");
		operators.put("GreaterOrEqual", ">=");
		operators.put("Equal", "=");
		operators.put("NotEqual", "<>");
	}

	public static UnivObjToCounterMapping getUnivObject() {
		return obj1;
	}

	public static ArrayList<String> getDataName() {
		return dataNameList;
	}

	public static ArrayList<String> getTableKeysList() {
		return ReportTableKeysList;
	}

	public Program() {

	}

	public Program(String serverURI, String folders[]) throws Exception {
		connection = new BO_Connection(serverURI);
		try {
			connection.connect(user_name, password, "SecEnterprise");
		} catch (Exception e) {
			logger.error("Issue in connecting to BO server ", e);
			System.out.println(e);
			return;
		}
		logger.info("Connected to BO server");
		httputils = new HttpUtilities(connection);

		String folders_url = String.format(FOLDERS_URL, serverURI);
		String folders_details = httputils.getJsonResponseCHILD(folders_url);

		int index = 0;
		try {
			index = folders_details.toUpperCase().indexOf(folders[0].toUpperCase());
			String str = folders_details.substring(index, folders_details.length());
			String arr[] = str.split(",");
			String idInfo[] = arr[2].split(":");
			mainParentId = idInfo[1].replaceAll("\"", "");
		} catch (Exception e) {
			//System.out.println(e);
			logger.warn("Please enter valid path ");
		}
	}

	public static String getSiName() {
		return siName;
	}

	public void createCSV() throws Exception {

		siName = siName.trim();
		siName = siName.replace("/", "_");
		String[] str = siName.split(",");

		dir = folderName;

		dir = dir.trim();

		System.out.println(newLine + reportId + " " + siName + " " + index + newLine);

		String directoryPath = path + pathSeperator + dir;
		queryFilePath = directoryPath + pathSeperator + "QueryBO.csv";
		reportsFilePath = directoryPath + pathSeperator + "ReportsBO.csv";
		variablesFilePath = directoryPath + pathSeperator + "VariablesBO.csv";
		promptFilePath = directoryPath + pathSeperator + "PromptsBO.csv";

		FileUtil.createDirectory(directoryPath);
		queryWriter = FileUtil.createFileAdnWriter(queryFilePath, siName);
		reportsWriter = FileUtil.createFileAdnWriter(reportsFilePath, siName);
		variableWriter = FileUtil.createFileAdnWriter(variablesFilePath, siName);
		promptWriter = FileUtil.createFileAdnWriter(promptFilePath, siName);

		String queryheader = "DataProvider, TableName, SQL Query, KeyNames, DataSourceCuid";
		queryWriter.write(String.valueOf(queryheader));
		queryWriter.write(System.lineSeparator());

		String reportheader = "Report Id, Report Name, Data Filters, Report Type,Sections, Columns, Category1, Value1, Region Color1, Col2, Category2, Value2,Region Color2, Col3, Category3, Value3, Region Color3";
		reportsWriter.write(String.valueOf(reportheader));
		reportsWriter.write(System.lineSeparator());

		String variableheader = "Data Type, Id, ColumnName, Formula, Qualification, Alerters, Alerters Description, Document Name";
		variableWriter.write(String.valueOf(variableheader));
		variableWriter.write(System.lineSeparator());

		String promptheader = "DataProvider, Prompt Filters";
		promptWriter.write(String.valueOf(promptheader));
		promptWriter.write(System.lineSeparator());

	}

	private void processQuery() throws Exception {
		logger.info("\nFetching DataProvider info");
		int count = 0;
		String id = "";
		final String dataProviderUri = String.format(DOCUMENT_DATA_PROVIDER_URL, serverURI, reportId);
		final DataProvidersMapper mapper = ((DataProviderWrapperMapper) httputils.getJsonResponse(dataProviderUri,
				DataProviderWrapperMapper.class)).getDataProvidersMapper();

		for (final DataProviders dataprovider : mapper.getDataProvider()) {
			isMultipleQueryPresent = false;
			final String report_details = String.format(RESULT_OBJECT_URL, serverURI, reportId, dataprovider.getId());
			resultObjects = httputils.getResponseXml(report_details);
			Program.getResultObjects(resultObjects);

			String prompt = String.format(PROMPT_FILTER_URL, serverURI, reportId, dataprovider.getId());
			String prompt_details = httputils.getResponseforPrompts(prompt);
			logger.info("Fetching prompt changes for the dp: " + dataprovider.getDataProviderName());
			Program.getPromptNames(prompt_details);

			final String dataProviderByIdUri = String.format(DOCUMENT_DATA_PROVIDER_BYID_URL, serverURI, reportId,
					dataprovider.getId());
			String dp_desc = httputils.getJsonResponseCHILD(dataProviderByIdUri);
			Alias_Qualification.clear();
			logger.info("Fetching key and counter details for the dp: " + dataprovider.getDataProviderName());
			getKeyDetails(dp_desc);
			Query queryMapper = ((QueryMapper) httputils.getJsonResponse(dataProviderByIdUri, QueryMapper.class))
					.getDpQuery();
			count++;

			String sql_query = queryMapper.getFormattedString();
			String columns[] = sql_query.split(",");
			columns[2] = columns[2].replaceAll(" ", "");

			if (!(columns[2].contains("SELECT") || columns[2].contains("select"))) {
				isMultipleQueryPresent = true;
				ArrayList<String> result_query = new ArrayList<String>();
				final String queryURI = String.format(query_url, serverURI, reportId, dataprovider.getId());
				String multiple_flow = httputils.getJsonResponseResultSet(queryURI);
				result_query = getQuery(multiple_flow);
				int i = 0;
				String name = queryMapper.getName();
				id = queryMapper.getId();
				for (String string : result_query) {
					String temp = name + "_" + (++i);
					queryMapper.setName(temp);
					temp = id + "_" + i;
					queryMapper.setId(temp);
					queryMapper.setQuery(string);
					sql_query = queryMapper.getFormattedString();
					FileUtil.write(queryWriter, sql_query);
				}

			}
			logger.info("Writing the dp: " + dataprovider.getDataProviderName() + " to QueryBO.csv");
			if (!isMultipleQueryPresent)
				FileUtil.write(queryWriter, sql_query);

			String array[] = sql_query.split(",");
			if (array.length > 1)
				tableNameList.add(array[1]);
		}
		id = "DP" + count;
		String custom = customQuery().replaceAll("\"", "'").replace(" AS* VARCHAR(10)* ", " AS VARCHAR(10) ");

		String str = id + "," + "CustomDataProvider" + "," + "\"" + custom + "\"" + "," + "\""
				+ String.join(",", keyAliasList) + "\"";
		FileUtil.write(queryWriter, str);

		logger.info("Prompt details are written to PromptsBO");
		logger.info("SQL query file created.");
	}

	public static void getTypeId(String query) {
		String typeId = obj1.extractTypeId(query);
		if (!typeId.equals("")) {
			if (typeIds.add(typeId))
				typeIdName = typeIdName + " " + typeId;
		}
	}

	private String customQuery() {
		logger.info("Creating custom data provider query");
		keyNamesOfCustomQuery = "";
		ArrayList<String> Union = new ArrayList<String>();
		Map<String, HashMap<String, String>> keysMap = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> keyAliasNames = new HashMap<String, String>();
		ArrayList<String> dataProviderNames = new ArrayList<String>();

		String query = "";
		int count = 0, count1 = 0;
		Union = Query.Union;
		keysMap = Query.keysMap;
		String keyName = "";
		dataProviderNames = Query.dataProviderNames;

		for (String string : keyAliasList) {
			keyNamesOfCustomQuery += string + ",";
		}

		for (String name : dataProviderNames) {
			count = 0;
			if (keysMap.containsKey(name)) {
				query += "SELECT DISTINCT ";
				keyAliasNames = keysMap.get(name);
				for (int i = 0; i < Union.size(); i++) {
					keyName = Union.get(i);

					if (!keyAliasNames.containsKey(keyName)) {
						/*
						 * if (keyNameandDataType.containsKey(keyName) &&
						 * (keyNameandDataType.get(keyName).equals("DateTime")||
						 * keyNameandDataType.get(keyName).equals("Date"))) query += "'@QWERTY'" +
						 * " as " + keyName;
						 */
						// if (keyNameandDataType.containsKey(keyName))
						query += "'@QWERTY'" + " as " + keyName;
						/*
						 * else query += "'@QWERTY'" + " as " + keyName;
						 */
					} else {
						query += keyAliasNames.get(keyName) + " as " + keyName;
					}
					if (count < Union.size() - 1)
						query += ",";
					count++;
				}

				query += " FROM ";
				count = 0;
				String string = Query.tablesList.get(name);
				query += string;
				if (count1 < dataProviderNames.size() - 1)
					query += " UNION ";
				count1++;
			}

		}
		keyNamesOfCustomQuery = keyNamesOfCustomQuery.substring(0, keyNamesOfCustomQuery.length() - 1);
		count = 0;

		String optimised = optimizedQuery(query);

		return optimised;
	}

	private static String optimizedQuery(String query) {
		logger.info("Optimizing the query for custom data provider");
		HashSet<Integer> removedIndexes = new HashSet<>();
		HashSet<String> mergedQueries = new HashSet<>();

		String qArray[] = query.split("UNION");
		ArrayList<String> queries = new ArrayList<>();
		for (String q : qArray)
			queries.add(q.trim());

		while (true) {

			removedIndexes.clear();
			mergedQueries.clear();

			for (int i = 0; i < queries.size(); i++) {
				for (int j = i + 1; j < queries.size(); j++) {

					String query1 = modifyQuery(queries.get(i), ",", ";");

					String query2 = modifyQuery(queries.get(j), ",", ";");
					if (removedIndexes.contains(i) || removedIndexes.contains(j))
						continue;
					// System.out.println("query1 " + query1);
					// System.out.println("query2 " + query2);
					query1 = query1.replaceFirst(" WHERE ", " where ");
					query2 = query2.replaceFirst(" WHERE ", " where ");
					if (!query1.contains(" where "))
						query1 = query1 + " where " + "(1=1)";
					if (!query2.contains(" where "))
						query2 = query2 + " where " + "(1=1)";
					String whereQuery1[] = query1.split(" where ");
					String whereQuery2[] = query2.split(" where ");
					whereQuery1[0] = modifyFROMKeyword(whereQuery1[0]);
					whereQuery2[0] = modifyFROMKeyword(whereQuery2[0]);
					String fromQuery1[] = whereQuery1[0].split("#FROM#");
					String fromQuery2[] = whereQuery2[0].split("#FROM#");
					List<String> fromListQuery1 = new ArrayList<>();
					for (String table : fromQuery1[1].split(","))
						fromListQuery1.add(table.trim());
					List<String> fromListQuery2 = new ArrayList<>();
					for (String table : fromQuery2[1].split(","))
						fromListQuery2.add(table.trim());

					fromListQuery1 = checkForInnerQueryTables(whereQuery1[1], fromListQuery1);
					fromListQuery2 = checkForInnerQueryTables(whereQuery2[1], fromListQuery2);

					List<String> columnsQuery1 = new ArrayList<>();
					for (String column : fromQuery1[0].split(","))
						columnsQuery1.add(column.trim());
					List<String> columnsQuery2 = new ArrayList<>();
					for (String column : fromQuery2[0].split(","))
						columnsQuery2.add(column.trim());

					if (canBeMerged(fromListQuery1, fromListQuery2)) {

						List<String> fromListMergedQuery = fromListQuery1.size() > fromListQuery2.size()
								? fromListQuery1
								: fromListQuery2;
						HashSet<String> columnsListMergedQuery = compareColumns(columnsQuery1, columnsQuery2);
						String whereConditionMergedQuery = "";
						String mergedQuery = "", whereQ1 = "", whereQ2 = "";

						if (whereQuery1.length > 1)
							whereQ1 = whereQuery1[1];
						else
							whereQ1 = "1=1";

						if (whereQuery2.length > 1)
							whereQ2 = whereQuery2[1];
						else
							whereQ2 = "1=1";

						whereConditionMergedQuery = getMergedWhereCondition(whereQ1, whereQ2);
						mergedQuery = "SELECT DISTINCT " + String.join(",", columnsListMergedQuery) + " FROM "
								+ String.join(",", fromListMergedQuery) + " WHERE " + whereConditionMergedQuery;

						removedIndexes.add(i);
						removedIndexes.add(j);
						mergedQuery = modifyQuery(mergedQuery, ";", ",");

						mergedQueries.add(mergedQuery);
					}

				}
			}

			ArrayList<String> queriesTemp = new ArrayList<>();

			for (int i = 0; i < queries.size(); i++) {
				if (!removedIndexes.contains(i))
					queriesTemp.add(queries.get(i));
			}

			for (String mergedQ : mergedQueries)
				queriesTemp.add(mergedQ);

			queries.clear();
			queries.addAll(queriesTemp);

			if (removedIndexes.isEmpty() && mergedQueries.isEmpty())
				break;
		}

		String mergedResultQuery = "";

		for (int i = 0; i < queries.size(); i++) {
			if (i != queries.size() - 1)
				mergedResultQuery += queries.get(i) + " UNION ";
			else
				mergedResultQuery += queries.get(i);
		}
		return mergedResultQuery;
	}

	private static List<String> checkForInnerQueryTables(String query, List<String> fromList) {

		String[] nestedFrom;
		String regex = " from " + "(.*?)" + "( where | group by | having | order by )";
		Pattern p = null;
		Matcher m = null;
		if (query.toLowerCase().contains(" from ")) {

			p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			m = p.matcher(query);
			while (m.find()) {

				nestedFrom = m.group(1).split(",");
				for (int k = 0; k < nestedFrom.length; k++) {
					fromList.add(nestedFrom[k].trim());
				}
			}
		}
		return fromList;
	}

	private static String getMergedWhereCondition(String whereQuery1, String whereQuery2) {
		String mergedWhereCondition = whereQuery1 + " AND " + "(" + whereQuery2 + ")";

		mergedWhereCondition = mergedWhereCondition.replace("BETWEEN  {STARTINGDATE}  AND  {ENDINGDATE}",
				"BETWEEN  {STARTINGDATE}  OR  {ENDINGDATE}");
		Set<String> whereConditions = new HashSet<>();
		String combinedWhereCon = "";

		String regex = "[a-zA-Z0-9_\\.]+" + "(\\s*)" + "=" + "[a-zA-Z0-9_\\.]+";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher(mergedWhereCondition);
		while (m.find()) {

			whereConditions.add("(" + m.group() + ")");
		}

		Set<String> promptsList = new HashSet<>();

		promptsList.addAll(prompt_map_CustomQuery.values());

		boolean isPromptCondition = false;
		Set<String> promptConditions = new HashSet<>();
		Map<String, String> map = new HashMap<>();
		regex = "\\(";
		pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		m = pattern.matcher(mergedWhereCondition);
		while (m.find()) {
			int open = m.end() - 1;
			int close = Query.findcloseParen(mergedWhereCondition.toCharArray(), open);

			String condition = mergedWhereCondition.substring(m.start(), close + 1).trim();

			condition = condition.substring(1, condition.length() - 1);

			for (String subCondition : condition.split(" AND ")) {

				isPromptCondition = false;
				subCondition = subCondition.trim();

				for (String prompt : promptsList) {
					if (subCondition.contains(prompt.toUpperCase().trim())) {

						isPromptCondition = true;
						break;
					}
				}

				for (String prompt : promptConditions) {

					if (prompt.contains(subCondition)) {
						isPromptCondition = false;
						break;
					}

				}

				if (isPromptCondition) {

					subCondition = removeInvalidParenthesis(subCondition);
					promptConditions.add(subCondition.trim());
				}
			}

		}

		whereConditions.addAll(promptConditions);

		List<String> whereConditionsList = new ArrayList<>(whereConditions);

		for (int i = 0; i < whereConditionsList.size(); i++) {
			if (i != whereConditionsList.size() - 1)
				combinedWhereCon += whereConditionsList.get(i) + " AND ";
			else
				combinedWhereCon += whereConditionsList.get(i);
		}

		combinedWhereCon = combinedWhereCon.replace("BETWEEN  {STARTINGDATE}  OR  {ENDINGDATE}",
				"BETWEEN  {STARTINGDATE}  AND  {ENDINGDATE}");

		return combinedWhereCon;

	}

	private static String removeInvalidParenthesis(String subCondition) {

		int count = 0;
		for (char ch : subCondition.toCharArray()) {
			if (ch == '(')
				count++;
			else if (ch == ')')
				count--;
		}
		StringBuilder sb = new StringBuilder(subCondition);
		if (count < 0) {
			for (int i = subCondition.length() - 1; i >= 0; i--) {
				char c = subCondition.charAt(i);
				if (c == ')' && count < 0) {
					sb.deleteCharAt(i);
					count++;
				}
			}
		} else {
			for (int i = 0; i < subCondition.length(); i++) {
				char c = subCondition.charAt(i);
				if (c == '(' && count > 0) {
					sb.deleteCharAt(i);
					count--;
				}
			}

		}

		return new String(sb);
	}

	private static HashSet<String> compareColumns(List<String> columnsQuery1, List<String> columnsQuery2) {
		Map<String, String> aliasAndColumnsQuery1 = new LinkedHashMap<>();
		Map<String, String> aliasAndColumnsQuery2 = new LinkedHashMap<>();
		Map<String, String> mergedQueryColumns = new LinkedHashMap<>();
		HashSet<String> mergedColumns = new LinkedHashSet<>();

		String firstColQuery1 = columnsQuery1.get(0).replaceAll("\\s+SELECT\\s*", "");
		firstColQuery1 = firstColQuery1.replaceAll("\\s*SELECT\\s+", "");
		firstColQuery1 = firstColQuery1.replaceAll("\\s*DISTINCT\\s+", "");
		firstColQuery1 = firstColQuery1.replaceAll("\\s+DISTINCT\\s*", "");
		columnsQuery1.set(0, firstColQuery1);

		String firstColQuery2 = columnsQuery2.get(0).replaceAll("\\s+SELECT\\s*", "");
		firstColQuery2 = firstColQuery2.replaceAll("\\s*SELECT\\s+", "");
		firstColQuery2 = firstColQuery2.replaceAll("\\s*DISTINCT\\s+", "");
		firstColQuery2 = firstColQuery2.replaceAll("\\s+DISTINCT\\s*", "");
		columnsQuery2.set(0, firstColQuery2);

		for (String columnQ1 : columnsQuery1) {
			String regex = "as" + "(\\s){1,}" + "(.*)";
			Pattern pattern = Pattern.compile(regex);
			java.util.regex.Matcher m = pattern.matcher(columnQ1);
			String alias = "";
			if (m.find())
				alias = m.group();
			alias = alias.replaceAll("\\s+" + "as" + "\\s*", "");
			alias = alias.replaceAll("\\s*" + "as" + "\\s+", "");
			aliasAndColumnsQuery1.put(alias, columnQ1);

		}

		for (String columnQ2 : columnsQuery2) {
			String regex = "as" + "(\\s){1,}" + "(.*)";
			Pattern pattern = Pattern.compile(regex);
			java.util.regex.Matcher m = pattern.matcher(columnQ2);
			String alias = "";
			if (m.find())
				alias = m.group();
			alias = alias.replaceAll("\\s+" + "as" + "\\s*", "");
			alias = alias.replaceAll("\\s*" + "as" + "\\s+", "");
			aliasAndColumnsQuery2.put(alias, columnQ2);

		}

		for (String aliasQ1 : aliasAndColumnsQuery1.keySet()) {
			String colQ1 = aliasAndColumnsQuery1.get(aliasQ1);
			if (aliasAndColumnsQuery2.containsKey(aliasQ1)) {
				String colQ2 = aliasAndColumnsQuery2.get(aliasQ1);

				if ((colQ1.contains("'@QWERTY'" + " as ")) && (!colQ2.contains("'@QWERTY'" + " as "))) {
					mergedQueryColumns.put(aliasQ1, colQ2);

				} else
					mergedQueryColumns.put(aliasQ1, colQ1);
			} else
				mergedQueryColumns.put(aliasQ1, colQ1);
		}

		for (String aliasQ2 : aliasAndColumnsQuery2.keySet()) {
			String colQ2 = aliasAndColumnsQuery2.get(aliasQ2);

			if (mergedQueryColumns.containsKey(aliasQ2)) {

				String colQ1 = mergedQueryColumns.get(aliasQ2);
				if ((colQ1.contains("'@QWERTY'" + " as ")) && (!colQ2.contains("'@QWERTY'" + " as "))) {
					mergedQueryColumns.replace(aliasQ2, colQ2);

				}

			} else {
				mergedQueryColumns.put(aliasQ2, colQ2);
			}
		}

		mergedColumns.addAll(mergedQueryColumns.values());

		return mergedColumns;
	}

	private static boolean canBeMerged(List<String> fromListQuery1, List<String> fromListQuery2) {
		boolean table1Totable2 = true, table2Totable1 = true;
		for (String tableName : fromListQuery1) {

			if (!fromListQuery2.contains(tableName)) {
				table1Totable2 = false;
			}

		}
		for (String tableName : fromListQuery2) {
			if (!fromListQuery1.contains(tableName))
				table2Totable1 = false;

		}
		if (table1Totable2 || table2Totable1)
			return true;

		return false;
	}

	public static HashMap<String, String> getMultipleQueryAlias() {
		multipleQueryAlias.put("Busy Hour", "HOUR_ID");
		multipleQueryAlias.put("Cell Names (H)", "CELL_NAME");
		multipleQueryAlias.put("NE Version", "DC_RELEASE");
		return multipleQueryAlias;
	}

	private static void getPromptNames(String result) {
		promptNames.clear();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		String promptName = "", name = "";
		String dpId = "";
		try {
			builder = factory.newDocumentBuilder();
			org.w3c.dom.Document doc = builder.parse(new InputSource(new StringReader(result)));
			NodeList nodeList = doc.getElementsByTagName("parameter");
			Node node = nodeList.item(0);
			if (node.hasAttributes()) {
				NamedNodeMap nodeMap = node.getAttributes();
				for (int i = 0; i < nodeMap.getLength(); i++) {
					Node tempNode = nodeMap.item(i);
					if (tempNode.getNodeName() == "dpId") {
						dpId = tempNode.getNodeValue();
						break;
					}
				}
			}
			NodeList nList = doc.getElementsByTagName("technicalName");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				name = nList.item(temp).getTextContent();
				if (!name.toUpperCase().contains("RAW DATA") && !name.toUpperCase().contains("FIRST DATE")
						&& !name.toUpperCase().contains("LAST DATE"))
					promptNames.add(name);
				if (!name.trim().endsWith(":"))
					name = name + ":";
				if (temp < nList.getLength() - 1)
					promptName = promptName + name + " ";
				else
					promptName = promptName + nList.item(temp).getTextContent();
			}
			promptName = dpId + "," + promptName;
			FileUtil.write(promptWriter, promptName);
		} catch (Exception e) {
			logger.error("Error in fetching prompt details", e);
		}
	}

	public static HashSet<String> getPromptNames() {
		return promptNames;
	}

	private ArrayList<String> getQuery(String multiple_flow) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		ArrayList<String> queries = new ArrayList<>();
		try {
			builder = factory.newDocumentBuilder();
			org.w3c.dom.Document doc = builder.parse(new InputSource(new StringReader(multiple_flow)));
			NodeList nList = doc.getElementsByTagName("multipleFlows");
			if (nList.getLength() > 0) {
				String multiple_flows = nList.item(0).getTextContent();
				String[] query = multiple_flows.split("SELECT");
				for (int i = 1; i < query.length; i++) {
					queries.add("SELECT " + query[i].trim());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return queries;
	}

	public static ArrayList<String> getTableName() {
		return tableNameList;
	}

	public static HashSet<String> getTypeId() {
		return typeIds;
	}

	private static void getResultObjects(String result) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		ArrayList<String> name = new ArrayList<String>();
		try {
			builder = factory.newDocumentBuilder();
			org.w3c.dom.Document doc = builder.parse(new InputSource(new StringReader(result)));
			NodeList nList = doc.getElementsByTagName("resultObjects");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node node = nList.item(temp);
				if (node.hasAttributes()) {
					NamedNodeMap nodeMap = node.getAttributes();
					for (int i = 0; i < nodeMap.getLength(); i++) {
						Node tempNode = nodeMap.item(i);
						if (tempNode.getNodeName() == "name") {
							String resultObj = tempNode.getNodeValue();

							name.add(resultObj);
						}
					}
				}
			}
			ResultObjects.setName(name);
		} catch (Exception e) {
			logger.error("Error in fetching result objects ", e);
		}
	}

	/**
	 * @throws Exception
	 */
	private static void getMergeDimesionKeys() {
		final String LINKUri = String.format(DOCUMENT_LINK_URL, serverURI, reportId);
		try {
			String res = httputils.getJsonResponseCHILD(LINKUri);
			HashMap<String, String> mergeDimensionAndId = getMergeDimensionNameandId(res);
			for (String id : mergeDimensionAndId.keySet()) {
				String LINK_BYID_URL = String.format(DOCUMENT_LINK_BYID_URL, serverURI, reportId, id);
				String mergeDimenResponse = httputils.getJsonResponseCHILD(LINK_BYID_URL);
				fetchMergeDimensionDetails(mergeDimenResponse);
			}
		} catch (Exception e) {
			logger.error("Error in fetching merge dimension details ", e);
		}

	}

	private static void processMergeDimension() {

		final String LINKUri = String.format(DOCUMENT_LINK_URL, serverURI, reportId);

		String res;
		try {
			res = httputils.getJsonResponseCHILD(LINKUri);
			mergeDimensionAndId = getMergeDimensionNameandId(res);

			for (String id : mergeDimensionAndId.keySet()) {
				String LINK_BYID_URL = String.format(DOCUMENT_LINK_BYID_URL, serverURI, reportId, id);
				String mergeDimenResponse = "";
				try {
					mergeDimenResponse = httputils.getJsonResponseCHILD(LINK_BYID_URL);
				} catch (Exception e) {
					logger.error("Error in fetching merge dimension details ", e);
				}
				mergeDimenDetails.put(mergeDimensionAndId.get(id), fetchMergeDimensionDetails(mergeDimenResponse));

			}

			for (String mergeDimenName : mergeDimenDetails.keySet()) {
				for (String mergeDimenId : mergeDimenDetails.get(mergeDimenName)) {

					String col = dPNameandID.get(mergeDimenId);
					String key = modifiedKey(col, mergeDimenId);
					mergeDimenName = modifiedKeyForHourAndMin(mergeDimenName);
					keyDPAndMergeDimension.put(key, mergeDimenName);

				}
			}

		} catch (Exception e) {
			logger.error("Error in fetching merge dimension details ", e);
		}

	}

	private static String modifiedKeyForHourAndMin(String key) {
		String matchedPattern = "";
		String reg = "(Hour|Min)\\s*\\([^)]*\\)";
		Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher(key);
		while (m.find()) {
			matchedPattern = m.group();
			if (matchedPattern.contains("Hour"))
				key = key.replace(matchedPattern, "Hour");
			else if (matchedPattern.contains("Min"))
				key = key.replace(matchedPattern, "Min");

		}
		return key;
	}

	private void processVariable() throws Exception {
		logger.info("\nFetching KPI info");
		String expression, formattedString;

		final String VariableUri = String.format(DOCUMENT_VARIABLES_URL, serverURI, reportId);

		final VariableMapper vmapper = ((VariableWrapper) httputils.getJsonResponse(VariableUri, VariableWrapper.class))
				.getMapper();

		for (final Variables variable : vmapper.getVariables()) {
			String column = variable.getName();
			if (!(column.equals("End Date:") || column.equals("Report Period:") || column.equals("Start Date:"))) {
				final String variableByIdUri = String.format(DOCUMENT_VARIABLE_BYID_URL, serverURI, reportId,
						variable.getId());
				logger.info("Fetching formula details for the KPI: " + column);
				Formula formulaMapper = ((FormulaMapper) httputils.getJsonResponse(variableByIdUri,
						FormulaMapper.class)).getDpQuery();
				column = formulaMapper.getName();
				kpiNames.add(column);

				formattedString = formulaMapper.getFormattedString();
				String[] expandFormula = checkForAlerters(column, formulaMapper);
				expression = expandFormula[0];
// ----->
				String expre = checkForAlerters_new(column, formulaMapper);
				//--->
				if (!expression.isEmpty() && expre.isEmpty()) {
				formattedString = formattedString + "," + "\"" + expression + "\""+ "," + "\"" +"\"";
				//formattedString =formattedString + "," + "\"" +"BackGround ="+bg_color+", FontColor ="+font_color +"\"";
				// formattedString = formattedString + "," + "\"" + Program.getSiName() + "\"";

				}
				else if(!expression.isEmpty() && !expre.isEmpty()) {
				formattedString = formattedString + "," + "\"" + expression + "\""+ "," + "\""+ expre +"\"";
				}
				else if(expression.isEmpty()) {

				formattedString = formattedString + "," + "\"" + "\""+ "," + "\""+ expre +"\"";
				}
				//--->	
// ----->
/*
				if (!expression.isEmpty())
					formattedString = formattedString + "," + "\"" + expression + "\"";
				  // formattedString = formattedString + "," + "\"" + Program.getSiName() + "\"";
                if(expression.isEmpty())
                	formattedString = formattedString + "," + "\""+"\"";
*/
/*
//--->
				if (!expression.isEmpty()) {
					formattedString = formattedString + "," + "\"" + expression + "\""+ "," + "\"" +"\"";
                    //formattedString =formattedString + "," + "\"" +"BackGround ="+bg_color+", FontColor ="+font_color +"\"";
				  // formattedString = formattedString + "," + "\"" + Program.getSiName() + "\"";
				
				}
                if(expression.isEmpty()) {
                	String expre = checkForAlerters_new(column, formulaMapper);
                	formattedString = formattedString + "," + "\"" + "\""+ "," + "\""+ expre +"\"";     
                } 
 //--->
  *           
  */
                formattedString = formattedString + "," + "\"" + Program.getSiName() + "\"";
				
				logger.info("Writing KPI: " + column + " to the variablesBO.csv");
				FileUtil.write(variableWriter, formattedString);
			}
		}

		logger.info("Variables and Formulas added to VariablesBO file.");

	}
	
private String checkForAlerters_new(String name, Formula formulaMapper) {
		
		logger.info("Checking for alerters");
		String expression = "";
		String value = "";
		String conditionalFormula = "";
		String expandFormula[] = new String[2];
       
		if (alerterDetails.containsKey(name)) {
			for (Integer id : alerterDetails.get(name)) {
				if (alerterList_Map.get(id) != null) {

					value = String.join(",", alerterList_Map.get(id));

					/*if (alerterIdandConditionalFormula.containsKey(id))
						conditionalFormula = String.join(",", alerterIdandConditionalFormula.get(id));*/

					if (value.contains("{currentKPIId}"))
						value = value.replace("{currentKPIId}", "{" + formulaMapper.getId() + "}");

					if (value.startsWith("="))
						value = value.substring(1);
					if (!value.isEmpty()) {
						value = value +" , " + aleters1.get(id);
						expression = expression + "&&" + value;
						}

				}
			}

			if (!expression.isEmpty()) {
				expression = expression.substring(2);

				expression = expression.replaceAll("\"", "'");
			}
		}
		//expandFormula[0] = expression;

		//expandFormula[1] = conditionalFormula.replaceAll("\"", "'");

		return expression;
	}


	private String[] checkForAlerters(String name, Formula formulaMapper) {
		logger.info("Checking for alerters");
		String expression = "";
		String value = "";
		String conditionalFormula = "";
		String expandFormula[] = new String[2];

		if (alerterDetails.containsKey(name)) {
			for (Integer id : alerterDetails.get(name)) {
				if (alerters.get(id) != null) {

					value = String.join(",", alerters.get(id));

					if (alerterIdandConditionalFormula.containsKey(id))
						conditionalFormula = String.join(",", alerterIdandConditionalFormula.get(id));

					if (value.contains("{currentKPIId}"))
						value = value.replace("{currentKPIId}", "{" + formulaMapper.getId() + "}");

					if (value.startsWith("="))
						value = value.substring(1);
					if (!value.isEmpty())
						expression = expression + "&&" + value;

				}
			}

			if (!expression.isEmpty()) {
				expression = expression.substring(2);

				expression = expression.replaceAll("\"", "'");
			}
		}
		expandFormula[0] = expression;

		expandFormula[1] = conditionalFormula.replaceAll("\"", "'");

		return expandFormula;
	}

	private static ArrayList<String> fetchMergeDimensionDetails(String mergeDimenResponse) {
		JSONParser parse = new JSONParser();
		ArrayList<String> subKeysId = new ArrayList<>();
		try {
			JSONObject jobj = (JSONObject) parse.parse(mergeDimenResponse);
			JSONObject jsonObj = (JSONObject) jobj.get("link");
			JSONObject jsonObj2 = (JSONObject) jsonObj.get("linkedExpressions");
			JSONArray jsonarr = (JSONArray) jsonObj2.get("linkedExpression");
			for (int i = 0; i < jsonarr.size(); i++) {
				JSONObject obj = (JSONObject) jsonarr.get(i);
				subKeysId.add((String) obj.get("@id"));

				mergeDimensionKeysId.add((String) obj.get("@id"));
			}

		} catch (ParseException e) {
			logger.error("Error in fetching mergedimension ", e);
		}
		return subKeysId;
	}

	private static HashMap<String, String> getMergeDimensionNameandId(String res) {
		// TODO Auto-generated method stub
		HashMap<String, String> mergeDimenId = new HashMap<>();
		String name = "";
		JSONParser parse = new JSONParser();
		try {
			JSONObject jobj = (JSONObject) parse.parse(res);
			JSONObject jsonObj = (JSONObject) jobj.get("links");
			JSONArray jsonarr = (JSONArray) jsonObj.get("link");
			for (int i = 0; i < jsonarr.size(); i++) {
				JSONObject obj = (JSONObject) jsonarr.get(i);
				name = (String) obj.get("name");
				mergeDimenId.put((String) obj.get("id"), name);
				mergeDimenDataType.put(name, (String) obj.get("@dataType"));

			}

		} catch (ParseException e) {
			logger.error("Error in fetching mergedimension ", e);
		}
		return mergeDimenId;
	}

	private void processReportsTable() throws Exception {

		final String ReportUri = String.format(DOCUMENT_REPORTS_URL, serverURI, reportId);
		final ReportsMapper rmapper = ((ReportsWrapper) httputils.getJsonResponse(ReportUri, ReportsWrapper.class))
				.getMapper();
		for (final Reports report : rmapper.getReports()) {
			final String reportByIdUri = String.format(DOCUMENT_REPORTS_BYID_URL, serverURI, reportId, report.getId());
			String report_response = httputils.getResponseXml(reportByIdUri);
			getTableKeysDetails(report_response);

		}

	}

	private void getTableKeysDetails(String report_response) {
		String table_details = "";

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;

		try {
			builder = factory.newDocumentBuilder();
			org.w3c.dom.Document doc = builder.parse(new InputSource(new StringReader(report_response)));

			boolean headerFlag = false;
//--->
			NodeList nList1 = doc.getElementsByTagName("REPORT");

			for (int temp1 = 0; temp1 < nList1.getLength(); temp1++) {
				table_details = "";
				Node node1 = nList1.item(temp1);

				if (node1.hasAttributes()) {
					NamedNodeMap attrMap = node1.getAttributes();
					for (int j = 0; j < attrMap.getLength(); j++) {
						Node attr = attrMap.item(j);
						if (attr.getNodeName() == "rId") {
							tempId = attr.getTextContent();
						}
					}
				}
			}		
//-->
			NodeList nList = doc.getElementsByTagName("ROWGROUP");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				table_details = "";
				Node node = nList.item(temp);
				headerFlag = false;

				if (node.hasAttributes()) {
					NamedNodeMap attrMap = node.getAttributes();
					for (int j = 0; j < attrMap.getLength(); j++) {
						Node attr = attrMap.item(j);
						if (attr.getNodeName() == "type" && attr.getTextContent().equalsIgnoreCase("header")) {
							headerFlag = true;
						}
					}
				}

				if (node.hasChildNodes()) {
					NodeList nodeMap = node.getChildNodes();

					for (int i = 0; i < nodeMap.getLength(); i++) {
						Node tempNode = nodeMap.item(i);

						if (!headerFlag && tempNode.hasChildNodes()) {
							NodeList nodeMap2 = tempNode.getChildNodes();
							for (int j = 0; j < nodeMap2.getLength(); j++) {
								Node node_child = nodeMap2.item(j);
								if (node_child.getNodeName() == "TDCELL") {

									if (node_child.hasChildNodes()) {
										NodeList nodeMap3 = node_child.getChildNodes();

										for (int k = 0; k < nodeMap3.getLength(); k++) {

											Node node_child2 = nodeMap3.item(k);
											if (node_child2.getNodeName() == "CONTENT") {

												String table_details_temp = node_child2.getTextContent();

												// if (keyandDPKey.containsKey(table_details_temp)) {
												// table_details_temp = keyandDPKey.get(table_details_temp);
												if (table_details_temp.startsWith("=NameOf")) {
													continue;
												}
												table_details_temp=table_details_temp.replace("=", "");
												
												if (table_details_temp.contains(".")) {
										
													String[] table_details_temp1 = table_details_temp.split("\\.");
													String s_temp = table_details_temp1[0];
							
													String s_temp1 = table_details_temp1[1];									

													if (s_temp.startsWith("[") && s_temp.endsWith("]")
															&& s_temp1.startsWith("[") && s_temp1.endsWith("]")) {
									
														s_temp = s_temp.substring(1, s_temp.length() - 1);
									
														s_temp1 = s_temp1.substring(1, s_temp1.length() - 1);
										
													}
													String table_details_temp1_modified = s_temp1 + "(" + s_temp + ")";
										
													if (keyDPAndMergeDimension
															.containsKey(table_details_temp1_modified)) {
											
													tbleName.put(tempId, s_temp);
													}
									
													table_details_temp = Formula.modifyKeyAlias(table_details_temp);
													table_details_temp = table_details_temp.replaceAll("=", "");
															// .replaceFirst("\\[", "'").replaceAll("\\]$", "'");
															table_details_temp = table_details_temp.replaceAll("\"", "'");

															table_details_temp = table_details_temp.replace(".", "__");

															table_details_temp = "'" + table_details_temp + "'";
													
															table_details += table_details_temp + ",";

												}
											}
                      
										}
									}
								}
							}

						}
					}
				}
				if (table_details.endsWith(","))
					table_details = table_details.substring(0, table_details.length() - 1);
				if (!headerFlag && !table_details.isEmpty())
					ReportTableKeysList.add("\"" + table_details + "\"");
			}
			
		} catch (Exception e) {
			logger.error("Error in fetching table details ", e);
		}
	}

	private void processReports() throws Exception {
		logger.info("\nFetching visualization info");
		final String ReportUri = String.format(DOCUMENT_REPORTS_URL, serverURI, reportId);
		final ReportsMapper rmapper = ((ReportsWrapper) httputils.getJsonResponse(ReportUri, ReportsWrapper.class))
				.getMapper();
		neNameDefinition = Formula.getMap();
		for (final Reports report : rmapper.getReports()) {
			logger.info("Fetching report details for: " + report.getName());
			chart_details = "";
			sections = "";
			table_details = "";
			ReportTablesList.clear();

			final String reportByIdUri = String.format(DOCUMENT_REPORTS_BYID_URL, serverURI, reportId, report.getId());
			String report_response = httputils.getResponseXml(reportByIdUri);

			boolean isChart = false;
			String visu_id = "";

			visu_id = getVisualizationId(report_response, visu_id);

			isChart = isChartTab(report_response, isChart);

			List<String> titleList = new ArrayList<>();

			if (isChart) {

				List<Long> titleIds = new ArrayList<>();
				titleList = new ArrayList<>();

				final String reportElementURI = String.format(REPORT_ELEMENTS_URL, serverURI, reportId, visu_id);
				String response = httputils.getJsonResponseCHILD(reportElementURI);

				sections = getSectionDetails(report_response);

				titleIds = getTitleElementIds(response);

				for (Long title_id : titleIds) {

					final String reportElementByIdURI = String.format(REPORT_ELEMENT_DETAILS_URL, serverURI, reportId,
							visu_id, title_id);
					String title_response = httputils.getResponseforPrompts(reportElementByIdURI);
					titleList.add(fetchTitleValues(title_response));

				}

			}

			dataFilters = getFilterDetails(report_response);
			getChartDetails(report_response, titleList);

			getTableDetails(report_response);

			Report reportXml = (Report) (httputils.getXmlResponse(reportByIdUri, Report.class));

			contentList.clear();
			String report_data = reportXml.getFormattedString();

			if (!ReportTablesList.isEmpty() && !chart_details.isEmpty()) {
				List<String> content = contentList;
				List<String> chartContent = new ArrayList<>();
				List<String> tableContent = new ArrayList<>();
				int i = 0;
				while (i < 2) {
					if (i == 0) {
						for (int j = 0; j < content.size(); j++) {
							if (j == 1)
								chartContent.add("\"" + content.get(j) + "_Chart" + "\"");
							else if (j == content.size() - 1) {
								String arr[] = content.get(j).split("#QWERTY#");
								chartContent.add(arr[0]);
							} else
								chartContent.add(content.get(j));
						}
						FileUtil.write(reportsWriter, String.join(",", chartContent));
					}
					if (i == 1) {
						String temStr = "";
						for (int j = 0; j < content.size(); j++) {
							if (j == 0) {
								tableContent.add(content.get(j) + "*");
						
							} else if (j == 1) {
								tableContent.add("\"" + content.get(j) + "_Table" + "\"");
							} else if (j == 2) {
								// tableContent.add("\"" + tbleName + "\"");
								tableContent.add("\"" + "" + "\"");
					
							} else if (j == 3) {
								tableContent.add("Table");
						
							} else if (j == 4) {
								tableContent.add("\"" + "" + "\"");
								temStr = content.get(j).replace('\"', '\'');
							
							} else if (j == content.size() - 1) {
								String arr[] = content.get(j).split("#QWERTY#");
							
								arr[1] = arr[1].substring(0, arr[1].length() - 1) + ',' + temStr + '\"';
								tableContent.add(arr[1]);
						
							} else {
								tableContent.add(content.get(j));
							
							}
						}
						logger.info("Writing report data to the csv");
						FileUtil.write(reportsWriter, String.join(",", tableContent));
					}
					i++;
				}
			}

		/*				for (int j = 0; j < content.size(); j++) {
							if (j == 0)
								tableContent.add(content.get(j) + "*");
							else if (j == 1)
								tableContent.add("\"" + content.get(j) + "_Table" + "\"");
							else if (j == 2)
								tableContent.add("\"" + "" + "\"");
							else if (j == 3)
								tableContent.add("Table");
							else if (j == 4)
								tableContent.add("\"" + "" + "\"");
							else if (j == content.size() - 1) {
								String arr[] = content.get(j).split("#QWERTY#");
								tableContent.add(arr[1]);
							} else
								tableContent.add(content.get(j));
						}
						logger.info("Writing report data to the csv");
						FileUtil.write(reportsWriter, String.join(",", tableContent));
			*/		
//						}
//					i++;
//				}
//			}

			String tableData[] = report_data.split("," + "\"");
			String rowData = tableData[tableData.length - 1];
			rowData = rowData.substring(0, rowData.length() - 1);

			String temp = "";
			for (int i = 1; i < tableData.length - 1; i++) {

				if (i == 1)
					temp += "\"" + tableData[i] + "," + "\"";
				else if (i != tableData.length - 2)
					temp += tableData[i] + "," + "\"";
				else
					temp += tableData[i];
			}

			String remainingData = temp;

			String rowArr[] = rowData.split("@");

			int i = 0;

			if (!ReportTablesList.isEmpty() && chart_details.isEmpty()) {
				if (tbleName.containsKey(tableData[0])) {

					remainingData = remainingData.substring(0, remainingData.length() - 2);

					remainingData = remainingData + "\"" + tbleName.get(tableData[0]) + "\"";
				}
				
				while (i < rowArr.length) {
					if (tableData[0].endsWith("\""))
						tableData[0] = tableData[0].substring(0, tableData.length - 1);
					String data = tableData[0] + "," + remainingData + "," + rowArr[i++];
				
					if (data.endsWith(","))
						data = data.substring(0, data.length() - 1);
					
					logger.info("Writing report data to the csv");
					FileUtil.write(reportsWriter, data);
				}
			} else if (ReportTablesList.isEmpty() && !chart_details.isEmpty()) {
				logger.info("Writing report data to the csv");
				FileUtil.write(reportsWriter, report_data);
			}
		}
		logger.info("Report structure fetched in ReportsBO file.");
	}

	private String getSectionDetails(String report_response) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		String sections = "";
		NodeList sectionNodeMap = null;
		boolean sectionFound = false;

		try {
			builder = factory.newDocumentBuilder();
			org.w3c.dom.Document doc = builder.parse(new InputSource(new StringReader(report_response)));

			NodeList nList = doc.getElementsByTagName("PAGE_BODY");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node node = nList.item(temp);
				if (node.hasChildNodes()) {
					NodeList nodeMap = node.getChildNodes();
					int len = nodeMap.getLength();
					sectionNodeMap = nodeMap;
					while (sectionNodeMap != null) {
						nodeMap = sectionNodeMap;
						sectionNodeMap = null;
						sectionFound = false;
						for (int i = 0; i < nodeMap.getLength(); i++) {
							node = nodeMap.item(i);
							if (node.getNodeName() == "SECTION") {
								if (node.hasChildNodes()) {
									nodeMap = node.getChildNodes();

									for (i = 0; i < nodeMap.getLength(); i++) {
										node = nodeMap.item(i);
										if (node.getNodeName() == "AXIS") {
											if (node.hasChildNodes()) {
												NodeList subNodeMap = node.getChildNodes();
												for (i = 0; i < subNodeMap.getLength(); i++) {
													node = subNodeMap.item(i);
													if (node.getNodeName() == "AXIS_EXPR") {
														String section = node.getTextContent();
														section = section.replace("=", "").trim();
														String modifySection = section.replace("[", "").replace("]",
																"");
														neNameKPIandmodifyDefinition = Formula.getNeNameMap();
														if (neNameKPIandmodifyDefinition.containsKey(modifySection)) {
															section = neNameKPIandmodifyDefinition.get(modifySection);

														} else if (keyAliasList
																.contains(modifyResultObject(modifySection))) {
															section = modifyResultObject(modifySection);

														} else if (dataFiltersAliasMap.containsKey(section)) {
															section = dataFiltersAliasMap.get(section);

														} else {
															for (String alias : dataFiltersAliasMap.keySet()) {
																if (alias.substring(alias.lastIndexOf(".") + 1)
																		.equals(section)) {
																	section = dataFiltersAliasMap.get(alias);
																}
															}

														}
														if (section.contains("[") && section.contains("]"))
															section = section.substring(section.indexOf("[") + 1,
																	section.indexOf("]"));

														sections += section + "@";
													}
												}
											}
										}
										if (node.getNodeName() == "SBODY") {

											if (node.hasChildNodes()) {
												sectionNodeMap = node.getChildNodes();
												sectionFound = true;
											}
										}
										if (sectionFound)
											break;
									}

								}
							}
							if (sectionFound)
								break;
						}
					}
				}

			}
			if (sections.endsWith("@"))
				sections = sections.substring(0, sections.length() - 1);
		} catch (Exception e) {
			logger.error("Error in fetching section details ", e);
		}
		return sections;
	}

	private boolean isChartTab(String report_response, boolean isChart) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;

		try {
			builder = factory.newDocumentBuilder();
			org.w3c.dom.Document doc = builder.parse(new InputSource(new StringReader(report_response)));
			NodeList nodeList2 = doc.getElementsByTagName("VISU");
			isChart = nodeList2.getLength() == 0 ? false : true;

		} catch (Exception e) {
			logger.error("Error in checking if it's chart ", e);
		}
		return isChart;
	}

	private String getVisualizationId(String report_response, String visu_id) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;

		try {
			builder = factory.newDocumentBuilder();
			org.w3c.dom.Document doc = builder.parse(new InputSource(new StringReader(report_response)));
			NodeList nodeList = doc.getElementsByTagName("REPORT");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.hasAttributes()) {
					NamedNodeMap nodeMap = node.getAttributes();
					for (int j = 0; j < nodeMap.getLength(); j++) {
						Node subNode = nodeMap.item(j);
						if (subNode.getNodeName() == "rId") {
							visu_id = subNode.getTextContent();

						}
					}
				}
			}

		} catch (Exception e) {
			logger.error("Error in getting visualisationId ", e);
		}
		return visu_id;
	}

	private String fetchTitleValues(String title_response) {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;

		try {
			builder = factory.newDocumentBuilder();
			org.w3c.dom.Document doc = builder.parse(new InputSource(new StringReader(title_response)));

			NodeList nodeList = doc.getElementsByTagName("chart");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.hasChildNodes()) {
					NodeList subList = node.getChildNodes();
					for (int j = 0; j < subList.getLength(); j++) {
						Node subNode = subList.item(j);

						if (subNode.getNodeName() == "title") {
							if (subNode.hasChildNodes()) {

								NodeList list = subNode.getChildNodes();
								for (int k = 0; k < list.getLength(); k++) {
									Node child_node = list.item(k);

									if (child_node.getNodeName() == "label") {
										if (child_node.hasChildNodes()) {
											NodeList inner_list = child_node.getChildNodes();
											for (int temp = 0; temp < inner_list.getLength(); temp++) {

												Node inner_node = inner_list.item(temp);

												if (inner_node.getNodeName() == "#text") {

													return inner_node.getNodeValue();
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}

		} catch (Exception e) {
			logger.error("Error in getting title ", e);
		}

		return null;
	}

	private static ArrayList<Long> getTitleElementIds(String response) {
		JSONParser parse = new JSONParser();
		String type = "";
		ArrayList<Long> titleIds = new ArrayList<Long>();

		try {
			JSONObject jobj = (JSONObject) parse.parse(response);
			JSONObject jsonObj = (JSONObject) jobj.get("elements");
			JSONArray jsonarr = (JSONArray) jsonObj.get("element");
			for (int i = 0; i < jsonarr.size(); i++) {
				JSONObject obj = (JSONObject) jsonarr.get(i);
				type = (String) obj.get("@type");
				if (type.equalsIgnoreCase("Visualization")) {
					titleIds.add((Long) obj.get("id"));
				}
			}

		} catch (ParseException e) {
			logger.error("Error in getting title elements ", e);
		}
		return titleIds;

	}

	private void getTableDetails(String report_response) {

		String table_details = "";

		Set<String> columns = new HashSet<>();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;

		try {
			builder = factory.newDocumentBuilder();
			org.w3c.dom.Document doc = builder.parse(new InputSource(new StringReader(report_response)));

			boolean headerFlag = false;
			NodeList nList = doc.getElementsByTagName("ROWGROUP");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				table_details = "";
				Node node = nList.item(temp);
				headerFlag = false;
				columns.clear();

				if (node.hasAttributes()) {
					NamedNodeMap attrMap = node.getAttributes();
					for (int j = 0; j < attrMap.getLength(); j++) {
						Node attr = attrMap.item(j);
						if (attr.getNodeName() == "type" && attr.getTextContent().equalsIgnoreCase("header")) {
							headerFlag = true;
						}
					}
				}

				if (node.hasChildNodes()) {
					NodeList nodeMap = node.getChildNodes();

					for (int i = 0; i < nodeMap.getLength(); i++) {
						Node tempNode = nodeMap.item(i);

						if (!headerFlag && tempNode.hasChildNodes()) {
							NodeList nodeMap2 = tempNode.getChildNodes();

							for (int j = 0; j < nodeMap2.getLength(); j++) {
								Node node_child = nodeMap2.item(j);
								if (node_child.getNodeName() == "TDCELL") {

									if (node_child.hasChildNodes()) {
										NodeList nodeMap3 = node_child.getChildNodes();

										for (int k = 0; k < nodeMap3.getLength(); k++) {

											Node node_child2 = nodeMap3.item(k);
											if (node_child2.getNodeName() == "CONTENT") {

												String table_details_temp = node_child2.getTextContent();
												if (columns.contains(table_details_temp))
													continue;
												columns.add(table_details_temp);

												if (table_details_temp.startsWith("=NameOf"))
													continue;
												String kpiName = table_details_temp.replaceFirst("\\[", "")
														.replaceAll("=", "").replaceAll("\\]$", "");
	  //-->                                  
												table_details_temp = table_details_temp.replaceAll("=", "");
												
												if(table_details_temp.startsWith("[") && table_details_temp.endsWith("]")){
																						         
												       table_details_temp=table_details_temp.substring(1,table_details_temp.length()-1);
												      
												       table_details_temp=table_details_temp.trim();

												       table_details_temp="[" + table_details_temp + "]";
												 }		
    //-->								
												table_details_temp = Formula.modifyKeyAlias(table_details_temp);
												flag = true;
												if (neNameDefinition.containsKey(table_details_temp))
													table_details_temp = neNameDefinition.get(table_details_temp);
												table_details_temp = Formula.removeTableName(table_details_temp);
												Pattern p = Pattern.compile("\\[([^\\[]*)\\]");
												Matcher m = p.matcher(table_details_temp);
												if (m.find()) {
													table_details_temp = table_details_temp.substring(0, m.start() + 1)
															+ m.group(1).trim()
															+ table_details_temp.substring(m.end() - 1);
												}

												p = Pattern.compile("\\]");
												m = p.matcher(table_details_temp);
												int close_bracket = 0;
												while (m.find()) {
													close_bracket++;
												}

												if (close_bracket == 1) {
													table_details_temp = table_details_temp.replace("]", "'");
												}
												if (table_details_temp.startsWith("[")
														&& table_details_temp.endsWith("]")) {
													table_details_temp = table_details_temp
															.substring(1, table_details_temp.length() - 1).trim();
													table_details_temp = "[" + table_details_temp + "]";
												}
												table_details_temp = table_details_temp.trim();
												table_details_temp = table_details_temp.replace("\\[", "[")
														.replace("\\]", "]");
												table_details_temp = table_details_temp.replaceAll("=", "")
														.replaceFirst("\\[", "'").replaceAll("\\]$", "'");
												table_details_temp = table_details_temp.replaceAll("\"", "'");
												table_details_temp = "'" + table_details_temp + "'";
												if (!kpiNames.contains(kpiName))

													table_details_temp = table_details_temp.replace(".", "__");

												table_details += table_details_temp.replace("''", "'") + ",";

											}

										}
									}
								}
							}

						}
					}
				}

				if (table_details.endsWith(","))
					table_details = table_details.substring(0, table_details.length() - 1);

				if (!headerFlag && !table_details.isEmpty())
					ReportTablesList.add("\"" + table_details + "\"");

			}

		} catch (Exception e) {
			logger.error("Error in getting table details ", e);
		}

	}

	private void getChartDetails(String report_response, List<String> titleList) {

		Set<String> chartDetailsSet = new LinkedHashSet<>();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		int index = 0;

		try {
			builder = factory.newDocumentBuilder();
			org.w3c.dom.Document doc = builder.parse(new InputSource(new StringReader(report_response)));

			NodeList nList = doc.getElementsByTagName("VISU");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				String category = "", val = "", col_val = "", region_color = "";
				Node node = nList.item(temp);
				if (node.hasChildNodes()) {
					NodeList nodeMap = node.getChildNodes();
					for (int i = 0; i < nodeMap.getLength(); i++) {
						Node tempNode = nodeMap.item(i);
						if (tempNode.getNodeName() == "XY_CHART") {
							NamedNodeMap attributes = tempNode.getAttributes();
							for (int j = 0; j < attributes.getLength(); j++) {
								if (attributes.item(j).getNodeName().toString().equalsIgnoreCase("type")) {
									chartType = attributes.item(j).getNodeValue();
									break;
								}
							}
							NodeList sub_nodes = tempNode.getChildNodes();
							if (sub_nodes == null)
								continue;
							for (int j = 0; j < sub_nodes.getLength(); j++) {
								Node tempNode2 = sub_nodes.item(j);
								if (tempNode2.getNodeName() == "VALUE_AXIS") {
									NodeList sub_nodes2 = tempNode2.getChildNodes();
									if (sub_nodes2 == null)
										continue;
									for (int k = 0; k < sub_nodes2.getLength(); k++) {
										Node tempNode3 = sub_nodes2.item(k);
										if (tempNode3.getNodeName() == "TITLE") {
											NamedNodeMap sub_nodes3 = tempNode3.getAttributes();

											for (int i1 = 0; i1 < sub_nodes3.getLength(); i1++) {
												Node node1 = sub_nodes3.item(i1);

												if (node1.getNodeName() == "label") {

													col_val += node1.getTextContent();

												}
												String title = "Chart";
												if (index < titleList.size())
													title = titleList.get(index++);
												if (col_val.isEmpty())
													col_val = title;
												col_val = col_val.replaceAll("\\s+", "_");
												col_val = col_val.replace("=", "");
												col_val = col_val.replaceAll("\"", "");
											}

										}
									}

									col_val = "\"" + col_val + "\"";
								} else if (tempNode2.getNodeName() == "FEEDS") {
									NodeList sub_nodes2 = tempNode2.getChildNodes();
									if (sub_nodes2 == null)
										continue;
									for (int k = 0; k < sub_nodes2.getLength(); k++) {
										Node tempNode3 = sub_nodes2.item(k);
										if (tempNode3.getNodeName() == "CATEGORY") {
											if (tempNode3.hasChildNodes()) {

												NodeList sub_nodes3 = tempNode3.getChildNodes();
												for (int x = 0; x < sub_nodes3.getLength(); x++) {
													Node Node_Attr = sub_nodes3.item(x);
													if (Node_Attr.getNodeName() == "FEED_EXPR") {
														if (Node_Attr.hasChildNodes()) {

															NodeList Map = Node_Attr.getChildNodes();
															for (int i1 = 0; i1 < Map.getLength(); i1++) {
																Node node1 = Map.item(i1);

																if (node1.getNodeName() == "CONTENT") {

																	String category_temp = node1.getTextContent();
																	if (category_temp
																			.equalsIgnoreCase("=[Node Name]")) {

																		if (!NE_NAME_details.isEmpty()) {

																			String ne_name[] = NE_NAME_details
																					.split(",");
																			category_temp = category_temp.replace(
																					category_temp,
																					"[" + ne_name[1] + "]");
																		}
																	}
																	if (category_temp.startsWith("[")
																			&& category_temp.endsWith("]")) {
																		category_temp = category_temp
																				.substring(1,
																						category_temp.length() - 1)
																				.trim();
																		category_temp = "[" + category_temp + "]";
																	}
																	category_temp = category_temp.replace("\\s+", "_");
																	category += category_temp;

																}
															}
														}
													}

												}
											}
											category = category.replaceFirst("=", "");
											category = category.replace("=", "@");
											category = "\"" + category + "\"";
										} else if (tempNode3.getNodeName() == "VALUE") {
											if (tempNode3.hasChildNodes()) {
												NodeList sub_nodes3 = tempNode3.getChildNodes();
												for (int x = 0; x < sub_nodes3.getLength(); x++) {
													Node Node_Attr = sub_nodes3.item(x);
													if (Node_Attr.getNodeName() == "FEED_EXPR") {
														if (Node_Attr.hasChildNodes()) {
															NodeList sub_nodes4 = Node_Attr.getChildNodes();
															for (int y = 0; y < sub_nodes4.getLength(); y++) {
																Node values = sub_nodes4.item(y);
																if (values.getNodeName() == "CONTENT") {
																	String val_temp = values.getTextContent();
																	Pattern p = Pattern.compile("\\[([^\\[]*)\\]");
																	Matcher m = p.matcher(val_temp);
																	if (m.find()) {
																		val_temp = val_temp.substring(0, m.start() + 1)
																				+ m.group(1).trim()
																				+ val_temp.substring(m.end() - 1);
																	}
																	val_temp = val_temp.replace("\\[", "[")
																			.replace("\\]", "]");
																	if (val_temp.startsWith("[")
																			&& val_temp.endsWith("]")) {
																		val_temp = val_temp
																				.substring(1, val_temp.length() - 1)
																				.trim();
																		val_temp = "[" + val_temp.trim() + "]";
																	}

																	val_temp = val_temp.replaceAll("\\s+", "_");

																	val += val_temp + "@";

																}
															}
														}
													}
												}
											}
											val = val.replace("=", "");
											if (val.endsWith("@")) {
												val = val.substring(0, val.length() - 1);
											}
											val = "\"" + val + "\"";
										} else if (tempNode3.getNodeName() == "COLOR") {

											if (tempNode3.hasChildNodes()) {
												NodeList sub_nodes3 = tempNode3.getChildNodes();
												for (int x = 0; x < sub_nodes3.getLength(); x++) {
													Node Node_Attr = sub_nodes3.item(x);
													if (Node_Attr.getNodeName() == "FEED_EXPR") {
														if (Node_Attr.hasChildNodes()) {
															NodeList sub_nodes4 = Node_Attr.getChildNodes();
															for (int y = 0; y < sub_nodes4.getLength(); y++) {
																Node values = sub_nodes4.item(y);
																if (values.getNodeName() == "CONTENT") {
																	String region_color_temp = values.getTextContent();

																	region_color_temp = modifyVisuColumn(
																			region_color_temp);

																	region_color += region_color_temp + "@";
																}
															}
														}
													}
												}
											}
											region_color = region_color.replace("=", "");
											if (region_color.endsWith("@")) {
												region_color = region_color.substring(0, region_color.length() - 1);
											}
											region_color = "\"" + region_color + "\"";
										}
									}
								}
							}
						}

					}
				}

				if (col_val.replaceAll("\"", "").replaceAll("=", "").isEmpty()) {

					col_val = val.replace("@", "/");
				}

				if (!category.replaceAll("\"", "").isEmpty() && !val.replaceAll("\"", "").isEmpty()) {
					chartDetailsSet.add(col_val + "," + category + "," + val + "," + region_color);
				}

			}
			chart_details = String.join(",", chartDetailsSet);

		} catch (Exception e) {
			logger.error("Error in getting chart details", e);
		}

	}

	private String modifyVisuColumn(String column) {
		// TODO Auto-generated method stub

		column = Formula.modifyKeyAlias(column);

		if (Formula.getKPINames().contains(column.substring(1, column.length() - 1))) {
			column = column.trim();
			column = column.replaceAll("\\s+", "_");

		} else if (keyAliasList.contains(modifyResultObject(column))) {

			column = modifyResultObject(column);

		} else if (dataFiltersAliasMap.containsKey(column)) {

			column = dataFiltersAliasMap.get(column);

		} else {
			for (String alias : dataFiltersAliasMap.keySet()) {
				if (alias.substring(alias.lastIndexOf(".") + 1).equals(column)) {
					column = dataFiltersAliasMap.get(alias);

				}
			}

		}

		return column;
	}

	private void closeAllFiles() throws IOException {
		FileUtil.close(queryWriter);
		FileUtil.close(reportsWriter);
		FileUtil.close(variableWriter);
		FileUtil.close(promptWriter);
	}

	private static int getReportsSize(String response) {
		JSONParser parse = new JSONParser();
		try {
			JSONObject jobj = (JSONObject) parse.parse(response);
			JSONArray jsonarr = (JSONArray) jobj.get("entries");
			size = jsonarr.size();

		} catch (ParseException e) {
			logger.error("Error getting report size ", e);
		}

		return size;
	}

	private void getKeyDetails(String dp_desc) {
		JSONParser parse = new JSONParser();
		try {
			JSONObject jobj = (JSONObject) parse.parse(dp_desc);
			JSONObject jobj2 = (JSONObject) jobj.get("dataprovider");
			JSONObject jobj3 = (JSONObject) jobj2.get("dictionary");
			JSONArray inner_jsonarr = (JSONArray) jobj3.get("expression");
			for (int j = 0; j < inner_jsonarr.size(); j++) {
				JSONObject inner_obj = (JSONObject) inner_jsonarr.get(j);
				String result_object = (String) inner_obj.get("formulaLanguageId");
				String qualification = (String) inner_obj.get("@qualification");
				String column_id = (String) inner_obj.get("id");
				String key = modifiedKey(result_object, column_id);
				Alias_Qualification.put(key, qualification);

			}
		} catch (ParseException e) {
			logger.error("Error while fetching key and counters ", e);
		}

	}

	public static String modifiedKey(String result_object, String column_id) {

		String[] keyDPArr;
		String keyDP = "", key = "";

		if (result_object.contains("].[")) {
			keyDPArr = result_object.split("\\]\\.\\[");
			String dP = keyDPArr[0].substring(1, keyDPArr[0].length());
			key = keyDPArr[1].substring(0, keyDPArr[1].length() - 1);
			keyDP = key + "(" + dP + ")";
		} else
			key = result_object.substring(1, result_object.length() - 1);

		if (!result_object.contains("].[") || !mergeDimensionKeysId.contains(column_id))
			keyDP = key;

		if (keyDPAndMergeDimension.containsKey(keyDP)) {
			keyDP = keyDPAndMergeDimension.get(keyDP);
		}

		return keyDP;
	}

	private static void getColumnsDetails() {
		dPNameandID.clear();
		keyAliasList.clear();
		keyNameandDataType.clear();
		final String dataProviderUri = String.format(DOCUMENT_DATA_PROVIDER_URL, serverURI, reportId);
		try {
			String dp_response = httputils.getJsonResponseCHILD(dataProviderUri);

			JSONParser parse = new JSONParser();

			JSONObject jobj = (JSONObject) parse.parse(dp_response);
			JSONObject jobj2 = (JSONObject) jobj.get("dataproviders");
			JSONArray jsonarr = (JSONArray) jobj2.get("dataprovider");
			HashSet<String> temp = new HashSet<>();

			for (int i = 0; i < jsonarr.size(); i++) {
				JSONObject obj = (JSONObject) jsonarr.get(i);
				String dp_id = (String) obj.get("id");

				String dataProviderByIdUri = String.format(DOCUMENT_DATA_PROVIDER_BYID_URL, serverURI, reportId, dp_id);
				String dp_desc = httputils.getJsonResponseCHILD(dataProviderByIdUri);

				jobj = (JSONObject) parse.parse(dp_desc);
				jobj2 = (JSONObject) jobj.get("dataprovider");

				JSONObject jobj3 = (JSONObject) jobj2.get("dictionary");

				String query = (String) jobj2.get("query");

				getTypeId(query);

				JSONArray inner_jsonarr = (JSONArray) jobj3.get("expression");

				for (int j = 0; j < inner_jsonarr.size(); j++) {
					JSONObject inner_obj = (JSONObject) inner_jsonarr.get(j);
					String column_id = (String) inner_obj.get("id");
					String column_name = (String) inner_obj.get("formulaLanguageId");
					String name = (String) inner_obj.get("name");
					keyCounterDetails.put(column_id, name);
					dPNameandID.put(column_id, column_name);

					String qualification = (String) inner_obj.get("@qualification");

					String data_type = (String) inner_obj.get("@dataType");

					String keyResultObject = column_name.substring(column_name.lastIndexOf(".") + 1);
					keyResultObject = keyResultObject.substring(1, keyResultObject.length() - 1);
					if (!qualification.equals("Dimension")) {
						if (qualification.equals("Measure") && data_type.equals("Numeric")) {
							counterNames.add(name);
						}
						continue;
					}

					String key = modifiedKey(column_name, column_id);

					if (!temp.contains(key.toUpperCase())) {
						keyNameandDataType.put(key, data_type);
						keyAliasList.add(key);
					}
					temp.add(key.toUpperCase());
				}
			}

		} catch (Exception e) {
			logger.error("Error in getting column details ", e);
		}
	}

	private void getFormatDetails() throws Exception {
		logger.info("Fetching formatting applied to the report elements");
		final String ReportUri = String.format(DOCUMENT_REPORTS_URL, serverURI, reportId);
		final ReportsMapper rmapper = ((ReportsWrapper) httputils.getJsonResponse(ReportUri, ReportsWrapper.class))
				.getMapper();

		for (final Reports report : rmapper.getReports()) {
			final String reportElementURI = String.format(REPORT_ELEMENTS_URL, serverURI, reportId, report.getId());
			ReportElementsMapper elementMapper = ((ReportElementsWrapper) httputils.getJsonResponse(reportElementURI,
					ReportElementsWrapper.class)).getReportElementMapper();
			getFormattingInfo(elementMapper.getReportElements(), report.getId());
		}
	}

	private static void getFormattingInfo(ArrayList<ReportElements> arrayList, int id) throws Exception {

		Template template = null;
		Content content = null;
		Expression expression = null;
		Format formatObj = null;
		Alerters alerters = null;
		ElementFormula formula = null;
		Axes axes = null;
		ArrayList<Axis> axis = new ArrayList<Axis>();
		ArrayList<ElementFormula> elementFormula = new ArrayList<ElementFormula>();

		String positive = null, name = null, dataType = null, type = null;

		for (ReportElements element : arrayList) {
			final String reportElementDetailsURI = String.format(REPORT_ELEMENT_DETAILS_URL, serverURI, reportId, id,
					element.getId());

			ElementData elementData = ((ElementMapper) httputils.getJsonResponse(reportElementDetailsURI,
					ElementMapper.class)).getElement();
			if (elementData != null) {
				if ((content = elementData.getContent()) != null) {
					if (elementData.getType().equalsIgnoreCase("VTable")) {

						if ((axes = content.getAxes()) != null) {
							if ((axis = axes.getAxis()) != null) {
								for (Axis axis2 : axis) {
									if (axis2.getExpression() != null) {
										elementFormula = axis2.getExpression().getFormula();
										for (ElementFormula elementFormula2 : elementFormula) {

											kpiNamesFromTable.add(elementFormula2.getKpiName());
										}
									}
								}
							}
						}
					}

					if ((expression = content.getExpression()) != null) {
						formula = expression.getFormula();

						name = formula.getKpiName();

						if ((alerters = content.getAlerters()) != null) {
							alerterDetails.put(name, alerters.getAlerterId());
						}
						if ((dataType = formula.getDataType()) != null && (type = formula.getType()) != null) {
							if (dataType.equalsIgnoreCase("Numeric") && type.equalsIgnoreCase("text")) {
								if ((formatObj = expression.getFormat()) != null
										&& (template = formatObj.getTemplate()) != null) {
									positive = template.getPositive();
									formatDetails.put(name, positive);
								}
							}
						}

					}
				}
			}
		}
	}

	private void alerterDetails() throws Exception {
		logger.info("Fetchng alerters applied to the report");
		alerters.clear();
		AlerterDetails detailsMapper = null;
		boolean isNotBetweenPresent = false, isTypeCasted = false;
		ArrayList<String> list;
		ArrayList<String> formulaList;
		ArrayList<Rule> rule = new ArrayList<>();
		Conditions conditions = null;
		ArrayList<Condition> condition = null;
		Formula formula = new Formula();
		String expression = "", operator = null;
		String value = "", matchedPattern = "";
		Pattern p = null;
		Matcher m = null;
		int id;
		String description = "", temp, modifiedTemp = "";

		String alerterUri = String.format(DOCUMENT_ALERTERS_URL, serverURI, reportId);
		AlertersMapper mapper = ((AlertersWrapper) httputils.getJsonResponse(alerterUri, AlertersWrapper.class))
				.getAlerterMapper();
		for (Alerter alerter : mapper.getAlerters()) {
			list = new ArrayList<String>();
			formulaList = new ArrayList<String>();
			alerterUri = String.format(DOCUMENT_ALERTER_DETAILS_URL, serverURI, reportId, alerter.getId());
			detailsMapper = ((AlerterDetailsMapper) httputils.getJsonResponse(alerterUri, AlerterDetailsMapper.class))
					.getAlerter();
			
			 bg_color="";
	    	 font_color="";
	    	 stylePresent = false;
			id = detailsMapper.getId();
			alerterUri = String.format(DOCUMENT_ALERTER_DETAILS_URL, serverURI, reportId, alerter.getId(), id);
			try {
				AlerterFormatDetails formatDetails = ((AlerterFormatDetailsMapper) httputils.getJsonResponse(alerterUri,
						AlerterFormatDetailsMapper.class)).getFormatDetails();
				ArrayList<FormatRule> rules = formatDetails.getRule();
				
				if (rules != null) {
					for (FormatRule formatRule : rules) {
						
						Action action = formatRule.getAction();
						if (action != null) {
							Data data = action.getData();
							if (data != null) {
								ConditionalFormula conditionalformula = data.getFormula();
								if (conditionalformula != null) {
									String formatFormula = conditionalformula.getFormula();
									formulaList.add(formatFormula);
								}
							}

							Style style=action.getStyle();
							if(style != null) {
								stylePresent = true;
								BackGround backGround = style.getBackground();

								if(backGround != null) {
									Color color = backGround.getColor();
									if (color != null) {
								
									 bg_color=color.getRgb();}
									
								}
								Font font=style.getFont();

								if(font != null) {
									font_color = font.getRgb();
								}
							}
						//	}
						}
					}

				}
				}catch(Exception e){
				//	System.out.println(e);
				}
	    //        System.out.println("p.2593 formulaList->"+formulaList);
	//--->
//				alerterIdandBgColor.put(id, bg_color_list);
//				alerterIdandFontColor.put(id, font_color_list);
	//---->			
				alerterIdandConditionalFormula.put(id, formulaList);
				description = detailsMapper.getDescription();
				alerterIdandDesc.put(id, description);

				// isNotBetweenPresent = false;
				if ((rule = detailsMapper.getRule()) != null) {
					for (Rule rule2 : rule) {
						if ((conditions = rule2.getConditions()) != null) {
							condition = conditions.getCondition();

							for (Condition condition2 : condition) {
								expression = "";
								temp = "";
								isTypeCasted = false;
								isNotBetweenPresent = false;
								operator = condition2.getOperator();
								if (operators.containsKey(operator))
									operator = operators.get(operator);
								if ((expression = condition2.getExpressionId()) != null) {
									if (mergeDimensionAndId.containsKey(expression)) {
										temp = mergeDimensionAndId.get(expression);
										expression = "[" + temp + "]";
									} else if (keyCounterDetails.containsKey(expression)) {
										temp = keyCounterDetails.get(expression);
										modifiedTemp = modifyResultObject(temp);
										expression = "[" + temp + "]";
									}

									else
										expression = "{" + condition2.getExpressionId() + "}";
									if (!temp.isEmpty() && ((mergeDimenDataType.containsKey(temp)
											&& mergeDimenDataType.get(temp).equalsIgnoreCase("string"))
											|| (keyNameandDataType.containsKey(modifiedTemp)
													&& keyNameandDataType.get(modifiedTemp).equalsIgnoreCase("string")))) {
										expression = "String(" + expression + ")";
										isTypeCasted = true;
									}
									expression += " ";
								} else
									expression = "{currentKPIId}";
								if (operator != null) {
									if (operator.equalsIgnoreCase("NotBetween")) {
										isNotBetweenPresent = true;
									} else {
										// expression = "String(" + expression + ")";
										expression = expression + operator + " ";
									}
								}
								if (condition2.getOperand() != null) {
									value = String.join(",", condition2.getOperand());

									if (isNotBetweenPresent) {
										String[] range = value.split(",");
										String startingRange = range[0];
										String endingRange = range[1];
										value = " < " + startingRange + " or " + expression + " > " + endingRange;
									}

									else if (isTypeCasted)
										value = "'" + value + "'";

									expression = expression + value;

								}
								formula.setDefinition(expression);
								expression = formula.getDefinition();
								list.add(expression);

							}
	//-->
							

							
	//-->						
						} else {
							expression = rule2.getExpression();

							if (expression != null) {
								p = Pattern.compile("Then\\s*\\({0,1}[1|0]\\){0,1} |\\s*Else\\s*\\({0,1}[1|0]\\){0,1}");
								m = p.matcher(expression);
								while (m.find()) {

									if (m.group().contains("(0)"))
										matchedPattern = m.group().replace("(0)", "false");
									else if (m.group().contains("(1)"))
										matchedPattern = m.group().replace("(1)", "true");
									else if (m.group().contains("0"))
										matchedPattern = m.group().replace("0", "false");
									else if (m.group().contains("1"))
										matchedPattern = m.group().replace("1", "true");
									expression = expression.replace(m.group(), matchedPattern);

								}
								formula.setDefinition(expression);
								expression = formula.getDefinition();

								list.add(expression);
							}

						}
					}
	//--->			
					if (stylePresent == true && (bg_color != "" || font_color != "") && !(aleters1.containsKey(id))){
						
						aleters1.put(id, "BackGround =" + bg_color + ", FontColor =" + font_color);
						alerterList_Map.put(id, expression);
					}
			      
	 //-->        
	                for (int i = formulaList.size(); i < list.size(); i++) {
						formulaList.add("");
					}

					for (int i = 0; i < list.size() && i < formulaList.size(); i++) {
						if (formulaList.get(i).isEmpty()) {
	//-->              
						//	aleters1.put(id,bg_color+","+font_color);
						//	alerterList_Map.put(id, list);
							list.set(i, "");
						}
					}
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).isEmpty())
							list.remove(i);
					}

					if (!list.isEmpty()) {
						alerters.put(id, list);
					
					}
				}
			}
		}

	private String getFilterDetails(String report_response) {

		String filter = "", logicalOperator = "", leftSide = "", rightSide = "", operator_val = "";

		List<String> filterList = new ArrayList<>();
		List<String> leftSideList = new ArrayList<>();
		List<String> rightSideList = new ArrayList<>();
		List<String> operatorsList = new ArrayList<>();

		List<String> completeFiltersList = new ArrayList<>();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
			org.w3c.dom.Document doc = builder.parse(new InputSource(new StringReader(report_response)));

			NodeList opr = doc.getElementsByTagName("OPERATOR");
			for (int i = 0; i < opr.getLength(); i++) {

				Node node = opr.item(i);

				if (node.hasAttributes()) {
					NamedNodeMap nodeMap = node.getAttributes();
					if (nodeMap == null)
						continue;
					for (int j = 0; j < nodeMap.getLength(); j++) {
						Node subNode = nodeMap.item(j);
						logicalOperator = subNode.getTextContent();

					}
				}
				if (node.hasChildNodes()) {
					NodeList nodeMap = node.getChildNodes();
					if (nodeMap == null)
						continue;
					for (int j = 0; j < nodeMap.getLength(); j++) {
						Node subNode = nodeMap.item(j);
						rightSide = subNode.getTextContent();

						rightSideList.add(rightSide);
						if (subNode.hasAttributes()) {
							NamedNodeMap nodeMap2 = subNode.getAttributes();
							if (nodeMap2 == null)
								continue;
							for (int j1 = 0; j1 < nodeMap2.getLength(); j1++) {
								Node subNode2 = nodeMap2.item(j1);
								leftSide = subNode2.getTextContent();

								leftSideList.add(leftSide);

							}
						}
						if (subNode.hasChildNodes()) {
							NodeList nodeMap2 = subNode.getChildNodes();
							if (nodeMap2 == null)
								continue;
							for (int j1 = 0; j1 < nodeMap2.getLength(); j1++) {
								Node subNode2 = nodeMap2.item(j1);
								if (subNode2.hasAttributes()) {
									NamedNodeMap operator = subNode2.getAttributes();
									if (operator == null)
										continue;
									for (int y = 0; y < operator.getLength(); y++) {
										Node operatorVal = operator.item(y);

										operator_val = operatorVal.getTextContent();
										if (operators.containsKey(operator_val))
											operator_val = operators.get(operator_val);
										operator_val = operator_val.replace("IsNotNull", "Is Not Null");
										operatorsList.add(operator_val);
									}
								}
							}
						}
					}
					for (int start = 0; start < leftSideList.size() && start < rightSideList.size()
							&& start < operatorsList.size(); start++) {
						String leftVal = leftSideList.get(start);

						if (Formula.getKPINames().contains(leftVal.substring(1, leftVal.length() - 1))) {
							leftVal = leftVal.trim();
							leftVal = leftVal.replaceAll("\\s+", "_");

						} else if (keyAliasList.contains(modifyResultObject(leftSideList.get(start)))) {
							leftVal = modifyResultObject(leftVal);

						} else if (dataFiltersAliasMap.containsKey(leftVal)) {
							leftVal = dataFiltersAliasMap.get(leftVal);

						} else {
							for (String alias : dataFiltersAliasMap.keySet()) {
								if (alias.substring(alias.lastIndexOf(".") + 1).equals(leftVal)) {
									leftVal = dataFiltersAliasMap.get(alias);

								}
							}

						}

						String rightVal = rightSideList.get(start);

						String leftValTemp = leftVal.substring(1, leftVal.length() - 1);

						if (keyNameandDataType.containsKey(leftValTemp)
								&& keyNameandDataType.get(leftValTemp).equals("String")) {

							rightVal = "'" + rightVal + "'";
						}
						filter = leftVal + " " + operatorsList.get(start) + " " + rightVal;

						filterList.add(filter);
					}
				}
				String completeFilter = "";
				for (int i1 = 0; i1 < filterList.size(); i1++) {
					if (i1 == filterList.size() - 1)
						completeFilter += filterList.get(i1);
					else
						completeFilter += filterList.get(i1) + " " + logicalOperator + " ";
				}
				filterList = new ArrayList<>();
				leftSideList = new ArrayList<>();
				rightSideList = new ArrayList<>();
				operatorsList = new ArrayList<>();

				completeFiltersList.add(completeFilter);

			}

		} catch (Exception e) {
			logger.error("Error getting filters ", e);
		}

		String completeFilters = String.join(",", completeFiltersList);
		return completeFilters;

	}

	public void getCounterDetails() {

		logger.info("Getting the formatting info for the counters");
		// try {
		// sc = new Scanner(new FileInputStream(roundOffPropertyFile));
		// } catch (FileNotFoundException e1) {
		// logger.error("Error in reading file ",e1);
		// }
		// String[] str;
		// boolean flag = false;
		// ArrayList<String> newCounterList = new ArrayList<String>();
		// for (String string : counterNames) {
		// flag = false;
		// if (!counterRoundOffValues.containsKey(string)) {
		// while (sc.hasNextLine()) {
		// String line = sc.nextLine();
		// str = line.split(":");
		// if (str[0].contains(string) && typeIdName.contains(str[1])) {
		// flag = true;
		// if (str.length > 2)
		// counterRoundOffValues.put(string, str[2]);
		// break;
		// }
		// }
		// } else
		// flag = true;
		// if (!flag) {
		// newCounterList.add(string);
		// }
		// }
		// sc.close();

		if (!counterNames.isEmpty()) {
			HashMap<String, String> counterRoundOffValues1 = new HashMap<>();
			counterRoundOffValues1 = obj1.writeCounterDetails(typeIds, typeIdName, counterNames);
			counterRoundOffValues.putAll(counterRoundOffValues1);
		}
	}

	public static HashMap<String, String> getRoundOffCounters() {
		return counterRoundOffValues;
	}

	static void clearCollection() {
		inc = 0;
		typeIds.clear();
		counterRoundOffValues.clear();
		typeIdName = "";
		kpiNamesFromTable.clear();
		kpiNames.clear();
		mergeDimensionKeysId.clear();
		keyandDPKey.clear();
		keyCounterDetails.clear();
		alias_data_provider.clear();
		alias_map.clear();
		alias_table_set.clear();
		counterNames.clear();
		mergeDimenDetails.clear();
		mergeDimenDataType.clear();
		keyDPAndMergeDimension.clear();
		alerterDetails.clear();
		formatDetails.clear();
		Distinct_count_alias = "";
		Query.keyNames.clear();
		Query.duplicateResultObjectForSingleKey.clear();
		Query.tablesList.clear();
		Query.keysMap.clear();
		Query.dataProviderNames.clear();
		Query.originalString.clear();
		Query.checkForDuplicateInKeys.clear();
		Query.checkForDuplicateInResultObject.clear();
		Query.Union.clear();
		Formula.getMap().clear();
		Query.duplicateResultObjectForSingleKey.clear();
		Query.duplicateResultObjects.clear();
		Query.DuplicateAliasesForKey.clear();
	}

	public static boolean getMultipleQuery() {
		return isMultipleQueryPresent;
	}

	public static HashMap<String, String> getMergeDimDetails() {
		return mergeDimenDataType;
	}

	private static void fetchReportDetails(String response) {
		JSONParser parse = new JSONParser();
		String siName = null;

		for (int i = 0; i < size; i++) {
			String path = "";
			JSONObject jobj = null;
			try {
				jobj = (JSONObject) parse.parse(response);
			} catch (ParseException e1) {
				logger.error("Error reading response ", e1);
			}
			JSONArray jsonarr = (JSONArray) jobj.get("entries");
			JSONObject obj = (JSONObject) jsonarr.get(i);
			long parentId = (long) obj.get("SI_PARENT_FOLDER");
			reportId = (long) obj.get("SI_ID");
			siName = (String) obj.get("SI_NAME");

			node_type = (String) obj.get("SI_KEYWORD");

			siName = siName.replaceAll("/", "_");
			if (!node_type.isEmpty()) {
				node_type = node_type.replace("\"", "_");
				if (!siName.contains(","))
					siName = node_type + "," + siName;
				else
					siName = siName + "_" + node_type;
			}
			String cmsUrl = String.format(CMS_URL, serverURI);

			try {
				String response2 = httputils.getJsonResponsePath(cmsUrl, parentId);
				path = fetchCompletePath(response2, siName, reportId);
				String folders[] = path.split(Pattern.quote("\\"));
				folderName = folders[folders.length - 1];

			} catch (Exception e) {
				logger.error("Error reading response ", e);
			}

			siNameandReportId.put(siName, reportId);
			siNameandKeyword.put(siName, node_type);
			siNamesList.add(siName);
			String GenerateVal = "\"" + siName + "\"" + "," + "\"" + path + "\"" + "," + "\"" + folderName + "\"";
			String ListVal = "\"" + siName + "\"" + "," + "\"" + path + "\"";

			SiNameAndPath.add(ListVal);
			siNamePathAndFolder.add(GenerateVal);

			if (siName == null) {
				siName = Integer.toString((int) reportId);
			}

		}

	}

	public long getIdForNextHierarchy(String response) throws ParseException {
		JSONParser parse = new JSONParser();

		JSONObject jobj = (JSONObject) parse.parse(response);
		JSONArray jsonarr = (JSONArray) jobj.get("entries");

		long nextId = -1;

		for (int indexx = 0; indexx < jsonarr.size(); indexx++) {
			JSONObject obj = (JSONObject) jsonarr.get(indexx);
			nextId = (long) obj.get("SI_ID");
		}
		return nextId;
	}

	public String getReportNameForNextHierarchy(String response) throws ParseException {
		JSONParser parse = new JSONParser();

		JSONObject jobj = (JSONObject) parse.parse(response);
		JSONArray jsonarr = (JSONArray) jobj.get("entries");

		String nextName = "";

		for (int indexx = 0; indexx < jsonarr.size(); indexx++) {
			JSONObject obj = (JSONObject) jsonarr.get(indexx);
			nextName = (String) obj.get("SI_NAME");
		}
		return nextName;
	}

	private static String fetchCompletePath(String response, String siName, long reportId) throws IOException {

		String path = "";
		JSONParser parse = new JSONParser();
		try {
			JSONObject jobj = (JSONObject) parse.parse(response);
			JSONArray jsonarr = (JSONArray) jobj.get("entries");

			for (int i = 0; i < jsonarr.size(); i++) {
				JSONObject inner_obj = (JSONObject) jsonarr.get(i);
				String parentName = (String) inner_obj.get("SI_NAME");
				JSONObject result_object = (JSONObject) inner_obj.get("SI_PATH");
				long totalFolders = (long) result_object.get("SI_NUM_FOLDERS");

				for (long j = totalFolders; j >= 1; j--) {
					String folder = (String) result_object.get("SI_FOLDER_NAME" + j);
					path += folder + "\\";

				}
				path += parentName;
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return path;
	}

	private static void createReportDetailsGenerateCSV() {
		String reportDetailsFilePath = path + pathSeperator + "Reports_Details_Generate.csv";
		String detailsHeader = "Wid File, Path, folderName";
		try {
			ReportDetailsGenerateWriter = FileUtil.createFileAdnWriter(reportDetailsFilePath);
			ReportDetailsGenerateWriter.write(String.valueOf(detailsHeader));
			ReportDetailsGenerateWriter.write(System.lineSeparator());
		} catch (IOException e2) {
			logger.error("Error in csv generation: ", e2);
		}

		if (siNamePathAndFolder.size() < 1) {
			widFilePresent = false;

		}

		for (String val : siNamePathAndFolder) {
			try {
				FileUtil.write(ReportDetailsGenerateWriter, val);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Reports_Details_Generate csv is generated");
	}

	private static void createReportDetailsListCSV() {
		// TODO Auto-generated method stub
		String reportDetailsFilePath = path + pathSeperator + "Reports_Details_List.csv";
		String detailsHeader = "Wid File, Path";
		try {
			ReportDetailsListWriter = FileUtil.createFileAdnWriter(reportDetailsFilePath);
			ReportDetailsListWriter.write(String.valueOf(detailsHeader));
			ReportDetailsListWriter.write(System.lineSeparator());
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		if (SiNameAndPath.size() >= 1) {
			try {
				for (String siNamePath : SiNameAndPath) {
					FileUtil.write(ReportDetailsListWriter, siNamePath);
				}
				// FileUtil.write(ReportDetailsListWriter, SiNameAndPath.iterator().next());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			widFilePresent = false;
		}

		System.out.println("Reports_Details_List csv is generated");
	}

	public static void main(String[] args) throws Exception {
		logger.info("Entering the application");
		HashMap<String, Integer> reportCount = new HashMap<String, Integer>();
		HashMap<String, Integer> failedCount = new HashMap<String, Integer>();
		HashMap<String, Integer> generatedCount = new HashMap<String, Integer>();
		path = args[0];
		serverURI = args[1];
		user_name = args[2];
		password = args[3];
		option = args[4].toLowerCase();
		folderPath = args[5];
		System.out.println("Path is: " + folderPath);
		counterRoundOffValues.clear();
		String folders[] = folderPath.split(Pattern.quote("\\"));
		String logFilePath = "", logfileNames = "";
        // system.out.println(" ");
		 Program program = new Program(serverURI, folders);

		String cmsUrl = String.format(CMS_URL, serverURI);

		if (mainParentId == null)
			return;

		long idd = 0;
		if (folders.length > 1) {
			idd = httputils.getId(cmsUrl, folders, Long.parseLong(mainParentId), 1);
			folderName = httputils.getFolderName();
		} else {
			idd = Long.parseLong(mainParentId);
			folderName = folders[0];
		}
		if (idd == -1) {
			logger.warn("Please enter valid path");
			return;
		}

		String res = httputils.getJsonResponseCMS(cmsUrl, idd);

		size = getReportsSize(res);
		fetchReportDetails(res);

		if (!widFilePresent) {
			logger.info("There is no wid file present under this path");
			return;
		}

		if (option.contains("List") || option.contains("LIST") || (option.contains("list"))) {

			createReportDetailsListCSV();

		} else if (option.contains("generate")) {
			DB_URL = args[6];
			USER = args[7];
			PASS = args[8];

			createReportDetailsGenerateCSV();

			obj1 = new UnivObjToCounterMapping();

			FileUtil.createDirectory(path);

			reportCount.put(folderPath, siNamesList.size());
			generatedCount.put(folderPath, 0);
			failedCount.put(folderPath, 0);

			System.out.println("Total no of Wid files under " + folderPath + " path: " + siNamesList.size());
			logger.info("Total no of Wid files under " + folderPath + " path: " + siNamesList.size());

			index = 0;

	    	for (int i = 0; i < siNamesList.size(); i++) {
			//	for (int i = 0; i < 1; i++) {
				siName = siNamesList.get(i);

				// System.setProperty("name",siName);
				// PropertyConfigurator.configure(log4jPath);
			//	System.out.println("3088 SI Name:->"+siName);
				logFilePath = log4jPath + siName.trim() + "_" + dateFormat.format(new Date()) + ".log";

				logfileNames += logFilePath + " && ";
			//	System.out.println("3092 logfileNames->"+logfileNames);
				fa.setFile(logFilePath);
				// fa.setFile(log4jPath+siName+dateFormat.format(new Date())+".log");
				fa.activateOptions();
				logger.info("\n\n-----------------Generating csv for: " + siName + " ----------------------\n");

				reportId = siNameandReportId.get(siName);
				node_type = siNameandKeyword.get(siName);
				if (!node_type.isEmpty())
					node_type = node_type.replace("\"", "_");

				clearCollection();
				++index;
				program.createCSV();

				try {
					if (!generatedCount.containsKey(folderPath))
						generatedCount.put(folderPath, 0);
					generatedCount.put(folderPath, generatedCount.get(folderPath) + 1);
					if (siName.contains("Description"))
						continue;

					program.getFormatDetails();
					getMergeDimesionKeys();
					getColumnsDetails();

					program.getCounterDetails();
					processMergeDimension();
					replaceResObjToMergeDimenResObj();
					program.alerterDetails();
					program.processQuery();
					program.processReportsTable();
					program.processVariable();

					program.processReports();

				} catch (Exception e) {
					failedCount.put(folderPath, failedCount.get(folderPath) + 1);
					logger.error("Issue in generating csv files ", e);
				}

				program.closeAllFiles();

			}
			if (reportCount.containsKey(folderPath)) {
				if (reportCount.get(folderPath) == generatedCount.get(folderPath) && reportCount.get(folderPath) > 0) {
					int reportsFailed = failedCount.get(folderPath);
					logger.info("Total no. of reports for which csv generation has failed " + ": " + reportsFailed);
					logger.info("Total no. of reports for which csv generation is successful " + ": "
							+ (reportCount.get(folderPath) - reportsFailed));
					generatedCount.put(folderPath, 0);
				}
			}

			obj1.closeConnection();
			System.out.println(logfileNames);
			logger.info("Exiting application");
		} else if (option.equals("compare")) {
			csvPath = args[6];
			report = args[7];

			for (int i = 0; i < siNamesList.size(); i++) {
				siName = siNamesList.get(i);
				if (!report.contains(siName))
					continue;

				logFilePath = log4jPath + siName + "_dataComparison.log";
				fa.setFile(logFilePath);
				fa.setLayout(new PatternLayout("%m%n"));
				fa.activateOptions();

				reportId = siNameandReportId.get(siName);
				// node_type = siNameandKeyword.get(siName);

				if (siName.contains("Description"))
					continue;

				program.getRefreshParameter();
				program.refreshReport(args);
				program.fetchTableName();
			}
		}
	}

	private void fetchTableName() throws Exception {
		String reportName = "";
		final String ReportUri = String.format(DOCUMENT_REPORTS_URL, serverURI, reportId);
		final ReportsMapper rmapper = ((ReportsWrapper) httputils.getJsonResponse(ReportUri, ReportsWrapper.class))
				.getMapper();
		for (final Reports report : rmapper.getReports()) {

			chart_details = "";
			table_details = "";
			ReportTablesList.clear();
			sections = "";

			final String reportByIdUri = String.format(DOCUMENT_REPORTS_BYID_URL, serverURI, reportId, report.getId());
			String report_response = httputils.getResponseXml(reportByIdUri);

			boolean isChart = false;

			isChart = isChartTab(report_response, isChart);
			if (!isChart)
				getReportDetailsList(report_response);

			Report reportXml = (Report) (httputils.getXmlResponse(reportByIdUri, Report.class));

			String report_data = reportXml.getFormattedString();

			String tableData[] = report_data.split("," + "\"");
			String rowData = tableData[tableData.length - 1];
			rowData = rowData.substring(0, rowData.length() - 1);
			String t = "";
			for (int i = 1; i < tableData.length - 1; i++) {
				if (i == 1)
					t += "\"" + tableData[i] + "," + "\"";
				else if (i != tableData.length - 2)
					t += tableData[i] + "," + "\"";
				else
					t += tableData[i];
			}

			String remainingData = t;
			String rowArr[] = rowData.split("@");

			int i = 0;

			if (!ReportTablesList.isEmpty()) {

				while (i < rowArr.length) {
					String uniqueID = "";
					for (int j = 0; j < i; j++)
						uniqueID += "*";
					if (tableData[0].endsWith("\""))
						tableData[0] = tableData[0].substring(0, tableData.length - 1);
					String data = tableData[0] + uniqueID + "," + remainingData + "," + rowArr[i++];
					if (data.endsWith(","))
						data = data.substring(0, data.length() - 1);
					reportName = data;
				}
			} else {
				reportName = report_data;
			}
			if (!isChart) {
				String temp[] = reportName.split(",");
				reportName = temp[1].replace("\"", "");
				reportName = reportName.replace("/", "_").replace(":", "");
				String exportReportUrl = String.format(REPORT_EXPORT_URL, serverURI, reportId, report.getId());
				String reportData = httputils.exportToCSV(exportReportUrl);
				fetchReportDataAndCompare(reportData, reportName);
			}
		}
	}

	private void getReportDetailsList(String report_response) {

		String table_details = "";

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;

		try {
			builder = factory.newDocumentBuilder();
			org.w3c.dom.Document doc = builder.parse(new InputSource(new StringReader(report_response)));

			int colSpan = 0;
			boolean headerFlag = false;
			multiColumnName.clear();
			NodeList nList = doc.getElementsByTagName("ROWGROUP");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				table_details = "";
				Node node = nList.item(temp);
				headerFlag = false;

				if (node.hasAttributes()) {
					NamedNodeMap attrMap = node.getAttributes();
					for (int j = 0; j < attrMap.getLength(); j++) {
						Node attr = attrMap.item(j);
						if (attr.getNodeName() == "type" && attr.getTextContent().equalsIgnoreCase("header")) {
							headerFlag = true;
						}
					}
				}

				if (node.hasChildNodes()) {
					NodeList nodeMap = node.getChildNodes();

					for (int i = 0; i < nodeMap.getLength(); i++) {
						colSpan = 1;
						Node tempNode = nodeMap.item(i);
						if (headerFlag && tempNode.hasChildNodes()) {
							NodeList nodeMap2 = tempNode.getChildNodes();
							for (int j = 0; j < nodeMap2.getLength(); j++) {
								Node node_child = nodeMap2.item(j);
								if (node_child.getNodeName() == "TDCELL") {
									NamedNodeMap attributes = node_child.getAttributes();
									for (int k = 0; k < attributes.getLength(); k++) {
										if (attributes.item(k).getNodeName().toString().equalsIgnoreCase("colspan")) {
											colSpan = Integer.parseInt(attributes.item(k).getNodeValue());
											break;

										}
									}
									if (node_child.hasChildNodes()) {
										NodeList nodeMap3 = node_child.getChildNodes();

										for (int k = 0; k < nodeMap3.getLength(); k++) {

											Node node_child2 = nodeMap3.item(k);
											if (node_child2.getNodeName() == "CONTENT") {

												String table_details_temp = node_child2.getTextContent();
												if (colSpan > 1) {
													multiColumnName.add(table_details_temp);
													continue;
												}

												if (keyAndMergeDimension.containsKey(table_details_temp))
													table_details_temp = keyAndMergeDimension.get(table_details_temp);

												table_details_temp = table_details_temp.replace("NameOf", "");
												flag = true;
												if (neNameDefinition.containsKey(table_details_temp))
													table_details_temp = neNameDefinition.get(table_details_temp);
												table_details_temp = Formula.removeTableName(table_details_temp);
												Pattern p = Pattern.compile("\\[([^\\[]*)\\]");
												Matcher m = p.matcher(table_details_temp);
												if (m.find()) {
													table_details_temp = table_details_temp.substring(0, m.start() + 1)
															+ m.group(1).trim()
															+ table_details_temp.substring(m.end() - 1);
												}

												p = Pattern.compile("\\]");
												m = p.matcher(table_details_temp);
												int close_bracket = 0;
												while (m.find()) {
													close_bracket++;
												}

												if (close_bracket == 1) {
													table_details_temp = table_details_temp.replace("]", "'");
												}
												if (table_details_temp.startsWith("[")
														&& table_details_temp.endsWith("]")) {
													table_details_temp = table_details_temp
															.substring(1, table_details_temp.length() - 1).trim();
													table_details_temp = "[" + table_details_temp + "]";
												}
												table_details_temp = table_details_temp.trim();
												table_details_temp = table_details_temp.replace("\\[", "[")
														.replace("\\]", "]");
												table_details_temp = table_details_temp.replaceAll("=", "")
														.replaceFirst("\\[", "'").replaceAll("\\]$", "'");
												table_details_temp = table_details_temp.replaceAll("\"", "'");
												table_details_temp = "'" + table_details_temp + "'";

												table_details += table_details_temp.replace("''", "'") + ",";

											}

										}
									}
								}
							}

						}
					}
				}
				if (table_details.endsWith(","))
					table_details = table_details.substring(0, table_details.length() - 1);
				if (headerFlag && !table_details.isEmpty())
					ReportTablesList.add("\"" + table_details + "\"");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void fetchReportDataAndCompare(String reportData, String reportName) throws IOException {
		int size = 0;
		ArrayList<ArrayList<String>> boData = new ArrayList<ArrayList<String>>();
		ArrayList<String> columns = new ArrayList<String>();
		ArrayList<String> columnsNetAN = new ArrayList<String>();
		String temp[];
		for (String string : multiColumnName) {
			reportData = reportData.replace(string, "");
		}
		for (int i = 0; i < ReportTablesList.size(); i++) {
			temp = ReportTablesList.get(i).replace("\"", "").split(",");
			for (int j = 0; j < temp.length; j++) {
				temp[j] = temp[j].replaceAll("\\s+", "_");
				temp[j] = temp[j].replace("'", "");
				columnsNetAN.add(temp[j]);
			}
		}
		size = columnsNetAN.size();
		columns.clear();
		Pattern p = Pattern.compile("([\"'])(?:(?=(\\\\?))\\2.)*?\\1");
		Matcher m = p.matcher(reportData);
		int row = 1;
		String str;
		while (m.find()) {
			str = m.group().replace("\"", "");
			if (!str.isEmpty())
				columns.add(str);
			else if (str.isEmpty() && !columns.isEmpty() && row != 1)
				columns.add(str);
			if (columns.size() == size) {
				if (row > 1) {
					boData.add(columns);
				}
				columns = new ArrayList<String>();
				row++;
			}
		}
		compareCsvs(boData, reportName);
	}

	private void compareCsvs(ArrayList<ArrayList<String>> boData, String name) {
		logger.info("comparing the data of table: " + name);

		ArrayList<String> columns = new ArrayList<String>();
		CSVReader csvReader = null;
		boolean flag = false, match = true;
		String boValue, netAnValue, column;
		double a, b;
		if (boData.size() == 0) {
			logger.info("\nThe table " + name + " has no data on BO\n");
		} else {
			try {
				csvReader = new CSVReader(new FileReader(csvPath + "\\" + name.trim() + ".csv"));
				String[] nextLine;
				int rowBO = 0, rowNetAn = 0;
				while ((nextLine = csvReader.readNext()) != null) {
					if (rowNetAn == 0) {
						for (int i = 0; i < nextLine.length; i++)
							columns.add(nextLine[i].replaceAll(",", ""));
						rowNetAn++;
					}

					else if (rowNetAn > 0) {
						logger.info("\nComparing the row " + rowNetAn);

						for (int i = 0; i < nextLine.length; i++) {
							flag = false;
							boValue = boData.get(rowBO).get(i).replaceAll("\"", "").trim().replaceAll(",", "");
							netAnValue = nextLine[i].replaceAll(",", "").trim();
							logger.info("BoValue: " + boValue + " NetAn: " + netAnValue);

							column = columns.get(i);
							if (netAnValue.startsWith(".") && Character.isDigit(netAnValue.charAt(1))) {
								netAnValue = "0" + netAnValue;
							}

							if (!(column.contains("DATE") || column.equalsIgnoreCase("TIME"))) {
								a = 0;
								b = 0;
								if (!(netAnValue.isEmpty() || boValue.isEmpty())) {
									if (Character.isDigit(boValue.charAt(0))
											&& Character.isDigit(netAnValue.charAt(0))) {
										a = Double.parseDouble(boValue);
										b = Double.parseDouble(netAnValue);
										flag = true;
									} else if (boValue.equalsIgnoreCase("#MULTIVALUE")
											&& Character.isDigit(netAnValue.charAt(0))) {
										match = false;
										logger.info("Value does not match for the kpi: " + columns.get(i) + " BOData: "
												+ boValue + " NetAnData: " + netAnValue);
										System.out.println("Value does not match for the kpi: " + columns.get(i)
												+ " BOData: " + boValue + " NetAnData: " + netAnValue);
									}
								}

								if (!(boValue.contains("N/A") && netAnValue.isEmpty())
										&& !(boValue.isEmpty() && netAnValue.equals("0.00"))) {
									if (flag && !(a == b)) {
										match = false;
										logger.info("Value does not match for the kpi: " + columns.get(i) + " BOData: "
												+ a + " NetAnData: " + b);
										System.out.println("Value does not match for the kpi: " + columns.get(i)
												+ " BOData: " + a + " NetAnData: " + b);
									} else if (!flag && !boValue.equalsIgnoreCase(netAnValue)) {
										match = false;
										logger.info("Value does not match for the kpi: " + columns.get(i) + " BOData: "
												+ boValue + " NetAnData: " + netAnValue);
										System.out.println("Value does not match for the kpi: " + columns.get(i)
												+ " BOData: " + boValue + " NetAnData: " + netAnValue);
									}
								}
							}
						}
						rowBO++;
						rowNetAn++;
					}
				}
				if (match) {
					logger.info("The data matches for the table: " + name + "\n");
					System.out.println("The data matches for the table: " + name + "\n");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void refreshReport(String[] args) throws Exception {
		String reportRefreshUri = String.format(REFRESH_PARAMETERS_URL, serverURI, reportId);
		String request = "{\n" + "  \"parameters\": {\n" + "    \"parameter\": [\n";
		request = request + generateRequestForRefreshing(args);
		try {
			httputils.putRequest(reportRefreshUri, request);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getRefreshParameter() throws Exception {
		String refreshParameterUrl = String.format(REFRESH_PARAMETERS_URL, serverURI, reportId);
		ArrayList<Parameter> parameter = ((ParametersWrapper) httputils.getJsonResponse(refreshParameterUrl,
				ParametersWrapper.class)).getParameters().getParameter();
		for (Parameter p : parameter) {
			parameterIds.put(p.getId(), p.getName());
		}
	}

	private static String generateRequestForRefreshing(String[] args) {
		String body = "", temp1 = "";
		int count = 0, i = 8;
		for (Integer id : parameterIds.keySet()) {
			temp1 = "      {\n" + "        \"id\": " + id + ",\n" + "        \"answer\": {\n"
					+ "          \"values\": {\n" + "            \"value\": [\n";
			temp1 = temp1 + "            \"" + args[i] + "\",\n";
			temp1 = temp1 + "            ]\n" + "          }\n" + "        }\n" + "      }\n";
			if (count < parameterIds.keySet().size() - 1) {
				body = body + temp1 + ",";
			} else
				body = body + temp1;
			count++;
			i++;
		}
		body = body + "]\n" + "}\n" + "}";
		return body;
	}

	private static void replaceResObjToMergeDimenResObj() {

		HashMap<String, String> keyNameandDataTypeTemp = new HashMap<>();
		String modifiedKey = "";

		for (String keyName : keyNameandDataType.keySet()) {

			if (keyDPAndMergeDimension.containsKey(keyName)) {

				modifiedKey = keyDPAndMergeDimension.get(keyName);
				keyNameandDataTypeTemp.put(modifyResultObject(modifiedKey), keyNameandDataType.get(keyName));
			} else
				keyNameandDataTypeTemp.put(modifyResultObject(keyName), keyNameandDataType.get(keyName));
		}
		keyNameandDataType.clear();
		keyNameandDataType.putAll(keyNameandDataTypeTemp);

		Set<String> keyAliasListTemp = new HashSet<>();

		for (String keyName : keyAliasList) {

			if (keyDPAndMergeDimension.keySet().contains(keyName)) {

				modifiedKey = keyDPAndMergeDimension.get(keyName);

				keyAliasListTemp.add(modifyResultObject(modifiedKey));
			} else {
				keyAliasListTemp.add(modifyResultObject(keyName));

			}

		}

		keyAliasList.clear();
		keyAliasList.addAll(keyAliasListTemp);

	}

}
