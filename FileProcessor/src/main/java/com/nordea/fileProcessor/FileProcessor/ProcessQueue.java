package com.nordea.fileProcessor.FileProcessor;

public interface ProcessQueue {

	public void put(Sentence processedSentence) throws Exception;
	public Sentence poll(String processName) throws Exception;
	public void registerProcess(String processName);
	
}
