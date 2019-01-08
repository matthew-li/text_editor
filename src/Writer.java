import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/** The Writer that writes program text to the output file.
 *	@author Matthew E. Li
 */
public class Writer {

	/* The buffered writer. */
	private BufferedWriter bufferedWriter;
	/* The file writer. */
	private FileWriter fileWriter;
	/* The path of the file being written. */
	private String filePath;
	
	/** Instantiate a new writer for PATH, creating a file in the directory if
	 * 	nonexistent	and writing to it. */
	public Writer(String path) {
		filePath = path;
		try {
			File outputFile = new File(filePath);
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}
			fileWriter = new FileWriter(filePath);
			bufferedWriter = new BufferedWriter(fileWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** Write TEXT to the writer. */
	public void write(String text) {
		try {
			bufferedWriter.write(text);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** Close the writer. */
	public void close() {
		try {
			bufferedWriter.close();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
