package com.nordea.fileProcessor.FileProcessor;

/*
 * Interface implemented by CSV and XML Writer
 * 
 * */
public interface SentenceWriter {

	public void writeContent() throws FileProcessorException;
}
