from Spotfire.Dxp.Data import *
from Spotfire.Dxp.Application.Visuals import TablePlot
from Spotfire.Dxp.Data.Import import DatabaseDataSource, DatabaseDataSourceSettings, DataTableDataSource
from System.Collections.Generic import List
import re
import sys, os
from collections import OrderedDict
from Spotfire.Dxp.Data.Formatters import *
from Spotfire.Dxp.Data.Transformations import *
Sections_cursor=""
Sections=""
from Spotfire.Dxp.Application.Visuals import *
from Spotfire.Dxp.Application.Layout import LayoutDefinition
from Spotfire.Dxp.Data import DataPropertyClass
from Spotfire.Dxp.Data import *
from System.Collections.Generic import List
import re
from Spotfire.Dxp.Application.Visuals import *
from System.Drawing import Size
from System.Drawing import Color

Path = Document.Properties["Path"]
filName = Document.Properties["logfile"]
name = filName[9:len(filName)]
print name
dataProvider_and_keys = {}
dict_of_keys_cal = {}
#name = "Bo_create_calculated_column"
log_file = r"C:\\csv\\" + name

try:
  valueTablename = "CUSTOMDATAPROVIDER"
  dataTable = Document.Data.Tables[valueTablename]
  #dataTable = Document.Data.Tables[TableName]
  dataTableDataSource = DataTableDataSource(dataTable)
  newTableName =valueTablename+'_NE'
  Document.Data.Tables.Add(newTableName, dataTableDataSource)
  #print TableName
  print newTableName
  print "NELevel Datatable created!"
except Exception as e:
  print e.message


def function_log(txt):
	with open(log_file, 'a+') as f_output:
		f_output.seek(0)
		data = f_output.read(100)
		if len(data)>0:
			f_output.write("\n")
		f_output.write(txt)

function_log("--[INFO]-- Log for '4. BO_CreateCalculatedColumns': ")
function_log("")
function_log("--[INFO]-- This script is used for Calculating the KPI ")
for dataTable in Document.Data.Tables:
	columnCollection=dataTable.Columns
	columnsToRemove=List[DataColumn]()
	#Find Calculated Columns
	for col in columnCollection:
		if col.Properties.ColumnType==DataColumnType.Calculated:
			print col.Properties.ColumnType
			columnsToRemove.Add(col)

	#Remove Calculcated Columns from Column Collection
	columnCollection.Remove(columnsToRemove)

Keys_and_yaxis={}
ReportsBOcolNames=[]
cursorcolNames=[]

# Get table name
Query_tablename = Document.Properties["csvfilename"]
Variable_tablename = Query_tablename.replace("QueryBO","VariablesBO")
Report_tablename = Query_tablename.replace("QueryBO","ReportsBO")

count=0
colNotAdded=""
		   
def fetch_uniqueCount(valueColExpr):
	ch = ""
	if valueColExpr.find("UNIQUECOUNT") >= 0:
		#print "inside uniqueCount"
		count = 0
		index = valueColExpr.find("UNIQUECOUNT")
		#print index
		#print valueColExpr[index-5:index] 
		for i in range(index+11,len(valueColExpr)):
			if valueColExpr[i] == "(":
				ch = ch + valueColExpr[i] 
				count= count + 1
			elif valueColExpr[i] == ")":
				ch = ch + valueColExpr[i] 
				count= count - 1 
			else:
				ch = ch + valueColExpr[i] 
			if count == 0:
				#print "ch"
				#print ch
				return ch
	else:
		return ch

def add_agg(str_counter):
		if re.search("_AVG",str_counter):
			str_counter_final = "AVG("+str_counter+")"
			#print str_counter_final
		elif re.search("_MAX",str_counter):
			str_counter_final = "MAX("+str_counter+")"
			#print str_counter_final
		elif re.search("_MIN",str_counter):
			str_counter_final = "MIN("+str_counter+")"
			#print str_counter_final
		else:
			str_counter_final = "SUM("+str_counter+")"
			#print str_counter_final

		return str_counter_final

#Pick over function
def pick_over(str_new):
    index_values = []
    if re.search("(?i)OVER", str_new):
        extra_over_final=[]
        count_open=0
        count_close=0
        over_string=str_new
        for match in re.finditer(r'[^a-zA-Z]OVER',str_new):
			index_values.append(match.start())
        for index_over in index_values:
			extra_over=""
			count_open=0
			count_close=0
			if str_new[index_over]==")":
				index_over=index_over+1
			for ch in range(index_over,len(str_new)):
				if(over_string[ch]=='('):
					count_open=count_open+1 
				elif(over_string[ch]==')'):
					count_close=count_close+1 
				if(count_close==count_open and count_open>0):
					extra_over=extra_over+str_new[ch]
					break
				else:
					extra_over=extra_over+str_new[ch]
			count_open=0
			count_close=0
			for ch in range(index_over-1,-1,-1):
				if(over_string[ch]=='('):
					count_open=count_open+1 
				elif(over_string[ch]==')'):
					count_close=count_close+1 
				if(count_open==count_close and count_close>0):
					extra_over=str_new[ch] + extra_over
					break
				else:
					extra_over=str_new[ch] + extra_over

			extra_over_final.append(add_pair_brac(extra_over))
        return extra_over_final

def unique_value(value_list):
	x_old = re.findall("\[(.*?)\]",value_list)
	myset = set(x_old)
	return list(myset) 
    
def replacing_for_sn(split_list_sn):
	replacevalue_dict = OrderedDict()
	for q_sn in split_list_sn:
		old_q = q_sn
		vd = unique_value(q_sn)
		for ed in vd:
			if column_typedict[ed] == "String":
				if "REAL(["+ ed + "])" in q_sn:
					print "inside sn if"
					new_val= ' SN(REAL([' + ed + ']),0)'
					old_val = "REAL([" + ed + "])"
				elif "REAL( UPPER(["+ ed + "]))" in q_sn:
					print "inside sn if"
					new_val= ' SN(REAL(UPPER([' + ed + '])),0)'
					old_val = "REAL( UPPER([" + ed + "]))"
				else:
					new_val= ' SN([' + ed + '],\'Null\')'
					old_val = "[" + ed + "]"
				q_sn = q_sn.replace(old_val,new_val)
			elif column_typedict[ed] == "Currency" or column_typedict[ed] == "Integer":
				new_val= ' SN([' + ed + '],0)'
				old_val = "[" + ed + "]"
				q_sn = q_sn.replace(old_val,new_val)
		replacevalue_dict[old_q] = q_sn
	return replacevalue_dict


def replacement_in_case_when(case_str,tbl):
	then_str = case_str.split("then")
	then_thing = re.findall("\[([^\[]*?)\]",then_str[1])
	string = ''
	if then_thing[0] in data_type_kpi.keys():
		if data_type_kpi[then_thing[0]] == "STRING" or data_type_kpi[then_thing[0]] == "DATETIME":
			string = "(SN(" + case_str + ",NULL))"
		else:
			string = "SUM(SN(" + case_str + ",0))"
	else:
		if Find_data_type(then_thing[0],tbl) == "String" or Find_data_type(then_thing[0],tbl) == "DateTime":
			string = "(SN(" + case_str + ",NULL))"
		else:
			string = "SUM(SN(" + case_str + ",0))"
	return string
            
def replacement_in_case_when_without_sum(case_str,tbl,over_keys):
	then_str = case_str.split("then")
	then_thing = re.findall("\[([^\[]*?)\]",then_str[1])
	string = ''
	if then_thing[0] in data_type_kpi.keys():
		if data_type_kpi[then_thing[0]] == "STRING" or data_type_kpi[then_thing[0]] == "DATETIME":
			string = "(SN(" + case_str + ",NULL))"
		else:
			string = "SN(" + case_str + ",0)"
	else:
		if Find_data_type(then_thing[0],tbl) == "String" or Find_data_type(then_thing[0],tbl) == "DateTime":
			string = "(SN(" + case_str + ",NULL))"
		else:
			if len(then_thing) == 1 and not ('_AVG' in then_thing[0] or '_MAX' in then_thing[0] or '_MIN' in then_thing[0]):
				string = "SUM(SN(" + case_str + ",0))"
			elif len(then_thing) == 1 and ('_AVG' in then_thing[0]):
				if over_keys <> str.Empty:
					Over_to_ag= find_the_keys('[' + then_thing[0] + ']',over_keys) 
					case_str = case_str.replace('[' + then_thing[0] + ']',add_agg(nonDuplicateRows('[' + then_thing[0] + ']'))+Over_to_ag)
				string = "SN(" + case_str + ",0)"
			else:
				string = "SN(" + case_str + ",0)"
	return string



def Find_type(Column_name):
	global Flag_count
	Flag_count = 0
	try:
		Table_nam=Document.Data.Tables[TableName]
		
		Column_nam=Table_nam.Columns[Column_name]
		
		Coulumn_datatyp = Column_nam.Properties.GetProperty("DataType")
		Coulumn_datatype = str(Coulumn_datatyp)
		
		return Coulumn_datatype
	except Exception as e:		
		global Flag_count
		Flag_count = 1
		return ""
		
def Find_data_type(Column_name,Table_Name):
	global Flag_count
	Flag_count = 0
	try:
		Table_nam=Document.Data.Tables[Table_Name]
		
		Column_nam=Table_nam.Columns[Column_name]
		
		Coulumn_datatyp = Column_nam.Properties.GetProperty("DataType")
		Coulumn_datatype = str(Coulumn_datatyp)
		
		return Coulumn_datatype
	except Exception as e:		
		global Flag_count
		Flag_count = 1
		return ""



#Bracket_Pairing
def remove_char(var_str, n):
      first_part = var_str[:n] 
      last_part = var_str[n+1:]
      #print(first_part + last_part)
     
      return first_part + last_part
      
def add_pair_brac(myStr): 
    brac_char=[]
    
    pair_brac=myStr.strip(" ")
    count_open=0
    count_close=0
    for ch in range(0,len(pair_brac)):    
      if(pair_brac[ch]=='('): 
         count_open=count_open+1 
      elif(pair_brac[ch]==')'): 
          count_close=count_close+1 
          if(count_close>count_open):
              brac_char.append(ch)
              
    if(count_open>count_close): 
        for i in range(0,(count_open-count_close)):
           pair_brac=pair_brac+')'
    
    elif(count_open<count_close):
        for bch in range(0,len(brac_char)):
            n=brac_char[bch]-bch
            pair_brac=remove_char(pair_brac,n)
            
    return pair_brac

    
def add_sqr_brac(myStr): 
    pair_brac=myStr.strip(" ")
    count=0 
    for ch in pair_brac:    
      if(ch=='['): 
         count=count+1 
      elif(ch==']'): 
         count=count-1 
    if(count>0): 
        for i in range(0,count):
           pair_brac=pair_brac+']'
    elif(count<0): 
        while(count<0): 
            pair_brac=pair_brac[:-1]
            count=count+1               
    return pair_brac
     
