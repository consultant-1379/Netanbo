package BOMain;

import java.util.ArrayList;
import java.sql.*;

public class RepDbConnection {

	public ArrayList<String> repDB() {

		final String DB_URL = "jdbc:sybase:Tds:atvts4080.athtem.eei.ericsson.se:2641";

		final String USER = "dwhrep";
		final String PASS = "dwhrep";

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		ArrayList<String> dataNameList = new ArrayList<String>();

		try {
			Class.forName("com.sybase.jdbc4.jdbc.SybDriver");

			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			String sql;
			sql = "Select distinct dataname from measurementcolumn where coltype = 'key' ";
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				String dataname = rs.getString("dataname");
				dataNameList.add(dataname);
			}

		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs!=null)
					rs.close();
			}catch (SQLException e1) {
				e1.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();
			}catch (SQLException e2) {
				e2.printStackTrace();
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e3) {
				e3.printStackTrace();
			}
		}
		return dataNameList;
	}
}
