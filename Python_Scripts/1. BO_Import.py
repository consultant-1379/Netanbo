from Spotfire.Dxp.Data  import *
from Spotfire.Dxp.Data.Import  import *
from Spotfire.Dxp.Data.Import import TextFileDataSource, TextDataReaderSettings
from Spotfire.Dxp.Data import DataTableSaveSettings
from Spotfire.Dxp.Application.Scripting import ScriptDefinition
from System.Collections.Generic import Dictionary
import clr
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
from Spotfire.Dxp.Application.Scripting import ScriptDefinition
from System.Collections.Generic import Dictionary
from Spotfire.Dxp.Data import DataProperty 
from Spotfire.Dxp.Data import DataType 
from Spotfire.Dxp.Data import DataPropertyClass
from System.Diagnostics import Process
import os
import datetime
import subprocess
import time
import datetime
from System import Array, Object
from multiprocessing.pool import ThreadPool
from System.Collections.Generic import Dictionary, List
from System.IO import StreamWriter, MemoryStream, SeekOrigin, FileStream, FileMode, File
from itertools import combinations,tee
import re
import clr
import System
import Spotfire.Dxp.Application
from Spotfire.Dxp.Data import *
from Spotfire.Dxp.Application.Visuals import VisualContent
from System.Collections.Generic import HashSet
from System.IO import FileStream, FileMode, File, MemoryStream, SeekOrigin, StreamWriter
import System.String
from Spotfire.Dxp.Data.Import import TextDataReaderSettings
from Spotfire.Dxp.Data.Import import TextFileDataSource
from Spotfire.Dxp.Data.Import import DataTableDataSource
clr.AddReference('System.Data')
from System.Data.Odbc import OdbcConnection, OdbcDataAdapter
from System.Data import DataSet
import collections
from Spotfire.Dxp.Application.Scripting import ScriptDefinition
from System.Collections.Generic import Dictionary
import clr
import sys
from Spotfire.Dxp.Data import DataColumn 

# Spotifre imports
from Spotfire.Dxp.Data.Import import *
from Spotfire.Dxp.Data import *
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

PassedReport = 0
FailedReport = 0
PartialPassedReport = 0
DescriptionReport = 0
TotalReport = 0
tablecount=0
tablecount1=0
ps = Application.GetService[ProgressService]()
Path = Document.Properties["Path"]
today=datetime.datetime.now()
date_time=today.strftime("%m-%d-%Y, %H.%M.%S")
myPropertyName  = "logfile" 
myAttributes   = DataProperty.DefaultAttributes 
myProperty   = DataProperty.CreateCustomPrototype(myPropertyName, DataType.String, myAttributes)

try:
	Document.Data.Properties.AddProperty(DataPropertyClass.Document, myProperty)
          
except:
    print("Property already exists")
    
log_file=""
dict_sas={}
mergeDocProp = 'mergeDimensionField'
mergeDimension = Document.Properties[mergeDocProp]
kpiSucc =""
#This function is used to writting a logs into txt file
def function_log(txt,log_file):
	with open(log_file, 'a+') as f_output:
		f_output.seek(0)
		data = f_output.read(100)
		if len(data)>0:
			f_output.write("\n")
		f_output.write(txt)
        
def generatingcsv():
	global dict_sas
	with open(filename, 'a') as f_output:
		p = subprocess.Popen(args, stdout=f_output)
	p.communicate()
	f = open(filename,'r')
	lines = f.read()
	Document.Properties["BOGenerateResult"] = "Successfully generated csvs"
	with open(filename, 'r') as file:
		first_line = file.readline()
		print first_line 
		for last_line in file:
			pass
		list=last_line
		print list
		m_list=[]
		s_list=[]
		list1= list.split('&&')
		for i in list1:
			m_list.append(i)
			s=len(i)-22
			name = i[0:s]
			s_list.append(name)
		m_list.pop()
		s_list.pop()
		dict_sas=dict(zip(m_list, s_list))
		print(dict_sas)
        
