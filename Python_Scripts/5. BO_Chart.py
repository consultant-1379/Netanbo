from Spotfire.Dxp.Application.Visuals import *
from Spotfire.Dxp.Application.Layout import LayoutDefinition
from Spotfire.Dxp.Data import DataPropertyClass
from Spotfire.Dxp.Data import *
from System.Collections.Generic import List
import re
from Spotfire.Dxp.Application.Visuals import *
from System.Drawing import Size
from System.Drawing import Color
from System.Drawing import *
from Spotfire.Dxp.Application.Visuals.ConditionalColoring import *
from Spotfire.Dxp.Application.Visuals import TablePlot, VisualTypeIdentifiers, LineChart, CrossTablePlot, HtmlTextArea
import time
import datetime

def getGeneralNavigationVis():
    #DocName= 'MGW Report'
    for page in Application.Document.Pages:
        for vis in page.Visuals:
            if vis.TypeId == VisualTypeIdentifiers.HtmlTextArea and vis.Title == 'HeaderTextArea':
                deshtml=resultsPage.Visuals.AddNew[HtmlTextArea]()
                source_html = vis.As[HtmlTextArea]().HtmlContent
                deshtml.HtmlContent=source_html
                temp = "Document Name: "+DocName #+ "                            Last Refreshed: " + str(datetime.datetime.now())
                print temp
                deshtml.Title=temp
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
                dest_html=deshtml.HtmlContent                
                deshtml.HtmlContent = dest_html.Replace('&lt;last refreshed&gt;', str(datetime.datetime.now()).rsplit(':',1)[0])                 
                print deshtml.TypeId
                print deshtml.HtmlContent
                return deshtml.Visual
    return None
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

function_log("--[INFO]-- Log for '6. BO_Chart': ")
function_log("")
function_log("--[INFO]-- The Script is used for displaying charts and table in visualization")
Query_tablename = Document.Properties["csvfilename"]
promptsTable_name = Query_tablename.replace("QueryBO","PromptsBO")
promptsTable = Document.Data.Tables[promptsTable_name]

cursorNode = DataValueCursor.CreateFormatted(promptsTable.Columns["Prompt Filters"])

mergeDocProp = 'mergeDimensionField'
mergeDimension = Document.Properties[mergeDocProp] 

section_temp=""
NodeNameinFilter=""
try:
    for row in promptsTable.GetRows(cursorNode):
        NodeNameFilter = cursorNode.CurrentValue.upper() 
        if re.search("FOR",NodeNameFilter):                  
                print NodeNameFilter.split("FOR")[1].strip(":")
                NodeNameinFilter_temp=NodeNameFilter.split("FOR")[1].strip(":")
                NodeNameinFilter=NodeNameinFilter_temp.replace(" ","").replace("(","").replace(")","")
                print NodeNameinFilter
                break;
        else:
                continue
except:
        NodeNameinFilter=""
#Function to get the keys from GROUP BY conditions 
def get_group_by(str):
    group_by=str.split("GROUP BY")
    #print group_by[1]
    group_by_keys=group_by[1].split(", ")
    #print (group_by_keys)
    keys=[]
    
    for i in range(0,len(group_by_keys)):
        key_pattern=group_by_keys[i].strip(" ")
        #if ")" in key_pattern:
            #key_pattern = key_pattern.replace(")","\)")
        key_pattern=key_pattern.replace("(","\(").replace(")","\)")+"(.*?,)"
        if re.search(key_pattern, str):
            key_temp=re.search(key_pattern, str).group().rstrip(",")
            #print(key_temp)
            #print (key_temp.split(" as ")[1])
            if(key_temp.count("FROM")>0):
                key_temp=key_temp.split("FROM")[0].strip(" ")
                #print(key_temp)
                try:
                     keys.append(key_temp.split(" AS ")[1])
                except Exception as e:
                     print e.message
                     
            else:
                try:
                     if(key_temp.count(" AS ")>0):
                           keys.append(key_temp.split(" AS ")[1])
                     else:
                           keys.append(key_temp.split(".")[1])
                except Exception as e:
                     print e.message
                     
        else:
            print (group_by_keys[i] +" not found")
    
    return keys

# Get table names
custom_keys_li =[]
Query_tablename = Document.Properties["csvfilename"]
Variable_tablename = Query_tablename.replace("QueryBO","VariablesBO")
Report_tablename = Query_tablename.replace("QueryBO","ReportsBO")
queryTable = Document.Data.Tables[Query_tablename]
cursorName = DataValueCursor.CreateFormatted(queryTable.Columns["TableName"])
cursorQuery = DataValueCursor.CreateFormatted(queryTable.Columns["SQL Query"])
cursorKeys = DataValueCursor.CreateFormatted(queryTable.Columns["KeyNames"])
ReportStructureDataTable = Document.Data.Tables[Report_tablename] 
tablesPre = []
for tabl in Document.Data.Tables:
	tablesPre.append(tabl.Name)
for row in queryTable.GetRows(cursorName,cursorKeys):
    valueTablename = cursorName.CurrentValue.replace(" ","").upper()
    if valueTablename == 'CUSTOMDATAPROVIDER':
		custom_keys =  cursorKeys.CurrentValue.upper()
		custom_keys_li = custom_keys.split(",")
		TableName = valueTablename
		break
#TableName = "CUSTOMDATAPROVIDER"
print custom_keys_li

group_by_keys=[]
for row in queryTable.GetRows(cursorQuery):    
    query = cursorQuery.CurrentValue.upper()
    ##print(get_group_by(query))
    if re.search("GROUP BY",query):
        group_by=get_group_by(query)
        for keys in range(0,len(group_by)):
             group_by_keys.append(group_by[keys]) 
