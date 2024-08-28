package jsonPojo.dataProviders.Tables;

import static BOMain.Program.chart_details;
import static BOMain.Program.sections;
import static BOMain.Program.dataFilters;
import static BOMain.Program.table_details;
import static BOMain.Program.ReportTablesList;
import static jsonPojo.Variables.Formula.flag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import BOMain.Program;
import jsonPojo.Variables.Formula;
import jsonPojo.dataProviders.section.Feeds;
import jsonPojo.dataProviders.section.TITLE;
import jsonPojo.dataProviders.section.Visu;

@JacksonXmlRootElement(localName = "Report")
public class Report {

	public static String prev = "";

	private Map<String, String> neNameDefinition = Formula.getMap();
	
	public static List<String> contentList = new ArrayList<>();

	@JacksonXmlProperty(localName = "rId", isAttribute = true)
	private String id;

	@JacksonXmlProperty(localName = "name", isAttribute = true)
	private String name;

	@JacksonXmlProperty(localName = "PAGE_BODY")
	private PageBody PAGE_BODY;

	public String getFormattedString() {

		
		setTableOrChartContent();
		return id + "," + "\"" + name.replaceAll("\\s+", "_") + "\"" + "," + "\"" +
		dataFilters + "\"" + "," + tableOrChart().replaceAll("\\s+", "_") + "," +
		"\"" + sections + "\"" + "," + getRowWithBoth().replaceAll("\\s+", "_");
	}
	
	public List<String> getTableOrChartContent() {
		return contentList;
	}
	
	public void setTableOrChartContent() {
		contentList.add(id);
		contentList.add(name.replaceAll("\\s+", "_"));
		contentList.add("\""+dataFilters+"\"");
		contentList.add(tableOrChart().replaceAll("\\s+", "_"));
		contentList.add("\""+sections+"\"");
		contentList.add(getRowWithBoth().replaceAll("\\s+", "_"));
	}

	private String getRowWithBoth() {
		String reportTableListDetails = "";
		for(int i=0; i < ReportTablesList.size(); i++) {
			if(i == ReportTablesList.size()-1)
			     reportTableListDetails += ReportTablesList.get(i);
			else
				 reportTableListDetails += ReportTablesList.get(i) + "@";
		}
		if (!chart_details.replaceAll("\"", "").isEmpty() && !ReportTablesList.isEmpty()) {
			return chart_details + "#QWERTY#" + reportTableListDetails;
		}
	
		if (PAGE_BODY.isTaable()) {
			final List<RowGroup> allRows = PAGE_BODY.getVtables().getRowGroups();
			if (allRows != null) {
				for (RowGroup row : allRows) {
					if (row.getType().equalsIgnoreCase("body")) {

						StringBuilder stringBuilder = new StringBuilder();
						String columns_data = "";
						flag = true;
						List<TdCell> tdCells = row.getTr().getTdcell();
						for (int i = 0; i < tdCells.size(); i++) {
							String cleanContent = tdCells.get(i).getContent();
							if(neNameDefinition.containsKey(cleanContent))
								cleanContent = neNameDefinition.get(cleanContent);
							cleanContent = Formula.removeTableName(cleanContent);
							if(cleanContent == null)
								cleanContent = "";
							Pattern p = Pattern.compile("\\[([^\\[]*)\\]");
							Matcher m = p.matcher(cleanContent);
							if (m.find()) {
								cleanContent = cleanContent.substring(0, m.start() + 1) + m.group(1).trim()
										+ cleanContent.substring(m.end() - 1);
							}
							if (cleanContent.startsWith("[") && cleanContent.endsWith("]")) {
								cleanContent = cleanContent.substring(1, cleanContent.length() - 1).trim();
								cleanContent = "[" + cleanContent + "]";
							}
							cleanContent = cleanContent.trim();
							cleanContent = cleanContent.replace("\\[", "[").replace("\\]", "]");
							cleanContent = cleanContent.replaceAll("=", "").replaceFirst("\\[", "'").replaceAll("\\]$",
									"'");
							stringBuilder.append("" + cleanContent);

							if (i + 1 != tdCells.size()) { // last element do not add comma
								stringBuilder.append(",");
							}
						}
						columns_data = stringBuilder.toString().replaceAll("\"", "'");
						columns_data = "\"" + reportTableListDetails + "\"";
						
						
						return columns_data;
					}
				}
			}
		} else if (PAGE_BODY.isChart()) {
			List<Visu> visuals = PAGE_BODY.getSection().getSbody().getVisu();
			StringBuilder stringBuilder = new StringBuilder();
			String columns_data = "";

			if (visuals != null) {
				for (Visu visual : visuals) {

					try {
						final TITLE title = visual.getXyChart().getValueAxis().getTitle();
						final Feeds feeds = visual.getXyChart().getFeeds();

						stringBuilder.append(title + ",");
						String category = feeds.getCategory().getFeedExpression().getContent();

						if (category.startsWith("[") && category.endsWith("]")) {
							category = category.substring(1, category.length() - 1).trim();
							category = "[" + category + "]";
						}
						prev = "";
						
						String value = feeds.getValue().getFeedExpression().getContent();
						// value = Formula.removeTableName(value);
						Pattern p = Pattern.compile("\\[([^\\[]*)\\]");
						Matcher m = p.matcher(value);
						if (m.find()) {
						value = value.substring(0, m.start() + 1) + m.group(1).trim()
						+ value.substring(m.end() - 1);
						}
						value = value.replace("\\[", "[").replace("\\]", "]");
						if (value.startsWith("[") && value.endsWith("]")) {
						value = value.substring(1, value.length() - 1).trim();
						value = "[" + value.trim() + "]";
						}
						stringBuilder.append("\"" + category.replace("\\s+", "_") + "\"" + ",");
						stringBuilder.append("\"" + value.replace("\\s+", "_") + "\"" + ",");
					} catch (Exception e) {

					}
				}
			}
			columns_data = stringBuilder.toString();
			columns_data = columns_data.replaceAll("=", "");
			

			
				columns_data = chart_details;
				
			
			columns_data = columns_data.replaceAll("null", "");	

			if (!columns_data.replaceAll("\"", "").isEmpty())
				return columns_data;
		}
		if (!chart_details.replaceAll("\"", "").isEmpty()) {
			
			return chart_details;
		}
		
		if (!ReportTablesList.isEmpty()) {
			
			return "\"" + reportTableListDetails + "\"";
		}
		return "UNSUPPORTED_PAGE_TYPE";
	}

	private String tableOrChart() {
		String chartType = Program.chartType;
		
		if (chartType.isEmpty())
			return (PAGE_BODY.isTaable() || !ReportTablesList.isEmpty()) ? "Table" : "Chart";
		else if(!ReportTablesList.isEmpty() && !chart_details.isEmpty())
			return chartType + "Chart";
		else
			return (PAGE_BODY.isTaable() || !ReportTablesList.isEmpty()) ? "Table"
					: chartType + "Chart";
	}

	@Override
	public String toString() {
		return "Report{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", PAGE_BODY=" + PAGE_BODY + '}';
	}

	public PageBody getPAGE_BODY() {
		return PAGE_BODY;
	}

	public void setPAGE_BODY(PageBody PAGE_BODY) {
		this.PAGE_BODY = PAGE_BODY;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