#WhereToCase
def WhereToCase(myStr,tbl,over_keys):
    ind_wh = valueColExpr.find(myStr)
    final_ind = ind_wh + len(myStr)-1
    ope = 0
    clo = 0
    for iki in range(ind_wh,0,-1):
        if re.search('(\(|\s|\[)',valueColExpr[iki]):
            continue  
        elif re.search('(\+|\-|\*|\/)',valueColExpr[iki]):
        #elif re.search('(\+|\-)',valueColExpr[iki]):
            ope = 1
            break
        else:
            break 
    for iki in range(final_ind,len(valueColExpr),1):
        if re.search('(\)|\s|\])',valueColExpr[iki]):
            continue  
        elif re.search('(\+|\-|\*|\/)',valueColExpr[iki]):
        #elif re.search('(\+|\-)',valueColExpr[iki]):
            clo = 1
            break
        else:
            break 
    if valueColExpr[ind_wh-3:ind_wh] == 'SUM':
        clo = 1    
    if re.search("(?i)Where", myStr):
        where_issue=myStr.split("WHERE")
        where_to_case=" (case "+" when "+where_issue[1]+" then "+add_pair_brac(where_issue[0])+" End) "
        if ope == 1 or clo == 1:
            where_to_case = replacement_in_case_when(where_to_case,tbl)
        else:
            where_to_case = replacement_in_case_when_without_sum(where_to_case,tbl,over_keys)
    return where_to_case

#get required where function part    
def find_all_where(txt,n):
    
    var_str=""
    count_open=0
    count_close=0
    
    index=txt.find('WHERE', n)    
    for ch in range(index,len(txt)):
        
        if(txt[ch]=='('): 
            count_open=count_open+1 
        elif(txt[ch]==')'): 
            count_close=count_close+1 
               
        if(count_close==count_open and count_open>0):
            var_str=var_str+txt[ch]
            new_index=ch
            break
        elif((txt[ch]=='+'  or txt[ch]=='/') and count_close>=count_open):
            if(count_open>count_close):
                var_str=var_str+txt[ch]
            else:
                new_index=ch
                break
        elif(ch==len(txt)-1) :
            var_str=var_str+txt[ch]
            
        elif(txt[ch]=='-' and count_close>=count_open):
            if(re.search('[a-zA-Z]',txt[ch+1]) or re.search('[a-zA-Z]',txt[ch-1]) ):
                var_str=var_str+txt[ch]
            else:
                new_index=ch
                break
        else:
            var_str=var_str+txt[ch]
        
    count_open=0
    count_close=0
    
    for ch in range(index-1,-1,-1):
        count_of_square = 0
        if(txt[index-1] == ']'):
			for sch in range(index-1,-1,-1):
				var_str=txt[sch]+var_str
				if(txt[sch] == '['):
					count_of_square = count_of_square+1
					break
			if count_of_square == 1:
				break
        if(txt[ch]=='('): 
            count_open=count_open+1 
        elif(txt[ch]==')'): 
            count_close=count_close+1 
               
        if(count_close==count_open and count_close>0):
            var_str=txt[ch]+var_str
            break
        
        elif(count_close==0 and count_open>0):
            break
        
        elif((txt[ch]=='+'  or txt[ch]=='/') and count_open>=count_close):
            if(count_close>count_open):
                var_str=txt[ch]+var_str
            else:
                break
        elif(ch==0) :
            var_str=txt[ch]+var_str
            
        elif(txt[ch]=='-' and count_open>=count_close):
            if(re.search('[a-zA-Z]',txt[ch+1]) or re.search('[a-zA-Z]',txt[ch-1]) ):
                var_str=txt[ch]+var_str
            else:
                break
        else:
            var_str=txt[ch]+var_str     
    index=txt.find('WHERE', new_index)
    if(index<len(txt)-1 and index>=0):
        var_str=var_str+"$$"+find_all_where(txt,index)
        
    return var_str
    
    
def paired_bracket_division(txt,n):
    
    var_str=""
    count_open=0
    count_close=0
    
    index=txt.find('/', n)    
    if  (index+1 != len(txt)-1):    
		for ch in range(index+1,len(txt)):
			count_of_square = 0
			if(txt[index+1] == '[' or (txt[index+2] == '[' and txt[index+1] == ' ')):
				for sch in range(index+1,len(txt),1):
					var_str=var_str+txt[sch]
					if(txt[sch] == ']'):
						new_index=sch
						count_of_square = count_of_square+1
						break
				if count_of_square == 1:
					break
			if(txt[index+1] == '(' or (txt[index+2] == '(' and txt[index+1] == ' ')):
				if(txt[ch]=='('): 
					count_open=count_open+1 
				elif(txt[ch]==')'): 
					count_close=count_close+1 
               
				if(count_close==count_open and count_open>0):
					var_str=var_str+txt[ch]
					new_index=ch
					break
				else:
					var_str=var_str+txt[ch]
        
    if var_str =="":
		return ""    
    count_open=0
    count_close=0
    var_str = '/'+var_str
    for ch in range(index-1,-1,-1):
        count_of_square = 0
        if(txt[index-1] == ']' or (txt[index-2] == ']' and txt[index-1] == ' ')):
            for sch in range(index-1,-1,-1):
                var_str=txt[sch]+var_str
                if(txt[sch] == '['):
                    count_of_square = count_of_square+1
                    break
            if count_of_square == 1:
                break
        if(txt[ch]=='('): 
            count_open=count_open+1 
        elif(txt[ch]==')'): 
            count_close=count_close+1 
               
        if(count_close==count_open and count_close>0):
            var_str=txt[ch]+var_str
            indec = txt.find(var_str)
            if re.search('[a-zA-Z]',txt[indec-1]):
                for i in range(indec-1,0,-1):
                    var_str = txt[i] + var_str
                    if re.search('[a-zA-Z]',txt[i]):
                        var_str = txt[i] + var_str
                    else:
                        break
            else:
                break
        
        else:
            var_str=txt[ch]+var_str
       
        
    index=txt.find('/', new_index)
    if(index<len(txt)-1 and index>=0):
        var_str=var_str+"$$"+paired_bracket_division(txt,index)
        
    return var_str


 
#Pick Sum
def Pick_Sum(str_val,n):
    
    var_Sum=""
    count_open=0
    count_close=0
    new_index=0
    index=str_val.find('SUM(', n)
    
    for ch in range(index,len(str_val)):
        
        if(str_val[ch]=='('): 
            count_open=count_open+1 
        elif(str_val[ch]==')'): 
            count_close=count_close+1 
               
        if(count_close==count_open and count_open>0):
            var_Sum=var_Sum+str_val[ch]
            new_index=ch
            break
        elif((str_val[ch]=='+'  or str_val[ch]=='/') and count_close>=count_open):
            if(count_open>count_close):
                var_Sum=var_Sum+str_val[ch]
            else:
                new_index=ch
                break
            
        elif(ch==len(str_val)-1) :
            var_Sum=var_Sum+str_val[ch]
       
        else:
            var_Sum=var_Sum+str_val[ch]                      
    index=str_val.find('SUM(', new_index)
    if(index<len(str_val)-1 and index>=0):
        var_Sum=var_Sum+"$$"+Pick_Sum(str_val,index)
        
    return var_Sum     


def findDepKpis(ele,val_grp):
	value = var_dict[ele]
	value_grp = re.findall("\[[^\[]*\]",value)
	for item in value_grp:
		if item in var_dict.keys() and item not in val_grp and item != ele:
			val_grp = val_grp + findDepKpis(item,val_grp)  
			val_grp.append(item)
	return val_grp
    
def nonDuplicateRows(colu):
	txt = "If(Rank(Baserowid(),'asc',"
	tab = find_the_table(colu)
	keys_present = dict_of_keys_cal[tab]
	for item in keys_present:
		txt = txt + '[' + item + '],'
	#txt = txt.strip(',') + ')=1,' + colu + ')'
	txt = txt + colu + ')=1,' + colu + ')'
	return txt
    
    
def find_the_table(xc):
	for key,d_key in dataProvider_and_keys.items():
		if xc[1:-1] in d_key:
			tab = key
	return tab

	
def find_the_keys(xc, Over_to_ag): 
	time_for_li = []
	if re.search("\[(.*?)\]",time_for):
		time_for_li = re.findall("\[.*?\]",time_for)
	time_val = ""
	if len(time_for_li) >0:
		for t in time_for_li:
			time_val = time_val  +","+ t
	Over_to_agg = Over_to_ag.replace("[DATE]","[DATE_ID]")
	'''if ",[TIME]" in Over_to_agg:
		Over_to_agg = Over_to_agg.replace(",[TIME]",time_val)
	elif "[TIME]," in Over_to_agg:
		Over_to_agg = Over_to_agg.replace("[TIME],",time_val.strip(",") +",")
	elif "[TIME]" in Over_to_agg:
		Over_to_agg = Over_to_agg.replace("[TIME]",time_val.strip(","))'''
	#----------------------------for master table---------------
	tab = find_the_table(xc)
	over_agg = "OVER("
	keys_p = dict_of_keys_cal[tab]
	if len(keys_p) !=0 and re.search("OVER\((.*?\])\)",Over_to_agg): 
		over_k = re.search("OVER\((.*?\])\)",Over_to_agg).group(1)
		over_k_li = re.findall("\[(.*?)\]",Over_to_agg)
		over_k_li = list(set(over_k_li))
		for ele in over_k_li:
			if ele == 'TIME':
				for t in time_for_li:
					if t[1:-1] in keys_p:
						over_agg = over_agg + '[' + ele +']' + ","
						break
			elif ele in keys_p:
				over_agg = over_agg + '[' + ele +']' + ","
		over_agg = over_agg.strip(",")
		over_agg = over_agg + ")" 
	else:
		over_agg = ""	
	if over_agg	== 'OVER()':
		over_agg = ''		
	return over_agg

def open_formula(string):
	for key,value in dict_of_variables.items():
		if string.find('[' + key + ']')>=0 and not (key in custom_keys_li):
			formula_flag = 0                    
			print "formula opening!"
			val_grp = re.findall("OVER\((.*?\])\)",string)
			over_val = []
			over_k = ""
			for v in val_grp:
				over_kkkk = re.findall("\[.*?\]",v)
				if "[DATETIME]" in over_kkkk:
					v_after= v.replace("[DATETIME]", "[DATE],[TIME]")
					string=string.replace(v,v_after)
				elif "[TIME]" in over_kkkk:
					continue
				else:
					for o_ele in over_kkkk:
						over_k = ""
						if o_ele[1:-1] in dict_of_variables.keys():
							v_ele = dict_of_variables[o_ele[1:-1]]
							for it_ele in re.findall("\[.*?\]",v_ele):
								if it_ele[1:-1] in custom_keys_li or it_ele[1:-1] in dict_of_vars.keys():
									over_k = over_k + it_ele + ','
							over_k = over_k.rstrip(',')
							if o_ele == over_k and o_ele[1:-1] == key:
								formula_flag =1
							if over_k == '' and o_ele + ',' in v:
								v_after= v.replace(o_ele + ',', over_k)
							elif over_k == '' and ',' + o_ele  in v:
								v_after= v.replace(',' + o_ele, over_k)
							else:
								v_after= v.replace(o_ele, over_k)
							string=string.replace(v,v_after)
							v = v_after
				over_val = over_val + re.findall("\[(.*?)\]",v)
				over_val = list(set(over_val))
			if formula_flag !=1 and key !="TIME": 
				string=string.replace("["+key+"]","("+value+")")
	return string
 