#print group_by_keys
unique_keys=set(group_by_keys)
#unique_keys.remove('DATE_ID')
#unique_keys.remove('HOUR')
#unique_keys.remove('MIN')
#print (unique_keys)
unique_key=list(unique_keys)
TableName = 'CUSTOMDATAPROVIDER_NE'
dict_of_alerter = {}
dict_of_vars = {} 
dict_of_ids = {}
dict_of_alerter_def = {}	
data_type_kpi ={} 
dataTable = Document.Data.Tables[TableName]
varTable = Document.Data.Tables[Variable_tablename]
cursorColName = DataValueCursor.CreateFormatted(varTable.Columns["ColumnName"])
cursorColExpr = DataValueCursor.CreateFormatted(varTable.Columns["Formula"])
cursorColQualification = DataValueCursor.CreateFormatted(varTable.Columns["Qualification"])
cursorColAlerters = DataValueCursor.CreateFormatted(varTable.Columns["Alerters"])
cursorColDocname = DataValueCursor.CreateFormatted(varTable.Columns["Document Name"])
cursorColAlerters_def = DataValueCursor.CreateFormatted(varTable.Columns["Alerters Description"])
cursorColId = DataValueCursor.CreateFormatted(varTable.Columns["Id"])
data_type_cursor = DataValueCursor.CreateFormatted(varTable.Columns["Data Type"])

for row in varTable.GetRows(cursorColName, cursorColAlerters, cursorColId, cursorColAlerters_def,data_type_cursor):
	cal_col_name = cursorColName.CurrentValue.upper()
	cal_col_alters = cursorColAlerters.CurrentValue.upper()
	cal_col_id = cursorColId.CurrentValue.upper()
	cal_col_alters_def = cursorColAlerters_def.CurrentValue
	dict_of_ids[cal_col_id] = cal_col_name
	data_t = data_type_cursor.CurrentValue.upper()
	data_type_kpi[cal_col_name] = data_t
	upper_case = re.split("\'[^\']*\'",cal_col_alters_def)
	for ele in upper_case:
		cal_col_alters_def = cal_col_alters_def.replace(ele,ele.upper(),1)	
	if (cal_col_alters <> "(EMPTY)"):
		dict_of_alerter[cal_col_name] = cal_col_alters
	if (cal_col_alters_def == '') or (cal_col_alters_def == "(EMPTY)"):
		print ""
	else:
		dict_of_alerter_def[cal_col_name] = cal_col_alters_def

print "dict_of_alerter"
print dict_of_alerter

operators = ["<=",">=","=","<>","<",">"]
opera =''


dynamicTable =  Document.Data.Tables["DynamicTable"]
#list of all prompts 
prompt_col=[]
for col in dynamicTable.Columns:
    prompt_col.append(col.Name)
  
for key, value in dict_of_alerter_def.items():
	if "&&" in value and re.search("\{(.*?)\}",value):    
		li_amp = re.split("&&",value)  
		for each_e in (li_amp):
				each_e_li = each_e.rsplit(',',2)
				each_e = each_e_li[0]
				if re.search("OR|AND",each_e):
					li_ele = re.split("OR|AND",each_e)
					for li in li_ele:
						li_org= li
						if not (re.search("\{(.*?)\}",li)):
							continue 
						id_k = re.search("\{(.*?)\}",li).group(1)
						for op in operators:
							if op in li:
								opera = op
								break 
						after_op = li.split(opera)
						if id_k in dict_of_ids.keys():
							type_kpi = data_type_kpi[dict_of_ids[id_k]]
							if type_kpi == 'STRING' and not (after_op[1].strip().startswith("'") or after_op[1].strip().startswith('"')):
								li = li.replace(after_op[1].strip(), '"' + after_op[1].strip() + '"').replace("{" + id_k + "}","[" + dict_of_ids[id_k] + "]")
							else:
								li = li.replace("{" + id_k + "}","[" + dict_of_ids[id_k] + "]")
						dict_of_alerter_def[key] = dict_of_alerter_def[key].replace(li_org,li)
				if not (re.search('OR|AND', each_e)):
					li = each_e
					li_org= li
					if not (re.search("\{(.*?)\}",li)):
							continue 
					id_k = re.search("\{(.*?)\}",li).group(1)
					for op in operators:
							if op in li:
								opera = op
								break 
					after_op = li.split(opera)
					if id_k in dict_of_ids.keys():
						type_kpi = data_type_kpi[dict_of_ids[id_k]]
						if type_kpi == 'STRING' and not (after_op[1].strip().startswith("'") or after_op[1].strip().startswith('"')):
							li = li.replace(after_op[1].strip(), '"' + after_op[1].strip() + '"').replace("{" + id_k + "}","[" + dict_of_ids[id_k] + "]")
						else:
								li = li.replace("{" + id_k + "}","[" + dict_of_ids[id_k] + "]")
					dict_of_alerter_def[key] = dict_of_alerter_def[key].replace(li_org,li)
	if not ("&&" in value) and re.search("\{(.*?)\}",value):
			value_li = value.rsplit(',',2)
			value = value_li[0]
			if re.search('OR|AND', value):
					li_ele = re.split('OR|AND',value)
					for li in li_ele:
						li_org= li
						if not (re.search("\{(.*?)\}",li)):
							continue 					
						id_k = re.search("\{(.*?)\}",li).group(1)
						for op in operators:
							if op in li:
								opera = op
								break 
						after_op = li.split(opera)
						if id_k in dict_of_ids.keys():
							type_kpi = data_type_kpi[dict_of_ids[id_k]]
							if type_kpi == 'STRING' and not (after_op[1].strip().startswith("'") or after_op[1].strip().startswith('"')):
								li = li.replace(after_op[1].strip(), '"' + after_op[1].strip()+ '"').replace("{" + id_k + "}","[" + dict_of_ids[id_k] + "]")
							else:
								li = li.replace("{" + id_k + "}","[" + dict_of_ids[id_k] + "]")
						dict_of_alerter_def[key] = dict_of_alerter_def[key].replace(li_org,li)
			if not (re.search('OR|AND', value)):
					li = value
					li_org= li
					if not (re.search("\{(.*?)\}",li)):
							continue 
					id_k = re.search("\{(.*?)\}",li).group(1)
					for op in operators:
							if op in li:
								opera = op
								break 
					after_op = li.split(opera)
					print after_op
					if after_op[1] == "  ": #or after_op[1] == '':
						for i in prompt_col:
							if 'THRESHOLD' in i:
								DocumentPropertyName = i.replace("(","").replace(")","").replace("_","").replace("%","").replace("-","").replace("#","").replace("*","").replace("&","").replace("^","")
								li = li.replace(after_op[1], 'Integer(DocumentProperty("' + DocumentPropertyName +'"))')                             
					if id_k in dict_of_ids.keys():
						type_kpi = data_type_kpi[dict_of_ids[id_k]]
						if type_kpi == 'STRING' and not (after_op[1].strip().startswith("'") or after_op[1].strip().startswith('"')):
							li = li.replace(after_op[1].strip(), '"' + after_op[1].strip() + '"').replace("{" + id_k + "}","[" + dict_of_ids[id_k] + "]")
						else:
							li = li.replace("{" + id_k + "}","[" + dict_of_ids[id_k] + "]")
					dict_of_alerter_def[key] = dict_of_alerter_def[key].replace(li_org,li)

