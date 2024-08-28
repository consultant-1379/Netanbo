import Spotfire.Dxp.Application
from Spotfire.Dxp.Data import *
from Spotfire.Dxp.Data.Import import DatabaseDataSource, DatabaseDataSourceSettings, DataTableDataSource, TextFileDataSource, TextDataReaderSettings
from Spotfire.Dxp.Framework.ApplicationModel import *
from System import Array, String
from System.IO import StreamWriter, MemoryStream, SeekOrigin, FileStream, FileMode, File
from System.Collections.Generic import List
from Spotfire.Dxp.Data.Import import DatabaseDataSource, DatabaseDataSourceSettings, DataTableDataSource
from Spotfire.Dxp.Framework.ApplicationModel import *
from Spotfire.Dxp.Data import *
import re
import time
import datetime
from datetime import datetime, timedelta
from System import Array, Object
from multiprocessing.pool import ThreadPool
from System.Collections.Generic import Dictionary, List
from System.IO import StreamWriter, MemoryStream, SeekOrigin, FileStream, FileMode, File
from itertools import combinations,tee
import re
import clr
today=datetime.now()
date_time=today.strftime("%m-%d-%Y, %H.%M.%S")
clr.AddReference('System.Data')
from System.Data.Odbc import OdbcConnection, OdbcDataAdapter
from System.Data import DataSet
import collections
from Spotfire.Dxp.Application.Scripting import ScriptDefinition
from System.Collections.Generic import Dictionary
import clr
 

# Spotifre imports
from Spotfire.Dxp.Data.Import import *
from Spotfire.Dxp.Data import *
import sys
import os
from Spotfire.Dxp.Application import PanelTypeIdentifiers
from Spotfire.Dxp.Data.Transformations import *
from System.Drawing import Color
from Spotfire.Dxp.Data.Import import DatabaseDataSource
from Spotfire.Dxp.Data.Import import DatabaseDataSourceSettings
from Spotfire.Dxp.Framework.ApplicationModel import ApplicationThread, ProgressService, ProgressCanceledException
from Spotfire.Dxp.Application import DocumentSaveSettings
from Spotfire.Dxp.Framework.Library import *
from Spotfire.Dxp.Application.Visuals import CategoryKey
from Spotfire.Dxp.Application.Layout import LayoutDefinition
from Spotfire.Dxp.Application.Visuals import TablePlot, VisualTypeIdentifiers, LineChart, CrossTablePlot, HtmlTextArea
from Spotfire.Dxp.Framework.ApplicationModel import NotificationService
from Spotfire.Dxp.Data.DataOperations import *
from Spotfire.Dxp.Data import DataFlowBuilder, DataColumnSignature, DataType, DataSourcePromptMode
from collections import OrderedDict
myPropertyName  = "logfile" 
myAttributes   = DataProperty.DefaultAttributes 
myProperty   = DataProperty.CreateCustomPrototype(myPropertyName, DataType.String, myAttributes)
try:
	Document.Data.Properties.AddProperty(DataPropertyClass.Document, myProperty)
          
except:
    print("Property already exists")
log_file=""
dictionary =OrderedDict()
sorted_dict = OrderedDict() 
NodeList = []


def callOtherScripts():
	scriptDef = clr.Reference[ScriptDefinition]()
	Document.ScriptManager.TryGetScript("2.1 RemoveExtraColumns", scriptDef)
	params = Dictionary[str, object]()
	Document.ScriptManager.ExecuteScript(scriptDef.Value, params)
	scriptDef = clr.Reference[ScriptDefinition]()
	Document.ScriptManager.TryGetScript("3. Find_Data_Tables_For_KPIS", scriptDef)
	params = Dictionary[str, object]()
	Document.ScriptManager.ExecuteScript(scriptDef.Value, params)
	scriptDef = clr.Reference[ScriptDefinition]()
	Document.ScriptManager.TryGetScript("4. BO_CreateCalculatedColumns", scriptDef)
	params = Dictionary[str, object]()
	Document.ScriptManager.ExecuteScript(scriptDef.Value, params)
	scriptDef = clr.Reference[ScriptDefinition]()
	Document.ScriptManager.TryGetScript("5. BO_Chart", scriptDef)
	params = Dictionary[str, object]()
	Document.ScriptManager.ExecuteScript(scriptDef.Value, params)


