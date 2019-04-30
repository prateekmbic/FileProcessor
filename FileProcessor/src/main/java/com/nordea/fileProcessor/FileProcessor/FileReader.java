package com.nordea.fileProcessor.FileProcessor;

/*
 * This interface is for Reading Files
 *  
 * */
public interface FileReader {

	public void readFile(String fileName) throws  FileProcessorException;
}
