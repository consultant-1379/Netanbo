package BOMain;

import static BOMain.Program.logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsonPojo.Query.Query;

public class UnivObjToCounterMapping {

	private static HashSet<String> typeIds = new HashSet<String>();

	private static HashSet<String> aliasNames = new HashSet<String>();

	private static HashMap<String, String> aliasMap = new HashMap<String, String>();

	public static HashMap<String, String> neNameValues = new HashMap<String, String>();

	private static HashMap<String, String> counterAggregationMap = new HashMap<String, String>();

	String tableName = "";

	final String DB_URL = Program.DB_URL;

	final String USER = Program.USER;
	final String PASS = Program.PASS;

	Connection conn = null;
	static PreparedStatement preparedStmt1 = null;
	static PreparedStatement preparedStmt2 = null;
	static PreparedStatement preparedStmt3 = null;

	static ResultSet rs1 = null;
	static ResultSet rs2 = null;
	String query1 = "Select distinct Dataname from MeasurementCounter where typeid like ? and UnivObject like ?";
	String query2 = "Select distinct Dataname from MeasurementKey where typeid like ? and UnivObject like ?";
	String query3 = "Select datascale, dataname, TIMEAGGREGATION from MeasurementCounter where typeid like ? and Dataname=?";

	public UnivObjToCounterMapping() {
		try {
			logger.info("Establishing connection to the Db");
			Class.forName("com.sybase.jdbc4.jdbc.SybDriver");

			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			preparedStmt1 = conn.prepareStatement(query1);

			preparedStmt2 = conn.prepareStatement(query2);

			preparedStmt3 = conn.prepareStatement(query3);

			logger.info("Connected to Db");

		} catch (SQLException | ClassNotFoundException se) {
			logger.error("Issue in connecting to DB ", se);
		}
	}

	public String compareUnivObjAndCounter(String definition, String columnName) {
		typeIds = Program.getTypeId();
		String formula = definition;
		ArrayList<String> dataNames = new ArrayList<String>();
		HashSet<String> compared = new HashSet<String>();
		aliasMap = Query.aliasMap;
		aliasNames = Query.aliasNames;

		String regex = "\\[([^\\[]*)\\]";
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(definition);
		while (m.find()) {
			String temp = m.group(1);

			String match = m.group(1);

			String match2 = m.group();

			if (columnName != null && columnName.equals("NE Name")) {
				/*
				 * if(mergeDimenDetails.containsKey(temp)) { String mergeDimenId =
				 * mergeDimenDetails.get(temp).get(0); String mergeDimenName =
				 * dPNameandID.get(mergeDimenId).substring(
				 * dPNameandID.get(mergeDimenId).lastIndexOf(".")+1); neNameValues.put(match,
				 * mergeDimenName); return definition.replace("[" + temp + "]", mergeDimenName);
				 * }
				 */
				if (!findMatchInAlias(temp)) {
					if (aliasMap.containsKey("NE NAME")) {
						neNameValues.put(match, aliasMap.get("NE NAME"));
						return definition.replace(temp, aliasMap.get("NE NAME"));
					} else if (aliasMap.containsKey("NE ID")) {
						neNameValues.put(match, aliasMap.get("NE ID"));
						return definition.replace(temp, aliasMap.get("NE ID"));
					}
				}
			}
			if (neNameValues.size() > 0) {
				if (neNameValues.containsKey(match)) {
			//		System.out.println("116 univ Obj ->");
			//		System.out.println("match " + match + "match2 " + match2 + "formula " + formula);
					formula = formula.replace(match2, "[" + neNameValues.get(match) + "]");
			//		System.out.println("after " + formula);
				}
			}
			
			if (!temp.equals("Date") || !temp.equals("Time")) {

				if (!findMatchInAlias(temp)) {
					for (Map.Entry<String, String> entry : aliasMap.entrySet()) {

						if (entry.getKey().equals(match)) {
							return definition.replaceAll(m.group(1), entry.getValue());
						}
					}

					if (compared.add(match)) {

						for (String typeId : typeIds) {
							String temp1 = match;
							String toBeReplaced = match;
							String dataName = "";
							match = modifyCounter(match);
							if (match.contains(" AVG")) { // SGSN Overview Pool Raw Data
								int index = match.indexOf("AVG");
								if (index > 0) {
									match = match.substring(0, index);
								}
								temp1 = temp1.replace("AVG", "(AVG)");
							}
							match = match.trim();
							dataNames = UnivObjToCounterMapping.getDataNameFromMeasurementCounter(match, typeId);

							if ((dataNames.isEmpty())) {
								dataNames = UnivObjToCounterMapping.getDataNameFromMeasurementKey(match, typeId);

							}
							boolean flag = false;

							if (!(dataNames.isEmpty())) {
								for (String str : dataNames) {
									for (String string : aliasNames) {

										if (string.equals(str)) {
											dataName = string;

											flag = true;
											break;
										}
									}
									if (flag)
										break;
								}
							}
							if (!(dataNames.isEmpty()) && dataName.isEmpty()) {
								for (String str : dataNames) {
									for (String string : aliasNames) {

										if (string.contains(str)) {
											dataName = string;

											flag = true;
											break;
										}
									}
									if (flag)
										break;
								}
							}
							if (!(dataName.isEmpty())) {

								if (!dataName.equals(match)) {
									dataName = "[" + dataName + "]";
									temp = temp1.replace(toBeReplaced, dataName);

									formula = formula.replace(m.group(), temp);

								}
							}
						}
					}
				}
			}
		}
		definition = formula;
		return definition;
	}