print "dict_of_alerter_def"
print dict_of_alerter_def

Object1=''
Time1=''
all_kpis =[]
for row in varTable.GetRows(cursorColName,cursorColExpr,cursorColQualification, cursorColDocname):
    cal_col_name=cursorColName.CurrentValue.upper()
    cal_val_exp = cursorColExpr.CurrentValue.upper()
    cal_quali = cursorColQualification.CurrentValue.upper()
    document_name = cursorColDocname.CurrentValue.upper()
    #print "Mahesh", cal_col_name
    all_kpis.append(cal_col_name)
    if cal_quali == 'DIMENSION':
        print "inside dimension if"
        dict_of_vars[cal_col_name] = cal_val_exp
        document_name= document_name.split('_')        
        DocName = document_name[0]			
    if 'OBJECT_RESOLUTION' in cal_col_name:
        Object1 = cal_val_exp
        print "Object", Object1

    if 'TIME_RESOLUTION' in cal_col_name:
        if "RAW DATA" in DocName or '(RAW)' in DocName:
            Time1 = Document.Properties['SELECTRAWDATATIMERESOLUTION']
        else:
            print "time resol"
            Time1 = cal_val_exp

#print "all_kpis", all_kpis	
print "dict_of_vars", dict_of_vars
#print "time resol", Time1
#print "Object", Object1
#valueTablename = "CUSTOMDATAPROVIDER"
#Getting column name
moid_level=""
for row in varTable.GetRows(cursorColName,cursorColExpr):
     splitOver = re.split('OVER\(.*?\]\)',cursorColExpr.CurrentValue.upper())
     splitOver = str(splitOver)
     if re.search("MOID",splitOver):
          moid_level="MOID";
          print "Moid found!"
          break
          
dataTable = Document.Data.Tables[valueTablename]
TableName=valueTablename
#print "moid_level "+moid_level
'''if moid_level=="MOID":
         dataTable = Document.Data.Tables[valueTablename]
         TableName=valueTablename
else:
         dataTable = Document.Data.Tables[valueTablename+"_NE"]
         TableName=valueTablename+"_NE"'''

#Frame Over with keys present in NE level datatable
dt = Document.Data.Tables[TableName]
keys_in_table=[]
try:
  for column in dataTable.Columns:
      if column.Name in unique_key:
            if (column.Name=="MOID") and (TableName==(valueTablename+"_NE")):
                   continue
            else:
                   keys_in_table.append(column.Name)
            #print column.Name in unique_keys

  
  print "unique_key  "
  print unique_key
  print "keys_in_table  "
  print keys_in_table

  unique_key=keys_in_table
  print unique_key

except Exception as e:
    print e.message


path = "https://arabbrains.com/wp-content/uploads/2015/03/ericsson1.jpg"

ReportCreationResult = "ReportCreationResult"

#---------------------------------------------------------------------------------------------------------------------------
#BOChart
	

ReportsBOcolNames=[]
cursorcolNames=[]
#table=Document.Data.Tables["ReportsBO"]
for Repcol in ReportStructureDataTable.Columns:
    ReportsBOcolNames.append(Repcol.Name)

tablename = Document.Data.Tables[Report_tablename]
Report_cursor = DataValueCursor.CreateFormatted(tablename.Columns["Report Type"])
columns_cursor = DataValueCursor.CreateFormatted(tablename.Columns["Columns"])
Reportname_cursor = DataValueCursor.CreateFormatted(tablename.Columns["Report Name"])
ReportID_cursor =DataValueCursor.CreateFormatted(tablename.Columns["Report Id"])
overr_keys=[]
counter = 0
id_name=""
dict_of_keys = {}
dict_of_tables = {}
dict_of_keys_1 = {}
dict_of_tables_1 = {}
dict_of_reportid_colums={}
dict_ids={}
#Fetchig details from ReportsBO
for row in tablename.GetRows(Report_cursor,columns_cursor,Reportname_cursor,ReportID_cursor):
    list_of_keys = []
    if (Report_cursor.CurrentValue.upper() == "TABLE"):
        id_name =""
        #print columns_cursor.CurrentValue.upper()
        x = columns_cursor.CurrentValue.upper()
        cols_reg = re.sub('FORMATDATE(.*?)\)',"'DATE'",x)
        cols_reg = re.sub('DATE_ID',"'DATE'",cols_reg)
        #cols_reg=cols_reg.replace("'","")
        cols=cols_reg.split(",'")
        for i in range(0,len(cols)):
            cols[i]=cols[i].strip("'")
        print cols
        if cols == ['<100.00%'] or cols == ['<100%']:
			print "topology"
			continue
        ov_keys = []
        for d in cols:
            if d == "DATE":
                ov_keys.append(d)
            elif d=="TIME":
                ov_keys.append(d)
            elif d in custom_keys_li:
                ov_keys.append(d)
            elif d in dict_of_vars.keys():
                val = dict_of_vars[d]
                if re.search("\[([^\[]*?)\]",val):
                    d_list = re.findall("\[([^\[]*?)\]",val)
                    for ele in d_list:
                        if ele in custom_keys_li:
                            print ele
                            ov_keys.append(ele)
                    
            
        print ov_keys
        ov_keys=set(ov_keys)
        ov_keys = list(ov_keys)
        ov_keys.sort()
        ov_keys = tuple(ov_keys)
        id_name="&"+ ReportID_cursor.CurrentValue.upper()
        if not(ov_keys in dict_of_keys.keys()):
            #counter =counter+1
            #value_string=str(counter)+id_name
            #dict_of_keys[ov_keys]=counter
            dict_of_keys[ov_keys]=id_name
        else:
            dict_of_keys[ov_keys] = dict_of_keys[ov_keys] + id_name
        dict_of_tables[Reportname_cursor.CurrentValue.upper()] = ov_keys#dict_of_keys[ov_keys]
        id_name_1= ReportID_cursor.CurrentValue.upper()

        #If reportsID's are same then not creating extra table. 
        if not (id_name_1 in dict_of_reportid_colums.keys()):
            dict_of_reportid_colums[id_name_1] = cols
        else:
            for i in cols:
                if not (i in dict_of_reportid_colums[id_name_1]):
                    dict_of_reportid_colums[id_name_1].append(i)
        count=0
        dict_ids[id_name_1]=count        
        
        if not(id_name_1 in dict_of_keys_1.keys()):
            #counter =counter+1
            #value_string=str(counter)+id_name
            #dict_of_keys[ov_keys]=counter
            list_of_keys=list_of_keys +list(ov_keys)
            dict_of_keys_1[id_name_1]=list_of_keys
            dict_of_tables_1[Reportname_cursor.CurrentValue.upper()] = id_name_1
        else:
            for it in dict_of_keys_1[id_name_1]:
				list_of_keys.append(it)
            list_of_keys=list_of_keys +list(ov_keys)
            dict_of_keys_1[id_name_1] = list_of_keys
        #dict_of_tables[Reportname_cursor.CurrentValue.upper()] = ov_keys#dict_of_keys[ov_keys]