def add_aggregation(txt, over_keys):
    inde = 0 
    if re.search("case",txt):
        case_counter = re.findall("(case  when.*?\[([^\[]*?)\].*?then)",txt)              
        for each_c in case_counter:
            if each_c[1] not in custom_keys_li:
                inde = txt.find(each_c[0],inde,len(txt))
                new_str = txt[inde+ len(each_c[0]) : len(txt)]
                new_str = new_str[0:new_str.find('End')]
                inde = inde + len(new_str)
                if 'case  when' in new_str:
                    new_in = new_str.find('case  when')
                    new_str_1 = new_str[0:new_in]
                    new_str_1_org = new_str_1
                    counter_g = re.findall("\[.*?\]", new_str_1)
                    counter_g = list(set(counter_g))
                    for xc in counter_g:
                        if not (xc[1:-1] in custom_keys_li) and over_keys.find(xc)<=0 and (Find_data_type(xc[1:-1],TableName) == "Currency" or Find_data_type(xc[1:-1],TableName) == "Integer"):
                            if over_keys <> str.Empty:
                                Over_to_ag= find_the_keys(xc,over_keys)
                                new_str_1 = new_str_1.replace(xc,add_agg(nonDuplicateRows(xc))+Over_to_ag)
                    txt = txt.replace(new_str_1_org,new_str_1)
                else:
                    new_str_org = new_str
                    counter_g = re.findall("\[.*?\]", new_str)
                    counter_g = list(set(counter_g))
                    for xc in counter_g:
                        if not (xc[1:-1] in custom_keys_li) and over_keys.find(xc)<=0 and (Find_data_type(xc[1:-1],TableName) == "Currency" or Find_data_type(xc[1:-1],TableName) == "Integer"):
                            if over_keys <> str.Empty:
                                Over_to_ag= find_the_keys(xc,over_keys)
                                new_str = new_str.replace(xc,add_agg(nonDuplicateRows(xc))+Over_to_ag)
                    txt = txt.replace(new_str_org,new_str)       
             
        case_split = re.split("\(case.*?End\)",txt)
        for ele_1 in case_split:
            then_spli = re.split("then.*?End",ele_1)
            for ele in then_spli:					
                ele_li = re.split("(SUM|AVG)\(.*?\)\s*OVER\(.*?\]\)",ele)
                for li in ele_li:                    
                    li_org = li
                    counter_g = re.findall("\[.*?\]", li)
                    counter_g = list(set(counter_g))
                    for xc in counter_g:
                        if not (xc[1:-1] in custom_keys_li) and over_keys.find(xc)<=0 and (Find_data_type(xc[1:-1],TableName) == "Currency" or Find_data_type(xc[1:-1],TableName) == "Integer"):
                            if over_keys <> str.Empty:
                                Over_to_ag= find_the_keys(xc,over_keys)                        
                                li = li.replace(xc,add_agg(nonDuplicateRows(xc))+Over_to_ag)
                    txt = txt.replace(li_org,li)
        case_grp = re.findall("SUM\(SN\(.*?case.*?End.*?\,0\)\)\s*OVER\s*\(.*?\]\)",txt)
        for cas in case_grp:
            cas_org = cas
            cas = re.sub("OVER\s*\(.*?\]\)",over_keys,cas)
            txt = txt.replace(cas_org, cas)
          

    else:                
        ele_li = re.split("(SUM|AVG)\(.*?\)\s*OVER\(.*?\]\)",txt)                
        for li in ele_li:					
            li_org = li
            counter_g = re.findall("\[.*?\]", li_org)
            counter_g = list(set(counter_g))
            for xc in counter_g:
                if not (xc[1:-1] in custom_keys_li) and over_keys.find(xc)<=0 and (Find_data_type(xc[1:-1],TableName) == "Currency" or Find_data_type(xc[1:-1],TableName) == "Integer"):
                    if over_keys <> str.Empty:
                        Over_to_ag= find_the_keys(xc,over_keys)
                        li = li.replace(xc,add_agg(nonDuplicateRows(xc))+Over_to_ag)
            txt = txt.replace(li_org,li)
    return txt
               
    
dict = {}
dep = {}
var_dict =OrderedDict()
sorted_dict = OrderedDict()
list_of_kpis = []
colname= []


dataProvider_and_keys = {}
dict_of_keys_cal = {}
custom_keys_li = []
TableNameList = []
queryTable = Document.Data.Tables[Query_tablename]
cursorName = DataValueCursor.CreateFormatted(queryTable.Columns["TableName"])
cursorQuery = DataValueCursor.CreateFormatted(queryTable.Columns["SQL Query"])
cursorKeys = DataValueCursor.CreateFormatted(queryTable.Columns["KeyNames"])
cursorProvider = DataValueCursor.CreateFormatted(queryTable.Columns["DataProvider"])
for row in queryTable.GetRows(cursorName,cursorQuery,cursorProvider,cursorKeys):
	valueName = cursorName.CurrentValue.replace(" ","").upper()
	TableNameList.append(valueName)
	valueQuery = cursorQuery.CurrentValue.upper()
	dataProvider = cursorProvider.CurrentValue.upper()
	AllKeys = cursorKeys.CurrentValue.upper().split(',')
	custom_keys_li = AllKeys
	if valueName != "CUSTOMDATAPROVIDER":		
			select_grp = re.search("(SELECT(?:(?!(SELECT))).*?)\s+FROM\s+", valueQuery).group(1)
			select_grp = select_grp.strip() + ','
			all_alias = re.findall("(DIM|DC).*?\s+AS\s+(.[^\s]*?)\,",select_grp)
			all_alias_final = []
			for c in all_alias:
				all_alias_final.append(c[1])  
			dataProvider_and_keys[dataProvider] = all_alias_final
			dict_of_keys_cal[dataProvider]= []
			for item in all_alias_final:
					if item in AllKeys:
						dict_of_keys_cal[dataProvider].append(item)

counter = 0
id_name=""
dict_of_vars = {}
dict_of_ids = {}
dict_of_alerter = {}	
data_type_kpi ={}
dict_of_variables = {}  
dictVariablesAlerter ={} 
varTable = Document.Data.Tables[Variable_tablename]
cursorColName = DataValueCursor.CreateFormatted(varTable.Columns["ColumnName"])
cursorColExpr = DataValueCursor.CreateFormatted(varTable.Columns["Formula"])
cursorColQualification = DataValueCursor.CreateFormatted(varTable.Columns["Qualification"])
cursorColAlerters = DataValueCursor.CreateFormatted(varTable.Columns["Alerters"])
cursorColId = DataValueCursor.CreateFormatted(varTable.Columns["Id"])
data_type_cursor = DataValueCursor.CreateFormatted(varTable.Columns["Data Type"])
for row in varTable.GetRows(cursorColName, cursorColId, cursorColAlerters,data_type_cursor,cursorColQualification,cursorColExpr):
	cal_col_name = cursorColName.CurrentValue.upper()
	cal_col_id = cursorColId.CurrentValue.upper()
	cal_col_alters = cursorColAlerters.CurrentValue
	cal_val_exp = cursorColExpr.CurrentValue.upper()
	cal_quali = cursorColQualification.CurrentValue.upper()
	dict_of_ids[cal_col_id] = cal_col_name
	data_t = data_type_cursor.CurrentValue.upper()
	data_type_kpi[cal_col_name] = data_t
	upper_case = re.split("\'[^\']*\'",cal_col_alters)
	for ele in upper_case:
		cal_col_alters = cal_col_alters.replace(ele,ele.upper(),1)
	if (cal_col_alters == '') or (cal_col_alters == "(EMPTY)"):
		print ""
	else:
		dict_of_alerter[cal_col_name] = cal_col_alters
	if cal_quali == 'DIMENSION':
		dict_of_vars[cal_col_name] = cal_val_exp
	dict_of_variables[cal_col_name] = cal_val_exp
	dictVariablesAlerter[cal_col_name] = cal_val_exp
        

tablename_reportbo = Document.Data.Tables[Report_tablename]
Report_cursor = DataValueCursor.CreateFormatted(tablename_reportbo.Columns["Report Type"])
columns_cursor = DataValueCursor.CreateFormatted(tablename_reportbo.Columns["Columns"])
Reportname_cursor = DataValueCursor.CreateFormatted(tablename_reportbo.Columns["Report Name"])
ReportID_cursor = DataValueCursor.CreateFormatted(tablename_reportbo.Columns["Report Id"])
DataFilter_cursor = DataValueCursor.CreateFormatted(tablename_reportbo.Columns["Data Filters"])

ReportStructureDataTable = Document.Data.Tables[Report_tablename]
for Repcol in ReportStructureDataTable.Columns:
    ReportsBOcolNames.append(Repcol.Name)

for r in range(1,len(ReportsBOcolNames)):
    cursorcolNames.append(DataValueCursor.CreateFormatted(ReportStructureDataTable.Columns[ReportsBOcolNames[r]]))

for row in ReportStructureDataTable.GetRows(*cursorcolNames):
    try:
        cursorType=cursorcolNames[2].CurrentValue
              
        if re.search("Chart", cursorType):
            Sections=cursorcolNames[3].CurrentValue.upper()
            rcn=4
            
            while rcn < len(cursorcolNames):
                charts_keys=[]
                Sections_cols=[] 
                Sections_keys=[] 
                regcolr_keys = [] 
                cursorCols=cursorcolNames[rcn].CurrentValue.upper()
                cursorCategory=cursorcolNames[rcn+1].CurrentValue.upper()
                cursorValue=cursorcolNames[rcn+2].CurrentValue.upper()
                regcolr=cursorcolNames[rcn+3].CurrentValue.upper()
                find_col_temp=cursorValue

                rcn=rcn+4
                xaxis=cursorCategory.replace("[","").replace("]","")
                if (xaxis!="(EMPTY)"):                          
                          if (xaxis.find("/")>0):
                                 xaxis_cols=xaxis.split("/")
                                 for xaxis_col in range(0,len(xaxis_cols)):
                                           charts_keys.append(xaxis_cols[xaxis_col]) 
                          else:
                                 charts_keys.append(xaxis)
                          if (Sections!="(EMPTY)"):
                                 if (Sections.find("@")>0):
                                           Sections_cols=Sections.split("@")
                                           for section_col in range(0,len(Sections_cols)):
                                                     Sections_keys.append(Sections_cols[section_col]) 
                                 else:
                                           Sections_keys.append(Sections)

                                 charts_keys=charts_keys+Sections_keys
                                 
                          if (regcolr!="(EMPTY)"):
                                 regcolr = regcolr.replace("[","").replace("]","")
                                 regcolr_keys.append(regcolr)
                                 charts_keys=charts_keys+regcolr_keys
                          for i_key_chart in range(0,len(charts_keys)):
                                 if (charts_keys[i_key_chart]=="DATETIME"):
                                        charts_keys.remove("DATETIME")
                                        charts_keys.append("DATE")
                                        charts_keys.append("TIME")
                                 elif charts_keys[i_key_chart] in dict_of_vars.keys() and charts_keys[i_key_chart]!="DATE" and charts_keys[i_key_chart]!="TIME":
                                      
                                        all_k = re.findall("\[([^\[]*?)\]",dict_of_vars[charts_keys[i_key_chart]])
                                        if len(all_k)>0:
											charts_keys.remove(charts_keys[i_key_chart])
											for item in all_k:
												charts_keys.append(item)
                                        
                          charts_keys=list(set(charts_keys))
                          if (find_col_temp.find("@")>0):
                            find_col_temp_cols=find_col_temp.split("@")
                            print "find_col_temp_cols"
                            print find_col_temp_cols
                            for i in find_col_temp_cols:
                                Keys_and_yaxis[i]=charts_keys
                          else:
                            Keys_and_yaxis[find_col_temp]=charts_keys
                                                                                                        
                else:
                          break
            
    except Exception as e:
        print e.message		
        function_log("--[WARNING]-- Below exception thrown while finding the keys for charts: "+str(e.message))	
        #function_log(str(e.message))		

        	
