package com.nordea.fileProcessor.FileProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/*
 * This is consumer thread to SentenceProducer.
 * Sentence Producer produces the Sentence Object and puts it into Process Queue.
 * This thread dequeus the message from the Process Queue and writes it in the .xml File
 * 
 * 
 * */
@Component
@Scope("prototype")
public class XMLWriter implements Runnable, SentenceWriter {

	private static final String XML_PROCESS = "XMLProcess";
	private static Logger logger = LoggerFactory.getLogger(XMLWriter.class);

	@Autowired
	private ProcessQueue blockingQueue;
	@Autowired
	private FileWriter fileWriter;

	@Value("${sourceFileName}")
	private String fileName;

	@Value("${xmlHeader}")
	private String xmlHeader;



	public String getXmlHeader() {
		return xmlHeader;
	}


	public void setXmlHeader(String xmlHeader) {
		this.xmlHeader = xmlHeader;
	}


	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public FileWriter getFileWriter() {
		return fileWriter;
	}


	public void setFileWriter(FileWriter fileWriter) {
		this.fileWriter = fileWriter;
	}

	private Sentence sentence;

	public XMLWriter(ProcessQueue blockingQueue, FileWriter fileWriter) {
		super();
		this.blockingQueue = blockingQueue;
		this.fileWriter = fileWriter;
		blockingQueue.registerProcess(XML_PROCESS);
	}


	@Override
	public void run() {
		logger.info(XML_PROCESS+" Started ");
		Thread.currentThread().setName(XML_PROCESS);
		try {
			fileName= fileWriter.getOutputFileName(fileName,".xml");
			writeContent();
			logger.info(XML_PROCESS+" Completed . File Output in "+fileName);
		} catch (FileProcessorException e) {
			logger.error(XML_PROCESS+" Failed");

		}

	}

	public void writeContent() throws FileProcessorException {

		try {

			boolean writeFlag=true;
			
			fileWriter.setFileName(fileName);
			fileWriter.openFile();
			while(writeFlag) {
				fileWriter.writeFile(xmlHeader);
				fileWriter.writeFile("\n<text>");
				do {
					sentence =blockingQueue.poll(XML_PROCESS);
					logger.debug(XML_PROCESS+ " deque msg"+sentence);

					if(sentence!=null){
						fileWriter.writeFile(sentence.toXML().toString());
					}else {
						fileWriter.writeFile("\n</text>");
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
			}catch(Exception e) {
				logger.error( e.getMessage(),e);
				FileProcessorException.raiseException(e);
			}
		}


	}

}
