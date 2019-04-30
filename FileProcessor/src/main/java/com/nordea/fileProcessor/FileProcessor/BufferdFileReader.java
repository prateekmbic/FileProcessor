package com.nordea.fileProcessor.FileProcessor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/*
 * This is File Reader Class which read's the file using Buffered Reader
 * */
@Component
@Scope("prototype")
public class BufferdFileReader implements FileReader, Runnable{


	private static final String FILE_READER = "File Reader";
	private static Logger logger = LoggerFactory.getLogger(BufferdFileReader.class);

	@Autowired
	private BlockingQueue<String> readerSentenceQueue ;

	@Value("${sourceFileName}")
	private String fileName;

	private BufferedReader br ;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public BlockingQueue<String> getRawSentenceQueue() {
		return readerSentenceQueue;
	}

	public void setRawSentenceQueue(BlockingQueue<String> rawSentenceQueue) {
		this.readerSentenceQueue = rawSentenceQueue;
	}

	@Override
	public void run(){
		Thread.currentThread().setName(FILE_READER);
		logger.info(FILE_READER +"Process Started for File"+ this.fileName );
		try {

			readFile(this.fileName);
			logger.info(FILE_READER +"Process Completed");

		} catch (Exception e) {
			logger.error( e.getMessage(),e);
			logger.error(FILE_READER +"Process Failed");
			FileProcessorException.raiseProcessFailureException(FILE_READER,e.getMessage());
		}

	}



	public void readFile(String fileName) throws  FileProcessorException{

		String line;

		try {


			br = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName),
					"UTF-8"));
			while ((line = br.readLine()) != null) {
				
				readerSentenceQueue.put(line);
			}

		}catch(Exception e) {
			logger.error( e.getMessage(),e);
			FileProcessorException.raiseException(e);
		}
		finally {
			try {
				if(br!=null)
					br.close();
			} catch (IOException e) {
				logger.error( e.getMessage(),e);

			}
		}
	}
}