dictionary =OrderedDict()
sorted_dict = OrderedDict()
mergeDocProp = 'mergeDimensionField'
mergeDimension = Document.Properties[mergeDocProp] 
count = 0
newQuery = 'SELECT * FROM ( '
dict_of_keys = {}
Query_tablename = Document.Properties["csvfilename"]
queryTable = Document.Data.Tables[Query_tablename]
#place generic data cursor on a specific column
cursorName = DataValueCursor.CreateFormatted(queryTable.Columns["TableName"])
cursorQuery = DataValueCursor.CreateFormatted(queryTable.Columns["SQL Query"])
cursorProvider = DataValueCursor.CreateFormatted(queryTable.Columns["DataProvider"])
cursorKeys = DataValueCursor.CreateFormatted(queryTable.Columns["KeyNames"])
for row in queryTable.GetRows(cursorName,cursorQuery,cursorProvider,cursorKeys):
	valueName = cursorName.CurrentValue.replace(" ","").upper()
	valueQuery = cursorQuery.CurrentValue
	upper_case = re.split("\'[^\']*\'", valueQuery)
	for ele in upper_case:
		valueQuery = valueQuery.replace(ele,ele.upper(),1)
	dataProvider = cursorProvider.CurrentValue.upper()
	AllKeys = cursorKeys.CurrentValue.upper().split(',')
	if mergeDimension!= "":
		AllKeys.append('TABLENAME')
	all_alias = []
	if valueName != "CUSTOMDATAPROVIDER":
		count = count +1
		if count ==1:
			select_grp = re.search("(SELECT(?:(?!(SELECT))).*?)FROM\s+", valueQuery).group(1)
			#print select_grp
			select_grp = select_grp.strip() + ','
			all_alias_pre = re.findall("(DC.|DIM\_)*?\s+AS\s+(.[^\s]*?)\,",select_grp)
			for all_a in all_alias_pre:
				all_alias.append(all_a[1])
			print all_alias
			if mergeDimension!= "":
				select_grp_table = select_grp + " '" + valueName + "' AS TABLENAME"
				valueQuery = valueQuery.replace(select_grp.strip(','),select_grp_table)
				all_alias.append('TABLENAME')
			newQuery =newQuery + ' ( ' +  valueQuery + ' ) ' + dataProvider + " FULL JOIN "			
			dict_of_keys[dataProvider]= []
			for item in all_alias:
					if item in AllKeys:
						dict_of_keys[dataProvider].append(item)
		else:
			select_grp = re.search("(SELECT(?:(?!(SELECT))).*?)FROM\s+", valueQuery).group(1)
			#print select_grp
			select_grp = select_grp.strip() + ','
			all_alias_pre = re.findall("(DC.|DIM\_)*?\s+AS\s+(.[^\s]*?)\,",select_grp)
			for all_a in all_alias_pre:
				all_alias.append(all_a[1])
			if mergeDimension!= "":	
				select_grp_table = select_grp + " '" + valueName + "' AS TABLENAME"
				valueQuery = valueQuery.replace(select_grp.strip(','),select_grp_table)			
				all_alias.append('TABLENAME')
			dict_of_keys[dataProvider]= []
			for item in all_alias:
					if item in AllKeys:
						dict_of_keys[dataProvider].append(item)
			joinCondition = ''
			#print 'dict_of_keys'
			#print dict_of_keys
			for ele in dict_of_keys[dataProvider]:
				temp_join ='(' + dataProvider + '.' + ele + ' = ' 
				tottal_ele = []
				for k,v in dict_of_keys.items():
					if k != dataProvider:
						if ele in v:
							tottal_ele.append(k + '.' + ele)			
				if len(tottal_ele) >= 2:
					temp_join = temp_join + 'ISNULL('
					for it in tottal_ele:
						temp_join = temp_join + it + ',' 
					temp_join = temp_join.strip(',') + '))'
					joinCondition = joinCondition + temp_join +  ' AND '
				elif len(tottal_ele) == 1:
					temp_join = temp_join + tottal_ele[0] + ')'
					joinCondition = joinCondition + temp_join +  ' AND '
				else:
					continue
			'''for k,v in dict_of_keys.items():
					if k != dataProvider:
						for ele in v:
							if ele in dict_of_keys[dataProvider]:
								joinCondition = joinCondition + k + '.' + ele + ' = ' + dataProvider + '.' + ele + ' AND '''
			joinCondition = joinCondition[0:len(joinCondition) - 5]# .strip(' AND ')
			newQuery = newQuery + ' ( ' +  valueQuery + ' ) ' + dataProvider + ' ON ' + joinCondition + ' FULL JOIN '
			print newQuery
            
            
