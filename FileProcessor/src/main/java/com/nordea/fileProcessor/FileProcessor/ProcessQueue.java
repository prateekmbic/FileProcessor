package com.nordea.fileProcessor.FileProcessor;


/*
 * This is wrapper to LinkedBlockingQueue<Sentence>.
 * Sentence Producer Populates this Queue with the Sentence Objects and
 * CSV and XML Writer Use this queue to recevie the Sentence Object
 * */
public interface ProcessQueue {

	public void put(Sentence processedSentence) throws Exception;
	public Sentence poll(String processName) throws Exception;
	public void registerProcess(String processName);
	
}
