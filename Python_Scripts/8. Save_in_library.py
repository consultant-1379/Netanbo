import clr
import sys,os
clr.AddReference("System.Windows.Forms")
from System.Windows.Forms import MessageBox,Form,MessageBoxButtons,DialogResult
from Spotfire.Dxp.Application import DocumentSaveSettings
from Spotfire.Dxp.Framework.Library import *
from Spotfire.Dxp.Framework.ApplicationModel import ProgressService
from Spotfire.Dxp.Data  import *
from Spotfire.Dxp.Data.Import  import *
from Spotfire.Dxp.Data.Import import TextFileDataSource, TextDataReaderSettings
from Spotfire.Dxp.Data import DataTableSaveSettings
try:
	libraryManager = Application.GetService(LibraryManager)
	Folder_structure = Document.Properties["FolderStructure"].split('\\')
	print Folder_structure
	
	mainfolder_path = '/Ericsson Library/'
	temp_subfolder = 'spotfire'
	csvfilename = Document.Properties["csvfilename"].split("QueryBO")[1]
	report_folder = csvfilename.split(",")[0]
	print csvfilename
	for Folder_name in Folder_structure:
		parentFolderExists, folder = libraryManager.TryGetItem(mainfolder_path, LibraryItemType.Folder)
		subfolderExists, subfolder = libraryManager.TryGetItem(mainfolder_path + Folder_name + '/', LibraryItemType.Folder)
		print mainfolder_path
		if not subfolderExists:
			libraryManager.CreateFolder(folder, Folder_name, LibraryItemMetadataSettings())
			print "dd"
		mainfolder_path =mainfolder_path + Folder_name + "/"
		print mainfolder_path
	index=Document.Pages.IndexOf(Document.ActivePageReference)
	for page in Document.Pages:
         if (page.Title == "Refresh"):
             Document.ActivePageReference=page
         ''''if((page.Title == "UI") or (page.Title == "Refresh")):
				print page.Title
         else:
				Document.Pages.Remove(page) '''

    #Document.Pages.RemoveAt(index)
	
	success, libraryFolder = libraryManager.TryGetItem(mainfolder_path, LibraryItemType.Folder)
	settings = DocumentSaveSettings()
	Application.SaveCopy(libraryFolder,csvfilename,LibraryItemMetadataSettings(), settings);
	print "saved"
	Document.Properties["LibraryResult"] =  "Passsed"
except Exception as e:
	exc_type, exc_obj, exc_tb = sys.exc_info()
	fname = os.path.split(exc_tb.tb_frame.f_code.co_filename)[1]
	print(exc_type, fname, exc_tb.tb_lineno)
	print e.message
	Document.Properties["LibraryResult"] =  e.message

