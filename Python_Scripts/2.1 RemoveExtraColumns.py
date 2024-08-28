from Spotfire.Dxp.Data import *
from Spotfire.Dxp.Application.Visuals import TablePlot
from Spotfire.Dxp.Data.Import import DatabaseDataSource, DatabaseDataSourceSettings, DataTableDataSource
from System.Collections.Generic import List
import re
import sys, os
from collections import OrderedDict
from Spotfire.Dxp.Data.Formatters import *
from Spotfire.Dxp.Data.Transformations import *
from Spotfire.Dxp.Application.Visuals import *
from Spotfire.Dxp.Application.Layout import LayoutDefinition
from Spotfire.Dxp.Data import DataPropertyClass
from Spotfire.Dxp.Data import *
from System.Collections.Generic import List
import re
from Spotfire.Dxp.Application.Visuals import *
from System.Drawing import Size
from System.Drawing import Color


def matchTheBrackets(txt):
    opening = 0
    closing = 0
    for i in range(0,len(txt)):
        if txt[i] == '(':
            opening = opening + 1
        elif txt[i] == ')':
            closing = closing+1
    if opening > closing:
        for i in range(0,(opening - closing)):
            txt= txt+')'
        print txt
    return txt


mergeDocProp = 'mergeDimensionField'
mergeDimension = Document.Properties[mergeDocProp] 
keys_list = [] 
custom_keys_li = []
Query_tablename = Document.Properties["csvfilename"]
queryTable = Document.Data.Tables[Query_tablename]
cursorName = DataValueCursor.CreateFormatted(queryTable.Columns["TableName"])
cursorKeyName = DataValueCursor.CreateFormatted(queryTable.Columns["KeyNames"])
cursorQuery = DataValueCursor.CreateFormatted(queryTable.Columns["SQL Query"])
for row in queryTable.GetRows(cursorName,cursorKeyName,cursorQuery):
    valueName = cursorName.CurrentValue.replace(" ","").upper()
    valueQuery = cursorQuery.CurrentValue.upper()
    if valueName == "CUSTOMDATAPROVIDER":
		keys = cursorKeyName.CurrentValue.upper()
		keys_list = keys.split(",")
		break
custom_keys_li = keys_list

if mergeDimension!= "":
	custom_keys_li.append('TABLENAME')

tableName = "CUSTOMDATAPROVIDER"
dataTable = Document.Data.Tables[tableName]
list_of_cols = []

cols = dataTable.Columns
for i in cols:
    list_of_cols.append(i.Name)

print list_of_cols    
for ele in custom_keys_li:
    if not ele in list_of_cols:
		continue
    print ele
    expression = ''
    columnsToDelete = []
    for item in list_of_cols:
        if (ele + ' (' in item and item.startswith(ele)) or ele == item:
            columnsToDelete.append(item)
            expression = expression + 'if([' + item + '] is not null, [' + item + '], '
    print "columnsToDelete"
    print columnsToDelete
    expression = expression.strip().strip(',')
    expression = matchTheBrackets(expression)
    name = ele + '_TRANS'
    transformation = ExpressionTransformation()            
    transformation.ColumnAdditions.Add(name,expression)
    print transformation
    dataTable.AddTransformation(transformation)
    cols_to_remove = List[DataColumn]()
    for col in cols:        
        if col.Name in columnsToDelete:
            print col.Name
            cols_to_remove.Add(col)
    print cols_to_remove
    cols.Remove(cols_to_remove)

'''for col in dataTable.Columns:
	if col.Name == 'DATE_ID_TRANS':
		valueColName = 'DATE_ID_TRANS'
		valueColExpr = 'Date([DATE_ID_TRANS])'
		list = List[DataColumnSignature]()
		list.Add(DataColumnSignature(dataTable.Columns[valueColName])) 
		transformation = ExpressionTransformation()            
		transformation.ColumnReplacements.Add(valueColName,valueColExpr,ColumnSelection(list));
		print transformation
		dataTable.AddTransformation(transformation)'''

for col in dataTable.Columns:
	if col.Name.endswith('_TRANS'):
		col.Name = col.Name[0:len(col.Name)-6]