newQuery = newQuery[0: len(newQuery)-11] + ')' #.strip(' FULL JOIN ')
print "newQuery"
print newQuery

ps = Application.GetService[ProgressService]()
#get the data table
Query_tablename = Document.Properties["csvfilename"]
queryTable = Document.Data.Tables[Query_tablename]
Variable_tablename = Query_tablename.replace("QueryBO","VariablesBO")
varTable = Document.Data.Tables[Variable_tablename]
Report_tablename = Query_tablename.replace("QueryBO","ReportsBO")
ReportStructureDataTable = Document.Data.Tables[Report_tablename]
CursorReportName = DataValueCursor.CreateFormatted(ReportStructureDataTable.Columns["Report Name"])
#place generic data cursor on a specific column
cursorName = DataValueCursor.CreateFormatted(queryTable.Columns["TableName"])
cursorQuery = DataValueCursor.CreateFormatted(queryTable.Columns["SQL Query"])

valDataName = List [str]();
valDataQuery = List [str]();
print valDataQuery
sql=cursorQuery

 

Path = Document.Properties["Path"]
filName = Document.Properties["csvfilename"]
name = filName[7:len(filName)]

log_file = r"C:\\csv\\" + name +date_time +"_log.log"
Document.Properties["logfile"]=log_file

def function_log(txt):
	with open(log_file, 'a+') as f_output:
		f_output.seek(0)
		data = f_output.read(100)
		if len(data)>0:
			f_output.write("\n")
		f_output.write(txt)

function_log("--[INFO]-- Log for '0.Refresh_bo_test': ") 

tablecount=0
tablecount1=0

dynamicTable =  Document.Data.Tables["DynamicTable"]
#list of all prompts 
prompt_col=[]
for col in dynamicTable.Columns:
    prompt_col.append(col.Name)
print "prompt_col"
print prompt_col 

StartDate  = Document.Properties["dt1"]
print StartDate
EndDate = Document.Properties["dt2"]
print EndDate 
Object1=''
Time1=''
DocName = ''


TableName = "CUSTOMDATAPROVIDER_NE"
valueTablename = "CUSTOMDATAPROVIDER_NE"
Main_dataTable = Document.Data.Tables[valueTablename]
hr_val = ""
min_val = ""
bhr_val = ""
for col in Main_dataTable.Columns:
	if re.search("^HOUR[A-Z_]{0,3}",str(col)):
		hr_val = str(col)
	if re.search("^BUSY[A-Z_]{0,3}HOUR[A-Z_]{0,3}",str(col)):
		bhr_val = str(col)
	if re.search("^MIN[A-Z_]{0,3}",str(col)):
		min_val = str(col)
	#colname.append(col.Name)


