package com.nordea.fileProcessor.FileProcessor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Classes implementing this interface writes the contents to the file
 * */
public interface FileWriter {

	public static final String UNABLE_TO_FIND_THE_OUTPUT_FILE_NAME = "Unable to Find the Output File Name";

	public void setFileName(String fileName);
	public String getFileName();
	void writeFile(String string) throws FileProcessorException;
	void closeFile() throws FileProcessorException;
	public void openFile() throws FileProcessorException;
	public void writeHeader(String headerText) throws FileProcessorException ;
	

	default String getOutputFileName(String inputFileName, String fileExtension) throws FileProcessorException {
		String newFileName= null;
		try {
			Pattern fileNamePattern = Pattern.compile("^.*(?=\\.)");

			Matcher matcher= fileNamePattern.matcher(inputFileName);
			while(matcher.find()) {
				newFileName= matcher.group()+fileExtension;
			}
			if(newFileName==null) {
				FileProcessorException.raiseException(new Exception(UNABLE_TO_FIND_THE_OUTPUT_FILE_NAME));
			}
		}catch(Exception e){

			FileProcessorException.raiseException(e);
		}
		return newFileName;
	}


}
