package com.nordea.fileProcessor.FileProcessor;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/*
 * This is wrapper to LinkedBlockingQueue<Sentence>.
 * Sentence Producer Populates this Queue with the Sentence Objects and
 * CSV and XML Writer Use this queue to recevie the Sentence Object
 * */
@Component
public class SentenceBlockingQueue implements ProcessQueue {
	
	private static Logger logger = LoggerFactory.getLogger(SentenceBlockingQueue.class);
	
	@Autowired
	Map<String,LinkedBlockingQueue<Sentence>> processQueueMap ;
	
	
	@Value("${processQueue.initialCapaity}")
	private int initialCapacity; 
	
	public void registerProcess(String processName) {
		
		processQueueMap.put(processName, new LinkedBlockingQueue<Sentence>(initialCapacity));
		logger.info(processName +" registered for receive Messages");
	}
	
	
	public Sentence poll(String processName) throws InterruptedException {	
	
		return (Sentence)processQueueMap.get(processName).poll(3,TimeUnit.SECONDS);
	}
	
	public void put(Sentence sentence) throws FileProcessorException {
		
		
		this.processQueueMap.entrySet().forEach((entrySet)->{
			try {
				entrySet.getValue().put(sentence);
			} catch (Exception e) {
				logger.error( e.getMessage(),e);

			}
		});
		
	}
	
	 

}