def getGeneralNavigationVis():
    #DocName= 'MGW Report'
    for row in ReportStructureDataTable.GetRows(CursorReportName):
		ReportName = CursorReportName.CurrentValue.replace(' ','')
		print ReportName
		for page in Application.Document.Pages:
			print page.Title        
			RName = page.Title.replace(' ','')
			if RName == ReportName:
				print "jinnnnnn"
				for vis in page.Visuals:
					if vis.TypeId == VisualTypeIdentifiers.HtmlTextArea: #and vis.Title == 'HeaderTextArea':
						print "innn"
						#deshtml=resultsPage.Visuals.AddNew[HtmlTextArea]()
						source_html = vis.As[HtmlTextArea]().HtmlContent
						print source_html
						source_html = re.sub('Report Period:.*?<FONT size=2>(\s+|[0-9]{2}\/[0-9]{2}\/[0-9]{4}|)<\/FONT>','Report Period: </FONT></STRONG><FONT size=2>' + StartDate + '</FONT>',source_html)
						print source_html
						source_html = re.sub('To.*?<FONT size=2>(\s+|[0-9]{2}\/[0-9]{2}\/[0-9]{4}|)','To </FONT><FONT size=2>' + EndDate,source_html)
						source_html = re.sub('Last Refreshed:.*?(\s+|[0-9]{4}\-[0-9]{2}\-[0-9]{2})<\/FONT>','Last Refreshed: </STRONG>' + str(today).split(' ')[0] +'</FONT>',source_html)
						#print all_dates
						#source_html = source_html.replace(all_dates[0][0],StartDate,1)
						#source_html = source_html.replace(all_dates[0][1],EndDate,1)
						print source_html 
						vis.As[HtmlTextArea]().HtmlContent = source_html
						'''deshtml=source_html
						print ReportName
						print Object1
						print Time1
						print StartDate
						#deshtml.Title="Document Name: "+DocName
						dest_html=deshtml.HtmlContent
						deshtml.HtmlContent = dest_html.Replace('&lt;Report Name&gt;',ReportName)
						dest_html=deshtml.HtmlContent				
						deshtml.HtmlContent = dest_html.Replace('&lt;Object Res&gt;',Object1)
						dest_html=deshtml.HtmlContent				
						deshtml.HtmlContent = dest_html.Replace('&lt;Time Res&gt;',Time1)
						dest_html=deshtml.HtmlContent				
						deshtml.HtmlContent = dest_html.Replace('&lt;StartDate&gt;',StartDate)
						dest_html=deshtml.HtmlContent				
						deshtml.HtmlContent = dest_html.Replace('&lt;EndDate&gt;',EndDate)			
						#print deshtml.TypeId
						print deshtml.HtmlContent
						#return deshtml'''
    return None 

def fetchDataFromENIQAsync():
    print tableName
    print valueQuery
    global tablecount
    global tablecount1
    tablecount=tablecount+1
    dataSourceSettings = DatabaseDataSourceSettings("System.Data.Odbc", "DSN=" + dataSourceName, valueQuery)
    try:
        dataTableDataSource = DatabaseDataSource(dataSourceSettings)
        if Document.Data.Tables.Contains(tableName):
            dt = Document.Data.Tables[tableName]
            dt.ReplaceData(dataTableDataSource)
        else:
            dt = Document.Data.Tables.Add(tableName, dataTableDataSource)
        settings = DataTableSaveSettings(dt,False, True)
        Document.Data.SaveSettings.DataTableSettings.Add(settings)
    except Exception as e:
        print tableName
        print e.message
        tablecount1=tablecount1+1
        exc_type, exc_obj, exc_tb = sys.exc_info()
        fname = os.path.split(exc_tb.tb_frame.f_code.co_filename)[1]
        print(exc_type, fname, exc_tb.tb_lineno)
        function_log("--[WARNING]-- Exception thrown" +str(e.message))
        

for row in queryTable.GetRows(cursorName,cursorQuery):
	valueName = cursorName.CurrentValue.replace(" ","").upper()
	valueQuery = cursorQuery.CurrentValue.upper()
	upper_case = re.split("\'[^\']*\'", valueQuery)
	for ele in upper_case:
		valueQuery = valueQuery.replace(ele,ele.upper(),1)    
	dictionary[valueName] = valueQuery

#print (dictionary)

keys = dictionary.keys()

#print (keys)


