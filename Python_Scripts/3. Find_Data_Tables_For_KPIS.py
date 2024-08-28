from Spotfire.Dxp.Data import *
from Spotfire.Dxp.Data.Import import *
from Spotfire.Dxp.Data.DataOperations import *
from Spotfire.Dxp.Data import DataValueCursor,RowSelection,IndexSet
from System.Collections.Generic import Dictionary,List
from Spotfire.Dxp.Data.Import import TextFileDataSource, TextDataReaderSettings
from Spotfire.Dxp.Data import DataTableSaveSettings
from System.Diagnostics import Process
from System import *
from System.IO import StreamWriter, MemoryStream, SeekOrigin, FileStream, FileMode, File

import re
import sys, os

#C:\\csv\\CPG, Overview (Raw Data)_CPG09-23-2021, 07.29.43_log.log
#QueryBOCPG, Overview (Raw Data)_CPG
Path = Document.Properties["Path"]
filName = Document.Properties["logfile"]
name = filName[9:len(filName)]

log_file = r"C:\\csv\\" + name 

def function_log(txt):
	with open(log_file, 'a+') as f_output:
		f_output.seek(0)
		data = f_output.read(100)
		if len(data)>0:
			f_output.write("\n")
		f_output.write(txt)
function_log("")
function_log("--[INFO]-- Log for '3. Find_Data_Tables_For_KPIS':")
function_log("")
function_log("--[INFO]-- This script is used for finding the data table which is used for calculating the KPI table")

def findDepKpis(ele,val_grp):
	print ("in dep_li")
	#val_grp = []
	for item in dep_dict[ele]:
		if item in dep_dict.keys() and item != ele:
			val_grp = val_grp + findDepKpis(item,val_grp)
			print (val_grp)
			val = var_dict[item]
			val_grp_1 = re.findall("(\[.*?\])[^[_a-zA-Z]",val)
			val_grp_1 = list(set(val_grp_1))
			print "aayu"
			print val
			print dep_dict[item]
			print val_grp_1
			for ea in dep_dict[item]:
			    val_grp_1.remove(ea)
			val_grp = val_grp+ val_grp_1
		else:
		    val = var_dict[item]
		    val_grp_1 = re.findall("(\[.*?\])[^[_a-zA-Z]",val)
		    val_grp_1 = list(set(val_grp_1))
		    val_grp =  val_grp + val_grp_1
		#print val_grp
	print (val_grp)
	return val_grp


countException = 0
dict_of_kpis_tables = {}
keys_list = [] 
custom_keys_li = []

Query_tablename = Document.Properties["csvfilename"]
Variable_tablename = Query_tablename.replace("QueryBO","VariablesBO")
Report_tablename = Query_tablename.replace("QueryBO","ReportsBO")
tablesPre = []
for tabl in Document.Data.Tables:
	tablesPre.append(tabl.Name)
queryTable = Document.Data.Tables[Query_tablename]
cursorName = DataValueCursor.CreateFormatted(queryTable.Columns["TableName"])
cursorKeyName = DataValueCursor.CreateFormatted(queryTable.Columns["KeyNames"])
cursorQuery = DataValueCursor.CreateFormatted(queryTable.Columns["SQL Query"])
List_of_tables = {}
for row in queryTable.GetRows(cursorName,cursorKeyName,cursorQuery):
    valueName = cursorName.CurrentValue.replace(" ","").upper()
    valueQuery = cursorQuery.CurrentValue.upper()
    if valueName != "CUSTOMDATAPROVIDER":
		#if valueName in tablesPre:
		select_grp = re.search("(SELECT(?:(?!(SELECT))).*?)FROM", valueQuery).group(1)
		#print select_grp
		select_grp = select_grp.strip() + ','
		all_alias = re.findall("DC.*?\s+AS\s+(.[^\s]*?)\,",select_grp)
		#print all_alias
		List_of_tables[valueName] = all_alias#[col.Name for col in Document.Data.Tables[valueName].Columns]
		keys = cursorKeyName.CurrentValue.upper()
		keys_list = keys.split(",")
    
        