def fetchcsv():
	try:
		global valueName
		global tablecount
		global tablecount1		
		print valueName
		File_Name = valueName.rsplit("_",1)
		Folder_Name = valueName.split(",",1)
		Folder_Name_strip = Folder_Name[0].strip()
		File_Name_strip = valueName
		File_Name_strip= File_Name_strip.strip()
		print File_Name_strip
		Querybo_Name = "C:\\csv" + "\\" + valueFolder + "\\QueryBO" + File_Name_strip + ".csv"
		Variablebo_Name = "C:\\csv" + "\\" + valueFolder + "\\VariablesBO" + File_Name_strip + ".csv"
		Reportbo_Name = "C:\\csv" +"\\" + valueFolder + "\\ReportsBO" + File_Name_strip + ".csv"
		Promptbo_Name = "C:\\csv" +"\\" + valueFolder + "\\PromptsBO" + File_Name_strip + ".csv"
		print Querybo_Name
		print Variablebo_Name
        
		#Adding csv in netan
		for path_csv in (Querybo_Name,Variablebo_Name,Reportbo_Name,Promptbo_Name):
			path_csv_li = path_csv.rsplit("\\")
			print path_csv_li
			path_csv_1 = path_csv_li[-1].split(".")
			print path_csv_1
			csv_tablename = path_csv_1[0]
			print csv_tablename
			settings=TextDataReaderSettings()
			settings.Separator=","
			settings.AddColumnNameRow(0)
			dataSource=TextFileDataSource(path_csv,settings)
			csv_table = Document.Data.Tables.Add(csv_tablename, dataSource)    
			settings = DataTableSaveSettings (csv_table,False, True);
			Document.Data.SaveSettings.DataTableSettings.Add(settings);

		csv_tablename = re.search("\\\(Query.*)\.",Querybo_Name).group(1)
		print csv_tablename
		Document.Properties[myPropertyName]  = csv_tablename
		queryTable = Document.Data.Tables[csv_tablename]
		prompt_tablename = csv_tablename.replace("QueryBO","PromptsBO")
		promptTable = Document.Data.Tables[prompt_tablename]
		filName = Document.Properties["csvfilename"]
		name = filName[7:len(filName)]
		name1="C://csv//"+name

#start code for dynamic script
		cursorPrompt = DataValueCursor.CreateFormatted(promptTable.Columns["Prompt Filters"])
		cursorPromptProvider = DataValueCursor.CreateFormatted(promptTable.Columns["DataProvider"])
		prompt_columns = [] 
		for row in promptTable.GetRows(cursorPromptProvider,cursorPrompt):    
			valuePrompt = cursorPrompt.CurrentValue.upper()
			valuePromptProvider = cursorPromptProvider.CurrentValue.upper()
			for i in valuePrompt.split(':'):
				prompt_columns.append(i.strip())

		prompt_columns = list (set(prompt_columns))
		print prompt_columns
		prompt_col=[]
		for i in prompt_columns:
			if "DATE" in i:
				continue
			elif i==str.Empty:
				continue
			else:
				prompt_col.append(i)

		print prompt_col
		for i in range(len(prompt_col)):
			prompt_col[i] = prompt_col[i].replace(" ","")
		print "afterrrrr"
		print prompt_col

		textData =""
		for i in prompt_col:
			textData=textData+i+","
		textData =textData.strip(",")
		textData = textData + "\r\n"
		print "textData-----"
		print textData
		def LoaadCSV(dataTableName):
			stream = MemoryStream()
			writer = StreamWriter(stream)
			writer.Write(textData)
			writer.Flush()
			stream.Seek(0, SeekOrigin.Begin)
			settings = TextDataReaderSettings()
			settings.Separator = ","
			settings.AddColumnNameRow(0)
			settings.ClearDataTypes(False)
			for i in range(len(prompt_col)):
				settings.SetDataType(i, DataType.String)
			stream.Seek(0, SeekOrigin.Begin)
			fs = TextFileDataSource(stream, settings)
			if Document.Data.Tables.Contains(dataTableName):
				Document.Data.Tables[dataTableName].ReplaceData(fs)
			else:
				Document.Data.Tables.Add(dataTableName, fs)

		LoaadCSV("DynamicTable")  
