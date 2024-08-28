package BOMain;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {

	public static void createDirectory(String directoryPath)
	{
		boolean success = false;
		File directory = new File(directoryPath);
		if(!directory.exists()) {
			success = directory.mkdirs();
			if(success)
			{
				System.out.println("Directory created");
			}
		}
	}

	public static BufferedWriter createFileAdnWriter(String queryFilePath, String reportName) throws IOException {

		String filePath = appendReportNametoFile(queryFilePath,reportName);
		File file = new File(filePath);
		if(file.exists()) {
			file.delete();
		}

		return new BufferedWriter(new FileWriter(file));

	}

	private static String appendReportNametoFile(String queryfilePath, String reportName) {
		String str[] = queryfilePath.split("\\.");
		String filePath =str[0]+reportName+"."+str[1];
		return filePath;
	}


	public static void write(BufferedWriter writer, String formattedString) throws IOException {
		writer.append(formattedString);
		writer.write(System.lineSeparator());
		writer.flush();
	}

	public static void close(BufferedWriter queryWriter) throws IOException {
		queryWriter.close();
	}

	public static BufferedWriter createFileAdnWriter(String reportDetailsFilePath) throws IOException {
		File file = new File(reportDetailsFilePath);
		if(file.exists()) {
			file.delete();
		}

		return new BufferedWriter(new FileWriter(file));
	}
	
	public static void createTxtFile(String path) throws IOException {
		File file = new File(path);
		if(file.createNewFile()) {
			System.out.println("File created");
		}
	}
}