custom_keys_li = keys_list
print "List_of_tables"
print List_of_tables

di_tables_keys ={}
for row in queryTable.GetRows(cursorName,cursorKeyName):
	valueName = cursorName.CurrentValue.replace(" ","").upper()
	li_added_keys =[]
	#keys = cursorKeys.CurrentValue.upper()
	#keys_list = keys.split(",")
	if valueName in tablesPre:
		tabl = Document.Data.Tables[valueName]
		print valueName
		if valueName != "CUSTOMDATAPROVIDER":
			for ele in List_of_tables[tabl.Name]:
				if ele in custom_keys_li:
					#print "ele"
					#print ele
					cursorCol = DataValueCursor.CreateFormatted(tabl.Columns[ele])
					for r in tabl.GetRows(cursorCol):
						#print cursorCol.CurrentValue
						if ele == "DATE_ID":
							if cursorCol.CurrentValue == "(Empty)":
								break
							else:
								li_added_keys.append(ele)
								break
						if cursorCol.CurrentValue == "@QWERTY":
							break
						else:
							li_added_keys.append(ele)
							break
					t_cols = tuple(List_of_tables[tabl.Name])
					di_tables_keys[tabl.Name] = li_added_keys
				
print "di_tables_keys"
print di_tables_keys
var_dict = {}
var_key_dict = []
varTable = Document.Data.Tables[Variable_tablename]
cursorColName = DataValueCursor.CreateFormatted(varTable.Columns["ColumnName"])
cursorColExpr = DataValueCursor.CreateFormatted(varTable.Columns["Formula"])
cursorColQualification = DataValueCursor.CreateFormatted(varTable.Columns["Qualification"])
for row in varTable.GetRows(cursorColName,cursorColExpr,cursorColQualification):
    valueColName = cursorColName.CurrentValue.replace(" ","").upper()
    valueColExpr = cursorColExpr.CurrentValue.upper()
    valueColQualification = cursorColQualification.CurrentValue.upper()
    #print valueColName
    #print valueColQualification
    if valueColQualification == "DIMENSION":
		var_key_dict.append("[" + valueColName + "]")
    var_dict["[" + valueColName + "]"] = valueColExpr
print "var keys"
print var_dict
print var_key_dict

dep_dict = {}
indep_li = []
list_of_kpis = var_dict.keys() 
for key,value in var_dict.items():
	val_grp = re.findall("(\[.*?\])[^[_a-zA-Z]",value)
	val_grp = set(val_grp)
	kpis = ""
	print key 
	print val_grp
	#print type(kpis)
	count = 0
	if key == "[DATETIME]" or key == "[TIME]":
		dict_of_kpis_tables[key] = "CUSTOMDATAPROVIDER"
	'''if key in var_key_dict:
		dict_of_kpis_tables[key] = "CUSTOMDATAPROVIDER"'''
	for ele in val_grp:
		#print "ele"
		#print ele
		if ele[1:-1] in keys_list:
			count = count + 1
	if len(val_grp) == count and (len(val_grp)> 0 and count>0): #and key in var_key_dict:
		dict_of_kpis_tables[key] = "CUSTOMDATAPROVIDER"
	for ele in val_grp:
		if (ele in list_of_kpis):# and not (key in var_key_dict): #and not (ele in var_key_dict):
			#print ele
			kpis = kpis + ele +"&"
	if kpis <> str.Empty:
		dep_dict[key] = kpis.strip("&").split("&")
print dict_of_kpis_tables
print dep_dict
dep_li = dep_dict.keys()
for li in dep_dict.values():
	dep_li = dep_li + li
dep_li =set(dep_li)
dep_li = list(dep_li)
print "dep_li"
print dep_li
for li in list_of_kpis:
	if not(li in dep_li) and not (li in dict_of_kpis_tables.keys()):
		indep_li.append(li)
print "indep_li"
print indep_li
newTables = []