dict_of_tables2 = dict_of_tables.copy()
for key in dict_of_keys.keys():
    for k,v in dict_of_tables.items():
        if v== key:
            dict_of_tables[k] = dict_of_keys[key]
for key, value in dict_of_keys_1.items():
    dict_of_keys_1[key]=list(set(value))

print "Hurrah!!!!!"
print dict_of_tables
print "dict_of_keys"
#print dict_of_keys
#print dict_of_tables_1
#print "dict_of_keys_1"
#print dict_of_keys_1

for r in range(1,len(ReportsBOcolNames)):
    cursorcolNames.append(DataValueCursor.CreateFormatted(ReportStructureDataTable.Columns[ReportsBOcolNames[r]]))

#cursorcolNames_copy
cursorcolNames_copy=[]
for r in range(0,len(ReportsBOcolNames)):
    cursorcolNames_copy.append(DataValueCursor.CreateFormatted(ReportStructureDataTable.Columns[ReportsBOcolNames[r]]))
    
#cursorcolNames_table
cursorcolNames_table=[]
for r in range(0,len(ReportsBOcolNames)):
    cursorcolNames_table.append(DataValueCursor.CreateFormatted(ReportStructureDataTable.Columns[ReportsBOcolNames[r]]))

	    
def IsNotNull(value):
    return value is not None and len(value) > 0

Queries_data_limit = {}	
Chart_empty_count=0
cols_tb_vis={}