dict_of_keys = {}
Table_with_ReportId={}
for row in tablename_reportbo.GetRows(Report_cursor,columns_cursor,Reportname_cursor,ReportID_cursor, DataFilter_cursor):
    if (Report_cursor.CurrentValue.upper() == "TABLE"):
		id_name = ""
		x = columns_cursor.CurrentValue.upper()
		cols_reg = re.sub('FORMATDATE(.*?)\)',"'DATE'",x)
		cols_reg=cols_reg.replace("'","")
		cols=cols_reg.split(",")
		x = x.rstrip("'")
		x= x.split("',")
		ov_keys = []        
		if DataFilter_cursor.CurrentValue.upper() !="(EMPTY)":
			filters = DataFilter_cursor.CurrentValue.upper()
			keys_pre = re.findall('\[(.*?)\]',filters)
			for i in keys_pre:
					if i in custom_keys_li:
						ov_keys.append(i)
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
					d_li = re.findall("\[([^\[]*?)\]",val)
					for d in d_li: 					   
						if d in custom_keys_li:
							ov_keys.append(d)
					
			
		
		ov_keys=set(ov_keys)
		ov_keys = list(ov_keys)
		ov_keys.sort()
		id_name= ReportID_cursor.CurrentValue.upper()
		if not(id_name in Table_with_ReportId.keys()):
			Table_with_ReportId[id_name]=x
			dict_of_keys[id_name] = ov_keys
		else:
			Table_with_ReportId[id_name]= list(set(Table_with_ReportId[id_name] + x))
			dict_of_keys[id_name] = list(set(dict_of_keys[id_name] + ov_keys))
	

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
	colname.append(col.Name)


varaibleCreationResult = "varaibleCreationResult"
replacevalue_dict = {}

for row in varTable.GetRows(cursorColName,cursorColExpr):
    valueColName = cursorColName.CurrentValue.replace(" ","").upper()
    valueColExpr = cursorColExpr.CurrentValue
    upper_case = re.split("\'[^\']*\'",valueColExpr)
    for ele in upper_case:
        valueColExpr = valueColExpr.replace(ele,ele.upper(),1)
    var_dict["[" + valueColName + "]"] = valueColExpr
list_of_kpis = var_dict.keys() 

operators = ["<=",">=","=","<>","<",">"]
opera =''

for key, value in dict_of_alerter.items():
	if "&&" in value and re.search("\{(.*?)\}",value):
		li_amp = re.split("&&",value)  
		for each_e in (li_amp):
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
						dict_of_alerter[key] = dict_of_alerter[key].replace(li_org,li)
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
					dict_of_alerter[key] = dict_of_alerter[key].replace(li_org,li)
	if not ("&&" in value) and re.search("\{(.*?)\}",value):
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
						dict_of_alerter[key] = dict_of_alerter[key].replace(li_org,li)
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
					if id_k in dict_of_ids.keys():
						type_kpi = data_type_kpi[dict_of_ids[id_k]]
						if type_kpi == 'STRING' and not (after_op[1].strip().startswith("'") or after_op[1].strip().startswith('"')):
							li = li.replace(after_op[1].strip(), '"' + after_op[1].strip() + '"').replace("{" + id_k + "}","[" + dict_of_ids[id_k] + "]")
						else:
							li = li.replace("{" + id_k + "}","[" + dict_of_ids[id_k] + "]")
					dict_of_alerter[key] = dict_of_alerter[key].replace(li_org,li)               
                    		
	
for key,value in var_dict.items():
	val_grp = re.findall("\[[^\[]*\]",value)
	val_grp = list(set(val_grp))
	if  key[1:-1]  in dict_of_alerter.keys():
		dict_grp = re.findall("\[[^\[]*\]",dict_of_alerter[key[1:-1]])
		dict_grp = list(set(dict_grp))
		val_grp = val_grp + dict_grp
	for ele in val_grp:
		if (ele in list_of_kpis) and (list_of_kpis.index(ele)>list_of_kpis.index(key)):
			sorted_grp = []
			final_sorted_grp = []
			sorted_grp = findDepKpis(ele,sorted_grp)
			for sor in sorted_grp:
				if sor not in final_sorted_grp:
					final_sorted_grp.append(sor)
			final_sorted_grp.append(ele)
			for each_ele in final_sorted_grp:
				sorted_dict[each_ele] = var_dict[each_ele]
	else:
		sorted_dict[key] = var_dict[key]

dep_dict = {}
list_of_kpis = var_dict.keys() 
for key,value in var_dict.items():
	val_grp = re.findall("\[[^\[]*\]",value)
	val_grp = set(val_grp)
	kpis = ""
	count = 0
	if key == "[DATETIME]" or key == "[TIME]":
		continue
	for ele in val_grp:
		if ele[1:-1] in custom_keys_li:
			count = count + 1
	if len(val_grp) == count and (len(val_grp)> 0 and count>0): #and key in var_key_dict:
		continue
	for ele in val_grp:
		if (ele in list_of_kpis):# and not (key in var_key_dict): #and not (ele in var_key_dict):
			kpis = kpis + ele +"&"
	if kpis <> str.Empty:
		dep_dict[key] = kpis.strip("&").split("&")		


print "raw"
tables_li = []
valueColName = "DATE"
valueColExpr = "(Date([DATE_ID]))"
if 'DATE' in dict_of_variables.keys():
    dict_of_variables['DATE'] = valueColExpr
    dictVariablesAlerter['DATE'] = valueColExpr
cols = Document.Data.Tables['CUSTOMDATAPROVIDER_NE'].Columns
cols.AddCalculatedColumn(valueColName,valueColExpr)


time_for = ""
valueColName = "TIME"
dynamicTable =  Document.Data.Tables["DynamicTable"]
prompt_col=[]
DocumentPropertyName = ""
for col in dynamicTable.Columns:
    prompt_col.append(col.Name)
print "prompt_col"
print prompt_col
DocumentPropertyName = ""
for i in prompt_col:
    if re.search("RESOLUTION", i): 
        DocumentPropertyName = i.replace("(","").replace(")","")
if DocumentPropertyName == str.Empty:
    Resolutioninput = "RAW"
elif ( Document.Data.Properties.ContainsProperty ( DataPropertyClass.Document, DocumentPropertyName )):
    Resolutioninput = Document.Properties[DocumentPropertyName]
else:
    Resolutioninput = "RAW"
print Resolutioninput
if "[TIME]" in sorted_dict.keys():
	val = sorted_dict["[TIME]"]
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
if 'TIME' in dict_of_variables.keys():
    dict_of_variables['TIME'] = valueColExpr
    dictVariablesAlerter['TIME'] = valueColExpr
time_for = valueColExpr
cols = Document.Data.Tables['CUSTOMDATAPROVIDER_NE'].Columns
cols.AddCalculatedColumn(valueColName,valueColExpr)

for key, value in dict_of_alerter.items():
	string_s = ""
	val_list = value.split("&&")
	for ele in val_list:
		string_s = string_s + "(" + ele + ")" + " Or "
	string_s = string_s.strip(" Or ")
	dict_of_alerter[key] = string_s
    		

overr_keys=[]
counter = 0

Overkeys_with_ReportId={}
#Create new counter as aggregation over all keys present in NE level datatable
try:
  unique_keys = custom_keys_li
  for key,value in dict_of_keys.items():
      #print set(key)
      #print value
      over_with_keys="OVER("
      overr_keys = set(value)
      print overr_keys
      
      for keys in overr_keys:
           over_with_keys=over_with_keys+"["+keys+"],"

      over_with_keys=over_with_keys.strip(",")+")"
      print over_with_keys 
      Overkeys_with_ReportId[key]=over_with_keys
      
  
except Exception as e:
  count = 1
  print e.message

round_p="#."
roundflag=0
count = 0
noFilterFlag = {}
for sort_item in sorted_dict.keys():
    val = sorted_dict[sort_item]
    if re.search('NOFILTER\-',val):
        noFilterFlag[sort_item] = 1
        if sort_item in dep_dict.keys():
            for i in dep_dict[sort_item]:
                noFilterFlag[i] = 1
                
