package com.nordea.fileProcessor.FileProcessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/*
 * This is the File Writer Class. It writes the Content to the File
 * 
 * */
@Component
@Scope("prototype")
public class RandomAccessFileWriter implements FileWriter {

	private static Logger logger = LoggerFactory.getLogger(RandomAccessFileWriter.class);
	private RandomAccessFile stream= null;
	private FileChannel channel= null;
	private String fileName;
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public RandomAccessFileWriter() {
		super();
	}

	public void openFile() throws FileProcessorException {
		try {				  
			File targetFile = new File(fileName);
			boolean isTargetFileDeleted = targetFile.delete();
			if(targetFile.exists() && !isTargetFileDeleted) {
				logger.warn( "Unable to delete the target file-> "+fileName);
			}
			stream =new RandomAccessFile(fileName,"rw");
			channel = stream.getChannel();

		}catch(Exception e) {
			logger.error( e.getMessage(),e);
			FileProcessorException.raiseException(e);
		}

	}


	public void writeFile(String text) throws FileProcessorException {
		try {
			stream.write(text.getBytes("UTF-8"));

		}catch(Exception e) {
			logger.error( e.getMessage(),e);
			FileProcessorException.raiseException(e);
		}

	}

	public void closeFile() throws FileProcessorException {
		try {
			if(channel!=null)
				channel.close();
			if(stream!=null)
				stream.close();

		} catch (Exception e) {
			logger.error( e.getMessage(),e);
			FileProcessorException.raiseException(e);
		}
	}

	public void writeHeader(String headerText) throws FileProcessorException {

		BufferedReader br = null; ;
		String line;
		String sourceFileName=null;
		logger.debug("Appending Header to File"+fileName);

		try {       
			sourceFileName=this.fileName;
			br = new BufferedReader(new InputStreamReader(
					new FileInputStream(sourceFileName),
					"UTF-8"));
			this.fileName=getOutputFileName(sourceFileName,".tmp");

			openFile();
			writeFile(headerText);

			while ((line = br.readLine()) != null) {
				writeFile(line);
				writeFile("\n");
			}    

		} catch (Exception e) {       
			logger.error( e.getMessage(),e);
			FileProcessorException.raiseException(e);
		}finally {
			closeFile();

			try {
				if(br!=null)
					br.close();
			} catch (IOException e) {
				logger.error( e.getMessage(),e);
				FileProcessorException.raiseException(e);
			}
		}
		logger.debug("Header appended to "+fileName+". Renaming the temp file to main file");
		renameFile(fileName,sourceFileName);
	}

	public void renameFile(String sourceFileName, String targetFileName) throws FileProcessorException {

		File sourceFile = new File(sourceFileName);
		File targetFile = new File(targetFileName);
		boolean isTargetFileDeleted = targetFile.delete();
		if(targetFile.exists() && !isTargetFileDeleted ) {
			logger.warn( "Unable to delete the tmp csv file-> "+targetFileName);
			//FileProcessorException.raiseException(new Exception("Unable to delete the tmp file-> "+sourceFileName ));
		}
		boolean isSourceFileRenamed = sourceFile.renameTo(targetFile);
		if(!isSourceFileRenamed) {
			logger.warn( "Unable to rename the tmp file-> "+sourceFileName +" to->"+targetFileName);
			FileProcessorException.raiseException(new Exception("Unable to rename the tmp file-> "+sourceFileName +" to->"+targetFileName));
		}
		boolean isSourceFileDeleted = sourceFile.delete();
		if(sourceFile.exists()&& !isSourceFileDeleted ) {
			logger.warn( "Unable to delete the tmp file-> "+sourceFileName);

		}

	}
}