for key in keys:
	value = dictionary[key]
	if "<NODENAME>" in value:
		dpvalue_check = re.findall("\(<NODENAME>\s+:\s+\[(.*?)\]", value)
		dpvalues= set(dpvalue_check)
		count=0
		length = len(dpvalues)
		for k in dpvalues:
			k = k.replace(" ","")
			print(k)
			if(keys.index(k) > keys.index(key)) & (not(k in sorted_dict.keys())):
				print ("inside if")
				sorted_dict[k] = dictionary[k]
				count= count + 1
				#print(count)
		if(count == length):
			sorted_dict[key] = dictionary[key]
		else:
			sorted_dict[key] = dictionary[key]
			for k in dpvalues:
				k = k.replace(" ","")
				if (not(k in sorted_dict.keys())):
					sorted_dict[k]=dictionary[k]
	else:
		sorted_dict[key] = value

# assign MASTER sql to CUSTOMDATAPROVIDER
sorted_dict["CUSTOMDATAPROVIDER"] = newQuery

pattern = "[^>]*\s(.*?_NAME) as"
patterne = "[^>]*\s(.*?) AS DATE"
dataSourceName = ''
Resolutioninput = ""
DocumentPropertyName = ""
start_split = Document.Properties["dt1"].split('/')
print start_split
start_date = "'" + start_split[2] + "/" + start_split[0] + "/" + start_split[1] + "'"
end_split =  Document.Properties["dt2"].split('/')
end_date = "'" + end_split[2] + "/" + end_split[0] + "/" + end_split[1] + "'"
print start_date
print end_date
Node_names = Document.Properties["SelectedNode"]
data_output = Document.Properties["Resolutioninput"]
data_resolution = "'Raw data'='Raw data'" 

for key, value in sorted_dict.items():
    
    valueName = key
    valueQuery = value
    function_log("")
    function_log("--[INFO]-- TableName: " + valueName)
    function_log("--[INFO]-- SQL:")
    function_log(valueQuery)
    print valueQuery
    print valueName
    if valueName <> str.Empty:
        valDataName.Add(valueName)
        valDataQuery.Add(valueQuery)
    if "{RAWDATA}" in valueQuery:
        valueQuery = valueQuery.replace("{RAWDATA} = {RAWDATA}",data_resolution)
        if "STARTINGDATE" in valueQuery:
            valueQuery = valueQuery.replace("{STARTINGDATE}",start_date).replace("{ENDINGDATE}",end_date)
    else:
        valueQuery = valueQuery.replace("{STARTINGDATE}",start_date).replace("{ENDINGDATE}",end_date)
        #print final_sql

    print valueQuery
    date_grp= re.findall("(\'[12]\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01])\')",valueQuery)
    if not (len(date_grp) == 0):
		valueQuery = valueQuery.replace(date_grp[0][0], start_date)
		valueQuery = valueQuery.replace(date_grp[1][0], end_date)

    if len(re.findall("SELECT(.*?)FROM", valueQuery))==1:
		select_grp = re.search("(SELECT.*)FROM", valueQuery).group(1)
		all_alias = re.findall("DC.*?\s+AS\s+(.*?)\,",select_grp)

	#Replace <NODENAME> with table.Column value	
    if "(<NODENAME>" in valueQuery:
		init_grp= re.findall("(\(<NODENAME>\s+:\s+\[.*?\]\))",valueQuery)
	
		for li in init_grp:
			table_grp = li.split(":")
			names = table_grp[1].split(".")
			table=names[0]
			table=table[2:-1]
			table=table.replace(" ","")
			Column = names[-1]
			Column=Column[1:-2]
			Column = Column.replace(" ","_")
			if Column == "DATE":
				Column = Column.replace("DATE","DATE_ID")
			new_table = table
			table=Document.Data.Tables[table]
			try:
				cursor = DataValueCursor.CreateFormatted(table.Columns[Column])
			except Exception as e:
				table_check = re.compile("[@_!#$%^&*()<>?/\|}{~:]")
				
				if table_check in new_table:
					new_table = new_table.replace(table_check,"_")
				Column = Column + "_" + new_table
				cursor = DataValueCursor.CreateFormatted(table.Columns[Column])


			Column_name = table.Columns[Column]
			Column_nam = str(Column_name)
			Coulumn_datatyp = Column_name.Properties.GetProperty("DataType")
			Coulumn_datatype = str(Coulumn_datatyp)

			output_name = List [str]();

 
			for row in table.GetRows(cursor):
				value = cursor.CurrentValue
				#if value <> str.Empty:
				if Coulumn_datatype == "String" or Coulumn_datatype == "DateTime":
					if Column_nam == "DATE_ID":
						value_list = value.split(" ")
						value = value_list[0]
						#print value
						if "/" in value:
							value_li = value.split("/")
							value = value_li[2] + "/" + value_li[0] + "/" + value_li[1]
						elif "-" in value:
							value = value
					value = "'" + value + "'"
				output_name.Add(value)
			output_name = List [str](set(output_name))
			print output_name;
			final_output = ', '.join(output_name)
			if final_output.strip() == "":
				final_output="IS NULL"
				#is_null = re.search("(IN  ("+ li +")",valueQuery).group(1)
				valueQuery = valueQuery.replace("IN  "+ li ,final_output)
			else:
				final_output = "(" + final_output + ")"
				valueQuery = valueQuery.replace(li,final_output)
 
    #replace prompt values with the selectedPrompt values
    for i in prompt_col:
        if re.search("RESOLUTION", i): 
			DocumentPropertyName = i.replace("(","").replace(")","")
        print i
        if i in valueQuery:
            DocumentPropertyName = i.replace("(","").replace(")","").replace("_","").replace("%","").replace("-","").replace("#","").replace("*","").replace("&","").replace("^","")
            if ( Document.Data.Properties.ContainsProperty ( DataPropertyClass.Document, DocumentPropertyName )):
                if Document.Properties[DocumentPropertyName] <> str.Empty:
                    valueQuery = valueQuery.replace("<"+i+">","("+Document.Properties[DocumentPropertyName]+")")
                else:
                    valueQuery = valueQuery.replace("<"+i+">","('')")
            else:
                valueQuery = valueQuery.replace("<"+i+">","('')")		
    function_log("--[INFO]-- Modified Query:")		
    function_log(valueQuery)
    function_log("")
    print valueQuery
    tableName = valueName
    sql = valueQuery

    dataSourceName = Document.Properties['ENIQDB'] #+ 'repdb'
    #ps.ExecuteWithProgress("Fetching counter and table mapping data", 'Testing Connection to ' + dataSourceName, fetchDataFromENIQAsync)
    fetchDataFromENIQAsync()
    dataSourceSettings = DatabaseDataSourceSettings("System.Data.Odbc", "DSN=" + dataSourceName, valueQuery)
        