#dynamic code end

		tablecount=0
		tablecount1=0
		for key, value in dict_sas.items():
			print("value->"+value)
			if value.strip()==name1:
				print ("value="+key)
				break
		log_file=key
		Document.Properties["logfile"]=log_file
		function_log("--[INFO]--  Log for '1. BO_Import': ",log_file)
		count = 0
		newQuery = 'SELECT * FROM ( '
		dict_of_keys = {}
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
                                  
		newQuery = newQuery[0: len(newQuery)-11] + ')' 
		print "newQuery"
		print newQuery
		valueName = 'CUSTOMDATAPROVIDER'
		valueQuery = newQuery
		tablecount=tablecount+1
		print tablecount
		function_log("",log_file)
		function_log("--[INFO]-- TABLENAME: " + valueName,log_file)
		function_log("--[INFO]-- SQL:",log_file)
		function_log(valueQuery,log_file)       
		start_date = "'2022/4/21'"
		end_date = "'2022/4/21'"
		print start_date
		print end_date
		data_output = Document.Properties["Resolutioninput"]
		data_resolution = "'Raw data'='Raw data'"
		if "{RAWDATA}" in valueQuery:
				valueQuery = valueQuery.replace("{RAWDATA} = {RAWDATA}",data_resolution)
		if "STARTINGDATE" in valueQuery:
				valueQuery = valueQuery.replace("{STARTINGDATE}",start_date).replace("{ENDINGDATE}",end_date)
		if "(<YEARPROMPT>)" in valueQuery:
				valueQuery = valueQuery.replace("(<YEARPROMPT>)", start_split[2])
		if "(<MONTHPROMPT>)" in valueQuery:
				valueQuery = valueQuery.replace("(<MONTHPROMPT>)", start_split[0])
		if "(<HITPROMPT>)" in valueQuery:
				valueQuery = valueQuery.replace("(<HITPROMPT>)",  str(Document.Properties["PromptVariableInt"]) )
		if "(<ENTERTHRESHOLD(%)>)" in valueQuery:
				valueQuery = valueQuery.replace("(<ENTERTHRESHOLD(%)>)",  str(Document.Properties["PromptVariableInt"]) )
		if "(<LABELPROMPT>)" in valueQuery:
				valueQuery = valueQuery.replace("(<LABELPROMPT>)",  "('" + str(Document.Properties["PromptVariableInt"]) + "')")
		if "(<THRESHOLDPROMPT>)" in valueQuery:
				valueQuery = valueQuery.replace("(<THRESHOLDPROMPT>)",  str(Document.Properties["PromptVariableInt"])  )
		if "(<SOURCEPROMPT>) " in valueQuery:
				valueQuery = valueQuery.replace("(<SOURCEPROMPT>)", "('ISBladeHybrid')")
		if "(<DATEPROMPT>) " in valueQuery:
				valueQuery = valueQuery.replace("(<DATEPROMPT>)",start_date )
		if "(<BACKWARDPROMPT>)" in valueQuery:
				valueQuery = valueQuery.replace("(<BACKWARDPROMPT>)",  str(Document.Properties["PromptVariableInt"]) )
		if "(<BUSYHOURPROMPT>)" in valueQuery:
				valueQuery = re.sub("(AND|OR\s+)(?:(?!(AND\s|OR\s)).)*?\(<BUSYHOURPROMPT>\)","",valueQuery)
		if "(<NODENAME>)" in valueQuery:
				
				valueQuery = valueQuery.replace("<NODENAME>", "''")
		if "(<NODENAME>" in valueQuery:
				init_grp= re.findall("(\(<NODENAME>\s+:\s+\[.*?\]\))",valueQuery)
				for li in init_grp:
					final_output="IS NULL"
					valueQuery = valueQuery.replace("IN  "+ li ,final_output)
		for i in prompt_col:
				if i in valueQuery:
					print "i"
					print i
					if i == 'ENTERTHRESHOLD(%)' or i == 'NUMBEROFDAYSBACKWARDS':
						valueQuery = valueQuery.replace("<"+i+">",str(Document.Properties["PromptVariableInt"]) )
					else:
						valueQuery = valueQuery.replace("<"+i+">","('')")
		print valueQuery
		tableName = valueName
		sql = valueQuery
		function_log("--[INFO]-- Modified Query: ",log_file)
		function_log(sql,log_file)
		dataSourceName = Document.Properties['ENIQDB']
		print dataSourceName 
		dataSourceSettings = DatabaseDataSourceSettings("System.Data.Odbc", "DSN=" + dataSourceName, sql)
        
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
				tablecount1=tablecount1+1
				function_log("--[WARNING]-- Exception Thrown: "+str(e.message),log_file)
                
		function_log("",log_file)
		function_log("The number of data table Presents:"+str(tablecount),log_file)
		function_log("The number of data table executed successfully:"+str(tablecount-tablecount1),log_file)
        
	except Exception as e:
		print e.message
		function_log("--[WARNING]-- Exception Thrown: "+str(e.message),log_file)
		error_Variable[csv_tablename] = e.message

	# The below lines are used to execute the following script automatically
	try:
		if (tablecount1 == 0) or (tablecount1 > 0 and (tablecount) != tablecount1):
			scriptDef = clr.Reference[ScriptDefinition]()
			Document.ScriptManager.TryGetScript("2.1 RemoveExtraColumns", scriptDef)
			params = Dictionary[str, object]()
			Document.ScriptManager.ExecuteScript(scriptDef.Value, params)
			scriptDef = clr.Reference[ScriptDefinition]()
			Document.ScriptManager.TryGetScript("3. Find_Data_Tables_For_KPIS", scriptDef)
			params = Dictionary[str, object]()
			Document.ScriptManager.ExecuteScript(scriptDef.Value, params)
			mergetable_output = Document.Properties["NEDataTableResult"]
			if not "Passs" in mergetable_output:
				error_Variable[csv_tablename] = mergetable_output
			else:
				scriptDef = clr.Reference[ScriptDefinition]()
				Document.ScriptManager.TryGetScript("4. BO_CreateCalculatedColumns", scriptDef)
				params = Dictionary[str, object]()
				Document.ScriptManager.ExecuteScript(scriptDef.Value, params)
				NEDataTable_output = Document.Properties["CreateCalculatedColumnsResult"]
				print NEDataTable_output
				kpiSucc = Document.Properties["KPISuccess"]
				print kpiSucc
				if "Fail" in kpiSucc:
					error_Variable[csv_tablename] = "All kpis have been failed."
					print "inside failure"
                if ("Partial" in kpiSucc) or ("pass" in kpiSucc):
                    print "innnn"
                    if ("Partial" in kpiSucc): 
                        print "inside PartialPass"
                        partial_Variable[csv_tablename] = 1
                    elif ("pass" in kpiSucc) and (tablecount1 > 0 and (tablecount) != tablecount1):
                        print "inside passed"
                        partial_Variable[csv_tablename] = 1 
                        print "partial_Variable"
                        print partial_Variable  
                    else: 
                        print "passed!"                    
                    scriptDef = clr.Reference[ScriptDefinition]()
                    Document.ScriptManager.TryGetScript("5. BO_Chart", scriptDef)
                    params = Dictionary[str, object]()
                    Document.ScriptManager.ExecuteScript(scriptDef.Value, params)
                    Brun_output = Document.Properties["ChartResult"]
                    if not "Passs" in Brun_output:
                            error_Variable[csv_tablename] = Brun_output
                    else:
                            scriptDef = clr.Reference[ScriptDefinition]()
                            Document.ScriptManager.TryGetScript("8. Save_in_library", scriptDef)
                            params = Dictionary[str, object]()
                            Document.ScriptManager.ExecuteScript(scriptDef.Value, params)
                            Chart_output = Document.Properties["LibraryResult"]
                            if not "Passs" in Chart_output:
                                error_Variable[csv_tablename] = Chart_output
                            else:
                                print "Success!"
		print "partial_Variable"
		print partial_Variable                               
		if tablecount == tablecount1:
			error_Variable[csv_tablename] = "All queries are failing."
	except Exception as e:
		print e.message
		error_Variable[csv_tablename] = e.message
        
