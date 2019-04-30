package com.nordea.fileProcessor.FileProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
@SpringBootApplication
public class FileProcessorApplication {
		
	
		
		
		
	    @Autowired
	    private TaskExecutor taskExecutor;
	    @Autowired
	    private ApplicationContext applicationContext;
	    @Bean
	    public void executeAsynchronously() {
	        FileReader fr = applicationContext.getBean(FileReader.class);
	        SentenceProducer sp = applicationContext.getBean(SentenceProducer.class);
	        XMLWriter xw = applicationContext.getBean(XMLWriter.class);
	        CSVWriter csw = applicationContext.getBean(CSVWriter.class);
	        taskExecutor.execute(fr);
	        taskExecutor.execute(sp);
	        taskExecutor.execute(xw);
	        taskExecutor.execute(csw);
	    }
	    @Bean
	    public BlockingQueue<String> blockingQueue() {	    	
	    	BlockingQueue<String> readerBlockingQueue = new LinkedBlockingQueue<String>(100);
	    	return readerBlockingQueue;
	    }
	  
	    @Bean 
	    @Scope("prototype")
	    public StringBuilder stringBuilder() {
	    	 return new StringBuilder();
	    }
	    
	    @Bean 
	    @Scope("prototype")
	    public List<String> list() {
	    	 return new ArrayList<String>();
	    }
	    

	    @Bean 
		public Map<String,LinkedBlockingQueue<Sentence>> processQueueMap() {
				return new HashMap<String, LinkedBlockingQueue<Sentence>>();
		}		
	    
	    
	  
	public static void main(String[] args) throws Exception {
		SpringApplication.run(FileProcessorApplication.class, args);
		
	}

}