try:
	for ele in indep_li:
		table_present = ""
		#print ele 
		val = var_dict[ele]
		val_grp = re.findall("(\[.*?\])[^[_a-zA-Z]",val)
		val_grp = list(set(val_grp))
		tables =""
		#print val_grp
		keys_pre = ""
		for li in val_grp:
			for key, value in List_of_tables.items():
				if li in custom_keys_li:
					keys_pre = keys_pre+ "&" + li
				elif li in value:
					tables = tables + "&" + key
		list_of_ta = list(set(tables.strip("&").split("&")))
		print list_of_ta
		keys_pre = keys_pre.strip("&")
		keys_pre = keys_pre.split("&")
		for k in keys_pre:
			count = 0
			for d in list_of_ta:
				if d in di_tables_keys.keys():
					if k in di_tables_keys[d]:
						count = count +1
			if count == 0:
				for d in List_of_tables.keys():
					if d in di_tables_keys.keys():
						if k in di_tables_keys[d]:
							list_of_ta.append(d)
		newTableName = ""
		'''if len(list_of_ta)>1:
			#print len(list_of_ta)
			valueTablename = list_of_ta[0]
			#print valueTablename
			dataTable = Document.Data.Tables[valueTablename]
			dataTableDataSource = DataTableDataSource(dataTable)
			for li in list_of_ta:			  
				newTableName =newTableName + "&" + li
			newTableName = newTableName.strip("&")
			if len(newTables)>0:
				ap_li = newTableName.split("&")
				for tp in newTables:
					tp_li = tp.split("&")
					set_t = set(tp_li+ap_li)
					if len(set_t) == len(tp_li) and len(set_t) == len(ap_li):
						table_present = "Already Present"
						newTableName = tp
			if table_present != "Already Present":
				try:
					newTables.append(newTableName)
					print "newTableName"
					function_log("--[INFO]-- newTableName")
					Document.Data.Tables.Add(newTableName, dataTableDataSource)
					print newTableName
					function_log(str(newTableName))
					print "Datatable created!"
					function_log("--[INFO]-- Datatable created!")
				except Exception as e:
					print e
					function_log("--[WARNING]--Exception thrown: "+str(e))
					#function_log(str(e))
					countException =countException+1
				for d in range(1,len(list_of_ta)):
					addColumnsTo=Document.Data.Tables[newTableName]
					addColumnsFrom=Document.Data.Tables[list_of_ta[d]]
					dataSource=DataTableDataSource(addColumnsFrom)
					map=Dictionary[DataColumnSignature,DataColumnSignature]()
					print keys_list
					function_log(str(keys_list))
					keys_list = list(set(keys_list))
					for key_name in keys_list:
						map.Add(DataColumnSignature(addColumnsTo.Columns[key_name]),DataColumnSignature(addColumnsFrom.Columns[key_name]))
					ignoredColumns=List[DataColumnSignature]()
					TreatEmptyValuesAsEqual = "true"
					settings=AddColumnsSettings(map,JoinType.FullOuterJoin,ignoredColumns,TreatEmptyValuesAsEqual)
					print settings
					#adding Columns
					result=addColumnsTo.AddColumns(dataSource,settings)
		else:
			newTableName = list_of_ta[0]'''
		dict_of_kpis_tables [ele] = newTableName

	#print var_dict
	for k, v in dep_dict.items():
		print "k"
		print k
		table_present = ""
		tables =[]
		keys_pre = ""
		val_grp= []
		for ele in v:
			print ele
			if ele in dep_dict.keys() and ele != k:
				val_grp = val_grp + findDepKpis(ele,val_grp)
				val = var_dict[ele]
				val_grp_1 = re.findall("(\[.*?\])[^[_a-zA-Z]",val)
				val_grp =val_grp+ list(set(val_grp_1))				
			else:
				val = var_dict[ele]
				val_grp_1 = re.findall("(\[.*?\])[^[_a-zA-Z]",val)
				val_grp =val_grp+ list(set(val_grp_1))
		val = var_dict[k]
		val_grp_1 = re.findall("(\[.*?\])[^[_a-zA-Z]",val)
		val_grp = val_grp + list(set(val_grp_1))
		val_grp = list(set(val_grp))
		print ("final")
		print (val_grp)
		
		for li in val_grp:
			for key, value in List_of_tables.items():
				if li in custom_keys_li:
					keys_pre = keys_pre+ "&" + li
				elif li in value:
					tables.append(key)
		list_of_ta = list(set(tables))
		print "list_of_ta"
		print list_of_ta
		keys_pre = keys_pre.strip("&")
		keys_pre = keys_pre.split("&")
		if len(list_of_ta)>0:
			for ke in keys_pre:
				count = 0
				for d in list_of_ta:
					if d in di_tables_keys.keys():
						if ke in di_tables_keys[d]:
							count = count +1
				if count == 0:
					for d in List_of_tables.keys():
						if d in di_tables_keys.keys():
							if ke in di_tables_keys[d]:
								list_of_ta.append(d)
		print list_of_ta
		newTableName = ""
		'''if len(list_of_ta)>1:
			print len(list_of_ta)
			valueTablename = list_of_ta[0]
			print valueTablename
			dataTable = Document.Data.Tables[valueTablename]
			dataTableDataSource = DataTableDataSource(dataTable)
			for li in list_of_ta:			  
				newTableName =newTableName + "&" + li
			newTableName = newTableName.strip("&")
			if len(newTables)>0:
				ap_li = newTableName.split("&")
				for tp in newTables:
					tp_li = tp.split("&")
					set_t = set(tp_li+ap_li)
					if len(set_t) == len(tp_li) and len(set_t) == len(ap_li):
						table_present = "Already Present"
						newTableName = tp
			if table_present != "Already Present":
				try:
					newTables.append(newTableName)
					print newTableName
					Document.Data.Tables.Add(newTableName, dataTableDataSource)
					print newTableName
					print "Datatable created!"
				except Exception as e:
					function_log(str("Exception thrown :"+e))
					countException =countException+ 1
				for d in range(1,len(list_of_ta)):
					addColumnsTo=Document.Data.Tables[newTableName]
					addColumnsFrom=Document.Data.Tables[list_of_ta[d]]
					dataSource=DataTableDataSource(addColumnsFrom)
					map=Dictionary[DataColumnSignature,DataColumnSignature]()
					print keys_list
					keys_list = list(set(keys_list))
					for key_name in keys_list:
						map.Add(DataColumnSignature(addColumnsTo.Columns[key_name]),DataColumnSignature(addColumnsFrom.Columns[key_name]))
					ignoredColumns=List[DataColumnSignature]()
					TreatEmptyValuesAsEqual = "true"
					settings=AddColumnsSettings(map,JoinType.FullOuterJoin,ignoredColumns,TreatEmptyValuesAsEqual)
					print settings
					#adding Columns
					result=addColumnsTo.AddColumns(dataSource,settings)
		elif len(list_of_ta) == 0:
			newTableName =''
		else:
			newTableName = list_of_ta[0]'''
		print newTableName
		for ele in v:
			dict_of_kpis_tables [ele] = newTableName
		dict_of_kpis_tables [k] = newTableName

	print "dict_of_kpis_tables"
	print dict_of_kpis_tables	

	for key,value in dict_of_kpis_tables.items():
		if value == '':
			dict_of_kpis_tables[key] = "CUSTOMDATAPROVIDER"
	kpi_count= 0
	textData = "KPIs,Datatable\r\n"
	for key,value in dict_of_kpis_tables.items():
		kpi_count = kpi_count + 1
		key = '"' + key + '"'
		value = '"' + value + '"'
		textData = textData + key + "," + value + "\r\n"
	print textData
	function_log(str(textData))
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

	# Create new data table with a row for every month

	LoadCSV("kpis_table")
	
except Exception as e:
  countException=countException+1
  function_log("--[WARNING]-- Exception thrown: " +str(e))
  #function_log(str(e))
  subject = getattr(e, 'message', '').replace('\n', '\\n').replace('\r', '\\r')[:989]
  print ("subject"+subject)
if countException == 0:
	Document.Properties["NEDataTableResult"] = "Passsed!"
else:
	Document.Properties["NEDataTableResult"] = getattr(e, 'message', '').replace('\n', '\\n').replace('\r', '\\r')[:989]
	print e