error_Variable = {}
partial_Variable ={}

#Below lines are used to get server and jar details
try:
	for prop in Document.Data.Properties.GetProperties(DataPropertyClass.Document):
    
		if prop.Name == "BOServerDetail":
			BOServerDetail=prop.Value
		if prop.Name == "BOServerUsername":
			BOServerUsername=prop.Value
		if prop.Name == "BOServerPassword":
			BOServerPassword=prop.Value
		if prop.Name == "EniqDbUser":
			EniqDbUser=prop.Value
		if prop.Name == "EniqDbPass":
			EniqDbPass=prop.Value
		if prop.Name == "EniqDbUrl":
			EniqDbUrl=prop.Value
		if prop.Name == "BOfolderpath":
			Folder_structure=prop.Value

	Document.Properties["BOGenerateResult"] = "   "
	dt = str(datetime.datetime.now())
	dt = dt.replace(" ","")
	dt = dt.replace(":",".")
	if not os.path.exists(r'C:\\csv\\logs'):
		os.mkdir(r'C:\\csv\\logs')
	filename="C:\\csv\\BO_Generate_"+dt+".log"
	jarfile = r"C:\\csv\\NetAnBO-21.1.jar"
	print filename
	f2 = open(filename,'w')
	f2.close()
	args = ['javaw', '-jar', jarfile, Path, BOServerDetail, BOServerUsername, BOServerPassword, 'Generate',Folder_structure , EniqDbUrl, EniqDbUser, EniqDbPass]
	tablesNotToBeDeleted = []       
	print args
	ps.ExecuteWithProgress("Generating CSV", "CSV generation started", generatingcsv)
	for s in Document.Data.Tables:
		if s.Name=="NodeList" or s.Name=="NodeType_List" or s.Name=="Reports_Details_Generate" or s.Name== "Reports_Details_List" or s.Name=="DynamicTable":
			print s.Name
			tablesNotToBeDeleted.append(s.Name)
			continue           
        if not (s.Name in tablesNotToBeDeleted):
			print s.Name
			Document.Data.Tables.Remove(s)
            
        if s.Name=="DynamicTable":
			dt = Document.Data.Tables["DynamicTable"]
			columnsToDelete = List[DataColumn]()
			for column in dt.Columns:
				columnsToDelete.Add(column)
			if columnsToDelete.Count != 0:
				dt.Columns.Remove(columnsToDelete) 
	print "done"
    
	myPropertyName  = "csvfilename" 
	myAttributes   = DataProperty.DefaultAttributes 
	myProperty   = DataProperty.CreateCustomPrototype(myPropertyName, DataType.String, myAttributes)
	try:
		Document.Data.Properties.AddProperty(DataPropertyClass.Document, myProperty) 
	except:
		print("Property already exists")