#print only unique values
valDataName = List [str](set(valDataName))
valDataQuery = List [str](set(valDataQuery))
print "End"


'''for dataTable in Document.Data.Tables:
	columnCollection=dataTable.Columns
	columnsToRemove=List[DataColumn]()
	#Find Calculated Columns
	for col in columnCollection:
		if col.Properties.ColumnType==DataColumnType.Calculated:
			print col.Properties.ColumnType
			columnsToRemove.Add(col)

	#Remove Calculcated Columns from Column Collection
	columnCollection.Remove(columnsToRemove)

#Remove Calculcated Columns from Column Collection
columnCollection.Remove(columnsToRemove)
tables_var = []
Query_tables = []
Query_tablename = Document.Properties["csvfilename"]
queryTable = Document.Data.Tables[Query_tablename]
cursorName = DataValueCursor.CreateFormatted(queryTable.Columns["TableName"])
for row in queryTable.GetRows(cursorName):
	valueName = cursorName.CurrentValue.replace(" ","").upper()
	Query_tables.append(valueName)

#Remove existing tables
for table in Document.Data.Tables:
	tables_var.append( table.Name)
	if not (table.Name in Query_tables) and not(re.search("NodeList|QueryBO|PromptsBO|VariablesBO|ReportsBO|CUSTOMDATAPROVIDER_NE|Reports_Details_List|NodeType_List|Reports_Details_Generate|ReprtError|DynamicTable",table.Name)):
		print table.Name
		Document.Data.Tables.Remove(table.Name)'''
function_log("--[INFO]-- The number of data table Presents:"+str(tablecount))
function_log("--[INFO]-- The number of data table executed successfully:"+str(tablecount-tablecount1))