tot_kpi=len(sorted_dict.keys())
function_log("--[INFO]-- Total KPIs Present:")
function_log("--[INFO]--"+str(len(sorted_dict.keys())))
function_log("--[INFO]-- KPIs:")
function_log(str(sorted_dict.keys()))
#print "raw"
for key,value in sorted_dict.items():
    try:
        valueColName = key[1:-1]
        print valueColName
        function_log("")
        function_log("")
        function_log("--[INFO]-- KPI:-")
        function_log(valueColName)
        function_log("--[INFO]-- EXPRESSION:-")
        function_log(str(value))
        valueColExpr = value
        value_count = 0
        print valueColExpr
        num_counter=0
        num_counter = len(re.findall("\[(.*?)\]",valueColExpr))
        print num_counter
        
        if re.search("COUNT\(",valueColExpr):
            valueColExpr=re.sub("COUNT\(","UNIQUECOUNT(",valueColExpr)
        if valueColName in colname:
			continue
        if  re.search("CONCATENATE\(PARSEREAL",valueColExpr): 
			if ("BUSY_HOUR" in valueColExpr) and ("MIN" in valueColExpr):
				valueColExpr = "Concatenate([BUSY_HOUR],':',[MIN])"
			elif ("HOUR" in valueColExpr) and ("MIN" in valueColExpr):
				valueColExpr = "Concatenate([HOUR],':',[MIN])"
			else:
				valueColExpr = "[HOUR]"

        TableName = 'CUSTOMDATAPROVIDER_NE'
        if re.search("\?\?L",valueColExpr):
			continue
        if valueColName == "DATE" :
			continue
            
        if re.search("/(NONE/)",valueColExpr):
            valueColExpr=valueColExpr.replace("(NONE)","")
              
        if valueColName == "TIME" :
            continue

        if valueColName == "DATETIME":
            valueColExpr = "Datetime(Concatenate(Date([DATE]),' ',[TIME]))"
                    
            
        if  "CURRENTDAYONLY".lower().replace("_","")  in valueColName.lower().replace("_","") :
            valueColExpr = "UNIQUECOUNT(([DATE_ID])"

        if ("USERRESPONSE" in valueColExpr) and ("LastDate" in valueColExpr):
            valueColExpr = 'Date(Integer(Right("${dt2}",4)),Integer(Left("${dt2}", 2)),  Integer(Mid("${dt2}",4,2)))'

        if ("USERRESPONSE" in valueColExpr) and ("RAWDATA" in valueColExpr):
            max_issue=valueColExpr.replace("USERRESPONSE('Selectrawdatatimeresolution:')","'RAWDATA'")
            valueColExpr=max_issue
        if ("USERRESPONSE" in valueColExpr) and ("Selectrawdatatimeresolution" in valueColExpr):
            max_issue=re.sub("USERRESPONSE\(.*?'Selectrawdatatimeresolution:'\)","'RAWDATA'",valueColExpr)
            valueColExpr=max_issue
        if ("USERRESPONSE" in valueColExpr) and ("EnterThreshold" in valueColExpr):
            valueColExpr=valueColExpr.replace("USERRESPONSE('EnterThreshold(%):')",str(Document.Properties["PromptVariableInt"]))
        if ("USERRESPONSE" in valueColExpr) and ("FirstDate" in valueColExpr):
            valueColExpr = 'Date(Integer(Right("${dt1}",4)),Integer(Left("${dt1}", 2)), Integer(Mid("${dt1}",4,2)))'
                    
        
        if re.search("AVERAGE\(",valueColExpr):
            max_issue=valueColExpr.replace("AVERAGE(","AVG(")
            valueColExpr=max_issue
        if re.search("(AVG)",valueColExpr):
            max_issue=valueColExpr.replace("(AVG)","_AVG")
            valueColExpr=max_issue
            
        roundflag=0
        if valueColExpr.startswith("ROUND("):
				lis = valueColExpr.rsplit(",",1)
				num = lis[1].strip(")")
				num=int(num)
				round_p="0."
				for i in range(num):
					round_p=round_p+"#"
				print "formatting____________________"
				roundflag=1
                
        if re.search("(MAX)",valueColExpr):
            max_issue=valueColExpr.replace("(MAX)","_MAX")
            valueColExpr=max_issue
            #print valueColExpr
            
        if re.search("(MIN)",valueColExpr):
            max_issue=valueColExpr.replace("(MIN)","_MIN")
            valueColExpr=max_issue
            #print valueColExpr
        
        if re.search("(SUM)",valueColExpr):
            max_issue=valueColExpr.replace("(SUM)","")
            valueColExpr=max_issue
           
            
        Rep_ID=""
        Over_to_agg=""
        for key,value in Table_with_ReportId.items():      
             table_cols_check_kpis = value
             if "'" + valueColName in value:
                   Rep_ID=key
                   Over_to_agg=Overkeys_with_ReportId[Rep_ID] 
                   break
        else:
             for k,v in dep_dict.items():
                   if '[' + valueColName + ']' in v:
                        for key,value in Table_with_ReportId.items():      
                            table_cols_check_kpis = value
                            if "'" + k[1:-1] in value:
                                Rep_ID=key
                                Over_to_agg=Overkeys_with_ReportId[Rep_ID] 
                                break       
         
        if re.search('NOFILTER\-',valueColExpr):
            valueColExpr = valueColExpr.replace('NOFILTER-','')         
        

        if not ('[' + valueColName + ']' in noFilterFlag.keys()):         
            if re.search('(OVER\(.*?\]\))',valueColExpr):
                all_keys = re.findall("\[.*?\]",Over_to_agg)               
                over_prep = re.findall('(OVER\(.*?\]\))',valueColExpr)
                for ele in over_prep:
                    ele1 = ele[:-1]
                    for i in all_keys:
                        if not (i in ele):
                            ele1 = ele1 + "," + i
                    ele1 = ele1 + ')'
                    valueColExpr=valueColExpr.replace(ele,ele1)

                    #print ele
				
        if ("TODATE" in valueColExpr) and  (re.search("\;\'HH\:mm\'",valueColExpr)):
			x = valueColExpr.replace("TODATE","PARSETIME")
			valueColExpr=x
			valueColExpr = valueColExpr.replace(";",",")
            
        if ("TODATE" in valueColExpr) and not (re.search("\,\'HH\:mm\'",valueColExpr)):
			x = valueColExpr.replace("TODATE","PARSEDATE")
			valueColExpr=x

        if ("TODATE" in valueColExpr) or ("PARSEDATE" in valueColExpr):
			z=valueColExpr.replace(";",",")
			valueColExpr=z
			print '235'

        if re.search ("PARSEDATE\(",valueColExpr) and (re.search("\,\'HH\:mm\'",valueColExpr)):
			y = re.findall("PARSEDATE\((.*?)\,", valueColExpr)
			valueColExpr=valueColExpr.replace(y[0], "STRING(" +y[0]+")")

        if re.search("PARSEDATE\(.*?\[([^\[]*?)\].*?\,\'yyyy-MM-dd\'\)",valueColExpr):      
			if re.search ("PARSEDATE\(",valueColExpr) and (re.search("PARSEDATE\(.*?\[([^\[]*?)\].*?\,\'yyyy-MM-dd\'\)",valueColExpr).group(1) == "DATE_ID"):
				if "DATETIME([DATE_ID])" in valueColExpr:
					valueColExpr = valueColExpr.replace("DATETIME([DATE_ID])", "String(DATETIME([DATE_ID]))") 
				else:          
					valueColExpr = valueColExpr.replace("[DATE_ID]", "String([DATE_ID])")
    
        if re.search("1440",valueColExpr):
            
            a=valueColExpr.replace("[","#")
            b=a.replace("]","!")
            
            for i in range(0,len(TableNameList)):
                TableNameList[i]=TableNameList[i].replace(" ","")
                issue1="#"+TableNameList[i]+"!.#1440!"
                issue2="#"+TableNameList[i]+"!.1440"
                                
                if re.search(issue1,b):
                    b=b.replace(issue1,"1440")
                if re.search(issue2,b):
                    b=b.replace(issue2,"1440")
                    
            valueColExpr=b.rstrip("#").replace("#","[").replace("!","]")
               
        c=0   
        
        max_bracket_issue=valueColExpr.replace("(","@")
        
        max_tbl_datacoverage=max_bracket_issue.replace(".","$")                
        max_tbl_datacoverage=max_tbl_datacoverage.replace("[","#")
        max_tbl_datacoverage=max_tbl_datacoverage.replace("]","!")

        if re.search("MAX\(.*?\)*[^A-Z]OVER", valueColExpr):
            Max_over_all_issue= re.findall(r'MAX\(.*?\)*[^A-Z]OVER', valueColExpr)
            for ch_Max in range(0,len(Max_over_all_issue)):
                try:
					if("SUM(" in Max_over_all_issue[ch_Max]):
										Max_over_all_issue[ch_Max] = re.search(r'(MAX\(.*?\)*[^A-Z]OVER\(.*?\)*[^A-Z]OVER\()', valueColExpr[valueColExpr.index(Max_over_all_issue[ch_Max]):len(valueColExpr)]).group(1)  
                except Exception as e:
					print e.message
                 
                var_str=""
                count_open=0
                count_close=0
                for ch in range(valueColExpr.index(Max_over_all_issue[ch_Max]),len(valueColExpr)):
                    var_str=var_str+valueColExpr[ch]
                    if(valueColExpr[ch]=='('):
                        count_open=count_open+1 
                    elif(valueColExpr[ch]==')'):
                        count_close=count_close+1 
                        if(count_close==count_open and count_open>0):
                            break
                Max_over_all_issue[ch_Max]=var_str
                
            if re.search("(?i)MAX", valueColExpr):
                
                for sov in range(0,len(Max_over_all_issue)):
                    index = valueColExpr.find(Max_over_all_issue[sov]) 
                    index_af = index + len(Max_over_all_issue[sov])
                    if not (valueColExpr.startswith("OVER(", index_af ,len(valueColExpr))):
						if re.search("OVER\(", Max_over_all_issue[sov]):
							over_split=re.split("[^a-zA-Z\_]OVER",Max_over_all_issue[sov])
							print (over_split)
							if re.search("(?i)All", over_split[1]):
								over_split[0]=over_split[0]+"]"
								over_split[1]="("+over_split[1].strip(" ")+")"
							max_part = add_sqr_brac(over_split[0])	
							over_part = "OVER"+add_pair_brac(over_split[1])                            
							counters_pre = re.findall('\[.*?\]',max_part)
							counters_pre = list(set(counters_pre))                           
							for cou in counters_pre:  
								if cou in sorted_dict.keys():
									continue                            
								if not (cou[1:-1] in custom_keys_li) and find_the_keys(cou, over_part).find(cou)<=0 and (Find_data_type(cou[1:-1],TableName) == "Currency" or Find_data_type(cou[1:-1],TableName) == "Integer"):
									max_part = max_part.replace(cou,add_agg(nonDuplicateRows(cou))+ find_the_keys(cou, over_part))
							Max_over_all_sol=add_pair_brac(max_part)+Over_to_agg                                
							valueColExpr=valueColExpr.replace(Max_over_all_issue[sov],"("+Max_over_all_sol+")")      
							
						else:
							continue
           
        if re.search("MIN\(.*?\)*[^A-Z]OVER", valueColExpr):      

            Min_over_all_issue= re.findall(r'MIN\(.*?\)*[^A-Z]OVER', valueColExpr)
            for ch_Min in range(0,len(Min_over_all_issue)):
                if("SUM(" in Min_over_all_issue[ch_Min]):
					Min_over_all_issue[ch_Min] = re.search(r'(MIN\(.*?\)*[^A-Z]OVER\(.*?\)*[^A-Z]OVER\()', valueColExpr[valueColExpr.index(Min_over_all_issue[ch_Min]):len(valueColExpr)]).group(1)
        
                var_str=""
                count_open=0
                count_close=0
                for ch in range(valueColExpr.index(Min_over_all_issue[ch_Min]),len(valueColExpr)):
                    var_str=var_str+valueColExpr[ch]
                    if(valueColExpr[ch]=='('):
                        count_open=count_open+1 
                    elif(valueColExpr[ch]==')'):
                        count_close=count_close+1 
                        if(count_close==count_open and count_open>0):
                            break
                Min_over_all_issue[ch_Min]=var_str
                

            
            min_where_issue = re.findall("(MIN\(.*?\)*[^A-Z]OVER\(.*?\))WHERE",valueColExpr)
            for where_min in range(0,len(min_where_issue)>0):
				valueColExpr = valueColExpr.replace(min_where_issue[where_min], "(" + min_where_issue[where_min] +")")
            if re.search("(?i)Min", valueColExpr):
                for sov in range(0,len(Min_over_all_issue)):
                    index = valueColExpr.find(Min_over_all_issue[sov]) 
                    index_af = index + len(Min_over_all_issue[sov])
                    if not (valueColExpr.startswith("OVER(", index_af ,len(valueColExpr))):
						if re.search("(?i)OVER", Min_over_all_issue[sov]):
                         
							over_split=re.split("[^a-zA-Z]OVER",Min_over_all_issue[sov])
							if re.search("(?i)All", over_split[1]):
								over_split[0]=over_split[0]+"]"
								over_split[1]="("+over_split[1].strip(" ")+")"
							min_part = add_sqr_brac(over_split[0])	
							over_part = "OVER"+add_pair_brac(over_split[1])                            
							counters_pre = re.findall('\[.*?\]',min_part) 
							counters_pre = list(set(counters_pre))
							for cou in counters_pre:
								if cou in sorted_dict.keys():
									continue 
								if not (cou[1:-1] in custom_keys_li) and find_the_keys(cou, over_part).find(cou)<=0 and (Find_data_type(cou[1:-1],TableName) == "Currency" or Find_data_type(cou[1:-1],TableName) == "Integer"):
									min_part = min_part.replace(cou,add_agg(nonDuplicateRows(cou))+ find_the_keys(cou, over_part))
							Min_over_all_sol=add_pair_brac(min_part)+Over_to_agg
							valueColExpr=valueColExpr.replace(Min_over_all_issue[sov],"("+Min_over_all_sol+")")  
						else:
							continue

			
    
        if re.search("AVG\(.*?\)*[^A-Z]OVER", valueColExpr):   

            Avg_over_all_sol="" 
            Avg_over_all_issue= re.findall(r'AVG\(.*?\)*[^A-Z]OVER', valueColExpr)
            for ch_avg in range(0,len(Avg_over_all_issue)):
                var_str=""
                count_open=0
                count_close=0
                for ch in range(valueColExpr.index(Avg_over_all_issue[ch_avg]),len(valueColExpr)):
                    var_str=var_str+valueColExpr[ch]
                    if(valueColExpr[ch]=='('):
                        count_open=count_open+1 
                    elif(valueColExpr[ch]==')'):
                        count_close=count_close+1 
                        if(count_close==count_open and count_open>0):
                            break
                Avg_over_all_issue[ch_avg]=var_str
                
            if re.search("(?i)AVG", valueColExpr):                
                for sov in range(0,len(Avg_over_all_issue)):
                    if re.search("(?i)OVER", Avg_over_all_issue[sov]):
                         
                        over_split=re.split("[^a-zA-Z]OVER",Avg_over_all_issue[sov])
                        if re.search("(?i)All", over_split[1]):
                            over_split[0]=over_split[0]+"]"
                            over_split[1]="("+over_split[1].strip(" ")+")"
                        avg_part = add_sqr_brac(over_split[0])	
                        if re.search("SUM\(",avg_part):
                            avg_part = avg_part.replace("SUM(","")
                        over_part = "OVER"+add_pair_brac(over_split[1])                            
                        counters_pre = re.findall('\[.*?\]',avg_part) 
                        counters_pre = list(set(counters_pre))
                        for cou in counters_pre:
                                if cou in sorted_dict.keys():
                                    continue 
                                if not (cou[1:-1] in custom_keys_li) and find_the_keys(cou, over_part).find(cou)<=0 and (Find_data_type(cou[1:-1],TableName) == "Currency" or Find_data_type(cou[1:-1],TableName) == "Integer"):
                                    avg_part = avg_part.replace(cou,add_agg(nonDuplicateRows(cou))+ find_the_keys(cou, over_part))
                        Avg_over_all_sol=add_pair_brac(avg_part)+Over_to_agg
                    valueColExpr=valueColExpr.replace(Avg_over_all_issue[sov],"("+Avg_over_all_sol+")")  

        
        #print "before SUM OVER!"
        if re.search("OVER ALL\(",valueColExpr):
			print "inside over if"
			valueColExpr = re.sub("OVER ALL\(","OVER(",valueColExpr)
        if re.search("SUM\(.*?\)*[^A-Z]OVER", valueColExpr):

            Sum_over_all_sol=""
            
            
            valueColExpr=valueColExpr.replace("OVER ALL","OVER")
            Sum_over_all_issue_new = []
            Sum_over_all_issue= re.findall(r'SUM(?:(?!SUM\().)*?[^A-Z]OVER', valueColExpr)
            #print(Sum_over_all_issue)
            temp_over=[]
            ch_sum_new = 0
            for ch_sum in range(0,len(Sum_over_all_issue)):
                str_newv=""
                count_open=0
                count_close=0
                try:
					for ch in range(valueColExpr.index(Sum_over_all_issue[ch_sum]),len(valueColExpr)):
						str_newv=str_newv+valueColExpr[ch]
						if(valueColExpr[ch]=='('):
							count_open=count_open+1 
						elif(valueColExpr[ch]==')'):
							count_close=count_close+1 
							if(count_close==count_open and count_open>0):
								break
                except:
					print "Caught!!"
                
                if re.search("[^A-Z]OVER", str_newv):
                        temp_sumover_1=str_newv[0:str_newv.index(re.search("[^A-Z]OVER", str_newv).group())+1]
                        temp_sumover_2=str_newv[str_newv.index(re.search("[^A-Z]OVER", str_newv).group())+1:len(str_newv)]
                        temp_str_newv=add_pair_brac(temp_sumover_1)+add_pair_brac(temp_sumover_2)
                        valueColExpr=valueColExpr.replace(str_newv,temp_str_newv)
                
                

            if re.search("(?i)SUM\(", valueColExpr):                
                for sov in range(0,len(Sum_over_all_issue_new)):
                    valueColExpr=valueColExpr.replace(temp_over[sov],Sum_over_all_issue_new[sov])
                    if re.search("(?i)OVER", Sum_over_all_issue_new[sov]):
                        
                        over_split=re.split("[^a-zA-Z]OVER",Sum_over_all_issue_new[sov])
                        if re.search("(?i)All", over_split[1]):
                            over_split[0]=over_split[0]+"]"
                            over_split[1]="("+over_split[1].strip(" ")+")"
                        Sum_over_all_sol=add_pair_brac(add_sqr_brac(over_split[0]))+"OVER"+add_pair_brac(over_split[1])
                        valueColExpr=valueColExpr.replace(Sum_over_all_issue_new[sov],Sum_over_all_sol)
                        
            valueColExpr=re.sub("UNIQUECOUNT\(+SUM\(","UNIQUECOUNT(",valueColExpr)
            valueColExpr=add_pair_brac(valueColExpr)
            
        
        print "before where"
        print valueColExpr           
        All_where=[]            
        All_where_temp=[]
        if re.search("(?i)Where", valueColExpr):
            All_where=find_all_where(valueColExpr,0).split("$$")
            All_where_temp=All_where
            for i in range(0,len(All_where)):
                where_temp=All_where[i]
                if re.search(r"BETWEEN", All_where[i]):
                    btw_br=0
                    str_btw=""
                    for btw in range(All_where[i].index(re.search(r"BETWEEN", All_where[i]).group()),len(All_where[i])):
                        var_str=All_where[i]
                        if(var_str[btw]==')'):
                            btw_br+=1
																		  
                        if(btw_br==2):
                            break
                        else:
                            str_btw=str_btw+var_str[btw]
            
                    rep_btw_br=re.search(r"([0-9_]+)", str_btw.replace(",","_")).group().replace("_",",")
                    In_digits=rep_btw_br.split(",")
                    In_digits_a=int(In_digits[0])
                    In_digits_b=int(In_digits[1])+1
                    rep_btw_br=""
                    for i_digit in range(In_digits_a,In_digits_b):
                                rep_btw_br=rep_btw_br+str(i_digit)+","
                                
                    rep_btw_br=rep_btw_br.strip(",")
                    temp=str_btw.replace(str_btw,' IN('+rep_btw_br+')').replace("_",",")                                        
                    All_where[i]=All_where[i].replace(str_btw ,temp)                    
                    valueColExpr=valueColExpr.replace(where_temp,All_where[i])
                    valueColExpr=valueColExpr.replace(All_where[i],WhereToCase(All_where[i],TableName,Over_to_agg))                        				
                else:              
                    valueColExpr=valueColExpr.replace(All_where[i],WhereToCase(All_where[i],TableName,Over_to_agg))
            if re.search("(?i)Where", valueColExpr):
                All_where=find_all_where(valueColExpr,0).split("$$")
                for i in range(0,len(All_where)):
                       valueColExpr=valueColExpr.replace(All_where[i],WhereToCase(All_where[i],TableName,Over_to_agg))
        print "after where"
        print valueColExpr
        if re.search("End\) \+ \(case", valueColExpr):
                valueColExpr=valueColExpr.replace("End) + (case","")
                valueColExpr=valueColExpr.replace("(case","SUM(case")
                valueColExpr=valueColExpr.replace("SUM SUM(","SUM(")
        valueColExpr=re.sub("SUM\(\(SUM\(","((SUM(",valueColExpr)
        valueColExpr=valueColExpr.replace("SUMSUM(","SUM(")
        
        
        if valueColName in dict_of_alerter.keys():
			value = dict_of_alerter[valueColName]
			if re.search("(?i)Where", value):
				All_where=find_all_where(value,0).split("$$")
				#print All_where
				All_where_temp=All_where
				for i in range(0,len(All_where)):
					where_temp=All_where[i]
					if re.search(r"BETWEEN", All_where[i]):
						btw_br=0
						str_btw=""
						for btw in range(All_where[i].index(re.search(r"BETWEEN", All_where[i]).group()),len(All_where[i])):
							var_str=All_where[i]
							if(var_str[btw]==')'):
								btw_br+=1
																			  
							if(btw_br==2):
								break
							else:
								str_btw=str_btw+var_str[btw]
				
						rep_btw_br=re.search(r"([0-9_]+)", str_btw.replace(",","_")).group().replace("_",",")
						In_digits=rep_btw_br.split(",")
						In_digits_a=int(In_digits[0])
						In_digits_b=int(In_digits[1])+1
						rep_btw_br=""
						for i_digit in range(In_digits_a,In_digits_b):
									rep_btw_br=rep_btw_br+str(i_digit)+","
									
						rep_btw_br=rep_btw_br.strip(",")
						temp=str_btw.replace(str_btw,' IN('+rep_btw_br+')').replace("_",",")                                        
						All_where[i]=All_where[i].replace(str_btw ,temp)                    
						value=value.replace(where_temp,All_where[i])
						value=value.replace(All_where[i],WhereToCase(All_where[i],TableName,Over_to_agg))                        				
					else:              
						value=value.replace(All_where[i],WhereToCase(All_where[i],TableName,Over_to_agg))
				if re.search("(?i)Where", value):
					All_where=find_all_where(value,0).split("$$")
					for i in range(0,len(All_where)):
						   value=value.replace(All_where[i],WhereToCase(All_where[i],TableName,Over_to_agg))
			dict_of_alerter[valueColName] = value

        
          
        if valueColExpr.find("SUM(")>=0:
                 sum_all=Pick_Sum(valueColExpr,0).split("$$")
                 sum_all_temp=set(sum_all)
                 sum_all=list(sum_all_temp)
                 all_sum_temp=""
                 all_sum_temp1=""
                 print sum_all
                 for all_sum in range(0,len(sum_all)):
                         unique_sum = sum_all[all_sum]
                         sum_all_rep = re.search('SUM\((.*)\)',sum_all[all_sum]).group(1)
                         if sum_all[all_sum] == valueColExpr:
							 counters = re.findall("\[(.*?)\]",sum_all_rep)
							 counters_out = []
							 for c_ele in counters:
								if c_ele in dict_of_variables.keys() or c_ele in custom_keys_li or c_ele == 'TIME' or c_ele == 'DATE':
									counters_out.append(c_ele)
							 if len(counters) == len(counters_out) and 'SUM(' + sum_all_rep + ')' == valueColExpr:
								valueColExpr = valueColExpr.replace(sum_all[all_sum],sum_all_rep)
								unique_sum = sum_all_rep
							 print "value"
							 print valueColExpr
                         print sum_all_rep
                         if 'SUM(' in sum_all_rep: 
                            all_counters = re.findall("\[(.*?)\]",sum_all[all_sum])
                            count_agg = 0
                            all_counters_p = all_counters
                            keys = []
                            for ele in all_counters:
                                if ele in custom_keys_li or ele == 'TIME' or ele == 'DATE':
                                    keys.append(ele)
                            for ele in keys:
                                all_counters_p.remove(ele)
                            for ele in all_counters_p:
                                if 'SUM([' +ele + ']' in sum_all_rep:
                                    count_agg = count_agg + 1
                            if len(all_counters_p) == count_agg:
                                valueColExpr = valueColExpr.replace(sum_all[all_sum],sum_all_rep)
                         all_sum_temp=""
                         sum_all_new=""
                         if sum_all[all_sum] + "OVER(" in valueColExpr:                                   
								   txt_con = valueColExpr[valueColExpr.find(sum_all[all_sum]+ "OVER("):len(valueColExpr)] 
								   over_keys = re.search("OVER\(.*?\]\)",txt_con).group()
								   sum_repl = re.findall("\[.*?\]",sum_all[all_sum])
								   if len(sum_repl) == 1 and not (sum_repl[0] in sorted_dict.keys()):
										over_to_ag = find_the_keys(sum_repl[0],over_keys)
										sum_all_pre = sum_all[all_sum].replace(sum_repl[0],nonDuplicateRows(sum_repl[0]))
										valueColExpr=valueColExpr.replace(sum_all[all_sum] + over_keys ,"("+sum_all_pre + over_to_ag + ")")
								   else:
										flag_for_sum = 0
										for it in sum_repl:
											if it[1:-1] in custom_keys_li:
												flag_for_sum = 1
												break
										if flag_for_sum ==0 and not (re.search('\/|\*',sum_all[all_sum])):											
											sum_all_pre = re.search('SUM\((.*)\)',sum_all[all_sum]).group(1)
											for it in sum_repl:
												if it in sorted_dict.keys():
													continue
												over_to_ag = find_the_keys(it,over_keys)
												sum_all_pre = sum_all_pre.replace(it, 'SUM(' + nonDuplicateRows(it) + ')' + over_to_ag)
											valueColExpr=valueColExpr.replace(sum_all[all_sum] + over_keys,"("+ sum_all_pre +")")
										else:
											continue 
                         else:
                                   sum_repl = re.findall("\[.*?\]",sum_all[all_sum])
                                   if len(sum_repl) == 1 and not (sum_repl[0] in sorted_dict.keys()):
                                        over_to_ag = find_the_keys(sum_repl[0],Over_to_agg)
                                        sum_all_pre = sum_all[all_sum].replace(sum_repl[0],nonDuplicateRows(sum_repl[0]))
                                        valueColExpr=valueColExpr.replace(sum_all[all_sum],"("+sum_all_pre+ over_to_ag + ")")                                      
                                   else:
                                        flag_for_sum = 0
                                        for it in sum_repl:
											if it[1:-1] in custom_keys_li:
												flag_for_sum = 1
												break
                                        if flag_for_sum ==0 and not (re.search('\/|\*',sum_all[all_sum])):											
											sum_all_pre = re.search('SUM\((.*)\)',sum_all[all_sum]).group(1)
											#sum_all_pre = sum_all[all_sum]
											for it in sum_repl:
												if it in sorted_dict.keys():
													continue
												over_to_ag = find_the_keys(it,Over_to_agg)
												sum_all_pre = sum_all_pre.replace(it, 'SUM(' + nonDuplicateRows(it) + ')' + over_to_ag)
											#sum_all_pre = sum_all_pre + ')'
											valueColExpr=valueColExpr.replace(sum_all[all_sum],"("+ sum_all_pre +")")
											
                                        else:
											valueColExpr=valueColExpr.replace(sum_all[all_sum],"("+sum_all[all_sum]+Over_to_agg+")")
                         print valueColExpr
                         sum_all[all_sum] = unique_sum					
                         all_sum_temp1= sum_all[all_sum][4:]
                         sum_new_pre = sum_all[all_sum]
                         if (all_sum_temp1.find('SUM(') !=-1):
                            all_sum_temp= Pick_Sum(all_sum_temp1,0).split("$$")
                            for i in range(0,len(all_sum_temp)):
								 
								 sum_all_rep = re.search('SUM\((.*)\)',all_sum_temp[i]).group(1)
								 if 'SUM(' in sum_all_rep: 
									all_counters = re.findall("\[(.*?)\]",all_sum_temp[i])
									count_agg = 0
									all_counters_p = all_counters
									keys = []
									for ele in all_counters:
										if ele in custom_keys_li or ele == 'TIME' or ele == 'DATE':
											keys.append(ele)
									for ele in keys:
										all_counters_p.remove(ele)
									for ele in all_counters_p:
										if 'SUM([' + ele + ']' in sum_all_rep:
											count_agg = count_agg + 1
									if len(all_counters_p) == count_agg:
										sum_all[all_sum] = sum_all[all_sum].replace(all_sum_temp[i],sum_all_rep)								
								 sum_all_new=""
								 if all_sum_temp[i] + "OVER(" in sum_all[all_sum]:                                   
										   txt_con = sum_all[all_sum][sum_all[all_sum].find(all_sum_temp[i]+ "OVER("):len(sum_all[all_sum])] 
										   over_keys = re.search("OVER\(.*?\]\)",txt_con).group()
										   sum_repl = re.findall("\[.*?\]",all_sum_temp[i])
										   if len(sum_repl) == 1 and not (sum_repl[0] in sorted_dict.keys()):
												over_to_ag = find_the_keys(sum_repl[0],over_keys)
												sum_all_pre = all_sum_temp[i].replace(sum_repl[0],nonDuplicateRows(sum_repl[0]))
												sum_all[all_sum]=sum_all[all_sum].replace(all_sum_temp[i] + over_keys ,"("+sum_all_pre + over_to_ag + ")")
										   else:
												flag_for_sum = 0
												for it in sum_repl:
													if it[1:-1] in custom_keys_li:
														flag_for_sum = 1
														break
												if flag_for_sum ==0 and not (re.search('\/|\*',all_sum_temp[i])):											
													sum_all_pre = re.search('SUM\((.*)\)',all_sum_temp[i]).group(1)
													for it in sum_repl:
														if it in sorted_dict.keys():
															continue
														over_to_ag = find_the_keys(it,over_keys)
														sum_all_pre = sum_all_pre.replace(it, 'SUM(' + nonDuplicateRows(it) + ')' + over_to_ag)
													sum_all[all_sum]=sum_all[all_sum].replace(all_sum_temp[i] + over_keys,"("+ sum_all_pre +")")
												else:
													continue 
								 else:
										   sum_repl = re.findall("\[.*?\]",all_sum_temp[i])
										   if len(sum_repl) == 1 and not (sum_repl[0] in sorted_dict.keys()):
												over_to_ag = find_the_keys(sum_repl[0],Over_to_agg)
												sum_all_pre = all_sum_temp[i].replace(sum_repl[0],nonDuplicateRows(sum_repl[0]))
												sum_all[all_sum]=sum_all[all_sum].replace(all_sum_temp[i],"("+sum_all_pre+ over_to_ag + ")")
												
										   else:
												flag_for_sum = 0
												for it in sum_repl:
													if it[1:-1] in custom_keys_li:
														flag_for_sum = 1
														break
												if flag_for_sum ==0 and not (re.search('\/|\*',all_sum_temp[i])):											
													sum_all_pre = re.search('SUM\((.*)\)',all_sum_temp[i]).group(1)
													#sum_all_pre = all_sum_temp[i]
													for it in sum_repl:
														if it in sorted_dict.keys():
															continue
														over_to_ag = find_the_keys(it,Over_to_agg)
														sum_all_pre = sum_all_pre.replace(it, 'SUM(' + nonDuplicateRows(it) + ')' + over_to_ag)
													#sum_all_pre = sum_all_pre + ')'
													sum_all[all_sum]=sum_all[all_sum].replace(all_sum_temp[i],"("+ sum_all_pre +")")
													
												else:
													sum_all[all_sum]=sum_all[all_sum].replace(all_sum_temp[i],"("+all_sum_temp[i]+Over_to_agg+")")
													
                         valueColExpr=valueColExpr.replace(sum_new_pre,sum_all[all_sum])                               
                 
                 valueColExpr=valueColExpr.replace(Over_to_agg+Over_to_agg,Over_to_agg) 
           
											  
																						
        if re.search("(?i)UNIQUECOUNT\(\[DCVECTOR_INDEX\]\)", valueColExpr):
			if '[DCVECTOR_INDEX]' in  Over_to_agg:
				valueColExpr =  valueColExpr.replace("UNIQUECOUNT([DCVECTOR_INDEX])","UNIQUECOUNT([DCVECTOR_INDEX])OVER([DCVECTOR_INDEX])")
        Sum_expr_count=  re.subn("SUM\(", '', valueColExpr)[1]
        Over_expr_count=  re.subn("OVER\(", '', valueColExpr)[1]


        if (valueColExpr.find("/")>0 and valueColExpr.find("IF")<0):
            all_cols = re.findall('\[(.+?)/(.+?)\]',valueColExpr )
            if len(all_cols)>0:
                     last_div_index=valueColExpr.rfind("/")
                     for all_cols_value in range(0,len(all_cols)):
                          for sub_list in range(0,len(all_cols[all_cols_value])):
                                try:
									if(last_div_index>last_div_index[all_cols_value][sub_list]):
										break
									else:
										str1=valueColExpr[0:last_div_index]
										str2=valueColExpr[last_div_index+1:len(valueColExpr)]
										valueColExpr='('+str1+')'+valueColExpr[last_div_index]+'('+str2+')'
                                        
                                except:
									print "hello"
            else:
                  last_div_index=valueColExpr.rfind("/")
                  str1=valueColExpr[0:last_div_index]
                  str2=valueColExpr[last_div_index+1:len(valueColExpr)]
                  if(str2.find(",")>-1):
                         str2_temp=str2.split(",")
                         valueColExpr='('+str1+valueColExpr[last_div_index]+'('+str2_temp[0]+')'+','+str2_temp[1]
                  else:        
                         valueColExpr='('+str1+')'+valueColExpr[last_div_index]+'('+str2+')'
                  
        valueColExpr=add_pair_brac(valueColExpr) 
        if (re.search("\/",valueColExpr)):
			all_division = paired_bracket_division(valueColExpr,0).split("$$")
			if (re.search("(\[[^\[]*\])", valueColExpr)):
				for ele in all_division:
					if ele <> str.Empty and re.search('^\s*OVER\(',valueColExpr[valueColExpr.find(ele)+len(ele):len(valueColExpr)]):
						over_par = re.search('^\s*OVER\(.*?\]\)',valueColExpr[valueColExpr.find(ele)+len(ele):len(valueColExpr)]).group()
						ele = ele + over_par
					ele_pre = ele
					print ele
					if ele <> str.Empty:
						ele = "SN(" + ele + ",0)"
						print ele
						valueColExpr = valueColExpr.replace(ele_pre,ele,1)
        print valueColExpr  
        column_typedict = {}
        new_value_sn = OrderedDict()
        Flag_count = 0
        Total_list = unique_value(valueColExpr)
        org_valueColExpr = valueColExpr
        value_count = 1
        for w in Total_list:
			Column_type=Find_data_type(w,TableName)
			column_typedict[w] = Column_type        
        
        if Flag_count == 0  and num_counter != 1:
			if re.search("MIN\(.*?\)\s*OVER\(.*?\]\)",valueColExpr):
				min_split = re.split("MIN\(.*?\)\s*OVER\(.*?\]\)",valueColExpr)
				for min_ele in min_split:
					split_over_value = re.split('OVER\(.*?\]\)',min_ele)
					print "split over"
					print split_over_value
					split_over_value_new = split_over_value
					if re.search("IS NULL",valueColExpr): 
						for p in split_over_value:
							split_over_value_new = re.split('\(.*\[.*?\].*IS NULL',p)
							#print split_over_value_new
							new_value_sn = replacing_for_sn(split_over_value_new)
							for key,value in new_value_sn.items():
								valueColExpr = valueColExpr.replace(key,value,1)	                        
					else:
						new_value_sn = replacing_for_sn(split_over_value_new)
						for key,value in new_value_sn.items():
							valueColExpr = valueColExpr.replace(key,value,1)
			else:
				split_over_value = re.split('OVER\(.*?\]\)',valueColExpr)
				print "split over"
				print split_over_value
				split_over_value_new = split_over_value
				if re.search("IS NULL",valueColExpr): 
					for p in split_over_value:
						split_over_value_new = re.split('\(.*\[.*?\].*IS NULL',p)
						print split_over_value_new
						new_value_sn = replacing_for_sn(split_over_value_new)
						for key,value in new_value_sn.items():
							valueColExpr = valueColExpr.replace(key,value,1)	                        
				else:
					new_value_sn = replacing_for_sn(split_over_value_new)
					for key,value in new_value_sn.items():
						valueColExpr = valueColExpr.replace(key,value,1)
        
        if valueColName in dict_of_variables.keys():
			dict_of_variables[valueColName] = valueColExpr
		
        print "ffffffffffffffffffff"
        print valueColExpr
          
        valueColExpr = open_formula(valueColExpr)

        if valueColName in dict_of_variables.keys():
			dict_of_variables[valueColName] = valueColExpr
			
        valueColName_chart=valueColName+"_chart"
        valueColExpr_chart=valueColExpr
        over_with_keys_charts=""
        for key,value in Keys_and_yaxis.items():           
           overr_keys_charts = value           
           check_kpi_chart=key
           if check_kpi_chart.find(valueColName)>0:                     
                      Over_to_agg_temp=Over_to_agg.replace("OVER(","").replace(")","").replace("[OSS_ID],","").split(",")
                      if (len(Over_to_agg_temp)!=len(overr_keys_charts)): 
                                 over_with_keys_charts="OVER("    
                                 for ov_keys_c in range(0, len(overr_keys_charts)):
                                            over_with_keys_charts=over_with_keys_charts+"["+overr_keys_charts[ov_keys_c]+"],"

                                 over_with_keys_charts=over_with_keys_charts.strip(",")+")"
                                 break
                      else:
                                 Over_to_agg_temp.sort()
                                 overr_keys_charts.sort()
                                 if (Over_to_agg_temp)!=(overr_keys_charts): 
                                     over_with_keys_charts="OVER("
                                     for ov_keys_c in range(0, len(overr_keys_charts)):
                                                over_with_keys_charts=over_with_keys_charts+"["+overr_keys_charts[ov_keys_c]+"],"

                                     over_with_keys_charts=over_with_keys_charts.strip(",")+")"
                                     break           
        
        # add aggregation for each counter in  formula     
        valueColExpr = add_aggregation(valueColExpr,Over_to_agg)	
        
        if re.search('\[.*?\]',valueColExpr):
                all_counters = re.findall('\[(.*?)\]',valueColExpr)	
                temp_list =[]
                for item in all_counters:
                    if item in custom_keys_li:
                        temp_list.append(item)
                for ele in temp_list:
                    all_counters.remove(ele)
                #print all_counters
                if len(all_counters) == 1 and 'SUM(' in valueColExpr and not (valueColExpr.startswith('ROUND(') or valueColExpr.startswith('(ROUND('))and data_type_kpi[valueColName] == 'NUMERIC':
					print "in ifffff"
					valueColExpr = 'ROUND(' + valueColExpr + ',0)'
					roundflag = 1
					round_p = '0.'        
      

        if len(over_with_keys_charts)>0:
                if re.search("\[(.*?)\]",valueColExpr_chart):
                     counters_list_chart = re.findall("\[(.*?)\]",valueColExpr_chart)
                counters_list_chart = list(set(counters_list_chart))
                st_kpi_name_chart="[" + valueColName + "]"
               
                TableName_chart = 'CUSTOMDATAPROVIDER_NE'              
                valueColExpr_chart =  add_aggregation(valueColExpr_chart,over_with_keys_charts)

                try:            
                        cols = Document.Data.Tables['CUSTOMDATAPROVIDER_NE'].Columns
                        cols.AddCalculatedColumn(valueColName_chart,valueColExpr_chart);
                        print "Kpis for chart added! "+valueColName_chart
                        if roundflag == 1:
                            newTable = Document.Data.Tables['CUSTOMDATAPROVIDER_NE']
                            column = newTable.Columns[valueColName_chart]
                            formatter = column.Properties.Formatter
                            newFormatter=formatter.Clone()
                            newFormatter.FormatString=round_p
                            column.Properties.Formatter=newFormatter
                except Exception as e:
                        print e.message
                        function_log("--[INFO]-- Expression for chart KPI: ")
                        function_log(valueColExpr_chart)
                        function_log("--[WARNING]--"+str(e.message))
		
             
                
        if valueColName in dictVariablesAlerter.keys():
			dictVariablesAlerter[valueColName] = valueColExpr
        if valueColName in dict_of_alerter.keys():
			for key,value in dictVariablesAlerter.items():
				if dict_of_alerter[valueColName].find(key)>=0:
					print "Again formula opening!"
					#print valueColExpr
					val_grp = re.findall("OVER\((.*?\])\)",dict_of_alerter[valueColName])
					#print val_grp
					for v in val_grp:
						if not (key in re.findall("\[(.*?)\]",v)):
							dict_of_alerter[valueColName]=dict_of_alerter[valueColName].replace("["+key+"]","("+value+")")
					if len(val_grp) == 0:
						dict_of_alerter[valueColName]=dict_of_alerter[valueColName].replace("["+key+"]","("+value+")")
					#print valueColExpr

        if valueColName in dict_of_alerter.keys():
			valueColExpr = "If(" + dict_of_alerter[valueColName] + ", NULL, " + valueColExpr + ")"
        print valueColName	
        print valueColExpr
        if valueColName == "DATETIME":
                kpi_name_datetime="[DATETIME]"
                valueColExpr = "Datetime(Concatenate(Date([DATE]),' ',[TIME]))"
                cols = Document.Data.Tables['CUSTOMDATAPROVIDER_NE'].Columns
                cols.AddCalculatedColumn(valueColName,valueColExpr);
        else:

                cols = Document.Data.Tables[TableName].Columns
                cols.AddCalculatedColumn(valueColName,valueColExpr);
                function_log("")
                function_log("--[INFO]-- Expression After Modification:")
                function_log(valueColExpr)
	                
		

        if roundflag == 1:
				newTable = Document.Data.Tables[TableName]
				column = newTable.Columns[valueColName]
				formatter = column.Properties.Formatter
				newFormatter=formatter.Clone()
				newFormatter.FormatString=round_p
				column.Properties.Formatter=newFormatter
				roundflag=0       
        
        Document.Properties[varaibleCreationResult] = Document.Properties[varaibleCreationResult]+valueColName+'KPI created successfully'
        								   
       
       
    except Exception as e:
		exc_type, exc_obj, exc_tb = sys.exc_info()
		fname = os.path.split(exc_tb.tb_frame.f_code.co_filename)[1]
		print(exc_type, fname, exc_tb.tb_lineno)
		print e.message
		#if value_count == 0:
		dict[valueColName] = valueColExpr
		function_log("")
		function_log("--[INFO]-- Expression After Modification:")
		function_log(valueColExpr)
		function_log("--[WARNING]-- Exception Thrown:"+str(e.message))
									 
		
