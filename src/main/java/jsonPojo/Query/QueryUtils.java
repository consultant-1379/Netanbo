package jsonPojo.Query;

import java.util.Arrays;
import java.util.List;

import javax.annotation.CheckForNull;

public class QueryUtils {

	static char openBraket = '(';
	static char closeBraket = ')';
	final static String specialCharacters = " !#$%&'()*+,-./:;<=>?@[]^_`{|}";
	final String appendString = "( ( DIM_ROWSTATUS.ROWSTATUS ) NOT IN ('DUPLICATE','SUSPECTED') )";
	final List queryKeyWords = Arrays.asList(new String[]{"SELECT,DISTINCT"});
	
	public static String validateStatement(final String query) {
		final StringBuilder clearQuery = new StringBuilder();

		for (int index = 0; index < query.length(); index++) {
			char currentChar = query.charAt(index);
			if (currentChar == openBraket) {
				String statement = "";
				statement = getStatement(query.substring(index, query.length()));
				if(statement != null) {
				index = index + statement.length() - 1;
				if (isValidStatement(statement)) {
					clearQuery.append(statement);
				}
				}
			} 
			else {
				clearQuery.append(currentChar);
			}
		}
		return clearQuery.toString();
	}

	private static boolean isValidStatement(String statement) {
		statement = statement.replaceAll("\\(", "").trim();
		Character character = statement.charAt(0);
		if (specialCharacters.contains(character.toString())) {
			return false;
		}
		return true;
	}

	private static String getStatement(final String statementString) {
		int braketsCounter = 0;
		final StringBuilder statement = new StringBuilder();
		for (int index = 0; index < statementString.length(); index++) {
			char currentChar = statementString.charAt(index);
			if (currentChar == openBraket) {
				braketsCounter = braketsCounter + 1;
				statement.append(currentChar);
			} else if (currentChar == closeBraket) {
				statement.append(currentChar);
				braketsCounter = braketsCounter - 1;
				if (braketsCounter == 0) {
					return statement.toString();
				}
			} else {
				statement.append(currentChar);
			}
		}
		return null; // should never happen
	}


	private static String addAsToColumns(String query) {
		String columns = query.substring(0, query.indexOf("FROM"));
		String allColumns = removeKeywords(columns);
		for (final String column : allColumns.split(",")) {
			if (column.contains(")")) {
				columns = columns.replace(column, column + " as '" + column.split("\\.")[1].replace(")", "") + "'");
			} else {
				columns = columns.replace(column, column + " as " + "'" + column.split("\\.")[1] + "'");
			}
		}
		return columns + query.substring(query.indexOf("FROM"), query.length());
	}


	private static String removeKeywords(String columns) {
		columns = columns.startsWith("SELECT") ? columns.replaceFirst("SELECT ", "") : columns;
		columns = columns.startsWith("DISTINCT") ? columns.replaceFirst("DISTINCT ", "") : columns;
		return columns;
	}

	public static class QueryBuilder {

		private final String query;

		public QueryBuilder(final String query) {
			this.query = query;
		}

		private String replaceString;
		private String replaceSource;
		private boolean asEnabled;
		private boolean ignoreInvalidStatements;
		private boolean replaceEnabled;

		public QueryBuilder replace(final String replaceSource, final String replaceString) {
			this.replaceEnabled = true;
			this.replaceSource = replaceSource;
			this.replaceString = replaceString;
			return this;
		}

		public QueryBuilder addAs(final boolean asEnabled) {
			this.asEnabled = asEnabled;
			return this;
		}

		public QueryBuilder ignoreInvalidStatements(final boolean ignoreInvalidStatements) {
			this.ignoreInvalidStatements = ignoreInvalidStatements;
			return this;
		}

		public String build() {

			String query = this.query;
			if (ignoreInvalidStatements) {
				query = QueryUtils.validateStatement(query);
			}
			if (replaceEnabled) {

				query = query.replaceAll(replaceSource, replaceString);
			}
			if (asEnabled) {
				query = QueryUtils.addAsToColumns(query);
			}
			return query;
		}
	}

}