for row in ReportStructureDataTable.GetRows(*cursorcolNames_copy):       
    try:
        
        #for row in ReportStructureDataTable.GetRows(*cursorcolNames_copy):
        Report_ID=cursorcolNames_copy[0].CurrentValue
		#print "Report_ID"                      
        ReportName=cursorcolNames_copy[1].CurrentValue
        #printReportName
        '''if re.search("(?i)Topology",ReportName):
            continue'''
        cursorDataFilters = cursorcolNames_copy[2].CurrentValue
        cursorType=cursorcolNames_copy[3].CurrentValue
        #printcursorType
        cursorSection = cursorcolNames_copy[4].CurrentValue.upper().replace(' ','')
        ReportColumns=cursorcolNames_copy[5].CurrentValue.upper()
        #printReportColumns
        if re.search("(?i)Topology",ReportName) and (ReportColumns == '<100.00%' or ReportColumns == '<100%'):
            continue
        cols_reg = re.sub('FORMATDATE(.*?)\)',"'DATE'",ReportColumns)
        cols_reg = re.sub('DATE_ID',"'DATE'",ReportColumns)
        ##printcols_reg
        
        height_tmp=375
        height_count=1
        height=0
		
        StartDate  = Document.Properties["dt1"]
        print StartDate
        EndDate = Document.Properties["dt2"]
        print EndDate
		
        #Visualization setting for Table
        if cursorType == "Table":
            print "inside table if"
            print ReportName
            if ReportName.upper() in dict_of_tables_1.keys():
                number = dict_of_tables_1[ReportName.upper()]
                print number
                valueTablename = "CUSTOMDATAPROVIDER_NE" #"CUSTOMDATAPROVIDER_" + str(Report_ID)
                print "@@@@@@@@@@" + valueTablename
                dataTable = Document.Data.Tables[valueTablename]
            if dict_ids[dict_of_tables_1[ReportName.upper()]]>0:
                continue
            else:
                dict_ids[dict_of_tables_1[ReportName.upper()]]=1                
            keys_unique=[]
            cols_reg=cols_reg.replace("OSSID","OSS_ID")
            for k in range(0,len(unique_key)):
                ##print cols_reg
                ##print unique_key[k]
                if re.search(unique_key[k],cols_reg):
                    continue
                      
                else:
                    keys_unique.append(unique_key[k])
            #printkeys_unique
            
            cols=cols_reg.split(",'")
            print cols 
            for place,items in enumerate(cols):
                #print items
                if "'.'" in items:
                    a,b = items.split("'.'")
                    cols[place]= b
                    print b
        
            cols=dict_of_reportid_colums[dict_of_tables_1[ReportName.upper()]]        
            print "table"

            #dataTable = Document.Data.Tables["CUSTOMDATAPROVIDER"]
            
            resultsPage = Document.Pages.AddNew(ReportName)
            print "page",resultsPage.Title
            resultsPage.AutoConfigure()
            tablePlot = Application.Document.ActivePageReference.Visuals.AddNew[TablePlot]()
            if Document.Data.Tables.Contains(valueTablename):
				tablePlot.Data.DataTableReference = Document.Data.Tables[valueTablename]
            #tablePlot.AutoConfigure()
            #tablePlot.AutoAddNewColumns = True
            tablePlot.Legend.Visible = False
            tablePlot.Title = ReportName
            navigationVis = getGeneralNavigationVis()
            layout = LayoutDefinition()
            layout.BeginStackedSection()		
            layout.BeginSideBySideSection(90)
            layout.BeginStackedSection(80)
            layout.Add(navigationVis, 10)
            layout.Add(tablePlot.Visual, 100)
            layout.EndSection()
            layout.EndSection()
            layout.EndSection()
            print "Section added"
            resultsPage.ApplyLayout(layout)
            print "layout done"
            Document.ActivePageReference = resultsPage
            Document.ActivePageReference.DetailsOnDemandPanel.Visible = False
                
            #list of Column to add
            listofColumns=List[DataColumn]()
            listofColumns_unique =List[DataColumn]()
            
            for cols_in in range(0,len(keys_unique)):
                   if keys_unique[cols_in] in cols:
                          cols.remove(keys_unique[cols_in])

            #cols=keys_unique+cols                         
            list_cols_charts=[]
            try:
                for i in range(0,len(cols)):
                    cols[i]=cols[i].strip("'")
                    #print cols[i]
                print "cols after merge!"

                
            
                for i in range(0,len(cols)):
                    #print cols[i]
                    if not (cols[i] in custom_keys_li) and not(cols[i] in all_kpis):
                        if '(' in cols[i] or ')' in cols[i]:
                            cols[i] = cols[i].replace('(','_').replace(')','_')
                            if cols[i].endswith('_'):
                                cols[i] = cols[i][0:-1]
                    #print 'cols', cols
                    for column in dataTable.Columns:
                        s1=column.Name      #excluding column "columnName" from the list
                        s2=cols[i].strip().strip("'")
                        #s1="hello"
                        #s2="hello"
                   
                        if s1 == s2:
                            #print "hello"
                            list_cols_charts.append(s2)
                            print "column matched and added"
                            listofColumns.Add(column)
                            continue
                    #print cols
                    #print listofColumns
                print list_cols_charts
                cols_tb_vis[ReportName]=list_cols_charts
                print cols_tb_vis
                tablePlot.TableColumns.AddRange(listofColumns)
                #myKey=[key for key,value in dict_of_keys.items() if value==number]
                myKey_li = dict_of_keys_1[number]
                print "len(myKey_li)"
                print len(myKey_li)
                #print myKey
                data_limit = ""
                rank_in = ""
                myKey = []
                ovr_data = ""			
                myKey=myKey_li
                if mergeDimension != "" and cursorSection != "(EMPTY)":
                    myKey_li.append('TABLENAME')
                print myKey
                for li in myKey:
                    print li
                    li_l = []
                    if li.find("#")>0:
						li_l = li.split("#")
                    if len(li_l) == 0 :
						rank_in = rank_in +  "[" + li + "],"

                    else :
						rank_in = rank_in +  "[" + li_l[0] + "]," + "[" + li_l[1] + "],"
							
                rank_in = rank_in.strip(",") #+ "[DATE]"

                #Setting up datalimit expression for Table view				
                data_limit = data_limit + " (Rank(Baserowid(),'asc', " + rank_in + ")=1)"
                if mergeDimension != "" and cursorSection != "(EMPTY)":
                    data_limit = data_limit +" And TABLENAME = '" + cursorSection + "'"
                if cursorDataFilters != "(Empty)": #or cursorDataFilters != "":
                    print "inside data filters"				
                    if re.search("\[[^\[]*?\]",cursorDataFilters):
                        cursorDataFilters_li = re.findall("\[[^\[]*?\]",cursorDataFilters)
                        cursorDataFilters_li = list(set(cursorDataFilters_li))
                        for ele in cursorDataFilters_li:
                            ele_org = ele
                            ele = ele.upper()
                            cursorDataFilters = cursorDataFilters.replace(ele_org,ele)
                    print "cursorDataFilters"
                    print cursorDataFilters
                    data_limit = data_limit +" And " + cursorDataFilters
                print "data_limit"
                print data_limit
                Queries_data_limit[ReportName]=data_limit
                tablePlot.Data.WhereClauseExpression = data_limit
                ''''tableP = tablePlot
                columns = tablePlot.Columns
                col = tableP.Colorings.AddNew("New grouping")
                col.DefaultColor = Color.White
                print "Columns"
                print type(columns)
                for ele in columns:
                    print ele.DataColumnName
                    if ele.DataColumnName in dict_of_alerter.keys():              
                        categoryKey = CategoryKey(ele.DataColumnName)
                        col.AddStringColorRule(StringComparisonOperator.Equal, ConditionValue.CreateLiteral("(Empty)"),Color.Green)
                        tableP.Colorings.AddMapping(categoryKey, col) '''
                tableP = tablePlot
                columns = tablePlot.Columns
                #col = tableP.Colorings.AddNew("New grouping")
                #col.DefaultColor = Color.White
                #col.EmptyColor = Color.Orange
                #col.AddExpressionRule("If([NE_VERSION]>'11B', true , false)",Color.Red)
                Expression_rule=""
                ''''for ele in columns:
                    if ele.DataColumnName in dict_of_alerter_def.keys():              
                        print ele.DataColumnName
                        col = tableP.Colorings.AddNew(str(ele.DataColumnName)+" grouping")
                        col.DefaultColor = Color.White
                        Expression_rule=dict_of_alerter_def[ele.DataColumnName].split(',')[0]
                        col.AddExpressionRule(Expression_rule,Color.Red)
                        categoryKey = CategoryKey(ele.DataColumnName)
                        tableP.Colorings.AddMapping(categoryKey, col) '''
                try:
					for ele in columns:
						if ele.DataColumnName in dict_of_alerter_def.keys():              
							print ele.DataColumnName
							all_alerters = dict_of_alerter_def[ele.DataColumnName].split('&&')
							for item in all_alerters:
								if item.split(',')[-1].split('=')[-1] !="": 
									if item.split(',')[-1].split('=')[-1] !="#000000":
										print "txt color change"
										col = tableP.Colorings.AddNew(str(ele.DataColumnName)+str(item)+" grouping")
										col.DefaultColor = Color.Black
										col.SetColorOnText=True
										Expression_rule=item.split(',')[0]
										col.AddExpressionRule(Expression_rule,Color.Red)
										categoryKey = CategoryKey(ele.DataColumnName)
										tableP.Colorings.AddMapping(categoryKey, col)
								if item.split(',')[-2].split('=')[-1] !="":
									if item.split(',')[-2].split('=')[-1] !="#000000":
										print "background color change"
										col = tableP.Colorings.AddNew(str(ele.DataColumnName)+str(item)+" grouping")
										col.DefaultColor = Color.White
										Expression_rule=item.split(',')[0]
										col.AddExpressionRule(Expression_rule,Color.Orange)
										categoryKey = CategoryKey(ele.DataColumnName)
										tableP.Colorings.AddMapping(categoryKey, col)
                except Exception as e:
					print e.message

                print "color coding!!!!!!!!"	

            except Exception as e:
                print e.message
                #function_log(str(e.message))
            
            print "table add column"
            #function_log(str("table add column"))

    except Exception as e:
        print e.message
        print "All tables not created!"
        function_log("--[WARNING]-- All tables not created!" +str(e.message))
        #function_log(str(e.message))

