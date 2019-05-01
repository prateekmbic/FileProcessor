package com.nordea.fileProcessor.FileProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/*
 * This is the Application Exception Class. 
 * The ErrorCodes and Error Message are yet not defined.
 * All the exceptions in the application are wrapped in the Exception
 * and rethrown
 * */
public class FileProcessorException extends Exception{

	private static final long serialVersionUID = 1L;
	private String errorCode;
	private String errorMessage;
	private static Logger logger = LoggerFactory.getLogger(FileProcessorException.class);



	public FileProcessorException(String errorCode, String errorMessage) {
		super();
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public FileProcessorException(String errorCode) {
		super();
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return this.errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getMessage() {
		return errorMessage;
	}

	public static void raiseException(Exception ex) throws FileProcessorException
	{

		raiseException(ex.getMessage(),ex);
	}

	public static void raiseException(String erroCode, Exception ex) throws FileProcessorException
	{
		throw new FileProcessorException(erroCode, ex.getMessage());
	}

	public static void raiseProcessFailureException(String processName, String errMsg) 
	{
		logger.error(processName +" Process Failed with error"+errMsg+" Interrupting other Processes");
		for (Thread t : Thread.getAllStackTraces().keySet()) 
		{  
			if("CSVProcess".equalsIgnoreCase(t.getName()) ||
					"XMLProcess".equalsIgnoreCase(t.getName()) ||
					"File Reader".equalsIgnoreCase(t.getName()) ||
					"Sentence Producer".equalsIgnoreCase(t.getName()) )
				logger.error(errMsg +" Process Failed. Interrupting "+ t.getName());

			t.interrupt(); 

		} 
	}

}
