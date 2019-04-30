package com.nordea.fileProcessor.FileProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CSVWriter implements Runnable,SentenceWriter {
	
	private static final String CSV_PROCESS = "CSVProcess";
	private static Logger logger = LoggerFactory.getLogger(CSVWriter.class);
	
	@Autowired
	private ProcessQueue blockingQueue;
	
	@Autowired
	private FileWriter fileWriter;
	private Sentence sentence;
	@Autowired
	private StringBuilder headerStringBuilder;
	
	@Value("${sourceFileName}")
	private String fileName;
	
	public FileWriter getFileWriter() {
		return fileWriter;
	}


	public void setFileWriter(FileWriter fileWriter) {
		this.fileWriter = fileWriter;
	}


	public CSVWriter(ProcessQueue blockingQueue, FileWriter fileWriter) {
		super();
		this.blockingQueue = blockingQueue;
		this.fileWriter = fileWriter;
		blockingQueue.registerProcess(CSV_PROCESS);
	}


	@Override
	public void run() {
		Thread.currentThread().setName(CSV_PROCESS);
		
		logger.info(CSV_PROCESS+" Started ");
		try {
			fileName= fileWriter.getOutputFileName(fileName,".csv");
			writeContent();
			logger.info(CSV_PROCESS+" Completed : File Output in "+fileName);
		} catch (FileProcessorException e) {
			logger.error( e.getMessage(),e);
			logger.error(CSV_PROCESS+" Failed");
			
		}
		
	}

	public void writeContent() throws FileProcessorException {
		boolean writeFlag=true;
		int headerLength=0;
		int currentWordListLength=0;

		try {
			fileWriter.setFileName(fileName);
			fileWriter.openFile();

			while(writeFlag) {

				do {
					sentence =blockingQueue.poll(CSV_PROCESS);
					logger.debug(CSV_PROCESS+ " deque msg"+sentence);

					if(sentence!=null &&sentence.getWordList()!=null)
					{ 
						currentWordListLength =sentence.getWordList().size();
						headerLength= currentWordListLength>headerLength?currentWordListLength:headerLength;
						fileWriter.writeFile(sentence.toCSV().toString());
					}
					
					if(sentence==null) {
						writeFlag=false;
					}
				}while(sentence!=null);

			}	 
		}catch(Exception e) {
			logger.error( e.getMessage(),e);
			FileProcessorException.raiseException(e);
			
		}
		finally {
			try {
				fileWriter.closeFile();
				
				fileWriter.writeHeader( getCSVFileHeader(headerLength)) ;
				
			}catch(Exception e) {
				logger.error( e.getMessage(),e);
				FileProcessorException.raiseException(e);
			}
		}

	}
	
	
	public String getCSVFileHeader(int headerLength){
		
		
		for(int i =1;i<=headerLength;i++) {
			headerStringBuilder.append(", Word ").append(i);
		}
		
		return headerStringBuilder.toString();
	}

}