print "Queries_data_limit"
function_log(str("--[INFO]-- Queries_data_limit expression: "))
function_log(str(Queries_data_limit))
cursorType=""   
ReportName=""   
Chart_empty_count=0
chart_data_limit =""
Report_id_in_use=""

for row in ReportStructureDataTable.GetRows(*cursorcolNames):       
    try:
                     
        ReportName_chart=cursorcolNames[0].CurrentValue
        #printReportName
        '''if re.search("(?i)Topology",ReportName):
            continue'''
        cursorType=cursorcolNames[2].CurrentValue
        #printcursorType
        
        
        height_tmp=575
        height_count=1
        height=0

         
        #Report is of chart type
        #if cursorType == "Chart":
        if re.search("Chart", cursorType):
            print "ReportName" +ReportName_chart
            print cursorType
            print "Chart"
            Sections=cursorcolNames[3].CurrentValue 
            print "Sections" +Sections       
            rcn=4
            
            if (cursorcolNames[4].CurrentValue == "(Empty)"):
                    Chart_empty_count+=1
                    continue
            page = Document.Pages.AddNew(ReportName_chart)
            layout = LayoutDefinition()
            layout.BeginStackedSection()
            
            
            print Document.ActivePageReference.GetVisualizationAreaSize()
            #unique_key.append("GGSN_NAME")

            node_name_colorby=""
            for n_key in range(0,len(unique_key)):
                if re.search(NodeNameinFilter, unique_key[n_key].replace("_","").replace("(","").replace(")","").replace(" ","")):
                      print "node name"
                      print re.search(NodeNameinFilter, unique_key[n_key].replace("_","").replace("(","").replace(")","").replace(" ","")).group()
                      node_name_colorby=unique_key[n_key]
                      print node_name_colorby
                      
                      break

            while rcn < len(cursorcolNames):
                print"entered in while"
                #Check if Table and charts keys are same			
                cursorCols=cursorcolNames[rcn]
                print "4 "+cursorCols.CurrentValue
                cursorCategory=cursorcolNames[rcn+1]
                print "5 "+cursorCategory.CurrentValue
                if cursorCategory.CurrentValue.upper()=="(EMPTY)":
                          break
                else:
                          x_val= cursorCategory.CurrentValue
                print "x-val!!!!!!!!!!!!!!!!!!!"+x_val
                cursorValue=cursorcolNames[rcn+2]
                print "6 "+cursorValue.CurrentValue
                kpi_chart =0 # assign 1 if over_keys in tables and charts are not the same
                find_col=[] 
                find_col_temp=cursorValue.CurrentValue
                if find_col_temp.upper()=="(EMPTY)":
                       break
                if find_col_temp.find("@")>0:
                       find_col_list=find_col_temp.split("@")
                       for find_c_l in range(0,len(find_col_list)):
                                 find_col.append(find_col_list[find_c_l][1:len(find_col_list[find_c_l])-1].upper())                      

                else:
                       find_col.append(find_col_temp[1:len(find_col_temp)-1].upper())
                print "find_col"
                print find_col
                cursorRegCol=cursorcolNames[rcn+3]
                print cursorRegCol.CurrentValue
                rcn=rcn+4

                try:
                    Report_Name=""
                    if (find_col_temp.upper() != "EMPTY"):
                        print "reportsBO ID!"
                        #Geeting charts related details from ReportsBO
                        for row in ReportStructureDataTable.GetRows(*cursorcolNames_copy):
                                  Report_ID=cursorcolNames_copy[0].CurrentValue
                                  ReportName_temp=cursorcolNames_copy[1].CurrentValue 
                                  cursorType_temp=cursorcolNames_copy[3].CurrentValue

                                  ReportColumns_temp=cursorcolNames_copy[5].CurrentValue.upper()
                                  print ReportColumns_temp
                                  
                                  if cursorType_temp.upper()=='TABLE':
                                          for find_col_i in range(0,len(find_col)):
                                                  if ReportColumns_temp.find("'"+find_col[find_col_i]+"'")>0:
                                                          print "find table in which col found!" 
                                                          print  ReportColumns_temp   
                                                          print find_col[find_col_i]                       
                                                          Report_Name=ReportName_temp
                                                          Report_id_in_use=Report_ID
                                                          chart_data_limit=Queries_data_limit[Report_Name]
                                                          print 
                                                          break
                                                          
                        Kpis_dict={} 
                        Kpis_table=Document.Data.Tables["kpis_table"]
                        Kpis_cursor=DataValueCursor.CreateFormatted(Kpis_table.Columns["KPIs"])
                        Datatable_cursor=DataValueCursor.CreateFormatted(Kpis_table.Columns["Datatable"]) 
                        Kpis_key="" 
                        Kpis_value=""
                        for row in Kpis_table.GetRows(Kpis_cursor,Datatable_cursor): 
                                  Kpis_key=Kpis_cursor.CurrentValue 
                                  Kpis_value=Datatable_cursor.CurrentValue 
                                  Kpis_dict[Kpis_key]=Kpis_value                        
                                              
                        print "table in which col found"
                        print Report_Name
                        print len(Report_Name)
                        print dict_of_tables
                        #Report_id_in_use="1"								  
						
                        for dtable in Document.Data.Tables:
                            print dtable.Name
                            d_table=dtable.Name

                            if re.search("CUSTOMDATAPROVIDER\_", d_table):
                                                     
                                d_table_temp = d_table.split('_')[-1]
                                #print d_table_temp
                                print Report_id_in_use
                                #d_table_temp=set(d_table_temp)
                                if str(Report_id_in_use) == d_table_temp: #d_table_temp:
                                    valueTablename= d_table
                                    print "CUSTOMDATAPROVIDER_NE found" +valueTablename 
                                    
                                    #chart_data_limit=Queries_data_limit[Report_Name]
                                    break
                        if len(Report_id_in_use)==0: 
                                  #Traverse KPIsTable to get table 
                                  Report_id_in_use="123456789"
                                  valueTablename=Kpis_dict[Kpi_name]                                  
                        valueTablename= "CUSTOMDATAPROVIDER_NE"
                        dataTable = Document.Data.Tables[valueTablename]
                        
                    else:
                         break    
                    #print newTableName

                except Exception as e:
                    print e.message
                    function_log("--[WARNING]-- "+str(e.message))
                
                print "Report_Name "+Report_Name  
                print "Report_id_in_use "+Report_id_in_use                
                print "@@@@@Charts@@@@@" + valueTablename
                print "datatable found!" + valueTablename
                
                # Seting up chart layouts based on NetAn compatability                 
                if (cursorCols.CurrentValue != "(Empty)"):

                    if cursorType=="TreeMap":
                               chart = Application.Document.ActivePageReference.Visuals.AddNew[TreeMap]() 
                               chart.Data.DataTableReference = dataTable
                               chart.Title = cursorCols.CurrentValue.upper()
                    
                    elif cursorType=="Pie" or cursorType=="PieWithDepth" or cursorType=="Doughnut":
                               chart = Application.Document.ActivePageReference.Visuals.AddNew[PieChart]()
                               chart.Data.DataTableReference = dataTable
                               chart.Title = cursorCols.CurrentValue.upper()
                               chart.VisualAttributes.SortSectorsBySize = True
                               chart.SectorSizeAxis.Expression = "Sum("+cursorCategory.CurrentValue.upper()+")"  
                    else:
                        
                        if cursorType=="HeatMap":                            
                            chart = Application.Document.ActivePageReference.Visuals.AddNew[HeatMap]() 
                            #chart.MeasureAxis.Expression = "Sum([Column])"
                    
                        if cursorType=="lineChart":
                            chart = Application.Document.ActivePageReference.Visuals.AddNew[LineChart]()                   
                        if cursorType=="verticalBarChart":
                            chart = Application.Document.ActivePageReference.Visuals.AddNew[BarChart]() 
                            chart.Orientation = BarChartOrientation.Vertical
                            chart.StackMode = StackMode.None                  
                    
                        if cursorType=="Waterfall":
                            chart = Application.Document.ActivePageReference.Visuals.AddNew[WaterfallChart]() 
                        if cursorType=="Scatter":
                            chart = Application.Document.ActivePageReference.Visuals.AddNew[ScatterPlot]() 
                        if cursorType=="Bubble":
                            chart = Application.Document.ActivePageReference.Visuals.AddNew[ScatterPlot]() 
                        if cursorType=="PolarScatter":
                            chart = Application.Document.ActivePageReference.Visuals.AddNew[ScatterPlot]() 
                        if cursorType=="PolarBubble":
                            chart = Application.Document.ActivePageReference.Visuals.AddNew[ScatterPlot]() 
                        if cursorType=="Box Plot":
                            chart = Application.Document.ActivePageReference.Visuals.AddNew[BoxPlot]()
                        #print chart
                        #chart = Application.Document.ActivePageReference.Visuals.AddNew[LineChart]()
                        #print  cursorCols.CurrentValue.upper()
                        #print  cursorCategory.CurrentValue.upper()
                        #print  cursorValue.CurrentValue.upper()
                        chart.Data.DataTableReference = dataTable
                        chart.Title = cursorCols.CurrentValue.upper()
                        chart_over_keys=[]                        
                        
                        # x-axis allotment, x_val
                        x_axis_cols = ""
                        x_axis_cols_new=""
                        if x_val.find('@') >= 0:
                               print "1"
                               xmultiVal=cursorCategory.CurrentValue.split('@')
                               print xmultiVal
                               for value_count in range(0,len(xmultiVal)):
                                   x_axis_cols+= xmultiVal[value_count].upper()+"NEST" 
                                   chart_over_keys.append(xmultiVal[value_count].upper())
                               print "2"
                               x_axis_cols=x_axis_cols.strip("NEST")
                               if x_axis_cols.find(".")>0:
                                          x_axis_cols_temp=x_axis_cols.split(".")
                                          x_axis_cols_new=x_axis_cols_temp[1]
                                          chart.XAxis.Expression = x_axis_cols_new
                               else:
                                          chart.XAxis.Expression = x_axis_cols
                        else:
                               print "3"
                               x_axis_cols=cursorCategory.CurrentValue.upper()                               
                               if x_axis_cols.find(".")>0:
                                          x_axis_cols_temp=x_axis_cols.split(".")
                                          x_axis_cols_new=x_axis_cols_temp[1]
                                          chart.XAxis.Expression = "<"+x_axis_cols_new+">" 
                               else:
                                          chart.XAxis.Expression = "<"+x_axis_cols+">" 
                               chart_over_keys.append(x_val.upper())                          
                        
                        #chart.XAxis.Expression = "<"+cursorCategory.CurrentValue.upper()+">"
                        y_axis_cols=""
                        #print "test"
                        #section_temp=""
                        x_val_list = []
                        table_over_keys=[]
                        #x_val_list.append(x_val)
                        #print x_val_list
                        #chart_over_keys.append(x_val.upper())
                        print "chart_over_keys"                 
                        print chart_over_keys
                        print Sections
                        
                        if '@' in Sections:
                               section_list=Sections.split('@')
                               print section_list
                               try:
                                      for i in range(0,len(section_list)):
                                            chart_over_keys.append("["+section_list[i].upper()+ "]")
                               except Exception as e:
                                      print e.message
                                      function_log("--[WARNING]-- "+str(e.message))

                               print chart_over_keys
                        else:
                               if Sections.upper()!="(EMPTY)":
                                      section_temp = "["+Sections.upper()+ "]"
                                      chart_over_keys.append(section_temp)

                        chart_over_keys = list (set(chart_over_keys) )
                        #print x_val
                        print chart_over_keys
                        #num= "&"+Report_id_in_use
                        #print Report_id_in_use
                        print dict_of_keys
                        myKey1= ''
                        for key,value in dict_of_keys.items():
                            value_temp= value.split('&')
                            if str(Report_id_in_use) in value_temp:
                                myKey1= key
                                break
                        #print num
                        #myKey1=[key for key,value in dict_of_keys.items() if value==num]
                        print myKey1
                        if myKey1 <> '': 
                            for li in myKey1:
                                print li
                                table_over_keys.append("["+li+"]")
                        #print table_over_keys
                        # removing oss_id from table keys and date & time
                        if "[OSS_ID]" in table_over_keys:
                            table_over_keys.remove("[OSS_ID]")
                        if "[DATETIME]" in chart_over_keys:
                            chart_over_keys.remove("[DATETIME]")
                            chart_over_keys.append("[TIME]")
                            chart_over_keys.append("[DATE]")
                            #table_over_keys.remove("[TIME]")
                            #table_over_keys.remove("[DATE]")
                        print "table_over_keys"
                        print table_over_keys
                        print "chart_over_keys"
                        print chart_over_keys
                        print cursorValue.CurrentValue.upper()
                        if len(table_over_keys) == 0:
                            kpi_chart=0                         
                        elif len(table_over_keys) != len(chart_over_keys):
                            kpi_chart=1
                        else:
                            table_over_keys.sort()
                            chart_over_keys.sort()
                            if (table_over_keys) != (chart_over_keys):
                                kpi_chart=1
                        print kpi_chart
                        if '@' in cursorValue.CurrentValue:
                               multiVal=cursorValue.CurrentValue.split('@')
                               #print multiVal
                               for i in range(0,len(multiVal)):
                                if multiVal[i][1:-1].find("]") >= 0: 
                                    index = multiVal[i][1:-1].find("]")
                                    multiVal[i]= multiVal[i][0:index+1] +"]"+  multiVal[i][index+1:]                               
                               if kpi_chart==1:
                                   for value_count in range(0,len(multiVal)):
                                             y_axis_cols+= multiVal[value_count].upper()[:-1] +"_chart]"+","
                               else:
                                   for value_count in range(0,len(multiVal)):
                                             y_axis_cols+= multiVal[value_count].upper()+","
                               y_axis_cols=y_axis_cols.strip(",")
                               print "y_axis_cols "+y_axis_cols
                               chart.YAxis.Expression = y_axis_cols
                        else:
                               singVal=cursorValue.CurrentValue.upper()
                               if singVal[1:-1].find("]") >= 0:
                                index = singVal[1:-1].find("]")
                                singVal=singVal[0:index+1] +"]"+ singVal[index+1:]
                               if kpi_chart==1:
                                y_val = singVal[:-1] +"_chart]"
                               else:
                                y_val = singVal                              
                               if cursorType=="verticalBarChart":
                                         chart.YAxis.Expression = "Sum("+y_val+")"
                               else:
                                         chart.YAxis.Expression = y_val


                        chart.Legend.Visible = True   
                        #chart.ColorAxis.Expression = "<[Axis.Default.Names]>" 
                        str_reg_color=cursorRegCol.CurrentValue.upper()

                        if len(cursorRegCol.CurrentValue) == 0:
                               #print "blank"
                               str_reg_color="(Empty)"

                        #print cursorRegCol.CurrentValue
                        #print len(str_reg_color)

                        if (str_reg_color.upper() != "(EMPTY)"):
                                #print "reg not empty"
                                if '@' in cursorValue.CurrentValue:
                                    if len(node_name_colorby)>0:
                                        #print "@ add default and nodename"
                                        chart.ColorAxis.Expression = "<[Axis.Default.Names] NEST ["+node_name_colorby+"]>"
                                    else:
                                        chart.ColorAxis.Expression = "<[Axis.Default.Names]>"

                                else:
                                    if len(node_name_colorby)>0:
                                        #print "node ****"
                                        #print str_reg_color
                                        #print node_name_colorby

                                        if str_reg_color == "["+node_name_colorby+"]":
                                               #print "node name same as region color"
                                               chart.ColorAxis.Expression="<"+str_reg_color+">"

                                        else:
                                               chart.ColorAxis.Expression="<["+node_name_colorby+"] NEST "+str_reg_color+">"

                                    else:
                                        chart.ColorAxis.Expression="<"+str_reg_color+">" 
                                                                       
                               
                        else:
                                #print "else"
                                if '@' in cursorValue.CurrentValue:
                                    #print "@ present"                            
                                    
                                    if len(node_name_colorby)>0:

                                        #print "@ add default and nodename"
                                        chart.ColorAxis.Expression = "<[Axis.Default.Names] NEST ["+node_name_colorby+"]>"
                                    else:
                                        chart.ColorAxis.Expression = "<[Axis.Default.Names]>"  
                                else:
                                    #print "else 2"
                                    #print node_name_colorby
                                    if len(node_name_colorby)>0:
                                        #print "node_name_colorby"
                                        chart.ColorAxis.Expression="<"+node_name_colorby+">"
                                    else:
                                        #print "else 3"
                                        chart.ColorAxis.Expression = "<[Axis.Default.Names]>"
                                        #print "else 4"

                        #print "reg"
                        #print cursorRegCol.CurrentValue
                        #chart.ColorAxis.Expression = "<[Axis.Default.Names]>"
                    print "before chart data limiting" 
                    print chart_data_limit
                    function_log(str("--[INFO]-- chart_data_limit expression:")) 
                    function_log(str(chart_data_limit))                    
                    chart.Data.WhereClauseExpression = chart_data_limit 
                    print "after chart data limiting"           
                    ch  = chart.Visual                   
                    layout.Add(ch)
                    height=height_tmp*height_count
                    height_count+=1
                    continue
                    print "chart added!"
                    function_log(str("--[INFO]-- chart added!"))
                      

            
            
            VisSize = Size(1000, height)

            #set fit to window = false and set visualization area size
            Document.Pages.VisualizationAreaSize.FitToWindow = False
            Document.Pages.VisualizationAreaSize.Size = VisSize    
            active_page = Document.ActivePageReference
            
            
            layout.EndSection()
            resultsPage.ApplyLayout(layout)
            #Get current visualization area size
            
            #layout.EndSection()
            #layout.Add(vis)
            #page.ApplyLayout(layout)
            print"Layout added in page!"

        Document.Properties["ChartResult"] = "Passsed"
    except Exception as e:
		Document.Properties["ChartResult"] = "error while creating visualisation"
		print e.message