##Add reportdatatable
	file=Path + "\\Reports_Details_Generate.csv"
	settings=TextDataReaderSettings()
	settings.Separator=","
	settings.AddColumnNameRow(0)
	dataSource=TextFileDataSource(file,settings)

	
	if Document.Data.Tables.Contains("Reports_Details_Generate"):
		Reportpath_table = Document.Data.Tables["Reports_Details_Generate"]
		Reportpath_table.ReplaceData(dataSource)
	else:
		Reportpath_table = Document.Data.Tables.Add("Reports_Details_Generate", dataSource)

	settings = DataTableSaveSettings (Reportpath_table,False, True)
	Document.Data.SaveSettings.DataTableSettings.Add(settings)

	#Looping through list
	ReportName = DataValueCursor.CreateFormatted(Reportpath_table.Columns["Wid File"])
	ReportPath = DataValueCursor.CreateFormatted(Reportpath_table.Columns["Path"])
	ReportFolderName = DataValueCursor.CreateFormatted(Reportpath_table.Columns["folderName"])

	for row in Reportpath_table.GetRows(ReportName,ReportPath,ReportFolderName):
		print "----------------------"
		print row
		TotalReport = TotalReport + 1
		for s in Document.Data.Tables:
			if s.Name=="NodeList" or s.Name=="NodeType_List" or s.Name=="Reports_Details_Generate" or s.Name=="Reports_Details_List":
				print s.Name
				print "table found"
			elif s.Name=="DynamicTable":
				dt = Document.Data.Tables["DynamicTable"]
				columnsToDelete = List[DataColumn]()
				for column in dt.Columns:
					columnsToDelete.Add(column)  
				if columnsToDelete.Count != 0:
					dt.Columns.Remove(columnsToDelete)  
			else:
				Document.Data.Tables.Remove(s)
		for Page in Document.Pages:
			if Page.Title == "UI" or Page.Title == "Refresh" or Page.Title == "Header":
				print Page.Title
			else:
				Document.Pages.Remove(Page)
		Document.Properties["TablesMergedResult"] = ""
		Document.Properties["CreateCalculatedColumnsResult"] = ""
		Document.Properties["ChartResult"] = ""
		Document.Properties["LibraryResult"] = ""
		Document.Properties["BrunResult"] = ""
		valueName = ReportName.CurrentValue
		valuePath = ReportPath.CurrentValue
		valueFolder = ReportFolderName.CurrentValue
		Document.Properties["FolderStructure"] = valuePath
		print "#######################"
		print valueName
		if "DESCRIPTION" in valueName.upper():
			print valueName
			DescriptionReport = DescriptionReport + 1
		else:
			ps.ExecuteWithProgress("Creating Analysis", valueName, fetchcsv)
		