print (dict)		
dict_keys = dict.keys()
dict_values = dict.values()
for key,val in dict.items():
	if re.search("@UNKOWNTOKEN",val) or re.search("\[\?\?DP",val):
		dict.pop(key)
#print dict_keys
for value in dict.values():
	if any(z in value for z in dict_keys):
		dep[(dict_keys[dict_values.index(value)])]= dict[(dict_keys[dict_values.index(value)])]
#print "------------------------------------------------------"
#print (dep)
all(map(dict.pop, dep))
#print "----------------------------------------------------------"
#print (dict)
#print dict     
for key in dict.keys():
	try:
		valueColName = key
		
		valueColExpr = dict[key]
		column_typedict = {}
		new_value_sn = OrderedDict()
		Flag_count = 0
		Total_list = unique_value(valueColExpr)
		#print Total_list
		org_valueColExpr = valueColExpr
		for w in Total_list:
			Column_type=Find_type(w)
			column_typedict[w] = Column_type
		#print Flag_count
		if Flag_count == 0:
			split_over_value = re.split('OVER\(.*?\]\)',valueColExpr)
			split_over_value_new = split_over_value
			if re.search("IS NULL",valueColExpr): 
				for p in split_over_value:
					split_over_value_new = re.split('\(.*\[.*?\].*IS NULL',p)
			new_value_sn = replacing_for_sn(split_over_value_new)
			for key,value in new_value_sn.items():
				valueColExpr = valueColExpr.replace(key,value,1)
				
		if valueColName in dict_of_alerter.keys():
			valueColExpr = "If(" + dict_of_alerter[valueColName] + ", NULL, " + valueColExpr + ")"
		#print valueColExpr
		cols = Document.Data.Tables["CUSTOMDATAPROVIDER_NE"].Columns													  
		cols.AddCalculatedColumn(valueColName,valueColExpr);
		function_log("")
		function_log("--[INFO]-- Expression After Modification:")
		function_log(valueColExpr)            	
	except Exception as e:
		#print e.message
		Document.Properties[varaibleCreationResult] = Document.Properties[varaibleCreationResult]+valueColName+'KPI Creation Failed'
		colNotAdded=colNotAdded+" :: "+valueColName+"  :: => "+valueColExpr
		count=count+1
		function_log("")
		function_log("--[INFO]-- Expression After Modification:")
		function_log(valueColExpr)
		function_log("--[WARNING]-- Exception Thrown:"+str(e.message))
		function_log(str(e.message))

