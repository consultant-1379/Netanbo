package jsonPojo;

public class URLConstants {


    public static String LOGIN = "http://%s:6405/biprws";
    public static String DOCUMENT = "http://%s:6405/biprws/raylight/v1/documents";
    public static String DOCUMENT_DATA_PROVIDER_URL = "http://%s:6405/biprws/raylight/v1/documents/%s/dataproviders";
    public static String DOCUMENT_DATA_PROVIDER_BYID_URL = "http://%s:6405/biprws/raylight/v1/documents/%s/dataproviders/%s";
    public static String RESULT_OBJECT_URL = "http://%s:6405/biprws/raylight/v1/documents/%s/dataproviders/%s/specification";
    
    public static String DOCUMENT_VARIABLES_URL = "http://%s:6405/biprws/raylight/v1/documents/%s/variables";
    public static String DOCUMENT_VARIABLE_BYID_URL = "http://%s:6405/biprws/raylight/v1/documents/%s/variables/%s";
    
    public static String DOCUMENT_LINK_URL = "http://%s:6405/biprws/raylight/v1/documents/%s/links";
    public static String DOCUMENT_LINK_BYID_URL = "http://%s:6405/biprws/raylight/v1/documents/%s/links/%s";

    public static String DOCUMENT_REPORTS_URL = "http://%s:6405/biprws/raylight/v1/documents/%s/reports";
    public static String Prompt_REPORTS_URL = "http://%s:6405/biprws/raylight/v1/documents/%s/reports/%s";
    public static String DOCUMENT_REPORTS_BYID_URL = "http://%s:6405/biprws/raylight/v1/documents/%s/reports/%s/specification";
    
    public static String FOLDERS_URL = "http://%s:6405/biprws/v1/folders";//folders
    public static String CMS_URL = "http://%s:6405/biprws/v1/cmsquery";
    
    public static String DP_FLOW_URL = "http://%s:6405/documents/%s/dataproviders/%s/flows/count";
    public static String DATA_OF_FLOW = "http://%s:6405/documents/%s/dataproviders/%s/flows/0";
    public static String query_url = "http://%s:6405/biprws/raylight/v1/documents/%s/dataproviders/%s/queryplan";
    
    public static String PROMPT_FILTER_URL = "http://%s:6405/biprws/raylight/v1/documents/%s/dataproviders/%s/parameters";
    public static String REPORT_ELEMENTS_URL = "http://%s:6405/biprws/raylight/v1/documents/%s/reports/%s/elements";
    public static String REPORT_ELEMENT_DETAILS_URL = "http://%s:6405/biprws/raylight/v1/documents/%s/reports/%s/elements/%s";
    public static String DOCUMENT_ALERTERS_URL = "http://%s:6405/biprws/raylight/v1/documents/%s/alerters";
    public static String DOCUMENT_ALERTER_DETAILS_URL = "http://%s:6405/biprws/raylight/v1/documents/%s/alerters/%s";
    public static String REFRESH_PARAMETERS_URL = "http://%s:6405/biprws/raylight/v1/documents/%s/parameters?formattedValues=true";
    public static String REPORT_EXPORT_URL = "http://%s:6405/biprws/raylight/v1/documents/%s/reports/%s?columnDelimiter=,";
}