except Exception as e:
	print e
	error_Variable["General error"] = e.message
	exc_type, exc_obj, exc_tb = sys.exc_info()
	fname = os.path.split(exc_tb.tb_frame.f_code.co_filename)[1]
	print(exc_type, fname, exc_tb.tb_lineno)
	log_file = r"C:\\csv\\" + valueName +date_time +"_log.log"
	function_log("--[INFO]-- Log for '1. BO_Import': ",log_file)
	function_log("--[WARNING]-- Exception Thrown: "+str(e.message),log_file)
print (error_Variable)
for Page in Document.Pages:
	if Page.Title == "UI" or Page.Title == "Refresh" or Page.Title == "Header":
		print Page.Title
	else:
		Document.Pages.Remove(Page)

PartialPassedReport = len(partial_Variable.keys())
textData = "ReportName,Error\r\n"
for key,value in error_Variable.items():
	FailedReport = FailedReport + 1
	key = '"' + key + '"'
	value = '"' + value + '"'
	textData = textData + key + "," + value + "\r\n"
print textData
for page in Document.Pages:
	if (page.Title == "UI"):
		Document.ActivePageReference=page
        
def LoadCSV(dataTableName):
	stream = MemoryStream()
	writer = StreamWriter(stream)
	writer.Write(textData)
	writer.Flush()
	stream.Seek(0, SeekOrigin.Begin)
	settings = TextDataReaderSettings()
	settings.Separator = ","
	settings.AddColumnNameRow(0)
	settings.ClearDataTypes(False)
	settings.SetDataType(0, DataType.String)
	settings.SetDataType(1, DataType.String)
	stream.Seek(0, SeekOrigin.Begin)
	fs = TextFileDataSource(stream, settings)
	if Document.Data.Tables.Contains(dataTableName):
		Document.Data.Tables[dataTableName].ReplaceData(fs)
	else:
		Document.Data.Tables.Add(dataTableName, fs)

LoadCSV("ReprtError")

asde = TotalReport
ds = DataTableDataSource(Document.Data.Tables["ReprtError"])
TotalReport = TotalReport - DescriptionReport
Document.Properties["TotalReport"] = str(TotalReport)
PassedReport = asde - FailedReport - PartialPassedReport - DescriptionReport
Document.Properties["PassedReport"] = str(PassedReport)
Document.Properties["FailedReport"] = str(FailedReport)
Document.Properties["PartialPassedReport"] = str(PartialPassedReport)
Document.Properties[mergeDocProp] = ""
print PassedReport
print PartialPassedReport
print TotalReport
print FailedReport
print DescriptionReport