for key in dep.keys():
	try:
		valueColName = key
		#print valueColName
		valueColExpr = dep[key]
		column_typedict = {}
		new_value_sn = OrderedDict()
		Flag_count = 0
		Total_list = unique_value(valueColExpr)
		#print Total_list
		org_valueColExpr = valueColExpr
		for w in Total_list:
			Column_type=Find_type(w)
			column_typedict[w] = Column_type
		if Flag_count == 0:
			split_over_value = re.split('OVER\(.*?\]\)',valueColExpr)
			split_over_value_new = split_over_value
			if re.search("IS NULL",valueColExpr): 
				for p in split_over_value:
					split_over_value_new = re.split('\(.*\[.*?\].*IS NULL',p)
			new_value_sn = replacing_for_sn(split_over_value_new)
			for key,value in new_value_sn.items():
				valueColExpr = valueColExpr.replace(key,value,1)
		
		if valueColName in dict_of_alerter.keys():
			valueColExpr = "If(" + dict_of_alerter[valueColName] + ", NULL, " + valueColExpr + ")"
		#print valueColExpr
		cols = Document.Data.Tables["CUSTOMDATAPROVIDER_NE"].Columns
		cols.AddCalculatedColumn(valueColName,valueColExpr)
		function_log ("")
		function_log("--[INFO]-- Expression After Modification:")
		function_log(valueColExpr)
										  
	except:
		Document.Properties[varaibleCreationResult] = Document.Properties[varaibleCreationResult]+valueColName+'KPI Creation Failed'
		colNotAdded=colNotAdded+" :: "+valueColName+"  :: => "+valueColExpr
		count=count+1
		function_log ("")
		function_log("--[INFO]-- Expression After Modification:")
		function_log(valueColExpr)
		function_log("--[WARNING]-- Exception Thrown:"+str(e.message))


print count
if count == 0:
    Document.Properties["KPISuccess"] = "passed"
elif tot_kpi - count == tot_kpi:
    Document.Properties["KPISuccess"] = "Failure"
else:
    Document.Properties["KPISuccess"] = "Partial"
function_log ("")
#function_log(str(count) + "Kpis failed!")
function_log("")
function_log("--[INFO]--  out of "+str(tot_kpi)+" KPI's "+str(tot_kpi-count)+" KPI are added successfully")
function_log("")
print "KPIs not added"
if count == 0:
	Document.Properties["CreateCalculatedColumnsResult"] = "Passsed"
else:
	Document.Properties["CreateCalculatedColumnsResult"] = "KPI not added" + colNotAdded
print colNotAdded