scriptDef = clr.Reference[ScriptDefinition]()
Document.ScriptManager.TryGetScript("2.1 RemoveExtraColumns", scriptDef)
params = Dictionary[str, object]()
Document.ScriptManager.ExecuteScript(scriptDef.Value, params)

Document.Data.Tables['CUSTOMDATAPROVIDER_NE'].Refresh()
#ps.ExecuteWithProgress("RefreshingReport", 'Report is getting refreshed...', callOtherScripts)
#callOtherScripts()
time_val = ""
cursorColName = DataValueCursor.CreateFormatted(varTable.Columns["ColumnName"])
cursorColExpr = DataValueCursor.CreateFormatted(varTable.Columns["Formula"])
cursorColQualification = DataValueCursor.CreateFormatted(varTable.Columns["Qualification"])
cursorColAlerters = DataValueCursor.CreateFormatted(varTable.Columns["Alerters"])
cursorColDocname = DataValueCursor.CreateFormatted(varTable.Columns["Document Name"])
for row in varTable.GetRows(cursorColName,cursorColExpr, cursorColDocname):
    cal_col_name=cursorColName.CurrentValue.upper()
    cal_val_exp = cursorColExpr.CurrentValue.upper()    
    document_name = cursorColDocname.CurrentValue.upper()     
    document_name= document_name.split('_')        
    DocName = document_name[0]	
    if 'TIME' == cal_col_name:
		time_val = 	cal_val_exp	
    if 'OBJECT_RESOLUTION' in cal_col_name:
        Object1 = cal_val_exp
        print "Object", Object1

    if 'TIME_RESOLUTION' in cal_col_name:
        print "time resol"
        Time1 = cal_val_exp

getGeneralNavigationVis()

if DocumentPropertyName == str.Empty:
    Resolutioninput = "RAW"
elif ( Document.Data.Properties.ContainsProperty ( DataPropertyClass.Document, DocumentPropertyName )):
    Resolutioninput = Document.Properties[DocumentPropertyName]
else:
    Resolutioninput = "RAW"

NEtable = Document.Data.Tables['CUSTOMDATAPROVIDER_NE']
myColumn = NEtable.Columns.Item['TIME']
ColProperties= myColumn.Properties
'''for property in ColProperties.PropertyNames:
	print property
'''
expres = myColumn.Properties.Expression
print expres

if time_val !="":
	val = time_val
	col_grp = re.findall("\[[^\[]*?\]",val)
	if ("[HOUR]" in col_grp ) and "[MIN]" in col_grp and not re.search("HOUR",Resolutioninput):
		valueColExpr = "Concatenate([" + hr_val + "],':',[" + min_val + "])"
	elif "[BUSY_HOUR]" in col_grp and "[MIN]" in col_grp and not re.search("HOUR",Resolutioninput):
		valueColExpr = "Concatenate([" + bhr_val + "],':',[" + min_val + "])"
	elif "[HOUR]" in col_grp:
		valueColExpr = "Concatenate([" + hr_val + "],':00')"
	elif "[BUSY_HOUR]" in col_grp:
		valueColExpr = "Concatenate([" + bhr_val + "],':00')"
	else:
		valueColExpr = "'00:00'"
else:
	if bhr_val <> str.Empty:
		if min_val <> str.Empty and not re.search("HOUR",Resolutioninput):
			valueColExpr = "Concatenate([" + bhr_val + "],':',[" + min_val + "])"
		else:
			valueColExpr = "Concatenate([" + bhr_val + "],':00')"
	elif hr_val <> str.Empty:	
		if min_val <> str.Empty and not re.search("HOUR",Resolutioninput):
			valueColExpr = "Concatenate([" + hr_val + "],':',[" + min_val + "])"
		else:
			valueColExpr = "Concatenate([" + hr_val + "],':00')"
	else:
		valueColExpr = "'00:00'"

'''if "HOUR" in Resolutioninput and '[MIN]' in expres:
	expres = expres.replace('[MIN]','00')'''
myColumn.Properties.SetProperty('Expression',valueColExpr)
