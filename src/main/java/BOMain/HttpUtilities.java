package BOMain;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static BOMain.Program.mainParentId;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class HttpUtilities {

	private final BO_Connection connection;

	final ObjectMapper objectMapper = new ObjectMapper();

	final Program program = new Program();
	
	private String reportName = "";

	final ObjectMapper xmlMapper = new XmlMapper();

	private static final Logger logger = LogManager.getLogger(HttpUtilities.class);

	public HttpUtilities(BO_Connection connection) {
		this.connection = connection;
	}

	public Object getJsonResponse(final String uri, Class mapperCalss) throws Exception {
		final String response = connection.query("GET", uri, "application/json");
		return objectMapper.readValue(response, mapperCalss);
	}

	public String getJsonResponseCMS(final String uri, long id) throws Exception {
		String response = "";
		try {
			String cms_query = "\""
					+ "SELECT SI_ID, SI_NAME, SI_KEYWORD, SI_PARENT_FOLDER FROM CI_INFOOBJECTS WHERE SI_KIND = 'Webi' AND SI_INSTANCE=0 AND SI_ANCESTOR = "
					+ id + "\"";
			String body = "{" + "\"query\"" + ":" + cms_query + "}";
			response = connection.queryCMS("POST", uri, "application/json", body);

		} catch (Exception e) {
			logger.warn("Please enter valid path");
		}
		return response;
	}

	public String getJsonResponseCHILD(String uri) throws Exception {
		final String response = connection.query("GET", uri, "application/json");
		return response;
	}

	public Object getJsonResponseString(final String uri, Class mapperCalss) throws Exception {
		final String jsonAsString = connection.query("GET", uri, "application/json");
		return objectMapper.readValue(jsonAsString, mapperCalss);
	}

	public long getId(final String uri, String folders[], long id, int index) throws Exception {

		String response = "";
		if (index >= folders.length)
			return id;
		String cms_query = "\"" + "SELECT SI_ID, SI_NAME FROM CI_INFOOBJECTS WHERE SI_NAME = " + "'" + folders[index++]
				+ "'" + " AND SI_PARENTID =  " + id + "\"";
		String body = "{" + "\"query\"" + ":" + cms_query + "}";
		try {
			response = connection.queryCMS("POST", uri, "application/json", body);
		} catch (Exception e) {
			return -1;
		}
		id = program.getIdForNextHierarchy(response);
		reportName = program.getReportNameForNextHierarchy(response);
		if (id == -1) {
			return -1;
		}
		return getId(uri, folders, id, index);
	}

	public String getFolderName() {
		return reportName;
	}

	public String getResponseXml(String uri) throws Exception {
		final String response = connection.query("GET", uri, "text/xml");
		return response;
	}
	
	public String getJsonResponsePath(final String uri, long id) throws Exception {
		String response = "";
		try {
		String cms_query = "\"" + "SELECT * FROM CI_INFOOBJECTS WHERE SI_ID =  " + id + "\"";
		String body = "{" + "\"query\"" + ":" + cms_query + "}"; 
		response = connection.queryCMS("POST", uri, "application/json", body);
		}
		catch(Exception e) {
			logger.warn("Please enter valid path");
		}
		return response;
	}

	public String getJsonResponseResultSet(String uri) throws Exception {
		final String response = connection.querytest("GET", uri, "application/xml");
		return response;
	}

	public String getResponseforPrompts(String uri) throws Exception {
		final String response = connection.query("GET", uri, "application/xml");
		return response;
	}

	public Object getXmlResponse(final String uri, Class mapperCalss) throws Exception {
		final String response = connection.query("GET", uri, "text/xml");
		xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return xmlMapper.readValue(response, mapperCalss);
	}
	
	public void putRequest(String reportRefreshUri, String requestBody) throws Exception {
		final String response = connection.queryCMS("PUT", reportRefreshUri,"application/json",requestBody);
		System.out.println("response: "+response);
	}
	
	public String exportToCSV(String uri) throws Exception {
		return connection.query("GET", uri, "text/csv");
	}


	public String getResponseInXml(String uri) throws Exception {
		final String response = connection.query("GET", uri, "text/plain");
		return response;
	}

}