	private boolean findMatchInAlias(String match) {

		for (String string : aliasNames) {
			if (string.equals(match)) {
				return true;
			}
		}
		return false;

	}

	private String modifyCounter(String match) {
		Pattern p = Pattern.compile("(\\(avg\\)|\\(min\\)|\\(max\\)|\\(sum\\))");
		Matcher m1 = p.matcher(match);
		if (m1.find()) {
			match = match.substring(0, m1.start() - 1);
		}
		return match.replace(".", "_");
	}

	public static HashMap<String, String> getMap() {
		return neNameValues;
	}

	private static ArrayList<String> getDataNameFromMeasurementKey(String univObject, String typeId) {
		ArrayList<String> dataNames = new ArrayList<String>();
		typeId = "%" + typeId + "%";
		try {
			preparedStmt2.setString(1, typeId);
			preparedStmt2.setString(2, univObject);
			rs2 = preparedStmt2.executeQuery();

			while (rs2.next()) {
				dataNames.add(rs2.getString("dataname"));
			}
		} catch (Exception e) {
			logger.error("Error in connecting to MeasurementKey table ", e);
		}
		return dataNames;
	}

	public static ArrayList<String> getDataNameFromMeasurementCounter(String matchedPattern, String techPack) {

		ArrayList<String> dataNames = new ArrayList<String>();
		HashMap<String, String> counterAggregation = new HashMap<>();
		String aggregation = "", dataName = "";
		try {
			techPack = "%" + techPack + "%";
			preparedStmt1.setString(1, techPack);
			preparedStmt1.setString(2, matchedPattern);
			rs1 = preparedStmt1.executeQuery();

			while (rs1.next()) {
				dataName = rs1.getString("dataname");

				dataNames.add(dataName);
				aggregation = rs1.getString("TIMEAGGREGATION");

				// counterAggregation.put(dataName, aggregation);
			}
			// setCounterAggregationValues(counterAggregation);
		} catch (Exception e) {
			logger.error("Error in connecting to MeasurementCounter table ", e);
		}
		return dataNames;

	}

	private static void setCounterAggregationValues(HashMap<String, String> counterAggregation) {
		// TODO Auto-generated method stub
		counterAggregationMap = counterAggregation;

	}

	public static HashMap<String, String> getCounterAggregationMap() {
		return counterAggregationMap;
	}

	public String extractTypeId(String query) {
		String underscore = "_";
		if (query != null) {

			String[] str = query.split("\\.");
			for (String string : str) {
				tableName = "";
				Pattern p = Pattern.compile("DC_E_.*");
				Matcher m = p.matcher(string);
				boolean find = m.matches();
				if (find) {
					String[] str1 = m.group().split(underscore);
					int i = 0;
					for (i = 0; i < str1.length - 2; i++) {
						tableName = tableName + str1[i] + underscore;
					}
					tableName = tableName + str1[i];
					break;
				}
			}

			/*
			 * String[] str = query.split("FROM"); String[] str1 = str[1].split("WHERE");
			 * String[] tableNames = str1[0].split(","); for (String string : tableNames) {
			 * Pattern p = Pattern.compile(".*\\.(.*)"); Matcher m = p.matcher(string); if
			 * (m.find()) { if (m.group(1).startsWith("DC")) tableName = m.group(1); } }
			 */

		}
		return tableName;

	}

	public HashMap<String, String> writeCounterDetails(HashSet<String> typeIds, String typeIdName,
			HashSet<String> counterNames) {
		HashMap<String, String> counterRoundOffValues = new HashMap<String, String>();
		HashMap<String, String> counterAggregation = new HashMap<>();
//		try {
//			writer = new BufferedWriter(new FileWriter(path,true));
//		} catch (IOException e) {
//			logger.error("File not found ",e);
//		}
		
		String roundOffValue = "", typeId, temp, aggregation = "", dataName = "";

		boolean flag;

		for (String string1 : counterNames) {
			
			flag = false;

			temp = modifyCounter(string1);

			// temp = string1;

			try {
//				writer.append(string1);
//			writer.append(":");

				for (String string : typeIds) {

					typeId = "%" + string + "%";
					preparedStmt3.setString(1, typeId);
					preparedStmt3.setString(2, temp);
					rs1 = preparedStmt3.executeQuery();
					if (rs1.next()) {

//					writer.append(string);
//					writer.append(":");
						flag = true;
						roundOffValue = rs1.getString("datascale");
						dataName = rs1.getString("dataname");

						aggregation = rs1.getString("TIMEAGGREGATION");

						counterAggregation.put(string1, aggregation);

						break;
					}
				}
				if (flag) {
//				writer.append(typeIdName);
//				writer.append(":");
					// roundOffValue = "0";
					counterRoundOffValues.put(string1, roundOffValue);
				}
//			writer.append(roundOffValue);
//			writer.append("\n");
				// counterRoundOffValues.put(string1,roundOffValue);
				setCounterAggregationValues(counterAggregation);
//			writer.close();
			} catch (Exception e) {
				logger.error("Error while fetching the formatting values for the counters ", e);
			}
		}
		return counterRoundOffValues;
	}

	public void closeConnection() {
		System.out.println("Closing statement, rs, and conn");
		try {
			if (preparedStmt1 != null)
				preparedStmt1.close();
			if (preparedStmt2 != null)
				preparedStmt2.close();
			if (rs1 != null)
				rs1.close();
			if (rs2 != null)
				rs2.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			logger.error("Encountered error while closing db connection: ", e);
		}
	}
}
